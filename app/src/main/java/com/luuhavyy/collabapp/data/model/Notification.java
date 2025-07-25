package com.luuhavyy.collabapp.data.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {
    String message;
    String notificationid;
    String relatedid;
    String timestamp;
    String title;
    String type;
    String userid;
}
