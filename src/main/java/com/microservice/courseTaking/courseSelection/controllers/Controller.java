package com.microservice.courseTaking.courseSelection.controllers;

import com.microservice.courseTaking.courseSelection.models.TakenCourse;
import com.microservice.courseTaking.courseSelection.repository.TakenCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Controller {

    @Autowired
    TakenCourseRepository repository;

    @Value("${AuthServiceIp}")
    private String authServiceIp;

    private String getRole(String jwttoken){

        System.out.println(authServiceIp);
        final String uri = "http://"+ authServiceIp +":8080/getRole";
        System.out.println(jwttoken);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwttoken);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        String s = restTemplate.exchange(uri, HttpMethod.GET,entity,String.class).getBody().toString();
        return s;
    }

    private int getUserId(String jwttoken){
        return -1;
    }

    @GetMapping(value = "/takecourse")
    public void takeCourse(String jwttoken, int courseId){
        if(getRole(jwttoken).equals("ROLE_STUDENT") || getRole(jwttoken).equals("ROLE_ADMIN") ) {
            //check if course exists

            TakenCourse tk = new TakenCourse(getUserId(jwttoken),courseId);
            repository.save(tk);
        }
    }

    @GetMapping(value = "/removetakencourse")
    public void removeTakenCourse(String jwttoken, int courseId){
        if(getRole(jwttoken).equals("ROLE_STUDENT") || getRole(jwttoken).equals("ROLE_ADMIN") ) {

        }
    }

    @GetMapping(value = "/getstudentcourseslist")
    public void getStudentCoursesList(String jwttoken){
        if(getRole(jwttoken).equals("ROLE_STUDENT") || getRole(jwttoken).equals("ROLE_ADMIN") ) {

        }
    }

    @GetMapping(value = "/getcoursememberlist")
    public void getCourseMembersList(String jwttoken){
        if(getRole(jwttoken).equals("ROLE_PROFESSOR") || getRole(jwttoken).equals("ROLE_ADMIN") ) {

        }
    }
}
