package com.example.nfc;

import java.util.Date;

public class StudentTime extends  Student{

    private String dateIn;
    private String dateOut;

    public StudentTime(String id, String firstName, String name, String dateIn, String dateOut) {
        super(id, firstName, name);
        this.dateIn = dateIn;
        this.dateOut = dateOut;
    }

    public String getDateIn() {
        return dateIn;
    }

    public void setDateIn(String dateIn) {
        this.dateIn = dateIn;
    }

    public String getDateOut() {
        return dateOut;
    }

    public void setDateOut(String dateOut) {
        this.dateOut = dateOut;
    }

    @Override
    public String toString() {
        return super.toString()+" StudentTime{" +
                "dateIn='" + dateIn + '\'' +
                ", dateOut='" + dateOut + '\'' +
                '}';
    }
}
