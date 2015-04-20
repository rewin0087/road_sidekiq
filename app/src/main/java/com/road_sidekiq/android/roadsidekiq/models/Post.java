package com.road_sidekiq.android.roadsidekiq.models;

import java.util.List;

/**
 * Created by paul.aragones on 4/18/15.
 */
public class Post {
    private String id;
    private String description;
    private String category;
    private String created_at;
    private String location;
    private String positive;
    private String negative;

    public String latitude;
    public String longitude;

    private List<Feedback> feedbacks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Post setLatitude(String latitude) {
        this.latitude = latitude;

        return this;
    }

    public Post setLongitude(String longitude) {
        this.longitude = longitude;

        return this;
    }

    public String getDescription() {
        return description;
    }

    public Post setDescription(String description) {
        this.description = description;

        return this;
    }

    public String getCategory() {
        return category;
    }

    public Post setCategory(String category) {
        this.category = category;

        return this;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Post setCreated_at(String created_at) {
        this.created_at = created_at;

        return this;
    }

    public String getLocation() {
        return location;
    }

    public Post setLocation(String location) {
        this.location = location;

        return this;
    }

    public String getPositive() {
        return positive;
    }

    public Post setPositive(String positive) {
        this.positive = positive;

        return this;
    }

    public String getNegative() {
        return negative;
    }

    public Post setNegative(String negative) {
        this.negative = negative;

        return this;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Post{");
        sb.append("id='").append(id).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", category='").append(category).append('\'');
        sb.append(", created_at='").append(created_at).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
