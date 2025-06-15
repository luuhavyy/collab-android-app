package com.luuhavyy.collabapp.data.model;

import java.io.Serializable;

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
    String address;
//    useractivity
}

