package com.luuhavyy.collabapp.data.model;

public enum NotificationType {
    PROMOTION,
    DELIVERED,
    PENDING,
    SHIPPING,
    ORDER_STATUS;

    public static NotificationType fromFirebaseType(String type) {
        if (type == null) return ORDER_STATUS;
        switch (type.trim().toLowerCase()) {
            case "promotion":
                return PROMOTION;
            case "delivered":
                return DELIVERED;
            case "pending":
                return PENDING;
            case "shipping":
                return SHIPPING;
            default:
                return ORDER_STATUS;
        }
    }
}

