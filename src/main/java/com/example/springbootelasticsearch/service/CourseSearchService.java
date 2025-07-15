package com.example.springbootelasticsearch.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.springbootelasticsearch.model.CourseDocument;

@Service
public class CourseSearchService {

    public List<CourseDocument> coursesSearch(String q, String category, String type, Integer minAge, Integer maxAge, Double minPrice, Double maxPrice, LocalDate startDate, String sort, String order, int page, int size) {
        
      return null;
    }

}
