package com.luuhavyy.collabapp.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    private String cartid;
    private String userid;
    private List<CartItem> products = new ArrayList<>();
    private double totalamount;
    private String promotionid;
    private String createdat;
    private String updatedat;

    public Cart() {
    }

    public Cart(String cartid, String userid, List<CartItem> products, double totalamount, String promotionid, String createdat, String updatedat) {
        this.cartid = cartid;
        this.userid = userid;
        this.products = products;
        this.totalamount = totalamount;
        this.promotionid = promotionid;
        this.createdat = createdat;
        this.updatedat = updatedat;
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