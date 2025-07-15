package com.example.springbootelasticsearch.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootelasticsearch.model.CourseDocument;
import com.example.springbootelasticsearch.service.CourseSearchService;

@RestController
public class CourseController {

    @Autowired
    private CourseSearchService courseSearchService;

    @GetMapping("/api/search")
    public List<CourseDocument> coursesSearch(
    @RequestParam(required = false) String q,
    @RequestParam(required = false) String category,
    @RequestParam(required = false) String type,
    @RequestParam(required = false) Integer minAge,
    @RequestParam(required = false) Integer maxAge,
    @RequestParam(required = false) Double minPrice,
    @RequestParam(required = false) Double maxPrice,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(defaultValue = "nextSessionDate") String sort,
    @RequestParam(defaultValue = "asc") String order,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
    ){

        return courseSearchService.coursesSearch(   q, category, type,
        minAge, maxAge,
        minPrice, maxPrice,
        startDate,
        sort, order,
        page, size);
        
    }
}
