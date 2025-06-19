package com.luuhavyy.collabapp.ui.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.Review;
import com.luuhavyy.collabapp.data.model.ReviewWithUser;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.utils.DateTimeUtil;
import com.luuhavyy.collabapp.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<ReviewWithUser> reviewWithUserList = new ArrayList<>();

    public void setData(List<ReviewWithUser> reviewWithUserList) {
        this.reviewWithUserList = reviewWithUserList != null ? reviewWithUserList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewWithUser rw = reviewWithUserList.get(position);
        Review review = rw.review;
        User user = rw.user;

        holder.tvUser.setText(user != null ? user.getName() : review.getUserid());
        holder.tvReviewDate.setText(DateTimeUtil.formatTimeAgo(review.getTimestamp()));
        holder.ratingBar.setRating(review.getRating());
        holder.tvReviewContent.setText(review.getReview());

        // ---- Thêm đoạn này để show avatar từ base64 ----
        if (holder.imgAvatar != null && user != null && user.getProfilepicture() != null && !user.getProfilepicture().isEmpty()) {
            Bitmap bitmap = ImageUtil.decodeBase64ToBitmap(user.getProfilepicture());
            if (bitmap != null) {
                holder.imgAvatar.setImageBitmap(bitmap);
            } else {
                holder.imgAvatar.setImageResource(R.drawable.avatar_placeholder);
            }
        } else {
            holder.imgAvatar.setImageResource(R.drawable.avatar_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return reviewWithUserList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvUser, tvReviewDate, tvReviewContent;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            tvUser = itemView.findViewById(R.id.tv_user);
            tvReviewDate = itemView.findViewById(R.id.tv_review_date);
            tvReviewContent = itemView.findViewById(R.id.tv_review_content);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}
