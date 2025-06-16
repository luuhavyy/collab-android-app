package com.luuhavyy.collabapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Promotion implements Parcelable {
    private String promotionid;
    private String userid;
    private String categoryid;
    private String discounttype;
    private long discountvalue;
    private String validfrom;
    private String validuntil;
    private boolean isused;
    private String promotioncode;
    private boolean selected;

    public Promotion() {
    }

    protected Promotion(Parcel in) {
        promotionid = in.readString();
        userid = in.readString();
        categoryid = in.readString();
        discounttype = in.readString();
        discountvalue = in.readLong();
        validfrom = in.readString();
        validuntil = in.readString();
        isused = in.readByte() != 0;
        promotioncode = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<Promotion> CREATOR = new Creator<Promotion>() {
        @Override
        public Promotion createFromParcel(Parcel in) {
            return new Promotion(in);
        }

        @Override
        public Promotion[] newArray(int size) {
            return new Promotion[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(promotionid);
        dest.writeString(userid);
        dest.writeString(categoryid);
        dest.writeString(discounttype);
        dest.writeLong(discountvalue);
        dest.writeString(validfrom);
        dest.writeString(validuntil);
        dest.writeByte((byte) (isused ? 1 : 0));
        dest.writeString(promotioncode);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    public String getPromotionid() {
        return promotionid;
    }

    public void setPromotionid(String promotionid) {
        this.promotionid = promotionid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getDiscounttype() {
        return discounttype;
    }

    public void setDiscounttype(String discounttype) {
        this.discounttype = discounttype;
    }

    public long getDiscountvalue() {
        return discountvalue;
    }

    public void setDiscountvalue(long discountvalue) {
        this.discountvalue = discountvalue;
    }

    public String getValidfrom() {
        return validfrom;
    }

    public void setValidfrom(String validfrom) {
        this.validfrom = validfrom;
    }

    public String getValiduntil() {
        return validuntil;
    }

    public void setValiduntil(String validuntil) {
        this.validuntil = validuntil;
    }

    public boolean isIsused() {
        return isused;
    }

    public void setIsused(boolean isused) {
        this.isused = isused;
    }

    public String getPromotioncode() {
        return promotioncode;
    }

    public void setPromotioncode(String promotioncode) {
        this.promotioncode = promotioncode;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}