package com.luuhavyy.collabapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Cart implements Parcelable {
    private String cartid;
    private String userid;
    private List<CartItem> products = new ArrayList<>();
    private double totalamount;
    private String promotionid;
    private String createdat;
    private String updatedat;

    public Cart() {
    }

    protected Cart(Parcel in) {
        cartid = in.readString();
        userid = in.readString();
        products = in.createTypedArrayList(CartItem.CREATOR);
        totalamount = in.readDouble();
        promotionid = in.readString();
        createdat = in.readString();
        updatedat = in.readString();
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cartid);
        dest.writeString(userid);
        dest.writeTypedList(products);
        dest.writeDouble(totalamount);
        dest.writeString(promotionid);
        dest.writeString(createdat);
        dest.writeString(updatedat);
    }


    public String getCartid() {
        return cartid;
    }

    public void setCartid(String cartid) {
        this.cartid = cartid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<CartItem> getProducts() {
        return products;
    }

    public void setProducts(List<CartItem> products) {
        this.products = products;
    }

    public double getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(double totalamount) {
        this.totalamount = totalamount;
    }

    public String getPromotionid() {
        return promotionid;
    }

    public void setPromotionid(String promotionid) {
        this.promotionid = promotionid;
    }

    public String getCreatedat() {
        return createdat;
    }

    public void setCreatedat(String createdat) {
        this.createdat = createdat;
    }

    public String getUpdatedat() {
        return updatedat;
    }

    public void setUpdatedat(String updatedat) {
        this.updatedat = updatedat;
    }
}