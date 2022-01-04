package com.example.nfc;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.LinkedList;

public class Exam {

    private String module;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private LinkedList<StudentTime> studentsTime;

    public Exam(String module, LocalDateTime start_date, LocalDateTime end_date, LinkedList<StudentTime> studentsTime) {
        this.module = module;
        this.start_date = start_date;
        this.end_date = end_date;
        this.studentsTime = studentsTime;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "module='" + module + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", studentsTime=" + studentsTime +
                '}';
    }








}
