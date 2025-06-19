package com.luuhavyy.collabapp.utils;

import android.text.format.DateUtils;

public class DateTimeUtil {
    public static String formatTimeAgo(String isoTimestamp) {
        try {
            java.time.OffsetDateTime odt = java.time.OffsetDateTime.parse(isoTimestamp);
            long timeInMillis = odt.toInstant().toEpochMilli();
            long now = System.currentTimeMillis();

            return DateUtils.getRelativeTimeSpanString(
                    timeInMillis, now, DateUtils.MINUTE_IN_MILLIS).toString();
        } catch (Exception e) {
            return "";
        }
    }
}
