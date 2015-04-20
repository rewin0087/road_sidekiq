package com.road_sidekiq.android.roadsidekiq.models;

/**
 * Created by paul.aragones on 4/18/15.
 */
public class Feedback {
    private String message;
    private String userId;
    private String userName;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Feedback{");
        sb.append("message='").append(message).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", userName='").append(userName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
