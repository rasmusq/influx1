package com.ralleq.influx.files;

import android.content.Context;
import android.graphics.Typeface;

import com.ralleq.influx.R;

public class FontHandler {

    public static Typeface mainFont;

    public FontHandler(Context context) {
        mainFont = context.getResources().getFont(R.font.somatic_font);
    }
}
