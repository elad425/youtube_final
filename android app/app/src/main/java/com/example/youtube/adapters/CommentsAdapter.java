package com.example.youtube.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.entities.Comment;
import com.example.youtube.R;
import com.example.youtube.repositories.MediaRepository;
import com.example.youtube.screens.VideoPlayerActivity;
import com.example.youtube.utils.GeneralUtils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private final VideoPlayerActivity videoPlayerActivity;
    private final String userId;
    private final MediaRepository mediaRepository;

    public CommentsAdapter(List<Comment> commentList, VideoPlayerActivity videoPlayerActivity,
                           String userId, MediaRepository mediaRepository) {
        this.commentList = commentList;
        this.videoPlayerActivity = videoPlayerActivity;
        this.userId = userId;
        this.mediaRepository = mediaRepository;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment currentComment = commentList.get(position);
        holder.tvCommentUser.setText(currentComment.getUser_id().getUsername());
        holder.tvCommentText.setText(currentComment.getCommentMessage());
        holder.tvCommentDate.setText(GeneralUtils.timeAgo(currentComment.getDate()));
        holder.user_pic.setImageBitmap(mediaRepository.getImage(currentComment.getUser_id().getIcon()));

        if (!currentComment.getUser_id().get_id().equals(userId)) {
            holder.tvEditComment.setVisibility(View.GONE);
        } else {
            holder.tvEditComment.setVisibility(View.VISIBLE);
            holder.tvEditComment.setOnClickListener(v -> showCommentOptions(position, v));
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void setComments(List<Comment> newComments) {
        this.commentList = newComments;
        notifyDataSetChanged();
    }

    private void showCommentOptions(int position, View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.comment_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit_comment) {
                videoPlayerActivity.editComment(position);
                return true;
            } else if (item.getItemId() == R.id.action_delete_comment) {
                videoPlayerActivity.removeComment(position);
                return true;
            }
            return false;
        });
        popup.show();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentUser;
        TextView tvCommentText;
        TextView tvCommentDate;
        ImageButton tvEditComment;
        ShapeableImageView user_pic;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentUser = itemView.findViewById(R.id.tv_comment_user);
            tvCommentText = itemView.findViewById(R.id.tv_comment_text);
            tvCommentDate = itemView.findViewById(R.id.tv_comment_date);
            tvEditComment = itemView.findViewById(R.id.tv_comment_options);
            user_pic = itemView.findViewById(R.id.user_pic);
        }
    }
}