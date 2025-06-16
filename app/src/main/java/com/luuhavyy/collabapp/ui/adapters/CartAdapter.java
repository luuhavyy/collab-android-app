package com.luuhavyy.collabapp.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.luuhavyy.collabapp.R;

import java.util.List;

import com.luuhavyy.collabapp.data.model.CartItem;
import com.luuhavyy.collabapp.data.model.Product;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<CartItem> cartItems;
    private List<Product> products;
    private OnQuantityChangeListener quantityChangeListener;


    protected Bitmap getProductImage(Product product) {
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            try {
                String base64Image = product.getImage();
                // Remove data URL prefix if exists
                if (base64Image.startsWith("data:image")) {
                    base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
                }
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            } catch (Exception e) {
                e.printStackTrace();
                // Return default image if error occurs
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
            }
        }
        // Return default image if no image data
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
    }



    public interface OnQuantityChangeListener {
        void onQuantityChanged(int position, int newQuantity);
        void onItemSelected(int position, boolean isSelected);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, List<Product> products, OnQuantityChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.products = products;
        this.quantityChangeListener = listener;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        }

        CartItem cartItem = cartItems.get(position);
        Product product = products.get(position);

        CheckBox chkChooseProduct = convertView.findViewById(R.id.chkChooseProduct);
        ImageView imgProduct = convertView.findViewById(R.id.imgProduct);
        TextView txtProductName = convertView.findViewById(R.id.txtProductName);

        EditText edtProductQuantity = convertView.findViewById(R.id.edtProductQuantity);
        ImageView imgMinus = convertView.findViewById(R.id.imgMinus);
        ImageView imgPlus = convertView.findViewById(R.id.imgPlus);
        TextView txtProductPrice = convertView.findViewById(R.id.txtProductPrice);

        // Set product data
        chkChooseProduct.setChecked(cartItem.isSelected());
        txtProductName.setText(product.getName());

        double price = product.getPrice();
        txtProductPrice.setText(String.format("%,.0f", price));

        // Load image from base64
        Bitmap productImage = getProductImage(product);
        if (productImage != null) {
            imgProduct.setImageBitmap(productImage);
        } else {
            imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Set quantity
        edtProductQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // Quantity change listeners
        imgMinus.setOnClickListener(v -> {
            int quantity = cartItem.getQuantity();
            if (quantity > 1) {
                quantity--;
                cartItem.setQuantity(quantity);
                edtProductQuantity.setText(String.valueOf(quantity));
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityChanged(position, quantity);
                }
            }
        });

        imgPlus.setOnClickListener(v -> {
            int quantity = cartItem.getQuantity();
            quantity++;
            cartItem.setQuantity(quantity);
            edtProductQuantity.setText(String.valueOf(quantity));
            if (quantityChangeListener != null) {
                quantityChangeListener.onQuantityChanged(position, quantity);
            }
        });

        chkChooseProduct.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartItem.setSelected(isChecked);
            if (quantityChangeListener != null) {
                quantityChangeListener.onItemSelected(position, isChecked);
            }
        });

        return convertView;
    }

    public void updateData(List<CartItem> cartItems, List<Product> products) {
        this.cartItems = cartItems;
        this.products = products;
        notifyDataSetChanged();
    }
}