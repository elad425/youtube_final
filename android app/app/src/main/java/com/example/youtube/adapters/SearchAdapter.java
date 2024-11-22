package com.example.youtube.adapters;

import static com.example.youtube.utils.GeneralUtils.removeTrailingNewline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.R;
import com.example.youtube.entities.Video;
import com.example.youtube.screens.VideoPlayerActivity;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.VideoViewHolder> {
    private List<Video> filteredVideoList;
    private final Context context;
    private final Activity activity;

    public SearchAdapter(List<Video> filteredVideoList, Context context, Activity activity) {
        this.filteredVideoList = filteredVideoList;
        this.context = context;
        this.activity = activity;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(List<Video> filteredList){
        this.filteredVideoList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = filteredVideoList.get(position);
        holder.searchResultTextView.setText(removeTrailingNewline(video.getTitle()));

        holder.itemView.setOnClickListener(v -> {
            Video clickedVideoItem = filteredVideoList.get(holder.getAdapterPosition());
            Intent i = new Intent(context, VideoPlayerActivity.class);
            i.putExtra("video_item", clickedVideoItem.get_id());
            context.startActivity(i);
            activity.finish();
        });
    }

    @Override
    public int getItemCount() {
        return filteredVideoList.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView searchResultTextView;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            searchResultTextView = itemView.findViewById(R.id.search_result);
        }
    }
}