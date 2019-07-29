package com.fnc.receiving.android.model;

public class Notification {

    private String notifiationId;
    private String title;
    private String message;
    private String notificationDate;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotifiationId() {
        return notifiationId;
    }

    public void setNotifiationId(String notifiationId) {
        this.notifiationId = notifiationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }
}

