package com.microservice.courseTaking.courseSelection.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="takenCourses")
public class TakenCourse {

    @Id
    int id;


    int studentId;

    public TakenCourse(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    int courseId;

}
