package com.example.springbootelasticsearch.service;

import com.example.springbootelasticsearch.model.CourseDocument;

import org.elasticsearch.search.sort.SortOrder;

import org.elasticsearch.index.query.*;
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

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseSearchService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public List<CourseDocument> coursesSearch(
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

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

    }
}
