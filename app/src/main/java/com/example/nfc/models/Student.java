package com.example.nfc.models;

public class Student {
    private String id;
    private String firstName;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getFullName(){
        return this.firstName +"  "+this.name ;
    }

    public Student(String id, String firstName, String name) {
        this.id = id;
        this.firstName = firstName;
        this.name = name;
    }
}
