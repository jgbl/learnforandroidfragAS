/*
 * Copyright (c) 2015 GPL by J.M.Goebel. Distributed under the GNU GPL v3.
 * 
 * 08.06.2015
 * 
 * This file is part of learnforandroid.
 *
 * learnforandroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  learnforandroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.de.jmg.lib;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class BorderedTextView extends TextView implements IBorderedView {
	private Paint paint = new Paint();
	private Drawable _MeaningBG;
	public boolean showBorders;

	// private RectF RoundedRect;

	public BorderedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BorderedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BorderedTextView(Context context) {
		super(context);
		init();
	}

	public void setShowBorders(boolean showBorders, int backColor) {
		this.showBorders = showBorders;
		if (showBorders) {
			if (_MeaningBG != null) _MeaningBG = this.getBackground();
			this.setBackgroundResource(org.de.jmg.learn.R.drawable.roundedbox);
			GradientDrawable drawable = (GradientDrawable) this.getBackground();
			drawable.setColor(backColor);

		} else {
			if (_MeaningBG!=null)
			{
				lib.setBgEditText(this, _MeaningBG);
			}
			else {
				this.setBackgroundResource(0);
			}
		}
		this.invalidate();
	}

	public BottomOrTop getScrollBottomOrTopReached() throws Exception

	{

		if (getLineCount()>0)
		{
			int Top = getLayout().getLineTop(0);
			int Bottom = getLayout().getLineBottom(getLineCount()-1);
			int ScrollY = getScrollY();
			if (ScrollY <= Top && ScrollY + getHeight() >= Bottom)
			{
				return BottomOrTop.both;
			}
			else if(ScrollY <= Top)
			{
				return BottomOrTop.top;
			}
			else if (ScrollY + getHeight() >= Bottom)
			{
				return BottomOrTop.bottom;
			}
			else
			{
				return BottomOrTop.between;
			}
		}
		return BottomOrTop.undefined;
	}


	public void init() {
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(4);
		// this.setPadding(5, 5, 5, 5);

	}
	/*
	 * @Override protected void onDraw(Canvas canvas) { //this.setPadding(5, 5,
	 * 5, 5); super.onDraw(canvas); //this.setPadding(0, 0, 0, 0);
	 * if(!showBorders) return; if (RoundedRect == null || RoundedRect.width()
	 * != getWidth()) RoundedRect = new RectF(0, 0, getWidth()-0,
	 * getHeight()-0); canvas.drawRoundRect(RoundedRect, 6, 6, paint); }
	 */
}