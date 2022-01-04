package com.example.nfc;

import java.util.Date;

public class StudentTime extends  Student{
    private Date dateIn;
    private Date dateOut;

    public StudentTime(String id, String firstName, String name, Date dateIn, Date dateOut) {
        super(id, firstName, name);
        this.dateIn = dateIn;
        this.dateOut = dateOut;
    }

    public Date getDateIn() {
        return dateIn;
    }

    public void setDateIn(Date dateIn) {
        this.dateIn = dateIn;
    }

    public Date getDateOut() {
        return dateOut;
    }

    public void setDateOut(Date dateOut) {
        this.dateOut = dateOut;
    }

    @Override
    public String toString() {
        return super.toString()+"   StudentTime{" +
                "dateIn=" + dateIn +
                ", dateOut=" + dateOut +
                '}';
    }
}
