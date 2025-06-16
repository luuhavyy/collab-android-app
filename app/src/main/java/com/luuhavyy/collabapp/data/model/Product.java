package com.luuhavyy.collabapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product implements Parcelable {
    String productid;
    String name;
    int price;
    int ratings;
    int stock;
    String image;
    String description;
    String categoryid;

    protected Product(Parcel in) {
        productid = in.readString();
        name = in.readString();
        price = in.readInt();
        ratings = in.readInt();
        stock = in.readInt();
        image = in.readString();
        description = in.readString();
        categoryid = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productid);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeInt(ratings);
        dest.writeInt(stock);
        dest.writeString(image);
        dest.writeString(description);
        dest.writeString(categoryid);
    }
}