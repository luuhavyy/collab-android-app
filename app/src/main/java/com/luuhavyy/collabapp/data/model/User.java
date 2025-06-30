package com.luuhavyy.collabapp.data.model;

import android.location.Address;

import java.io.Serializable;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements Serializable {
    String userid;
    String username;
    String password;
    String name;
    String email;
    String phonenumber;
    String profilepicture;
    String gender;
    Address defaultaddress;
    List<UserActivity> useractivity;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Address implements Serializable {
        String city;
        String country;
        String street;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserActivity implements Serializable {
        String action;
        String activityid;
        String targetid;
        String timestamp;
    }
}

