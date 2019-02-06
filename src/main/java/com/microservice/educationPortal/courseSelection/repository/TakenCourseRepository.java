package com.microservice.educationPortal.courseSelection.repository;

import com.microservice.educationPortal.courseSelection.models.TakenCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TakenCourseRepository extends JpaRepository<TakenCourse,Integer> {
//    TakenCourse findByStudentId(int studentId);
//    TakenCourse findBycourseId(int courseId);

    List<TakenCourse> findBystudentId(int studentId);
    List<TakenCourse> findBycourseId(int courseId);
}
