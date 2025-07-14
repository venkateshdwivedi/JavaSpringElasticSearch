package com.example.springbootelasticsearch.repo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.springbootelasticsearch.model.CourseDocument;


public interface CourseRepo extends ElasticsearchRepository<CourseDocument,String> {

    
} 