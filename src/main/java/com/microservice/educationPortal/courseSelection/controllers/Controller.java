package com.microservice.educationPortal.courseSelection.controllers;

import com.microservice.educationPortal.courseSelection.models.TakenCourse;
import com.microservice.educationPortal.courseSelection.repository.TakenCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    @Autowired
    TakenCourseRepository repository;

    @Value("${AuthServiceIp}")
    private String authServiceIp;

    @Value("${CourseServiceIp}")
    private String courseServiceIp;

    @Value("${AuthServicePort}")
    private String authServicePort;

    @Value("${CourseServicePort}")
    private String courseServicePort;

    private String getRole(String jwttoken){
        final String uri = "http://"+ authServiceIp +":"+authServicePort+"/getRole";
        System.out.println(jwttoken);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwttoken);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        String s = restTemplate.exchange(uri, HttpMethod.GET,entity,String.class).getBody().toString();
        return s.substring(1,s.length()-1);
    }

    private int getUserId(String jwttoken){
        final String uri = "http://"+ authServiceIp +":"+authServicePort+"/getUserId";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwttoken);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        Integer res = restTemplate.exchange(uri, HttpMethod.GET,entity,Integer.class).getBody();
        return res;
    }

    private boolean courseExists(int courseId){
        final String uri = "http://"+ courseServiceIp +":"+courseServicePort+"/exists?courseid="+courseId;
        RestTemplate restTemplate = new RestTemplate();
        String s = restTemplate.getForObject(uri,String.class);
        System.out.println(s);
        if("ok".equals(s)){
            return true;
        }
        return false;

    }

    @GetMapping(value = "/takecourse")
    public String takeCourse(String jwttoken, int courseId){
        String role = getRole(jwttoken);
        if(role.equals("ROLE_STUDENT") || role.equals("ROLE_ADMIN") || role.equals("ROLE_EMPLOYEE")) {
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
        String role = getRole(jwttoken);
        if(role.equals("ROLE_STUDENT") || role.equals("ROLE_ADMIN") || role.equals("ROLE_EMPLOYEE")) {
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
    public String getStudentCoursesList(String jwttoken,int studentId){
        String role = getRole(jwttoken);
        if(role.equals("ROLE_STUDENT") || role.equals("ROLE_ADMIN") || role.equals("ROLE_EMPLOYEE")) {
            List<TakenCourse> l = repository.findBystudentId(studentId);
            ArrayList<Integer> resList = new ArrayList<Integer>();
            for(int i=0;i<l.size();i++){
                resList.add(l.get(i).getCourseId());
            }
            return resList.toString();
        }
        return "not authenticated";
    }

    @GetMapping(value = "/getcoursememberlist")
    public String getCourseMembersList(String jwttoken, int courseId){
        String role = getRole(jwttoken);
        if(role.equals("ROLE_PROFESSOR") || role.equals("ROLE_ADMIN") || role.equals("ROLE_EMPLOYEE")) {
            List<TakenCourse> l = repository.findBycourseId(courseId);
            ArrayList<Integer> resList = new ArrayList<Integer>();
            for(int i=0;i<l.size();i++){
                resList.add(l.get(i).getStudentId());
            }
            return resList.toString();
        }
        return "not authenticated";
    }


}
