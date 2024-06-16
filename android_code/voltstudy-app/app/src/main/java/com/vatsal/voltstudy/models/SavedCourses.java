package com.vatsal.voltstudy.models;

/** Model for Saved Courses */

public class SavedCourses {

    private String courseCode;
    private String courseName;
    private String courseAuthor;
    private String courseLanguage;
    private String courseCategory;
    private String courseSubCategory;
    private String courseImageUrl;
    private String saveCourseKey;
    private String courseType;
    private String courseDesc;
    private Double coursePrice;

    public SavedCourses(String courseCode, String courseName, String courseAuthor, String courseLanguage, String courseCategory, String courseSubCategory, String courseImageUrl, String courseType, Double coursePrice, String courseDesc) {

        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseAuthor = courseAuthor;
        this.courseLanguage = courseLanguage;
        this.courseCategory = courseCategory;
        this.courseSubCategory = courseSubCategory;
        this.courseType = courseType;
        this.courseImageUrl = courseImageUrl;
        this.coursePrice = coursePrice;

    }


    public SavedCourses(){}

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseAuthor() {
        return courseAuthor;
    }

    public void setCourseAuthor(String courseAuthor) {
        this.courseAuthor = courseAuthor;
    }

    public String getCourseLanguage() {
        return courseLanguage;
    }

    public void setCourseLanguage(String courseLanguage) {
        this.courseLanguage = courseLanguage;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public String getCourseSubCategory() {
        return courseSubCategory;
    }

    public void setCourseSubCategory(String courseSubCategory) {
        this.courseSubCategory = courseSubCategory;
    }

    public String getCourseImageUrl() {
        return courseImageUrl;
    }

    public void setCourseImageUrl(String courseImageUrl) {
        this.courseImageUrl = courseImageUrl;
    }

    public String getSaveCourseKey() {
        return saveCourseKey;
    }

    public void setSaveCourseKey(String saveCourseKey) {
        this.saveCourseKey = saveCourseKey;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public Double getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(Double coursePrice) {
        this.coursePrice = coursePrice;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    public void setCourseDesc(String courseDesc) {
        this.courseDesc = courseDesc;
    }
}