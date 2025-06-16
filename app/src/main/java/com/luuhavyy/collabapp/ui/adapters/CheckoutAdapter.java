package com.luuhavyy.collabapp.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.data.model.CartItem;

import java.util.List;

public class CheckoutAdapter extends BaseAdapter {
    private Context context;
    private List<CartItem> cartItems;
    private List<Product> products;

    public CheckoutAdapter(Context context, List<CartItem> cartItems, List<Product> products) {
        this.context = context;
        this.cartItems = cartItems;
        this.products = products;
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_checkout_order_items, parent, false);
            holder = new ViewHolder();
            holder.imgProduct = convertView.findViewById(R.id.imgProduct);
            holder.txtProductName = convertView.findViewById(R.id.txtProductName);
            holder.txtProductQuantity = convertView.findViewById(R.id.txtProductQuantity);
            holder.txtProductPrice = convertView.findViewById(R.id.txtProductPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem cartItem = cartItems.get(position);
        Product product = products.get(position);

        holder.txtProductName.setText(product.getName());
        holder.txtProductQuantity.setText(String.format("x%d", cartItem.getQuantity()));
        holder.txtProductPrice.setText(String.format("%,d VNĐ", product.getPrice() * cartItem.getQuantity()));

        // Load product image
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(product.getImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imgProduct.setImageBitmap(decodedByte);
            } catch (Exception e) {
                holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView imgProduct;
        TextView txtProductName;
        TextView txtProductQuantity;
        TextView txtProductPrice;
    }
}