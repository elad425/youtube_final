package com.example.youtube.entities;

public class Comment {
    private String _id;
    private String commentMessage;
    private String date;
    private User user_id;
    private String video_id;

    public Comment(String commentMessage, User user_id, String video_id) {
        this.commentMessage = commentMessage;
        this.user_id = user_id;
        this.video_id = video_id;
    }

    public Comment(String _id, String commentMessage, String date , User user_id, String video_id){
        this.commentMessage = commentMessage;
        this.user_id = user_id;
        this.video_id = video_id;
        this._id = _id;
        this.date = date;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCommentMessage() {
        return commentMessage;
    }

    public void setCommentMessage(String commentMessage) {
        this.commentMessage = commentMessage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }
}
