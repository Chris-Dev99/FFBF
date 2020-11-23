package edu.catalin.forfoodiesbyfoodies.utils;

public class Restaurant {

    private String name;
    private String location;
    private String timeTable;
    private String reviews;
    private String imageId;

    public Restaurant(String name, String location, String timeTable, String reviews, String imageId) {
        this.name = name;
        this.location = location;
        this.timeTable = timeTable;
        this.reviews = reviews;
        this.imageId = imageId;
    }

    public Restaurant() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(String timeTable) {
        this.timeTable = timeTable;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
