package com.epicodus.ccnearme.views;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Jeffrey on 3/18/2016.
 */
public class FontManager {

    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }
}
