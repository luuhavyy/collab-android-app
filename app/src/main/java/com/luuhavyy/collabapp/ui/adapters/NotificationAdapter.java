package com.luuhavyy.collabapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.Notification;
import com.luuhavyy.collabapp.data.model.OrderNotificationStatus;

public class NotificationAdapter extends ListAdapter<Notification, NotificationAdapter.ViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    private final OnNotificationClickListener listener;

    public NotificationAdapter(OnNotificationClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Notification> DIFF_CALLBACK = new DiffUtil.ItemCallback<Notification>() {
        @Override
        public boolean areItemsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
            return oldItem.getNotificationid().equals(newItem.getNotificationid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
            // Sửa lại nếu Notification có thêm trường cần so sánh
            return oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getMessage().equals(newItem.getMessage())
                    && oldItem.getType().equals(newItem.getType())
                    && oldItem.getRelatedid().equals(newItem.getRelatedid())
                    && oldItem.getTimestamp().equals(newItem.getTimestamp());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = getItem(position);
        holder.bind(notification);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onNotificationClick(notification);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgIcon;
        private final TextView tvTitle;
        private final TextView tvMessage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvMessage = itemView.findViewById(R.id.tv_message);
        }

        public void bind(Notification notification) {
            tvTitle.setText(notification.getTitle());
            tvMessage.setText(notification.getMessage());

            String type = notification.getType();
            if ("Promotion".equalsIgnoreCase(type)) {
                imgIcon.setImageResource(R.drawable.ic_promotion);
            } else if ("Order Status".equalsIgnoreCase(type)) {
                OrderNotificationStatus status = OrderNotificationStatus.fromTitle(notification.getTitle());
                switch (status) {
                    case PENDING:
                        imgIcon.setImageResource(R.drawable.ic_pending);
                        break;
                    case SHIPPING:
                        imgIcon.setImageResource(R.drawable.ic_shipping);
                        break;
                    case DELIVERED:
                        imgIcon.setImageResource(R.drawable.ic_delivered);
                        break;
                    default:
                        imgIcon.setImageResource(R.drawable.ic_pending);
                        break;
                }
            }
        }
    }
}
