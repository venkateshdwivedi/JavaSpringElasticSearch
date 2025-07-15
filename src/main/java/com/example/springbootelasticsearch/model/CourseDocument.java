package com.example.springbootelasticsearch.model;

import java.time.Instant;

import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.DateFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "courses")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDocument {
    
    @Id
    private String id;
    private String title;
    @CompletionField
    private Completion suggest;
    private String description;
    private String category;
    private String type;
    private String gradeRange;
    private Integer minAge;
    private Integer maxAge;
    private Double price;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant nextSessionDate;

}
