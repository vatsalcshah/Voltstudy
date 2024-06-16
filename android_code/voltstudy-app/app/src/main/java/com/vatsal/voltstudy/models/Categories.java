package com.vatsal.voltstudy.models;


/** Model for Course Categories */

public class Categories {

    private String category_name;

    public Categories(String category_name) {

        this.category_name = category_name;

    }


    public Categories(){}


    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
