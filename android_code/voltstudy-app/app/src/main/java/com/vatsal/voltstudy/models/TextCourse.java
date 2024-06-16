package com.vatsal.voltstudy.models;

/** Model for PDF Textual Courses */

public class TextCourse {

    private String textcontent;
    private String name;
    private Double number;
    private String textCourseID;

    public TextCourse(String textcontent, String name, Double number) {
        this.textcontent = textcontent;
        this.name = name;
        this.number = number;
    }

    public TextCourse() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }

    public String getTextcontent() {
        return textcontent;
    }

    public void setTextcontent(String textcontent) {
        this.textcontent = textcontent;
    }

    public String getTextCourseID() {
        return textCourseID;
    }

    public void setTextCourseID(String textCourseID) {
        this.textCourseID = textCourseID;
    }
}