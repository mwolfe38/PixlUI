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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;

import com.android.export.AllCapsTransformationMethod;
import com.neopixl.pixlui.FontFactory;
import com.neopixl.pixlui.R;
import com.neopixl.pixlui.intern.PixlUIUtils;

/**
 * Provide more possibility with Button and enable new methods on old api
 * 
 * @author Olivier Demolliens. @odemolliens Dev with Neopixl
 */
public class TextView extends android.widget.TextView {


    public static final float LETTER_SPACING_NORMAL = 1.0f;

    private static final String NONBREAKING_SPACE = "\u00A0";

    private float mLetterSpacing = LETTER_SPACING_NORMAL;

    private static final String NEWLINE_STR = "\n";
    private static final char NEWLINE_CHAR = '\n';

    private static final String NOWRAP_START = "<nowrap>";
    private static final String NOWRAP_END = "</nowrap>";
    private static final int NOWRAP_LEN = NOWRAP_START.length();
    private static final int NOWRAP_END_LEN = NOWRAP_END.length();

    private CharSequence mOriginalText;

    private BufferType mBufferType = BufferType.NORMAL;


    public TextView(Context context) {
        super(context);
    }

	public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs, 0);
        setAllCaps(context, attrs, 0);
        setLetterSpacing(context, attrs, 0);
	}
//
//    public TextView(Context context) {
//        this(context,false);
//    }

	public TextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
//
//	public TextView(Context context, boolean canBeEllipsized) {
//		this(context, null, canBeEllipsized);
//	}

//	public TextView(Context context, AttributeSet attrs, boolean canBeEllipsized) {
//		super(context, attrs, canBeEllipsized);
//        setCustomFont(context, attrs, 0);
//        setAllCaps(context, attrs, 0);
//        setLetterSpacing(context, attrs, 0);
//	}
//
//	public TextView(Context context, AttributeSet attrs, int defStyle, boolean canBeEllipsized) {
//		super(context, attrs, defStyle,canBeEllipsized);
//		setCustomFont(context, attrs, defStyle);
//        setAllCaps(context, attrs, defStyle);
//        setLetterSpacing(context, attrs, defStyle);
//	}


    private void adjustForLineBreaks() {
        Editable editable = getEditableText();
        if (editable == null) {
            return;
        }
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        if (width == 0) {
            return;
        }



        Paint p = getPaint();
        float[] widths = new float[editable.length()];
        p.getTextWidths(editable.toString(), widths);
        float curWidth = 0.0f;
        int lastWSPos = -1;
        int strPos = 0;
        boolean reset = false;
        int insertCount = 0;

        String chars = editable.toString();
        boolean containsNowrap = chars.contains(NOWRAP_START) && chars.contains(NOWRAP_END);


        /*
         * Traverse the string from the start position, adding each character's width to the total
         * until: 1) A whitespace character is found. In this case, mark the whitespace position. If
         * the width goes over the max, this is where the newline will be inserted. 2) A newline
         * character is found. This resets the curWidth counter. curWidth > width. Replace the
         * whitespace with a newline and reset the counter.
         */
        boolean inNowrap = false;
        while (strPos < editable.length()) {
            curWidth += widths[strPos];

            char curChar = editable.charAt(strPos);

            if (curChar == NEWLINE_CHAR) {
                reset = true;
            } else if (!inNowrap && Character.isWhitespace(curChar)) {
                lastWSPos = strPos;
            } else {
                if (containsNowrap && curChar == '<') {
                    char[] dest = new char[editable.length()-strPos];
                    editable.getChars(strPos, editable.length(), dest, 0);
                    String test = new String(dest);
                    if (test.startsWith(NOWRAP_START) && test.contains(NOWRAP_END)) {
                        inNowrap = true;
                        editable.replace(strPos, strPos + NOWRAP_LEN, "");
                    } else if (test.startsWith(NOWRAP_END)) {
                        inNowrap = false;
                        editable.replace(strPos, strPos + NOWRAP_END_LEN, "");
                    }
                }
                if (curWidth > width && lastWSPos >= 0) {
                    editable.replace(lastWSPos, lastWSPos + 1, NEWLINE_STR);
                    insertCount++;
                    strPos = lastWSPos;
                    lastWSPos = -1;
                    reset = true;
                }
            }

            if (reset) {
                curWidth = 0.0f;
                reset = false;
            }
            strPos++;
        }

        if (insertCount != 0) {
            setText(editable);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        adjustForLineBreaks();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (isInEditMode()) {
            super.setText(text, type);
            return;
        }
        if (mBufferType == BufferType.NORMAL && type != BufferType.NORMAL) {
            mBufferType = BufferType.SPANNABLE;
            if (mBufferType == BufferType.SPANNABLE && type == BufferType.EDITABLE) {
                mBufferType = BufferType.EDITABLE;
            }
        }
        if (text != null) {
            if (text instanceof Spannable) {
                Spannable spannable = (Spannable) text;
                ScaleXSpan[] spans = spannable.getSpans(0, text.length(), ScaleXSpan.class);
                if (spans == null || spans.length == 0) {
                    mOriginalText = text;
                    applyLetterSpacing(mBufferType);
                    return;
                }
            }
            mOriginalText = text;
            applyLetterSpacing(mBufferType);
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
        applyLetterSpacing(mBufferType);
    }

    public float getLettersSpacing() {
        return mLetterSpacing;
    }

    private void applyLetterSpacing(BufferType type) {

        if (mOriginalText == null || mOriginalText.length() < 2 || mLetterSpacing == LETTER_SPACING_NORMAL || mLetterSpacing == 0.0f) {
            super.setText(mOriginalText, type);
            return;
        }

        final SpannableStringBuilder builder =  new SpannableStringBuilder();
        float spacingMultiplier = mLetterSpacing;
        final float MAX_SINGLE_SPACE = 1.5f;
        int spaceInsertionCount = 1;
        if (mLetterSpacing > MAX_SINGLE_SPACE) {
            spaceInsertionCount = 1 + (int) Math.floor(mLetterSpacing / MAX_SINGLE_SPACE);
            spacingMultiplier = mLetterSpacing / spaceInsertionCount;

        }
        String insertion = new String(new char[spaceInsertionCount]).replace('\0', NONBREAKING_SPACE.charAt(0));

        for (int i = 0; i<mOriginalText.length(); i++)
        {
            int idx = i*(1+spaceInsertionCount);
            builder.insert(idx, mOriginalText.subSequence(i, i+1));
            if (i < mOriginalText.length()-1) {
                builder.insert(idx + 1, insertion);
                builder.setSpan(new ScaleXSpan(spacingMultiplier), idx + 1, idx + 1 + spaceInsertionCount,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        if (mBufferType == BufferType.NORMAL) {
            mBufferType = BufferType.SPANNABLE;
        }
        super.setText(builder, mBufferType);
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
        TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.com_neopixl_pixlui_components_textview_TextView, R.styleable.com_neopixl_pixlui_components_textview_TextView_letterSpacing, defStyle);
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