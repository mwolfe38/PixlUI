package com.neopixl.pixlui.intern;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.neopixl.pixlui.components.textview.FontFactory;

import static com.neopixl.pixlui.intern.PixlUIConstants.ATTR_TEXT_ALL_CAPS;

/**
 * Created by mwolfe on 6/11/14.
 */
public class PixlUIUtils {

    public static boolean containsUppercaseStyleOrAttribute(Context ctx, int[] attrs, int uppercaseId, AttributeSet attributeSet, int defStyle) {
        TypedArray a = ctx.obtainStyledAttributes(attributeSet, attrs, defStyle, 0);
        boolean returnVal =  a.getBoolean(uppercaseId, false);

        a.recycle();
        return returnVal;
    }

    public static void setCustomFont(Context ctx, TextView view, int[] attrs, int typefaceId, AttributeSet set, int defStyle) {

        // Retrieve style attributes.
        TypedArray a = ctx.obtainStyledAttributes(set, attrs, defStyle, 0);
        String typefaceName = a.getString(typefaceId);
        a.recycle();

        if(typefaceName != null) {
            Typeface tf = FontFactory.getInstance(ctx).getFont(typefaceName);
            if (tf != null) {
                view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.LINEAR_TEXT_FLAG);
                view.setTypeface(tf);
            }
        }
    }
}
