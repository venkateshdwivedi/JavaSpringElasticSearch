package com.example.springbootelasticsearch.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.example.springbootelasticsearch.model.CourseDocument;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSearchResponse {
    private long total;
    private List<CourseDocument> courses;
}