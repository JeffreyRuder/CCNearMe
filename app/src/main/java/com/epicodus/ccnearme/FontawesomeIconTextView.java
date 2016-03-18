package com.epicodus.ccnearme;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Jeffrey on 3/18/2016.
 */
public class FontAwesomeIconTextView extends TextView {
    public FontAwesomeIconTextView (Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
    }
}
