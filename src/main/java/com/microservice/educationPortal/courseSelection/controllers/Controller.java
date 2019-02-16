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

    @Deprecated
    private int getUserFK(String jwttoken){
        final String uri = "http://"+ authServiceIp +":"+authServicePort+"/getUserFK";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwttoken);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        Integer res = restTemplate.exchange(uri, HttpMethod.GET,entity,Integer.class).getBody();
        return res;
    }

    private Long getStudentNumber(String jwttoken){
        final String uri = "http://"+ authServiceIp +":"+authServicePort+"/getStudentNumber";
        System.out.println(uri);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwttoken);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        try {
            String res = restTemplate.exchange(uri, HttpMethod.GET,entity,String.class).getBody();
            return Long.parseLong(res);
        }catch (Exception e){
            return null;
        }

    }

    private boolean courseExists(int courseCode){
        final String uri = "http://"+ courseServiceIp +":"+courseServicePort+"/exists?courseCode="+courseCode;
        RestTemplate restTemplate = new RestTemplate();
        String s = restTemplate.getForObject(uri,String.class);
        System.out.println(s);
        if("ok".equals(s)){
            return true;
        }
        return false;

    }


//    private boolean studentExists(int studentNumber){
//        //todo
//        return false;
//    }

    @GetMapping(value = "/takecourse")
    public String takeCourse(String jwttoken, int courseCode){
        String role = getRole(jwttoken);
        if(role.equals("ROLE_STUDENT")) {
            if(courseExists(courseCode)) {
                long studentNumber = getStudentNumber(jwttoken);
                if(repository.findByCourseCodeAndStudentNumber(courseCode,studentNumber) != null){
                    return "course already selected";
                }

                TakenCourse tk = new TakenCourse(studentNumber, courseCode);
                repository.save(tk);
                return "ok";
            }
            else{
                return "courseNotFound";
            }
        }
        return "not authenticated";
    }


    @GetMapping(value = "/takecourseforstudent")
    public String takeCourseForStudent(String jwttoken, int courseCode,int studentNumber){
        String role = getRole(jwttoken);
        if( role.equals("ROLE_ADMIN") || role.equals("ROLE_EMPLOYEE")) {
            if(courseExists(courseCode)) {
                TakenCourse tk = new TakenCourse(studentNumber, courseCode);
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
    public String removeTakenCourse(String jwttoken, int courseCode){
        String role = getRole(jwttoken);
        if(role.equals("ROLE_STUDENT")) {
            long studentNumber = getStudentNumber(jwttoken);
            try {
                repository.deleteByStudentNumberAndCourseCode(studentNumber,courseCode);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return "ok";
        }
        return "not authenticated";
    }

    @GetMapping(value = "/removetakencourseforstudent")
    public String removeTakenCourseForStudent(String jwttoken, long studentNumber, int courseCode){
        String role = getRole(jwttoken);

        if( role.equals("ROLE_ADMIN") || role.equals("ROLE_EMPLOYEE")) {
//            if(studentExists(studentNumber)){
                if(courseExists(courseCode)) {
                    repository.deleteByCourseCode(courseCode);
                    return "ok";
                }

//            }
            return "invalid input";
        }
        return "not authenticated";
    }


    @GetMapping(value = "/getstudentcourseslist")
    public String getStudentCoursesList(String jwttoken,long studentNumber){
        String role = getRole(jwttoken);
        if(role.equals("ROLE_STUDENT") || role.equals("ROLE_ADMIN") || role.equals("ROLE_EMPLOYEE")) {
            List<TakenCourse> l = repository.findByStudentNumber(studentNumber);
            ArrayList<Integer> resList = new ArrayList<Integer>();
            for(int i=0;i<l.size();i++){
                resList.add(l.get(i).getCourseCode());
            }
            return resList.toString();
        }
        return "not authenticated";
    }

    @GetMapping(value = "/getcoursememberlist")
    public String getCourseMembersList(String jwttoken, int courseCode){
        String role = getRole(jwttoken);
        if(role.equals("ROLE_PROFESSOR") || role.equals("ROLE_ADMIN") || role.equals("ROLE_EMPLOYEE")) {
            List<TakenCourse> l = repository.findByCourseCode(courseCode);
            ArrayList<Long> resList = new ArrayList<>();
            for(int i=0;i<l.size();i++){
                resList.add(l.get(i).getStudentNumber());
            }
            return resList.toString();
        }
        return "not authenticated";
    }


}
