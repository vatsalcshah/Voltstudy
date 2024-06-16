package com.vatsal.voltstudy.models;

/** Model for Courses */

@SuppressWarnings("FieldCanBeLocal")
public class Course {

    private String author;
    private String course_code;
    private String desc;
    private String language;
    private String name;
    private String category;
    private String sub_category;
    private String imageUrl;
    private String course_type;
    private String author_img_url;
    private String author_id;
    private Double course_price;
    private String promocode;

    public Course(String author, String author_id, String author_img_url, String desc, String language, String name, String category,String sub_category, String imageUrl, String course_type, Double course_price) {
        this.author = author;
        this.author_id = author_id;
        this.author_img_url = author_img_url;
        this.desc = desc;
        this.language = language;
        this.name = name;
        this.category = category;
        this.sub_category = sub_category;
        this.imageUrl = imageUrl;
        this.course_type = course_type;
        this.course_price = course_price;
    }

    public Course() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    public String getAuthor_img_url() {
        return author_img_url;
    }

    public void setAuthor_img_url(String author_img_url) {
        this.author_img_url = author_img_url;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public Double getCourse_price() {
        return course_price;
    }

    public void setCourse_price(Double course_price) {
        this.course_price = course_price;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }
}
