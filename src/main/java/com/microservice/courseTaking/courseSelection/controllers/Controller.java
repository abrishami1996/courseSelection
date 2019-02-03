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

    @Value("${CourseServiceIp}")
    private String courseServiceIp;

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

    private boolean courseExists(int courseId){
        System.out.println(courseServiceIp);
        final String uri = "http://"+ courseServiceIp +":8080/getRole";
        RestTemplate restTemplate = new RestTemplate();
        String s = restTemplate.getForObject(uri,String.class);
        if("ok".equals(s)){
            return true;
        }
        return false;
    }

    @GetMapping(value = "/takecourse")
    public String takeCourse(String jwttoken, int courseId){
        if(getRole(jwttoken).equals("ROLE_STUDENT") || getRole(jwttoken).equals("ROLE_ADMIN") ) {
            //check if course exists
            if(courseExists(courseId)) {
                TakenCourse tk = new TakenCourse(getUserId(jwttoken), courseId);
                repository.save(tk);
                return "ok";
            }
            else{
                return "courseNotFound";
            }
        }
        return "not authenticated";
    }

    @GetMapping(value = "/removetakencourse")
    public String removeTakenCourse(String jwttoken, int courseId){
        if(getRole(jwttoken).equals("ROLE_STUDENT") || getRole(jwttoken).equals("ROLE_ADMIN") ) {
            if(courseExists(courseId)){
                if(courseExists(courseId)) {
                    repository.deleteById(courseId);
                    return "ok";
                }
                else{
                    return "courseNotFound";
                }
            }
        }
        return "not authenticated";
    }

    @GetMapping(value = "/getstudentcourseslist")
    public String getStudentCoursesList(String jwttoken){
        if(getRole(jwttoken).equals("ROLE_STUDENT") || getRole(jwttoken).equals("ROLE_ADMIN") ) {
            //return repository.finda
            return "todo";
        }
        return "not authenticated";
    }

    @GetMapping(value = "/getcoursememberlist")
    public String getCourseMembersList(String jwttoken){
        if(getRole(jwttoken).equals("ROLE_PROFESSOR") || getRole(jwttoken).equals("ROLE_ADMIN") ) {
            return "todo";
        }
        return "not authenticated";
    }
}
