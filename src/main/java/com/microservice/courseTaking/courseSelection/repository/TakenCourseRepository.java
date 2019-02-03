package com.microservice.courseTaking.courseSelection.repository;

import com.microservice.courseTaking.courseSelection.models.TakenCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TakenCourseRepository extends JpaRepository<TakenCourse,Integer> {
    TakenCourse findByStudentId(int studentId);
    TakenCourse findBycourseId(int courseId);
}
