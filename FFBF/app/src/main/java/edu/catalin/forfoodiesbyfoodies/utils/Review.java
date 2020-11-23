package edu.catalin.forfoodiesbyfoodies.utils;

public class Review {

    private String review;
    private String restaurantName;

    public Review(String review, String restaurantName) {
        this.review = review;
        this.restaurantName = restaurantName;
    }

    public Review() {
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
