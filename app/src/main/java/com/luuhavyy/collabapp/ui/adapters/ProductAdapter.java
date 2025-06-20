package com.luuhavyy.collabapp.ui.adapters;

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

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.ui.activities.ProductDetailActivity;
import com.luuhavyy.collabapp.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private Context context;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products != null ? products : new ArrayList<>();
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = products.get(position);

        Bitmap bitmap = ImageUtil.decodeBase64ToBitmap(p.getImage());
        if (bitmap != null) {
            holder.imgProduct.setImageBitmap(bitmap);
        } else {
            holder.imgProduct.setImageResource(R.drawable.sample_product);
        }

        holder.tvName.setText(p.getName());
        holder.tvPrice.setText("VND. " + p.getPrice());
        holder.tvRating.setText("⭐ " + p.getRatings() + "   " + 0 + " Reviews");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", p.getProductid());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, btnCart;
        TextView tvName, tvPrice, tvRating;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            btnCart = itemView.findViewById(R.id.btn_add_cart);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvRating = itemView.findViewById(R.id.tv_rating);
        }
    }
}