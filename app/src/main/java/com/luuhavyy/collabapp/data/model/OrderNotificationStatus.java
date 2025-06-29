package com.luuhavyy.collabapp.data.model;

public enum OrderNotificationStatus {
    PENDING,        // Chờ xác nhận
    SHIPPING,       // Đang giao hàng
    DELIVERED,      // Đã giao
    UNKNOWN;        // Không xác định (dành cho các trường hợp khác)

    public static OrderNotificationStatus fromTitle(String title) {
        if (title == null) return UNKNOWN;
        String t = title.trim().toLowerCase();
        if (t.contains("pending")) return PENDING;
        if (t.contains("shipped") || t.contains("being shipped") || t.contains("left the warehouse")) return SHIPPING;
        if (t.contains("delivered")) return DELIVERED;
        return UNKNOWN;
    }
}
