package com.luuhavyy.collabapp.data.model;

import java.io.Serializable;
import java.security.Timestamp;

public class Promotion implements Serializable {
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

    public Promotion(String promotionid, String userid, String categoryid, String discounttype, long discountvalue, String validfrom, String validuntil, boolean isused, String promotioncode, boolean selected) {
        this.promotionid = promotionid;
        this.userid = userid;
        this.categoryid = categoryid;
        this.discounttype = discounttype;
        this.discountvalue = discountvalue;
        this.validfrom = validfrom;
        this.validuntil = validuntil;
        this.isused = isused;
        this.promotioncode = promotioncode;
        this.selected = selected;
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
