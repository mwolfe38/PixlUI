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
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import com.android.export.AllCapsTransformationMethod;
import com.neopixl.pixlui.R;
import com.neopixl.pixlui.intern.PixlUIUtils;

/**
 * Provide more possibility with Button and enable new methods on old api
 * 
 * @author Olivier Demolliens. @odemolliens Dev with Neopixl
 */
public class TextView extends EllipsizingTextView {


	public TextView(Context context) {
		this(context,false);
	}

	public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs, 0);
        setAllCaps(context, attrs, 0);
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

	}

	public TextView(Context context, AttributeSet attrs, int defStyle, boolean canBeEllipsized) {
		super(context, attrs, defStyle,canBeEllipsized);
		setCustomFont(context, attrs, defStyle);
        setAllCaps(context, attrs, defStyle);
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
                R.styleable.com_neopixl_pixlui_components_textview_TextView_pixlTypeface,
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
                    R.styleable.com_neopixl_pixlui_components_textview_TextView_pixlTextAllCaps,
                    attrs, defStyle);

            if (allCaps) {
                setAllCaps(allCaps);
            }
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