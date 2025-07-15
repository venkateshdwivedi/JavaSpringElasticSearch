package com.example.springbootelasticsearch.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Service;

import com.example.springbootelasticsearch.model.CourseDocument;
import com.example.springbootelasticsearch.repo.CourseRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.annotation.PostConstruct;

@Service
public class CourseLoaderService {

    @Autowired
    private CourseRepo courseRepo;

    @PostConstruct
    public void loadCourses() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        InputStream inputStream = new ClassPathResource("sample-courses.json").getInputStream();

        CourseDocument[] courses = objectMapper.readValue(inputStream, CourseDocument[].class);

        List<CourseDocument> courseList = Arrays.asList(courses);

        for (CourseDocument course : courseList) {
            course.setSuggest(new Completion(List.of(course.getTitle())));
        }

        if (courseRepo.count() == 0) {
            courseRepo.saveAll(courseList);
            System.out.println("Loaded Course data");
        } else {
            System.out.println("Courses already exist");
        }

    }

}
