package com.neopixl.pixlui.intern;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.neopixl.pixlui.components.textview.FontFactory;

import static com.neopixl.pixlui.intern.PixlUIConstants.ATTR_TEXT_ALL_CAPS;
import static com.neopixl.pixlui.intern.PixlUIConstants.ATTR_TYPEFACE_NAME;
import static com.neopixl.pixlui.intern.PixlUIConstants.SCHEMA_URL;

/**
 * Created by mwolfe on 6/11/14.
 */
public class PixlUIUtils {

    public static boolean containsUppercaseStyleOrAttribute(Context ctx, int[] attrs, int uppercaseId, AttributeSet attributeSet, int defStyle) {
        int id=attributeSet.getAttributeResourceValue(SCHEMA_URL, ATTR_TEXT_ALL_CAPS, 0);

        if(id==0){
            for (int i=0; i< attributeSet.getAttributeCount(); i++) {

                if (ATTR_TEXT_ALL_CAPS.equals(attributeSet.getAttributeName(i))) {
                    return attributeSet.getAttributeBooleanValue(i, false);
                }
            }
        }
        else{
            return ctx.getResources().getBoolean(id);
        }

        TypedArray a = ctx.obtainStyledAttributes(attributeSet, attrs, defStyle, 0);
        boolean isUppercase;

        id=a.getResourceId(uppercaseId, 0);

        isUppercase = id==0 ?
                a.getBoolean(uppercaseId, false)
                : ctx.getResources().getBoolean(id);

        a.recycle();

        return isUppercase;
    }

    public static void setCustomFont(Context ctx, TextView view, int[] attrs, int typefaceId, AttributeSet set, int defStyle) {
        int id=set.getAttributeResourceValue(SCHEMA_URL, ATTR_TYPEFACE_NAME, 0);
        String typefaceName;

        typefaceName = id==0 ?
                set.getAttributeValue(SCHEMA_URL, ATTR_TYPEFACE_NAME)
                : ctx.getString(id);

        if (typefaceName == null) {
            TypedArray a = ctx.obtainStyledAttributes(set, attrs, defStyle, 0);

            id = a.getResourceId(typefaceId, 0);

            typefaceName = id==0 ?
                    a.getString(typefaceId)
                    : ctx.getString(id);

            a.recycle();
        }

        if(typefaceName != null) {
            Typeface tf = FontFactory.getInstance(ctx).getFont(typefaceName);

            if (tf != null) {
                view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.LINEAR_TEXT_FLAG);
                view.setTypeface(tf);
            }
        }
    }
}
