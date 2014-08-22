/*
 Copyright 2013 Neopixl - Olivier Demolliens

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this

file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under

the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF 

ANY KIND, either express or implied. See the License for the specific language governing

permissions and limitations under the License.
 */
package com.neopixl.pixlui.components.textview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.view.View;

import com.android.export.AllCapsTransformationMethod;
import com.neopixl.pixlui.R;
import com.neopixl.pixlui.intern.PixlUIConstants;
import com.neopixl.pixlui.intern.PixlUIUtils;

import java.nio.CharBuffer;

import static com.neopixl.pixlui.intern.PixlUIConstants.*;

/**
 * Provide more possibility with Button and enable new methods on old api
 * 
 * @author Olivier Demolliens. @odemolliens Dev with Neopixl
 */
public class TextView extends EllipsizingTextView {


    public static final float LETTER_SPACING_NORMAL = 1.0f;

    private static final String NONBREAKING_SPACE = "\u00A0";

    private float mLetterSpacing = LETTER_SPACING_NORMAL;

    private CharSequence mOriginalText;

    private CharSequence mLastSpacingAppliedToString = null;

	public TextView(Context context) {
		this(context,false);
	}

	public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs, 0);
        setAllCaps(context, attrs, 0);
        setLetterSpacing(context, attrs, 0);
	}

	public TextView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs, defStyle,false);
	}

	public TextView(Context context, boolean canBeEllipsized) {
		this(context, null, canBeEllipsized);
	}

	public TextView(Context context, AttributeSet attrs, boolean canBeEllipsized) {
		super(context, attrs, canBeEllipsized);
        setCustomFont(context, attrs, 0);
        setAllCaps(context, attrs, 0);
        setLetterSpacing(context, attrs, 0);
	}

	public TextView(Context context, AttributeSet attrs, int defStyle, boolean canBeEllipsized) {
		super(context, attrs, defStyle,canBeEllipsized);
		setCustomFont(context, attrs, defStyle);
        setAllCaps(context, attrs, defStyle);
        setLetterSpacing(context, attrs, defStyle);
	}


    @Override
    public void setText(CharSequence text, BufferType type) {
        if (isInEditMode()) {
            super.setText(text, type);
        }
        if (text != null) {
            if (text instanceof Spannable) {
                Spannable spannable = (Spannable) text;
                ScaleXSpan[] spans = spannable.getSpans(0, text.length(), ScaleXSpan.class);
                if (spans == null || spans.length == 0) {
                    mOriginalText = text;
                    applyLetterSpacing();
                    return;
                }
            }
            mOriginalText = text;
            applyLetterSpacing();
            return;
        }
        super.setText(text, type);
    }


    @Override
    public CharSequence getText() {
        if (mOriginalText == null) {
            return super.getText();
        }
        return mOriginalText;
    }

    public void setLetterSpacing(float spacing) {
        mLetterSpacing = spacing;
        applyLetterSpacing();
    }

    public float getLettersSpacing() {
        return mLetterSpacing;
    }

    private void applyLetterSpacing() {
        if (isInEditMode()) { return; }
        if (mOriginalText == null || mOriginalText.length() < 2 || mLetterSpacing == LETTER_SPACING_NORMAL || mLetterSpacing == 0.0f) {
            super.setText(mOriginalText, BufferType.NORMAL);
            return;
        }

        final SpannableStringBuilder builder =  new SpannableStringBuilder(mOriginalText.toString());

        for (int i = mOriginalText.length() - 1; i >= 1; i--)
        {
            builder.insert(i, NONBREAKING_SPACE);
            builder.setSpan(new ScaleXSpan(mLetterSpacing), i, i + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mLastSpacingAppliedToString = mOriginalText;
        super.setText(builder, BufferType.SPANNABLE);
    }

	/**
	 * XML methods
	 * 
	 * @param ctx
	 * @param attrs
	 */
	private void setCustomFont(Context ctx, AttributeSet attrs, int defStyle) {
        PixlUIUtils.setCustomFont(ctx, this,
                R.styleable.com_neopixl_pixlui_components_textview_TextView,
                R.styleable.com_neopixl_pixlui_components_textview_TextView_typeface,
                attrs, defStyle);
	}

	/**
	 * XML methods
	 * 
	 * @param ctx
	 * @param attrs
	 */
	@SuppressLint("NewApi")
	private void setAllCaps(Context ctx, AttributeSet attrs, int defStyle) {
		if(!isInEditMode()){
            boolean allCaps = PixlUIUtils.containsUppercaseStyleOrAttribute(ctx,
                    R.styleable.com_neopixl_pixlui_components_textview_TextView,
                    R.styleable.com_neopixl_pixlui_components_textview_TextView_textAllCaps,
                    attrs, defStyle);

            if (allCaps) {
                setAllCaps(allCaps);
            }
        }
	}



    private void setLetterSpacing(Context ctx, AttributeSet attrs, int defStyle) {
        TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.com_neopixl_pixlui_components_textview_TextView, 0,0);
        try {
            setLetterSpacing(a.getFloat(R.styleable.com_neopixl_pixlui_components_textview_TextView_letterSpacing, LETTER_SPACING_NORMAL));
        }finally {
            a.recycle();
        }
    }
    /**
     * Use this method to uppercase all char in text.
     *
     * @param allCaps
     */
    @SuppressLint("NewApi")
    @Override
    public void setAllCaps(boolean allCaps) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.setAllCaps(allCaps);
        } else {
            if (allCaps) {
                setTransformationMethod(new AllCapsTransformationMethod(getContext()));
            } else {
                setTransformationMethod(null);
            }
        }
    }

	/**
	 * Use this method to set a custom font in your code (/assets/fonts/)
	 * 
	 * @param ctx
	 * @param font
	 *            Name, don't forget to add file extension
	 * @return
	 */ 
	public boolean setCustomFont(Context ctx, String font) {
		Typeface tf = FontFactory.getInstance(ctx).getFont(font);
		if (tf != null) {
			setTypeface(tf);
			return true;
		} else {
			return false;
		}
	}

}