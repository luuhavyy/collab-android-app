package com.luuhavyy.collabapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private String productid;
    private int quantity;
    private double price;
    private boolean isSelected;

    public CartItem() {
    }

    public CartItem(String productid, int quantity, double price, boolean isSelected) {
        this.productid = productid;
        this.quantity = quantity;
        this.price = price;
        this.isSelected = isSelected;
    }

    protected CartItem(Parcel in) {
        productid = in.readString();
        quantity = in.readInt();
        price = in.readDouble();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productid);
        dest.writeInt(quantity);
        dest.writeDouble(price);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    // Getter và Setter
    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}