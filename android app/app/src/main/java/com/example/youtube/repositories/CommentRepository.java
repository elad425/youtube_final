package com.example.youtube.repositories;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.youtube.api.CommentApi;
import com.example.youtube.api.VideoApi;
import com.example.youtube.entities.Comment;
import com.example.youtube.entities.Comment2;
import com.example.youtube.entities.User;

import java.util.List;

public class CommentRepository {

    private final MutableLiveData<List<Comment>> comments;
    private final MutableLiveData<Comment> addedComment;
    private final CommentApi commentApi;

    public CommentRepository(Application application) {
        commentApi = new CommentApi(application.getApplicationContext());
        comments = new MutableLiveData<>();
        addedComment = new MutableLiveData<>();
    }

    public MutableLiveData<List<Comment>> getCommentByVideoId(String videoId) {
        commentApi.getCommentsById(videoId, new VideoApi.ApiCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> result) {
                comments.setValue(result);
            }

            @Override
            public void onError(String error) {
            }
        });
        return comments;
    }

    public void addComment(Comment comment, User user){
        commentApi.createComment(comment, new VideoApi.ApiCallback<Comment2>() {
            @Override
            public void onSuccess(Comment2 result) {
                Comment comment1 = result.commentFormatConvert(result,user);
                addedComment.setValue(comment1);
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    public MutableLiveData<Comment> getAddedComment() {return addedComment;}

    public void deleteComment(Comment comment){
        commentApi.deleteComment(comment.get_id());
    }

    public void updateComment(Comment comment){
        commentApi.updateComment(comment);
    }
}
