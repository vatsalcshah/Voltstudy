package com.vatsal.voltstudy.models;

/** Model for Videos */

@SuppressWarnings("FieldCanBeLocal")
public class Video {

    private String download_url;
    private String name;
    private Double number;
    private String videoID;

    public Video(String download_url, String name, Double number) {
        this.download_url = download_url;
        this.name = name;
        this.number = number;
    }

    public Video() {
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
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

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }
}

