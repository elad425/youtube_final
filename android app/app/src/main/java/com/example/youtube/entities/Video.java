package com.example.youtube.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.youtube.utils.userConverters;
import com.example.youtube.utils.videoConverters;

import java.util.List;
@Entity(tableName = "video")
public class Video {
    private String _id;
    private String title;
    private String description;
    private int views;
    private String date;
    @PrimaryKey
    @NonNull
    private String video_src;
    private String thumbnail;
    @TypeConverters({videoConverters.class})
    private User userDetails;
    @TypeConverters({userConverters.class})
    private List<String> comments;
    @TypeConverters({userConverters.class})
    private List<String> likes;
    @TypeConverters({userConverters.class})
    private List<String> dislikes;

    public Video(String title, String description, @NonNull String video_src, String thumbnail, User userDetails) {
        this.title = title;
        this.description = description;
        this.video_src = video_src;
        this.thumbnail = thumbnail;
        this.userDetails = userDetails;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @NonNull
    public String getVideo_src() {
        return video_src;
    }

    public void setVideo_src(@NonNull String video_src) {
        this.video_src = video_src;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public User getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(User userDetails) {
        this.userDetails = userDetails;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<String> dislikes) {
        this.dislikes = dislikes;
    }

    public void addToLiked(String _id) {
        for (String s : likes) {
            if (s.equals(_id)) {
                return;
            }
        }
        this.likes.add(_id);
    }

    public void removeFromLiked(String _id) {
        likes.removeIf(s -> s.equals(_id));
    }

    public void addToDisliked(String _id) {
        for (String s : dislikes) {
            if (s.equals(_id)) {
                return;
            }
        }
        this.dislikes.add(_id);
    }

    public void removeFromDisliked(String _id) {
        dislikes.removeIf(s -> s.equals(_id));
    }

    public boolean isLiked(String _id) {
        if(likes != null) {
            for (String s : likes) {
                if (s.equals(_id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDisLiked(String _id) {
        if(dislikes != null) {
            for (String s : dislikes) {
                if (s.equals(_id)) {
                    return true;
                }
            }
        }
        return false;
    }

}
