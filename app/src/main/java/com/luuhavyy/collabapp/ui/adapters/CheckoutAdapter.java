package com.luuhavyy.collabapp.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.CartItem;
import com.luuhavyy.collabapp.data.model.Product;

import java.util.List;

public class CheckoutAdapter extends BaseAdapter {
    private Context context;
    private List<CartItem> cartItems;
    private List<Product> products;
    private LruCache<String, Bitmap> memoryCache;


    private static final int DEFAULT_IMAGE_RES = R.drawable.ic_launcher_foreground;
    private static final int ERROR_IMAGE_RES = R.mipmap.ic_error;

    public CheckoutAdapter(Context context, List<CartItem> cartItems, List<Product> products) {
        this.context = context;
        this.cartItems = cartItems;
        this.products = products;
        initImageCache();
    }


    private void initImageCache() {

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);


        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {

                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    public int getCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return cartItems != null ? cartItems.get(position) : null;
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

        // Lấy dữ liệu sản phẩm tại vị trí hiện tại
        CartItem cartItem = cartItems.get(position);
        Product product = products.get(position);


        displayProductInfo(holder, product, cartItem);

        return convertView;
    }


    private void displayProductInfo(ViewHolder holder, Product product, CartItem cartItem) {
        // Hiển thị tên sản phẩm
        holder.txtProductName.setText(product != null ? product.getName() : "");

        // Hiển thị số lượng
        int quantity = cartItem != null ? cartItem.getQuantity() : 0;
        holder.txtProductQuantity.setText(context.getString(R.string.quantity_format, quantity));

        // Hiển thị giá (giá sản phẩm * số lượng)
        double price = product != null ? product.getPrice() : 0;
        holder.txtProductPrice.setText(context.getString(R.string.price_format, price * quantity));

        // Hiển thị ảnh sản phẩm
        Bitmap productImage = getProductImage(product);
        holder.imgProduct.setImageBitmap(productImage);
    }


    private Bitmap getProductImage(Product product) {
        if (product == null || product.getImage() == null) {
            return getDefaultBitmap();
        }

        String imageKey = product.getProductid();


        Bitmap cachedBitmap = getBitmapFromMemCache(imageKey);
        if (cachedBitmap != null) {
            return cachedBitmap;
        }

        try {
            String base64Image = product.getImage().trim();

            if (base64Image.startsWith("data:image")) {
                base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
            }

            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = calculateInSampleSize(decodedBytes);


            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);

            if (bitmap != null) {

                addBitmapToMemoryCache(imageKey, bitmap);
                return bitmap;
            }
        } catch (OutOfMemoryError e) {
            // Xử lý khi thiếu bộ nhớ
            System.gc();
            return getErrorBitmap();
        } catch (Exception e) {
            e.printStackTrace();
            return getErrorBitmap();
        }

        return getDefaultBitmap();
    }


    private int calculateInSampleSize(byte[] imageData) {
        // Đầu tiên decode với inJustDecodeBounds=true để lấy kích thước thực
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);


        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;


        if (height > 1000 || width > 1000) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;


            while ((halfHeight / inSampleSize) >= 1000
                    && (halfWidth / inSampleSize) >= 1000) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private Bitmap getDefaultBitmap() {
        return BitmapFactory.decodeResource(context.getResources(), DEFAULT_IMAGE_RES);
    }


    private Bitmap getErrorBitmap() {
        return BitmapFactory.decodeResource(context.getResources(), ERROR_IMAGE_RES);
    }


    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) return;
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }


    private Bitmap getBitmapFromMemCache(String key) {
        return key != null ? memoryCache.get(key) : null;
    }


    public void updateData(List<CartItem> cartItems, List<Product> products) {
        this.cartItems = cartItems;
        this.products = products;
        clearCache();
        notifyDataSetChanged();
    }


    public void clearCache() {
        if (memoryCache != null) {
            memoryCache.evictAll();
        }
    }


    private static class ViewHolder {
        ImageView imgProduct;
        TextView txtProductName;
        TextView txtProductQuantity;
        TextView txtProductPrice;
    }
}