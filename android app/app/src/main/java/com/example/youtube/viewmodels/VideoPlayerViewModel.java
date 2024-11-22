package com.example.youtube.viewmodels;


import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.youtube.api.VideoApi;
import com.example.youtube.entities.Comment;
import com.example.youtube.entities.User;
import com.example.youtube.entities.Video;
import com.example.youtube.repositories.CommentRepository;
import com.example.youtube.repositories.MediaRepository;
import com.example.youtube.repositories.VideoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VideoPlayerViewModel extends AndroidViewModel {
    private final VideoRepository videoRepository;
    private final MediaRepository mediaRepository;
    private final CommentRepository commentRepository;
    private final MutableLiveData<Bitmap> UserProfilePic = new MutableLiveData<>();
    private final MutableLiveData<Video> currentVideo = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<User> currentCreator = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLiked = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isDisliked = new MutableLiveData<>(false);
    private final MutableLiveData<List<Video>> recommendedVideosLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isEditVideoVisible = new MutableLiveData<>(false);
    private MutableLiveData<List<Comment>> commentList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Comment> addedComment;

    public VideoPlayerViewModel(Application application) {
        super(application);
        videoRepository = new VideoRepository(application);
        mediaRepository = new MediaRepository(application);
        commentRepository = new CommentRepository(application);
        addedComment = commentRepository.getAddedComment();
    }

    public LiveData<List<Video>> getVideos(){
        return videoRepository.getAllVideosLive();
    }

    public LiveData<byte[]> getVideoLive(){return mediaRepository.getVideoLive();}

    public MediaRepository getMediaRepository() {
        return mediaRepository;
    }

    public LiveData<List<Video>> getRecommendedVideos() {
        return recommendedVideosLiveData;
    }

    public void fetchRecommendedVideos(String currentVideoId,String userId) {
        videoRepository.getRecommendations(currentVideoId,userId, new VideoApi.ApiCallback<String>() {
            @Override
            public void onSuccess(String recommendations) {
                List<String> videoIds = parseVideoIds(recommendations);
                List<Video> recommendedVideos = getVideosByIds(videoIds);
                recommendedVideosLiveData.setValue(recommendedVideos);
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private List<String> parseVideoIds(String recommendations) {
        String[] videoIdsArray = recommendations.split(",");
        return new ArrayList<>(List.of(videoIdsArray));
    }
    public void loadVideo(String videoId) {
        Video loadedVideo = videoRepository.getVideoById(videoId);
        currentVideo.setValue(loadedVideo);
        if (loadedVideo != null) {
            currentCreator.setValue(loadedVideo.getUserDetails());
            commentList = commentRepository.getCommentByVideoId(
                    Objects.requireNonNull(currentVideo.getValue()).get_id());
            UserProfilePic.setValue(mediaRepository.getImage(
                    Objects.requireNonNull(currentVideo.getValue().getUserDetails().getIcon())));
        }
    }

    public void loadUser(User user) {
        currentUser.setValue(user);
        if (user != null && currentVideo.getValue() != null) {
            isLiked.setValue(currentVideo.getValue().isLiked(user.get_id()));
            isDisliked.setValue(currentVideo.getValue().isDisLiked(user.get_id()));
            if ((Objects.requireNonNull(getCurrentUser().getValue()).get_id().equals(
                    Objects.requireNonNull(getCurrentVideo().getValue()).getUserDetails().get_id()))){
                isEditVideoVisible.setValue(true);
            }
        }
    }

    public void initCommentsMedia(List<Comment> comments){
        mediaRepository.initCommentMedia(comments);
    }

    public void incrementViews(String userId) {
        Video video = currentVideo.getValue();
        if (video != null) {
            video.setViews(video.getViews() + 1);
            fetchRecommendedVideos(video.get_id(),userId);
            currentVideo.setValue(video);
        }
    }

    public void toggleLike() {
        User user = currentUser.getValue();
        Video video = currentVideo.getValue();
        if (user != null && video != null) {
            if (Boolean.FALSE.equals(isLiked.getValue())) {
                video.addToLiked(user.get_id());
                video.removeFromDisliked(user.get_id());
                isLiked.setValue(true);
                isDisliked.setValue(false);
            } else {
                video.removeFromLiked(user.get_id());
                isLiked.setValue(false);
            }
            videoRepository.updateVideo(video);
        }
    }

    public void toggleDislike() {
        User user = currentUser.getValue();
        Video video = currentVideo.getValue();
        if (user != null && video != null) {
            if (Boolean.FALSE.equals(isDisliked.getValue())) {
                video.addToDisliked(user.get_id());
                video.removeFromLiked(user.get_id());
                isDisliked.setValue(true);
                isLiked.setValue(false);
            } else {
                video.removeFromDisliked(user.get_id());
                isDisliked.setValue(false);
            }
            videoRepository.updateVideo(video);
        }
    }

    public void addComment(String commentText) {
        User user = currentUser.getValue();
        Video video = currentVideo.getValue();
        if (user != null && video != null) {
            Comment newComment = new Comment(commentText, user, video.get_id());
            commentRepository.addComment(newComment, user);
        }
    }

    public void removeComment(int position) {
        Video video = currentVideo.getValue();
        if (video != null) {
            List<Comment> updatedComments = commentList.getValue();
            if (updatedComments != null && position < updatedComments.size()) {
                commentRepository.deleteComment(updatedComments.get(position));
                updatedComments.remove(position);
                commentList.setValue(updatedComments);
            }
        }
    }

    public void editComment(int position, String editedCommentText) {
        Video video = currentVideo.getValue();
        if (video != null) {
            List<Comment> updatedComments = commentList.getValue();
            if (updatedComments != null && position < updatedComments.size()) {
                Comment editedComment = updatedComments.get(position);
                editedComment.setCommentMessage(editedCommentText);
                commentRepository.updateComment(editedComment);
                commentList.setValue(updatedComments);
            }
        }
    }

    public void updateVideoDetails(String newName, Uri newThumbnailUri, Uri newVideoUri) {
        Video video = currentVideo.getValue();
        if (video != null) {
            if (!newName.isEmpty()) {
                video.setTitle(newName);
            }

            if (newThumbnailUri != null || newVideoUri != null) {
                videoRepository.uploadVideoAndThumbnail(newVideoUri, newThumbnailUri, new VideoApi.ApiCallback<Map<String, String>>() {
                    @Override
                    public void onSuccess(Map<String, String> urls) {
                        if (urls.containsKey("thumbnailUrl")) {
                            video.setThumbnail(urls.get("thumbnailUrl"));
                        }
                        if (urls.containsKey("videoUrl")) {
                            video.setVideo_src(Objects.requireNonNull(urls.get("videoUrl")));
                        }

                        videoRepository.updateVideo(video);
                        currentVideo.setValue(video);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("API", "Failed to upload video or thumbnail: " + error);
                    }
                });
            } else {
                videoRepository.updateVideo(video);
                currentVideo.setValue(video);
            }
        }
    }

    public void deleteVideo() {
        Video video = currentVideo.getValue();
        if (video != null) {
            videoRepository.deleteVideo(video);
        }
    }

    public void reloadVideos(){
        videoRepository.reloadVideos();
    }

    public LiveData<Video> getCurrentVideo() { return currentVideo; }
    public LiveData<User> getCurrentUser() { return currentUser; }
    public LiveData<User> getCurrentCreator() { return currentCreator; }
    public LiveData<Boolean> isLiked() { return isLiked; }
    public LiveData<Boolean> isDisliked() { return isDisliked; }



    private void updateRecommendedVideos(List<Video> recommendedVideos) {
        recommendedVideosLiveData.setValue(recommendedVideos);
    }
    public LiveData<Boolean> isEditVideoVisible() { return isEditVideoVisible; }
    public MutableLiveData<List<Comment>> getCommentList() { return commentList; }
    public MutableLiveData<Bitmap> getUserProfilePic() {return UserProfilePic;}
    public MutableLiveData<Comment> getAddedComment() {return addedComment;}

    public List<Video> getVideosByIds(List<String> videoIds) {
        List<Video> videos = new ArrayList<>();
        for (String id : videoIds) {
            Video video = videoRepository.getVideoById(id);
            if (video != null) {
                videos.add(video);
            }
        }
        return videos;
    }
}