package com.microservice.educationPortal.courseSelection.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="takenCourses")
public class TakenCourse {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;


    private int studentId;

    public TakenCourse(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public TakenCourse() {
    }

    public int getStudentId() {
        return studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    private int courseId;

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
