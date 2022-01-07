
package com.example.nfc.models;

import com.example.nfc.models.StudentTime;

import java.util.LinkedList;

public class Exam {

    private String module;
    private String start_date;
    private String end_date;
    private LinkedList<StudentTime> studentsTime = new LinkedList<>();

    public Exam(String module, String start_date, String end_date) {
        this.module = module;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public LinkedList<StudentTime> getStudentsTime() {
        return studentsTime;
    }

    public void setStudentsTime(LinkedList<StudentTime> studentsTime) {
        this.studentsTime = studentsTime;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "module='" + module + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", studentsTime=" + studentsTime +
                '}';
    }
}

