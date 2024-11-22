package com.example.youtube.adapters;

import static com.example.youtube.utils.GeneralUtils.generateRandomString;
import static com.example.youtube.utils.GeneralUtils.removeTrailingNewline;
import static com.example.youtube.utils.GeneralUtils.removeVideo;
import static com.example.youtube.utils.GeneralUtils.timeAgo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.R;
import com.example.youtube.entities.Video;
import com.example.youtube.repositories.MediaRepository;
import com.example.youtube.screens.VideoPlayerActivity;
import com.example.youtube.utils.GeneralUtils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {
    private List<Video> videos;
    private final Video currentVideo;
    private final Activity activity;
    private final Boolean isMain;
    private final MediaRepository repo;
    private final Context context;


    public VideoListAdapter(Video currentVideo, Activity activity, MediaRepository repo, Context context) {
        this.currentVideo = currentVideo;
        this.videos = new ArrayList<>();
        this.activity = activity;
        this.isMain = currentVideo == null;
        this.repo = repo;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video currentVideo = videos.get(position);
        String formatViews = GeneralUtils.getViews(Integer.toString(currentVideo.getViews())) + " views";
        holder.video_name.setText(removeTrailingNewline(currentVideo.getTitle()));
        holder.creator.setText(currentVideo.getUserDetails().getUsername());
        holder.views.setText(formatViews);
        holder.publish_date.setText(timeAgo(currentVideo.getDate()));
        holder.video_length.setText(generateRandomString());

        holder.thumbnail.setImageBitmap(repo.getImage(currentVideo.getThumbnail()));
        Bitmap b = repo.getImage(currentVideo.getUserDetails().getIcon());
        holder.creator_pic.setImageBitmap(b);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("video_item", currentVideo.get_id());
            context.startActivity(intent);
            if (!isMain){
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setVideos(List<Video> videos) {
        this.videos.clear();
        this.videos.addAll(videos);
        this.videos = removeVideo(this.videos,currentVideo);
        notifyDataSetChanged();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView video_name;
        TextView creator;
        TextView views;
        TextView publish_date;
        ImageView thumbnail;
        TextView video_length;
        ShapeableImageView creator_pic;

        VideoViewHolder(View itemView) {
            super(itemView);
            video_name = itemView.findViewById(R.id.video_name);
            creator = itemView.findViewById(R.id.creator);
            views = itemView.findViewById(R.id.views);
            publish_date = itemView.findViewById(R.id.publish_date);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            video_length = itemView.findViewById(R.id.video_length);
            creator_pic = itemView.findViewById(R.id.creator_pic);
        }
    }
}