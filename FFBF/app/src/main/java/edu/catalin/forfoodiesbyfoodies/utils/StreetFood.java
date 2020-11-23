package edu.catalin.forfoodiesbyfoodies.utils;

public class StreetFood {

    private String name;
    private String location;
    private String kind;
    private String description;
    private String review;
    private String imageId;

    public StreetFood(String name, String location, String kind, String description, String review, String imageId) {
        this.name = name;
        this.location = location;
        this.kind = kind;
        this.description = description;
        this.review = review;
        this.imageId = imageId;
    }

    public StreetFood() {
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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
