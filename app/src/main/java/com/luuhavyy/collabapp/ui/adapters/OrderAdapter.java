package com.luuhavyy.collabapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.Order;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.data.repository.ProductRepository;
import com.luuhavyy.collabapp.ui.activities.OrderDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orders = new ArrayList<>();
    private final ProductRepository productRepository = new ProductRepository();


    public void setOrders(List<Order> newOrders) {
        orders = newOrders != null ? newOrders : new ArrayList<>();
        notifyDataSetChanged();
    }

    public OrderAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        // Status text & color như cũ...
        holder.tvStatus.setText(order.getStatus());
        int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.black);
        if ("Pending".equalsIgnoreCase(order.getStatus())) color = ContextCompat.getColor(holder.itemView.getContext(), R.color.bright_red);
        else if ("Shipping".equalsIgnoreCase(order.getStatus())) color = ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_primary);
        else if ("Delivered".equalsIgnoreCase(order.getStatus())) color = ContextCompat.getColor(holder.itemView.getContext(), R.color.deep_green);
        holder.tvStatus.setTextColor(color);

        // Lấy info product đầu tiên (giả sử mỗi order có nhiều sản phẩm)
        if (order.getProducts() != null && !order.getProducts().isEmpty()) {
            String productId = order.getProducts().get(0).getProductid();

            // Load thông tin sản phẩm từ Firebase bằng ValueEventListener
            productRepository.fetchProductById(productId, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        holder.tvProductName.setText(product.getName());
                        holder.imgProduct.setImageBitmap(
                                com.luuhavyy.collabapp.utils.ImageUtil.decodeBase64ToBitmap(product.getImage())
                        );
                    } else {
                        holder.tvProductName.setText(productId);
                        holder.imgProduct.setImageResource(R.drawable.sample_product);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    holder.tvProductName.setText(productId);
                    holder.imgProduct.setImageResource(R.drawable.sample_product);
                }
            });
        } else {
            holder.tvProductName.setText("-");
            holder.imgProduct.setImageResource(R.drawable.sample_product);
        }

        holder.tvOrderId.setText("Order ID: #" + order.getOrderid());
        holder.tvOrderDate.setText(order.getOrderdate());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("order_id", order.getOrderid());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvProductName, tvQuantity, tvOrderId, tvOrderDate;
        ImageView imgProduct;
        public OrderViewHolder(@NonNull View v) {
            super(v);
            imgProduct = v.findViewById(R.id.img_product);
            tvStatus = v.findViewById(R.id.tv_status);
            tvProductName = v.findViewById(R.id.tv_product_name);
            tvQuantity = v.findViewById(R.id.tv_quantity);
            tvOrderId = v.findViewById(R.id.tv_order_id);
            tvOrderDate = v.findViewById(R.id.tv_order_date);
        }
    }
}
