package com.example.springbootelasticsearch.service;

import com.example.springbootelasticsearch.DTO.CourseSearchResponse;
import com.example.springbootelasticsearch.model.CourseDocument;

import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseSearchService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public CourseSearchResponse coursesSearch(
            String q,
            String category,
            String type,
            Integer minAge,
            Integer maxAge,
            Double minPrice,
            Double maxPrice,
            LocalDate startDate,
            String sort,
            String order,
            int page,
            int size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (q != null && !q.isEmpty()) {
            MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(q)
                    .field("title")
                    .field("description");
            boolQuery.must(multiMatchQuery);
        }

        if (category != null && !category.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("category.keyword", category));
        }

        if (type != null && !type.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("type.keyword", type));
        }

        if (minAge != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("maxAge").gte(minAge));
        }
        if (maxAge != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("minAge").lte(maxAge));
        }

        if (minPrice != null || maxPrice != null) {
            RangeQueryBuilder priceRange = QueryBuilders.rangeQuery("price");

            if (minPrice != null) {
                priceRange.gte(minPrice);
            }

            if (maxPrice != null) {
                priceRange.lte(maxPrice);
            }
            boolQuery.filter(priceRange);
        }

        if (startDate != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("nextSessionDate")
                    .gte(startDate.atStartOfDay().toInstant(ZoneOffset.UTC)));
        }

        SortOrder sortOrder;

        if ("desc".equalsIgnoreCase(order)) {
            sortOrder = SortOrder.DESC;
        } else {
            sortOrder = SortOrder.ASC;
        }

        Pageable pageable = PageRequest.of(page, size);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(SortBuilders.fieldSort(sort).order(sortOrder))
                .withPageable(pageable)
                .build();

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(searchQuery, CourseDocument.class);

        List<CourseDocument> courses = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        long total = searchHits.getTotalHits();

        return new CourseSearchResponse(total, courses);

    }

    public List<String> suggestTitle(String q) throws IOException {
        CompletionSuggestionBuilder completionSuggestionBuilder = SuggestBuilders
                .completionSuggestion("suggest") 
                .prefix(q) 
                .skipDuplicates(true)
                .size(10); 

        
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("coursesuggest", completionSuggestionBuilder); 

      
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.suggest(suggestBuilder);

       
        SearchRequest searchRequest = new SearchRequest("courses");
        searchRequest.source(sourceBuilder);

        
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

       
        Suggest suggest = searchResponse.getSuggest();
        List<String> results = new ArrayList<>();

        if (suggest != null) {
            suggest.getSuggestion("coursesuggest")
                    .getEntries()
                    .forEach(entry -> entry.getOptions().forEach(option -> results.add(option.getText().string())));
        }

        return results;
    }
}
