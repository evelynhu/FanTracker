package com.mii.android.ui.util;

import java.text.NumberFormat;
import java.util.Locale;

public class ViewerFormatter {

    private ViewerFormatter() { }

    public static String format(long viewer) {
        if (viewer < 0) {
            return "";
        } else if (viewer == 1) {
            return String.format("%s person saw this post", String.valueOf(viewer));
        } else {
            return String.format("%s people saw this post", String.valueOf(viewer));
        }
    }
}
