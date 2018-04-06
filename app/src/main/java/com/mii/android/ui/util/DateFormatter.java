package com.mii.android.ui.util;

import android.text.format.DateUtils;

public class DateFormatter {

    private DateFormatter() { }

    public static String getRelativeTime(long time) {
        return DateUtils.getRelativeTimeSpanString(
                time,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
    }
}
