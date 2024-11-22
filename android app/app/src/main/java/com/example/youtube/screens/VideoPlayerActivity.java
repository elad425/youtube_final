package com.example.youtube.screens;

import static com.example.youtube.utils.GeneralUtils.removeTrailingNewline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.entities.Comment;
import com.example.youtube.MainActivity;
import com.example.youtube.R;
import com.example.youtube.entities.User;
import com.example.youtube.entities.Video;
import com.example.youtube.data.UserSession;
import com.example.youtube.adapters.CommentsAdapter;
import com.example.youtube.adapters.VideoListAdapter;
import com.example.youtube.utils.GeneralUtils;
import com.example.youtube.viewmodels.VideoPlayerViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_THUMBNAIL = 100;
    private static final int PICK_VIDEO_FILE = 101;
    private boolean areCommentsVisible = false;
    private RecyclerView rvComments;
    private TextView tvComments;
    private ImageButton ivToggleComments;
    private ImageButton AddCommentBtn;
    private CommentsAdapter commentsAdapter;
    private Uri newThumbnailUri;
    private Uri newVideoUri;
    private EditText inputVideoName;
    private VideoPlayerViewModel viewModel;
    private Boolean playing = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));

        viewModel = new ViewModelProvider(this).get(VideoPlayerViewModel.class);
        viewModel.loadVideo(getIntent().getStringExtra("video_item"));
        viewModel.loadUser(UserSession.getInstance().getUser());
        User user = UserSession.getInstance().getUser();
        String id ="0";
        if (user!=null){
             id = user.get_id();
        }

        viewModel.incrementViews(id);
        viewModel.getMediaRepository().downloadVideo(
                Objects.requireNonNull(viewModel.getCurrentVideo().getValue()).getVideo_src());

        observeData();
    }

    private void observeData() {
        AtomicBoolean videosComments = new AtomicBoolean(false);
        AtomicBoolean profilePic = new AtomicBoolean(false);
        AtomicBoolean videoFile = new AtomicBoolean(false);
        ImageButton btnEdit = findViewById(R.id.edit_video);

        viewModel.getAddedComment().observe(this, comment -> {
            if (comment != null){
                List<Comment> updatedComments = viewModel.getCommentList().getValue();
                assert updatedComments != null;
                updatedComments.add(comment);
                viewModel.getCommentList().setValue(updatedComments);
            }
        });

        viewModel.getCommentList().observe(this, comments -> {
            if (comments != null) {
                videosComments.set(true);
                checkDataAndSetupUI(profilePic.get(),videosComments.get(),videoFile.get());
            }
        });

        viewModel.getUserProfilePic().observe(this, bitmap -> {
            if (bitmap != null) {
                profilePic.set(true);
                checkDataAndSetupUI(profilePic.get(),videosComments.get(),videoFile.get());
            }
        });

        viewModel.getVideoLive().observe(this, video -> {
            if (video != null) {
                videoFile.set(true);
                checkDataAndSetupUI(profilePic.get(),videosComments.get(),videoFile.get());
            }
        });

        viewModel.isEditVideoVisible().observe(this,isVisible ->{
            if(isVisible){btnEdit.setVisibility(View.VISIBLE);}
        });
    }

    private void checkDataAndSetupUI(boolean videosComments, boolean profilePic, boolean videoFile) {
        if (videosComments && profilePic && videoFile) {
            hideLoadingIndicator();
            setupUI();
        }else {
            showLoadingIndicator();
        }
    }

    private void showLoadingIndicator() {
        ProgressBar loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingIndicator.setVisibility(View.VISIBLE);
        ConstraintLayout constraintLayout = findViewById(R.id.page);
        constraintLayout.setVisibility(View.GONE);
    }

    private void hideLoadingIndicator() {
        ProgressBar loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingIndicator.setVisibility(View.GONE);
        ConstraintLayout constraintLayout = findViewById(R.id.page);
        constraintLayout.setVisibility(View.VISIBLE);
    }

    public void setupUI(){
        initializeUI();
        setupVideoView();
        handleCommentsSection();
        handleActionButtons();
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getCurrentVideo().observe(this, this::updateVideoInfo);
        viewModel.getCurrentCreator().observe(this, this::updateCreatorInfo);
        viewModel.isLiked().observe(this, this::updateLikeButton);
        viewModel.isDisliked().observe(this, this::updateDislikeButton);
        viewModel.getCommentList().observe(this, this::updateComments);
        viewModel.getRecommendedVideos().observe(this, this::updateRecyclerView);
    }

    private void initializeUI() {
        RecyclerView lstVideos = findViewById(R.id.lstVideos);
        VideoListAdapter videoListAdapter = new VideoListAdapter(
                viewModel.getCurrentVideo().getValue(),this,viewModel.getMediaRepository(), this);
        lstVideos.setAdapter(videoListAdapter);
        lstVideos.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnBack = findViewById(R.id.tv_video_back);
        btnBack.setOnClickListener(v -> finish());
    }
    private void processRecommendedVideoIds(String ids) {
        String[] videoIdsArray = ids.split(",");

        List<String> videoIds = new ArrayList<>(Arrays.asList(videoIdsArray));

        List<Video> recommendedVideos = viewModel.getVideosByIds(videoIds);

        updateRecyclerView(recommendedVideos);
    }

    private void updateRecyclerView(List<Video> recommendedVideos) {
        RecyclerView lstVideos = findViewById(R.id.lstVideos);
        VideoListAdapter videoListAdapter = new VideoListAdapter(
                viewModel.getCurrentVideo().getValue(), this, viewModel.getMediaRepository(), this);
        lstVideos.setAdapter(videoListAdapter);
        lstVideos.setLayoutManager(new LinearLayoutManager(this));
        videoListAdapter.setVideos(recommendedVideos);  // Set the recommended videos
    }

    private void setupVideoView() {
        final VideoView videoView = findViewById(R.id.tv_video_view);
        viewModel.getCurrentVideo().observe(this, currentVideo -> {
            if (currentVideo != null && playing) {
                playing = false;
                File videoFile;
                try {
                    videoFile = viewModel.getMediaRepository().byteArrayToFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                videoView.setVideoPath(videoFile.getAbsolutePath());
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.start();
            }
        });
    }

    private void updateVideoInfo(Video currentVideo) {
        if (currentVideo != null) {
            TextView tvVideoName = findViewById(R.id.tv_video_name);
            TextView tvVideoViews = findViewById(R.id.tv_video_views);
            TextView tvPublishDate = findViewById(R.id.tv_publish_date);

            String formatViews = GeneralUtils.getViews(Integer.toString(currentVideo.getViews())) + " views";
            tvVideoName.setText(removeTrailingNewline(currentVideo.getTitle()));
            tvVideoViews.setText(formatViews);
            tvPublishDate.setText(GeneralUtils.timeAgo(currentVideo.getDate()));
        }
    }

    private void updateCreatorInfo(User currentCreator) {
        if (currentCreator != null) {
            TextView tvCreator = findViewById(R.id.tv_creator);
            ShapeableImageView creatorPic = findViewById(R.id.creator_pic);
            creatorPic.setImageBitmap(viewModel.getUserProfilePic().getValue());
            tvCreator.setText(currentCreator.getUsername());
        }
    }

    @SuppressLint("DefaultLocale")
    private void handleCommentsSection() {
        tvComments = findViewById(R.id.tv_comments);
        rvComments = findViewById(R.id.rv_comments);
        ivToggleComments = findViewById(R.id.iv_toggle_comments);
        AddCommentBtn = findViewById(R.id.iv_add_comments);

        String userId;
        if (viewModel.getCurrentUser().getValue() == null){
            userId = null;
        } else {
            userId = viewModel.getCurrentUser().getValue().get_id();
        }

        ivToggleComments.setOnClickListener(v -> toggleComments());
        tvComments.setOnClickListener(v -> toggleComments());

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsAdapter(new ArrayList<>(), this,
                userId,viewModel.getMediaRepository());
        rvComments.setAdapter(commentsAdapter);

        viewModel.getCommentList().observe(this, comments -> {
            commentsAdapter.setComments(comments);
            viewModel.initCommentsMedia(comments);
            tvComments.setText(String.format("Comments (%d)", comments.size()));
        });

        AddCommentBtn.setOnClickListener(v -> {
            if (viewModel.getCurrentUser().getValue() == null) {
                Toast.makeText(this, "Please login to add a comment", Toast.LENGTH_SHORT).show();
                goToLogIn();
            } else {
                showAddCommentDialog();
            }
        });
    }

    private void updateComments(List<Comment> comments) {
        commentsAdapter.setComments(comments);
        tvComments.setText(String.format(Locale.US, "Comments (%d)", comments.size()));
    }

    private void toggleComments() {
        if (areCommentsVisible) {
            rvComments.setVisibility(View.GONE);
            AddCommentBtn.setVisibility(View.GONE);
            ivToggleComments.setImageResource(R.drawable.ic_arrow_down);
        } else {
            rvComments.setVisibility(View.VISIBLE);
            AddCommentBtn.setVisibility(View.VISIBLE);
            ivToggleComments.setImageResource(R.drawable.ic_arrow_up);
        }
        areCommentsVisible = !areCommentsVisible;
    }

    private void handleActionButtons() {
        ImageButton btnShare = findViewById(R.id.tv_btn_share);
        ImageButton btnLike = findViewById(R.id.tv_btn_like);
        ImageButton btnDislike = findViewById(R.id.tv_btn_dislike);
        ImageButton btnEdit = findViewById(R.id.edit_video);

        btnShare.setOnClickListener(v -> shareVideo());
        btnLike.setOnClickListener(v -> handleLikeAction());
        btnDislike.setOnClickListener(v -> handleDislikeAction());
        btnEdit.setOnClickListener(this::showEditVideoOptions);
    }

    private void updateLikeButton(boolean isLiked) {
        ImageButton btnLike = findViewById(R.id.tv_btn_like);
        btnLike.setImageResource(isLiked ? R.drawable.ic_like_fill : R.drawable.ic_like);
    }

    private void updateDislikeButton(boolean isDisliked) {
        ImageButton btnDislike = findViewById(R.id.tv_btn_dislike);
        btnDislike.setImageResource(isDisliked ? R.drawable.ic_dislike_fill : R.drawable.ic_dislike);
    }

    private void showAddCommentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a comment");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String commentText = input.getText().toString().trim();
            if (!commentText.isEmpty()) {
                viewModel.addComment(commentText);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void removeComment(int position) {
        viewModel.removeComment(position);
    }

    public void editComment(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit comment");

        final EditText input = new EditText(this);
        input.setText(Objects.requireNonNull(viewModel.getCommentList().getValue()).get(position).getCommentMessage());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String editedCommentText = input.getText().toString().trim();
            if (!editedCommentText.isEmpty()) {
                viewModel.editComment(position, editedCommentText);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showEditVideoOptions(View v) {

        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.video_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete_video) {
                if (viewModel.getCurrentUser().getValue() == null) {
                    Toast.makeText(this, "Please login to delete a video", Toast.LENGTH_SHORT).show();
                    goToLogIn();
                } else {
                    viewModel.deleteVideo();
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                    Toast.makeText(this, "Video deleted successfully", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (item.getItemId() == R.id.action_edit_video) {
                showEditVideoDialog();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showEditVideoDialog() {
        if (viewModel.getCurrentUser().getValue() == null) {
            Toast.makeText(this, "Please login to edit a video", Toast.LENGTH_SHORT).show();
            goToLogIn();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Video");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_video, null);
        inputVideoName = view.findViewById(R.id.input_video_name);
        Button btnSelectThumbnail = view.findViewById(R.id.btn_select_thumbnail);
        Button btnSelectVideo = view.findViewById(R.id.btn_select_video);

        inputVideoName.setText(removeTrailingNewline(Objects.requireNonNull(viewModel.getCurrentVideo().getValue()).getTitle()));

        btnSelectThumbnail.setOnClickListener(v -> pickMedia(PICK_VIDEO_THUMBNAIL));
        btnSelectVideo.setOnClickListener(v -> pickMedia(PICK_VIDEO_FILE));

        builder.setView(view);
        builder.setPositiveButton("Save", (dialog, which) -> saveVideoChanges());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog editDialog = builder.create();
        editDialog.show();
    }

    private void pickMedia(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(requestCode == PICK_VIDEO_THUMBNAIL ? "image/*" : "video/*");
        startActivityForResult(intent, requestCode);
    }

    private void saveVideoChanges() {
        String newName = inputVideoName.getText().toString().trim();
        viewModel.updateVideoDetails(newName, newThumbnailUri, newVideoUri);
        viewModel.reloadVideos();
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        Toast.makeText(this, "Video edited successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedMediaUri = data.getData();
            if (requestCode == PICK_VIDEO_THUMBNAIL) {
                newThumbnailUri = selectedMediaUri;
                Toast.makeText(this, "Thumbnail selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PICK_VIDEO_FILE) {
                newVideoUri = selectedMediaUri;
                Toast.makeText(this, "Video selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareVideo() {
        Video currentVideo = viewModel.getCurrentVideo().getValue();
        if (currentVideo != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this video");
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentVideo.getVideo_src());
            startActivity(Intent.createChooser(shareIntent, "Share Video"));
        }
    }

    private void handleLikeAction() {
        if (viewModel.getCurrentUser().getValue() == null) {
            Toast.makeText(this, "Please login to like the video", Toast.LENGTH_SHORT).show();
            goToLogIn();
            return;
        }
        viewModel.toggleLike();
    }

    private void handleDislikeAction() {
        if (viewModel.getCurrentUser().getValue() == null) {
            Toast.makeText(this, "Please login to dislike the video", Toast.LENGTH_SHORT).show();
            goToLogIn();
            return;
        }
        viewModel.toggleDislike();
    }

    private void goToLogIn() {
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }
}
