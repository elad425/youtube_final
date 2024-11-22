package com.example.youtube.entities;

public class Comment2 {
    private final String _id;
    private final String commentMessage;
    private final String date;
    private final String video_id;

    public Comment2(String commentMessage, String _id, String date, String video_id) {
        this.commentMessage = commentMessage;
        this._id = _id;
        this.date = date;
        this.video_id = video_id;
    }

    public String get_id() {
        return _id;
    }

    public String getCommentMessage() {
        return commentMessage;
    }

    public String getDate() {
        return date;
    }

    public Comment commentFormatConvert(Comment2 comment, User user){
        return new Comment(comment.get_id(), comment.getCommentMessage(),comment.getDate(), user, comment.video_id);
    }
}
