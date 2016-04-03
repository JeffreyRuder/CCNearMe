package com.epicodus.ccnearme.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Jeffrey on 4/2/2016.
 */
public class FontAwesomeIconButton extends Button {
    public FontAwesomeIconButton (Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
    }
}
