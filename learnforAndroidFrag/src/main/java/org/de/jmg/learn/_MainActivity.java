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
package org.de.jmg.learn;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

import org.de.jmg.learn.vok.Vokabel;
import org.de.jmg.learn.vok.Vokabel.Bewertung;
import org.de.jmg.learn.vok.Vokabel.EnumSprachen;
import org.de.jmg.learn.R;
import org.de.jmg.lib.BorderedEditText;
import org.de.jmg.lib.IBorderedView.BottomOrTop;
import org.de.jmg.lib.BorderedTextView;
import org.de.jmg.lib.OnTouchListenerScroll;
import org.de.jmg.lib.RemoveCallbackListener;
import org.de.jmg.lib.ScrollGestureListener;
import org.de.jmg.lib.lib;
import org.de.jmg.lib.ColorSetting.ColorItems;
import org.de.jmg.lib.lib.Sounds;
import org.de.jmg.lib.lib.libString;
import org.de.jmg.lib.lib.yesnoundefined;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnWindowFocusChangeListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class _MainActivity extends Fragment implements RemoveCallbackListener {

	public View mainView;
	private Context context;
	private Button _btnRight;
	private Button _btnWrong;
	private Button _btnSkip;
	private Button _btnView;
	private Button _btnEdit;
	private BorderedTextView _txtWord;
	private BorderedTextView _txtKom;
	private BorderedEditText _txtedWord;
	private BorderedEditText _txtedKom;
	private BorderedTextView _txtStatus;
	private BorderedEditText _txtMeaning1;
	private BorderedEditText _txtMeaning2;
	private BorderedEditText _txtMeaning3;
	private ScrollView _scrollView; //= (ScrollView) findViewById(R.id.layoutMain);
	
	private double scale = 1;
	private Drawable _MeaningBG;
	Handler handler = new Handler();
	Vokabel _vok;
	MainActivity _main;
	public final static int fragID = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		
		//if (mainView!=null)return mainView;
		
		mainView = inflater.inflate(R.layout.activity_main, container,false);
		_main = (MainActivity) getActivity();
		 context = _main;
		_vok = _main.vok;
		libLearn.gStatus = "onCreate InitButtons";
		try 
		{
			InitControls();
			libLearn.gStatus = "onCreate InitMeanings";
			InitMeanings();


			mainView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            // Ensure you call it only once :
                            lib.removeLayoutListener(mainView.getViewTreeObserver(), this);
                            // Here you can get the size :)
                            resize();
							//_scrollView.fullScroll(View.FOCUS_UP);
                        }
                    });

			try {
				this.SetActionBarTitle();
				if (_vok.getCardMode() ) {
					SetViewsToCardmode();
				} 
				else 
				{
					SetViewsToVokMode();
				}
				if (_vok.getGesamtzahl() > 0)
				{
					setBtnsEnabled(true);
				}
				getVokabel(false, false);
			} catch (Exception e1) {

				lib.ShowException(_main, e1);
				getVokabel(true, true);
			}
		} 
		catch (Exception e) 
		{

			lib.ShowException(_main, e);
		}
		
		return mainView;
	}
	
	@Override
	public void onDestroyView ()
	{
		super.onDestroyView();
		mainView = null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainView=null;
		if (savedInstanceState!=null)
		{
			_lastIsWrongVokID = savedInstanceState.getInt("lastIsWrongVokID");
		}

	}
	

	
	
	@Override
	public void onSaveInstanceState(Bundle outState)
		
	{
		super.onSaveInstanceState(outState);
		outState.putInt("lastIsWrongVokID", _lastIsWrongVokID);
		if (lib.YesNoHandler!=null) lib.YesNoHandler.sendMessage(lib.YesNoHandler.obtainMessage());
		removeCallbacks();
	}
	
	public void removeCallbacks()
	{
		ArrayList<Runnable> runs = new ArrayList<>();
		//handler.removeCallbacks(runnableFalse);
		if (rFlashs!=null)
		{
			if (rFlashs.size() > 0 )
			{
				for (Runnable r: rFlashs)
				{
					handler.removeCallbacks(r);
					if (r instanceof resetLayoutTask || r instanceof hideBedBordersTask || r instanceof hideWordBordersTask)
					{
						runs.add(r);
						//handler.post(r);
					}
				}
				rFlashs.clear();
				//runs.add(runnableFalse);
			}
		}
		for (Runnable r : runs)
		{
			r.run();
		}
	}
	
	public UncaughtExceptionHandler ErrorHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

			ex.printStackTrace();
		}
	};
	
	
	private boolean _firstFocus = true;
	private boolean _isSmallDevice = false;
	Double ScaleWidth = 0d;
	Double ScaleTextButtons = 0d;
	boolean blnWrongWidth = false;
	int width;
	private void resize()
	{


		RelativeLayout.LayoutParams params;
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int height = metrics.heightPixels;
		width = metrics.widthPixels;
		int viewTop = _main.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		height = height - viewTop;
		scale = (double) height / (double) 950;
		double ratio = Math.pow((double)width / (double)height,.5d) ;
		boolean blnHorizontal = width > height;
		if (scale < .5f)
		{
			_isSmallDevice = true;
			_main.isSmallDevice = true;
			if (scale < .4f)
			{
				scale = .4f;
			}
		}
		/*
		 * lib.ShowMessage(this, "Meaning3 Bottom: " +_txtMeaning3.getBottom() +
		 * "\nbtnRight.Top: " + _btnRight.getTop() + "\nDisplayHeight: " +
		 * height);
		 */
		if (scale != 1)
		{
			// lib.ShowToast(_main, "Scaling font by " + scale + " Screenheight = "
					//+ height);

			float tSize = _txtMeaning1.getTextSize();
			if (tSize==40 || (_vok.getCardMode()&& tSize==25))
			{
				_txtMeaning1.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						(float) (_txtMeaning1.getTextSize() * scale));
			}
			params = (android.widget.RelativeLayout.LayoutParams) _txtMeaning1
					.getLayoutParams();
			if (params.topMargin == 120)
			{
				if (!_isSmallDevice)
				{
					params.topMargin = (int) (params.topMargin * scale);
				}
				else
				{
					params.topMargin = (int)(params.topMargin * .1f);
				}
				_txtMeaning1.setLayoutParams(params);
			}

			if (_txtMeaning2.getTextSize()==40)
			{
				_txtMeaning2.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						(float) (_txtMeaning2.getTextSize() * scale));
			}
			params = (android.widget.RelativeLayout.LayoutParams) _txtMeaning2
					.getLayoutParams();
			if (params.topMargin==56)
			{
				params.topMargin = (int) (params.topMargin * scale);
				_txtMeaning2.setLayoutParams(params);
			}

			if (_txtMeaning3.getTextSize()==40)
			{
				_txtMeaning3.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						(float) (_txtMeaning3.getTextSize() * scale));
			}
			params = (android.widget.RelativeLayout.LayoutParams) _txtMeaning3
					.getLayoutParams();
			if (params.topMargin==56)
			{
				params.topMargin = (int) (params.topMargin * scale);
				_txtMeaning3.setLayoutParams(params);
			}


			float size = _txtWord.getTextSize();
			if (size == 60)
			{
				size *= scale;
				_txtWord.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
			}
			if (_txtKom.getTextSize()==35)
			{
				_txtKom.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_txtKom.getTextSize() * scale));
			}
			if (_txtedWord.getTextSize()==60)
			{
				_txtedWord.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						(float) (_txtedWord.getTextSize() * scale));
			}
			if (_txtedKom.getTextSize()==35)
			{
				_txtedKom.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						(float) (_txtedKom.getTextSize() * scale));
			}
			if (_vok!= null && _vok.getCardMode())
			{
				SetViewsToCardmode();
			}
			else
			{
				SetViewsToVokMode();
			}

			/*
			 * _txtMeaning1.setOnFocusChangeListener(new
			 * View.OnFocusChangeListener() {
			 *
			 * @Override public void onFocusChange(View v, boolean hasFocus) {
			 *  if (_firstFocus && hasFocus) {
			 * hideKeyboard(); _firstFocus = false; } } });
			 */
		}
		if (scale != 1)
		{
			int widthButtons = _btnEdit.getRight() - _btnSkip.getLeft();
			int allButtonsWidth = 520; /*_btnEdit.getWidth()
					+_btnRight.getWidth()
					+_btnView.getWidth()
					+_btnWrong.getWidth()
					+_btnEdit.getWidth();
					*/
			if (widthButtons< allButtonsWidth) 
				{
					widthButtons=allButtonsWidth;
					blnWrongWidth = true;
				}
			ScaleWidth = (width - 50)/(double)widthButtons;
			if (ScaleWidth<.7)
			{
				_btnEdit.setVisibility(View.GONE);
				_btnSkip.setVisibility(View.GONE);
				widthButtons = _btnWrong.getRight() - _btnRight.getLeft();
				if (widthButtons< 330)
				{
					widthButtons=330;
					blnWrongWidth = true;
				}
				ScaleWidth = ((double)(_txtMeaning1.getRight() - _txtMeaning1.getLeft()))/(double)widthButtons;
				if (ScaleWidth<.4d) ScaleWidth=.4d;
				ScaleTextButtons = ((scale > ScaleWidth)?scale:ScaleWidth);
			}
			else
			{
				ScaleTextButtons = ((scale < ScaleWidth)?scale:ScaleWidth);

			}


			RelativeLayout layoutButtons = (RelativeLayout) findViewById(R.id.layoutButtonsInner);
			params = (android.widget.RelativeLayout.LayoutParams) layoutButtons
					.getLayoutParams();
			if (!blnWrongWidth) 
			{
				params.bottomMargin = (int) (params.bottomMargin * scale);
			}
			else {
				params.bottomMargin = (int) (0 * ScaleWidth);
			}
			layoutButtons.setLayoutParams(params);
			
			params = (android.widget.RelativeLayout.LayoutParams) _btnRight
					.getLayoutParams();
			if (!blnWrongWidth) 
			{
				params.height = (int) (params.height * scale);
				params.bottomMargin = (int) (params.bottomMargin * scale);
			}
			else
			{
				params.height = (int) (60 * ScaleWidth);
				params.bottomMargin = (int) (0 * ScaleWidth);
				_btnRight.setPadding((int)(_btnRight.getPaddingLeft()*ScaleWidth)
										, (int)(_btnRight.getPaddingTop()*ScaleWidth)
										, (int)(_btnRight.getPaddingRight()*ScaleWidth)
										, (int)(_btnRight.getPaddingBottom()*ScaleWidth));
			}
			params.width = (int) (params.width * ScaleWidth);
			double ScaleTextButtonsOrig = ScaleTextButtons;

			if (blnHorizontal) {
				params.height*= ratio;
				ScaleTextButtons *= ratio;
			}
			_btnRight.setLayoutParams(params);
			
			params = (android.widget.RelativeLayout.LayoutParams) _btnWrong
					.getLayoutParams();
			if (!blnWrongWidth) 
			{
				params.height = (int) (params.height * scale);
				params.bottomMargin = (int) (params.bottomMargin * scale);
			}
			else
			{
				params.height = (int) (60 * ScaleWidth);
				params.bottomMargin = (int) (0 * ScaleWidth);
				_btnWrong.setPadding((int)(_btnWrong.getPaddingLeft()*ScaleWidth)
						, (int)(_btnWrong.getPaddingTop()*ScaleWidth)
						, (int)(_btnWrong.getPaddingRight()*ScaleWidth)
						, (int)(_btnWrong.getPaddingBottom()*ScaleWidth));
			}
			params.width = (int) (params.width * ScaleWidth);
			if (blnHorizontal) params.height*= ratio;
			_btnWrong.setLayoutParams(params);
			
			params = (android.widget.RelativeLayout.LayoutParams) _btnSkip
					.getLayoutParams();
			if (!blnWrongWidth) 
			{
				params.height = (int) (params.height * scale);
				params.bottomMargin = (int) (params.bottomMargin * scale);
			}
			else
			{
				params.height = (int) (60 * ScaleWidth);
				params.bottomMargin = (int) (0 * ScaleWidth);
			}
			params.width = (int) (params.width * ScaleWidth);
			if (blnHorizontal) params.height*= ratio;
			_btnSkip.setLayoutParams(params);
			
			params = (android.widget.RelativeLayout.LayoutParams) _btnView
					.getLayoutParams();
			if (!blnWrongWidth) 
			{
				params.height = (int) (params.height * scale);
				params.bottomMargin = (int) (params.bottomMargin * scale);
			}
			else
			{
				params.height = (int) (60 * ScaleWidth);
				params.bottomMargin = (int) (0 * ScaleWidth);
				_btnView.setPadding((int)(_btnView.getPaddingLeft()*ScaleWidth)
						, (int)(_btnView.getPaddingTop()*ScaleWidth)
						, (int)(_btnView.getPaddingRight()*ScaleWidth)
						, (int)(_btnView.getPaddingBottom()*ScaleWidth));
			}
			params.width = (int) (params.width * ScaleWidth);
			if (blnHorizontal) params.height*= ratio;
			_btnView.setLayoutParams(params);
			
			params = (android.widget.RelativeLayout.LayoutParams) _btnEdit
					.getLayoutParams();
			if (!blnWrongWidth) 
			{
				params.height = (int) (params.height * scale);
				params.bottomMargin = (int) (params.bottomMargin * scale);
			}
			else
			{
				params.height = (int) (60 * ScaleWidth);
				params.bottomMargin = (int) (0 * ScaleWidth);
			}
			params.width = (int) (params.width * ScaleWidth);
			if (blnHorizontal) params.height*= ratio;
			_btnEdit.setLayoutParams(params);

			params = (android.widget.RelativeLayout.LayoutParams) _txtStatus
					.getLayoutParams();
			if (!blnWrongWidth)
			{
				params.topMargin = (int) (params.topMargin * scale);
			}
			else
			{
				params.topMargin = (int) (0 * ScaleWidth);
			}
			_txtStatus.setLayoutParams(params);

			_btnRight.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_btnRight.getTextSize() * ScaleTextButtons));
			_btnSkip.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_btnSkip.getTextSize() * ScaleTextButtons));
			_btnView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_btnView.getTextSize() * ScaleTextButtons));
			_btnWrong.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_btnWrong.getTextSize() * ScaleTextButtons));
			_btnEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_btnEdit.getTextSize() * ScaleTextButtons));
			_txtStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_txtStatus.getTextSize() * ScaleTextButtonsOrig));
			
			//_main.ActionBarOriginalTextSize = 0;
			resizeActionbar(0);
			Runnable r = new resetLayoutTask(null);
			rFlashs.add(r);
			handler.post(r);

		}
	}

	private View findViewById(int id) {

		if (mainView == null) return null;
		return this.mainView.findViewById(id);
	}

	public void getVokabel(boolean showBeds, boolean LoadNext)
	{
		getVokabel(showBeds, LoadNext, false, false);
	}

	public void getVokabel(boolean showBeds, boolean LoadNext, boolean requestFocusEdWord)
	{
		getVokabel(showBeds, LoadNext, requestFocusEdWord, false);
	}


		public void getVokabel(final boolean showBeds, boolean LoadNext, boolean requestFocusEdWord, boolean DontPrompt) {
		try {
			if (_btnRight == null) return;
			EndEdit(DontPrompt);
			setBtnsEnabled(true);
			if (showBeds && _vok.getIndex()>=1) {
				_btnRight.setEnabled(true);
				_btnWrong.setEnabled(true);
				_btnEdit.setEnabled(true);
				_btnSkip.setEnabled(true);
				_btnView.setEnabled(true);
			} else {
				_btnRight.setEnabled(false);
				_btnWrong.setEnabled(false);
				if(_vok.getIndex()<1)
				{
					_btnEdit.setEnabled(false);
					_btnSkip.setEnabled(false);
					_btnView.setEnabled(false);
				}
			}
			if (LoadNext)
				_vok.setLernIndex((short) (_vok.getLernIndex() + 1));

			View v = findViewById(R.id.word);
			TextView t = (TextView) v;
			/*
			 * if (!_vok.getCardMode()) { Rect bounds = new Rect(); Paint
			 * textPaint = t.getPaint();
			 * textPaint.getTextBounds(_vok.getWort(),0,
			 * _vok.getWort().length(),bounds); if (t.getWidth() <
			 * bounds.width()){ //int lines = t.getLineCount(); t.setLines((2));
			 * 
			 * if (((float)bounds.width() / (float)t.getWidth()) > 2) {
			 * t.setLines(3); } else { t.setLines((2)); }
			 * 
			 * } else { t.setLines(1); } }
			 */
			assert t != null;
			t.setText(lib.getSpanableString(_vok.getWort()), TextView.BufferType.SPANNABLE);
			if (_vok.getSprache() == EnumSprachen.Hebrew
					|| _vok.getSprache() == EnumSprachen.Griechisch
					|| (_vok.getFontWort().getName() == "Cardo")) {
				t.setTypeface(_vok.TypefaceCardo);
				_txtedWord.setTypeface(_vok.TypefaceCardo);
			} else {
				t.setTypeface(Typeface.DEFAULT);
				_txtedWord.setTypeface(Typeface.DEFAULT);
			}
			t.scrollTo(0, 0);

			v = findViewById(R.id.Comment);
			t = (TextView) v;
			assert t != null;
			t.setText(lib.getSpanableString(_vok.getKommentar()),
					TextView.BufferType.SPANNABLE);
			if (_vok.getSprache() == EnumSprachen.Hebrew
					|| _vok.getSprache() == EnumSprachen.Griechisch
					|| (_vok.getFontKom().getName() == "Cardo")) {
				t.setTypeface(_vok.TypefaceCardo);
				_txtedKom.setTypeface(_vok.TypefaceCardo);
			} else {
				t.setTypeface(Typeface.DEFAULT);
				_txtedKom.setTypeface(Typeface.DEFAULT);
			}
			if (_isSmallDevice && libString.IsNullOrEmpty(t.getText().toString()))
			{
				t.setVisibility(View.GONE);
			}
			else
			{
				t.setVisibility(View.VISIBLE);
			}
			t.scrollTo(0, 0);


			v = findViewById(R.id.txtMeaning1);
			t = (TextView) v;
			assert t != null;
			if (!libString.IsNullOrEmpty(_vok.getBedeutung2())) {
				t.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			}
			t.setText((showBeds ? lib.getSpanableString(_vok.getBedeutung1()) : Vokabel.getComment(_vok
					.getBedeutung1())));
			if (_vok.getFontBed().getName() == "Cardo") {
				t.setTypeface(_vok.TypefaceCardo);
			} else {
				t.setTypeface(Typeface.DEFAULT);
			}
			t.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {

					if (hasFocus && _firstFocus) {
						hideKeyboard();
						_firstFocus = false;
					}
				}
			});
			t.scrollTo(0, 0);

			v = findViewById(R.id.txtMeaning2);
			t = (TextView) v;
			assert t != null;
			t.setText((showBeds ? _vok.getBedeutung2() : Vokabel.getComment(_vok
					.getBedeutung2())));
			if (_vok.getFontBed().getName() == "Cardo") {
				t.setTypeface(_vok.TypefaceCardo);
			} else {
				t.setTypeface(Typeface.DEFAULT);
			}
			if (libString.IsNullOrEmpty(_vok.getBedeutung2())
					|| _vok.getCardMode()) {
				t.setVisibility(View.GONE);
				_txtMeaning1.setImeOptions(EditorInfo.IME_ACTION_DONE);
			} else {
				t.setVisibility(View.VISIBLE);
				_txtMeaning1.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			}

			v = findViewById(R.id.txtMeaning3);
			t = (TextView) v;
			assert t != null;
			t.setText((showBeds ? _vok.getBedeutung3() : Vokabel.getComment(_vok
					.getBedeutung3())));
			if (_vok.getFontBed().getName() == "Cardo") {
				t.setTypeface(_vok.TypefaceCardo);
			} else {
				t.setTypeface(Typeface.DEFAULT);
			}
			if (libString.IsNullOrEmpty(_vok.getBedeutung3())
					|| _vok.getCardMode()) {
				t.setVisibility(View.GONE);
				_txtMeaning2.setImeOptions(EditorInfo.IME_ACTION_DONE);
			} else {
				t.setVisibility(View.VISIBLE);
				_txtMeaning2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
				_txtMeaning3.setImeOptions(EditorInfo.IME_ACTION_DONE);
			}
			lib.setBgEditText(_txtMeaning1, _MeaningBG);
			lib.setBgEditText(_txtMeaning2, _MeaningBG);
			lib.setBgEditText(_txtMeaning3, _MeaningBG);
			if (!_isSmallDevice && !requestFocusEdWord)
			{
				_txtMeaning1.requestFocus();
			}
			else
			{
				if (!requestFocusEdWord) _txtWord.requestFocus(); else _txtedWord.requestFocus();
			}
			SetActionBarTitle();
			_scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
			{
				
				@Override
				public void onGlobalLayout() {

					lib.removeLayoutListener(_scrollView.getViewTreeObserver(), this);
					hideKeyboard();
					if (showBeds)
					{
						_scrollView.scrollTo(0,_txtMeaning1.getTop());
					}
					else
					{
						_scrollView.fullScroll(View.FOCUS_UP);
					}
				}
			});
			
			
		} catch (Exception e) {

			lib.ShowException(_main, e);
		}

	}

	@Override
	public void RemoveCallback() {
		removeCallbacks();
	}

	@SuppressLint("NewApi")
	private class wfcListener implements OnWindowFocusChangeListener
	{

		@Override
		public void onWindowFocusChanged(boolean hasFocus) {

			if (hasFocus) {
				lib.removewfcListener(_scrollView.getViewTreeObserver(), this);
				hideKeyboard();
				_scrollView.fullScroll(View.FOCUS_UP);
	        }
		}
		
	}
	
	private int _lastIsWrongVokID;
	MovementMethod oldMeaning1MovementMethod;
	MovementMethod oldWordMovementMethod;
	MovementMethod oldedWordMovementMethod;
	OnTouchListenerScroll OnTouchListenerScrollWord;
	OnTouchListenerScroll OnTouchListenerScrolledWord;
	OnTouchListenerScroll OnTouchListenerScrollKom;
	OnTouchListenerScroll OnTouchListenerScrolledKom;
	OnTouchListenerScroll OnTouchListenerScrollMeaning1;
	@SuppressLint("ClickableViewAccessibility")
	private void InitControls() throws Exception {
		View v = findViewById(R.id.btnRight);
		Button b = (Button) v;
		_btnRight = b;
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (_lastIsWrongVokID == _vok.getIndex()) {
						lib.playSound(_main, Sounds.Beep);
						getVokabel(false, true);
					} else {

						int Zaehler = _vok.AntwortRichtig();
						lib.playSound(_main, Zaehler);

						getVokabel(false, false);
					}
					_lastIsWrongVokID = -1;

				} catch (Exception e) {

					lib.ShowException(_main, e);

				}

			}
		});
		v = findViewById(R.id.btnWrong);
		b = (Button) v;
		_btnWrong = b;
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					_vok.AntwortFalsch();
					lib.playSound(_main, _vok.getZaehler());
					_lastIsWrongVokID = _vok.getIndex();

					if (!_vok.getCardMode()) {
						setBtnsEnabled(false);
						flashwords();
						
					} else {
						getVokabel(false, false);
					}
				} catch (Exception e) {

					lib.ShowException(_main, e);
				}

			}
		});
		v = findViewById(R.id.btnSkip);
		b = (Button) v;
		_btnSkip = b;
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					_vok.SkipVokabel();
					getVokabel(false, false);
				} catch (Exception e) {

					lib.ShowException(_main, e);
				}

			}
		});
		v = findViewById(R.id.btnView);
		b = (Button) v;
		_btnView = b;
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
		try {
					getVokabel(true, false);

				} catch (Exception e) {

					lib.ShowException(_main, e);
				}

			}
		});

		v = findViewById(R.id.btnEdit);
		b = (Button) v;
		_btnEdit = b;
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					edit();
				} catch (Exception e) {

					lib.ShowException(_main, e);
				}

			}
		});

		_txtMeaning1 = (BorderedEditText) findViewById(R.id.txtMeaning1);
		//int bottom = (_txtMeaning1.getPaddingBottom() == 0 ? 5 : _txtMeaning1.getPaddingBottom());
		/*_txtMeaning1.setPadding(_txtMeaning1.getPaddingLeft()
				, 0
				, _txtMeaning1.getPaddingRight()
				, 0);
				*/
		OnTouchListenerScrollMeaning1 = new OnTouchListenerScroll(detectorMeaning1,this);
		ListenerMeaning1.l = OnTouchListenerScrollMeaning1;
		ListenerMeaning1.t = _txtMeaning1;
		_txtMeaning1.setOnTouchListener(OnTouchListenerScrollMeaning1);
		_txtMeaning1.setOnLongClickListener(textlongclicklistener);
		//_txtMeaning1.setBackgroundColor(Color.BLACK);
		_MeaningBG = _txtMeaning1.getBackground();
		//_txtMeaning1.setBackgroundResource(0);
		//_txtMeaning1.setOnTouchListener(OnTouchListenerRemoveCallbacks);
		
		_txtMeaning2 = (BorderedEditText) findViewById(R.id.txtMeaning2);
		_txtMeaning2.setOnLongClickListener(textlongclicklistener);
		//_txtMeaning2.setBackgroundResource(0);
		_txtMeaning2.setOnTouchListener(OnTouchListenerRemoveCallbacks);
		
		_txtMeaning3 = (BorderedEditText) findViewById(R.id.txtMeaning3);
		_txtMeaning3.setOnLongClickListener(textlongclicklistener);
		//_txtMeaning3.setBackgroundResource(0);
		_txtMeaning3.setOnTouchListener(OnTouchListenerRemoveCallbacks);
		
		_txtWord = (BorderedTextView) findViewById(R.id.word);
		_txtWord.setOnLongClickListener(textlongclicklistener);
		//_txtWord.setOnTouchListener(OnTouchListenerRemoveCallbacks);
		OnTouchListenerScrollWord = new OnTouchListenerScroll(detectorWord,this);
		ListenerWord.l = OnTouchListenerScrollWord;
		ListenerWord.t = _txtWord;
		_txtWord.setOnTouchListener(OnTouchListenerScrollWord);


		_txtedWord= (BorderedEditText) findViewById(R.id.edword);
		_txtedWord.setOnLongClickListener(textlongclicklistener);
		OnTouchListenerScrolledWord = new OnTouchListenerScroll(detectoredWord,this);
		ListeneredWord.l = OnTouchListenerScrolledWord;
		ListeneredWord.t = _txtedWord;
		_txtedWord.setOnTouchListener(OnTouchListenerScrolledWord);
		
		_txtedKom = (BorderedEditText) findViewById(R.id.edComment);
		_txtedKom.setOnLongClickListener(textlongclicklistener);
		OnTouchListenerScrolledKom = new OnTouchListenerScroll(detectoredKom,this);
		ListeneredKom.l = OnTouchListenerScrolledKom;
		ListeneredKom.t = _txtedKom;
		_txtedKom.setOnTouchListener(OnTouchListenerScrolledKom);


		_txtKom = (BorderedTextView) findViewById(R.id.Comment);
		_txtKom.setOnLongClickListener(textlongclicklistener);
		OnTouchListenerScrollKom = new OnTouchListenerScroll(detectorKom,this);
		ListenerKom.l = OnTouchListenerScrollKom;
		ListenerKom.t = _txtKom;
		_txtKom.setOnTouchListener(OnTouchListenerScrollKom);
		_txtKom.setMovementMethod(LinkMovementMethod.getInstance());
		
		_txtStatus = (BorderedTextView) findViewById(R.id.txtStatus);
		
		_scrollView = (ScrollView) findViewById(R.id.layoutMain);
		_scrollView.setOnTouchListener(OnTouchListenerRemoveCallbacks);
		
		setBtnsEnabled(false);
		setTextColors();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	OnTouchListener OnTouchListenerRemoveCallbacks = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			removeCallbacks();
			return false;
		}
	};



	ScrollGestureListener ListenerWord = new ScrollGestureListener(_txtWord, OnTouchListenerScrollWord);
	GestureDetector detectorWord = new GestureDetector(_main,ListenerWord);
	ScrollGestureListener ListeneredWord = new ScrollGestureListener(_txtedWord, OnTouchListenerScrolledWord);
	GestureDetector detectoredWord = new GestureDetector(_main,ListeneredWord);
	ScrollGestureListener ListenerMeaning1 = new ScrollGestureListener(_txtMeaning1, OnTouchListenerScrollMeaning1);
	GestureDetector detectorMeaning1 = new GestureDetector(_main,ListenerMeaning1);
	ScrollGestureListener ListenerKom = new ScrollGestureListener(_txtKom, OnTouchListenerScrollKom);
	GestureDetector detectorKom = new GestureDetector(_main,ListenerKom);
	ScrollGestureListener ListeneredKom = new ScrollGestureListener(_txtedKom, OnTouchListenerScrolledKom);
	GestureDetector detectoredKom = new GestureDetector(_main,ListeneredKom);


	OnLongClickListener textlongclicklistener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {

			TextView txt = (TextView)v;
			Intent intent = null;
			if (libString.IsNullOrEmpty(txt.getText().toString())
					|| _txtedWord.getVisibility()==View.VISIBLE)
			{
				return false;
			}
			try
			{
				if (_main.prefs.getBoolean("translate", true) == true)
				{
					intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
		            intent.setType("text/plain");
		            intent.setPackage("com.google.android.apps.translate");
				}
				if (intent != null && intent.resolveActivity(context.getPackageManager()) != null) 
				{ 
					intent.putExtra(Intent.EXTRA_TEXT, txt.getText().toString());
					_main.startActivity(intent);
				}
				else if (_main.prefs.getBoolean("fora", true) == true)
				{
					intent = new Intent("com.ngc.fora.action.LOOKUP");
				}
				if (intent != null && intent.resolveActivity(context.getPackageManager()) != null) 
				{ 
					// parameters are optional
					intent.putExtra("HEADWORD", txt.getText().toString());
					//intent.putExtra(EXTRA_DICID, 1);
					_main.startActivity(intent);
				}
				  else
				  {
					  	final String SEARCH_ACTION = "nghs.intent.action.SEARCH";
						final String EXTRA_QUERY   = "EXTRA_QUERY";
						//final String EXTRA_DICID   = "dicID";
						if (_main.prefs.getBoolean("nghs", true)) 
						{
							intent = new Intent(SEARCH_ACTION);
						}
						 
						if (intent != null && intent.resolveActivity(context.getPackageManager()) != null) 
						{ 
							// parameters are optional
							intent.putExtra(EXTRA_QUERY, txt.getText().toString());
							//intent.putExtra(EXTRA_DICID, 1);
							_main.startActivity(intent);
						}
						else
						{
						//_main.startSearch(txt.getText().toString(), true, null, true);
							intent = new Intent(Intent.ACTION_SEARCH);
							intent.putExtra(SearchManager.QUERY, txt.getText().toString());
							_main.startActivity(intent);
						}
				  }
			}
			catch(Exception e)
			{
			   e.printStackTrace();
			}
					
			return false;
		}
	};
	public void edit() throws Exception {
		if (_txtWord.getVisibility()==View.VISIBLE)
		{
			StartEdit();
		}
		else
		{
			EndEdit(false);
		}
	}
	void StartEdit() throws Exception
	{
		_txtWord.setVisibility(View.GONE);
		_txtKom.setVisibility(View.GONE);
		_txtedWord.setVisibility(View.VISIBLE);
		_txtedWord.setText(_txtWord.getText());
		_txtedWord.setTextSize(TypedValue.COMPLEX_UNIT_PX,_txtWord.getTextSize());
		View LayWord = findViewById(R.id.LayWord);
		RelativeLayout.LayoutParams params = 
				(RelativeLayout.LayoutParams) LayWord.getLayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		LayWord.setLayoutParams(params);
		_txtedKom.setVisibility(View.VISIBLE);
		_txtedKom.setText(_vok.getKommentar());
		_txtedKom.setTextSize(TypedValue.COMPLEX_UNIT_PX,_txtKom.getTextSize());
		_txtedWord.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		_txtedKom.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		_txtedWord.setSingleLine(false);
		_txtedWord.setMaxLines(3);
		_txtedKom.setSingleLine(false);
		_txtedKom.setMaxLines(3);
		if (!_vok.getCardMode())
		{
			_txtMeaning1.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			_txtMeaning2.setVisibility(View.VISIBLE);
			_txtMeaning2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			_txtMeaning3.setVisibility(View.VISIBLE);
			_txtMeaning3.setImeOptions(EditorInfo.IME_ACTION_DONE);
			_txtMeaning2.setText(_vok.getBedeutung2());
			_txtMeaning3.setText(_vok.getBedeutung3());
			lib.setBgEditText(_txtMeaning1,_MeaningBG);
			lib.setBgEditText(_txtMeaning2,_MeaningBG);
			lib.setBgEditText(_txtMeaning3,_MeaningBG);
			_txtMeaning1.setLines(1);
			_txtMeaning1.setSingleLine();
			_txtMeaning2.setLines(1);
			_txtMeaning2.setSingleLine();
			_txtMeaning3.setLines(1);
			_txtMeaning3.setSingleLine();
			
			
		}
		else
		{
			lib.setBgEditText(_txtMeaning1,_MeaningBG);
			_txtMeaning1.setImeOptions(EditorInfo.IME_ACTION_DONE);
			//_originalMovementmethod = _txtMeaning1.getMovementMethod();
			//_txtMeaning1.setAutoLinkMask(0);
			if (_originalMovementmethod!=null && _txtMeaning1.getMovementMethod() == LinkMovementMethod.getInstance())
			{
				_txtMeaning1.setMovementMethod(_originalMovementmethod);
			}
			//_txtMeaning1.requestFocus();
			//InputMethodManager Imn = (InputMethodManager) _main.getSystemService(Context.INPUT_METHOD_SERVICE);
			//Imn.showSoftInputFromInputMethod(_txtMeaning1.getWindowToken(), 0);
		}
		//_txtMeaning1.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		_txtMeaning1.setText(_vok.getBedeutung1());
		setBtnsEnabled(false);
		_btnEdit.setEnabled(true);
		_txtMeaning1.clearFocus();
		_txtedWord.clearFocus();
		//_txtedWord.requestFocusFromTouch();
		_txtedWord.requestFocus();
		mainView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						lib.removeLayoutListener(mainView.getViewTreeObserver(), this);
						_txtedWord.requestFocus();
					}
				});
	}
	
	boolean EndEdit(boolean dontPrompt) throws Exception
	{
		if (_txtedWord==null)return true;
		if (_txtedWord.getVisibility()== View.VISIBLE)
		{
			
			try
			{
				if ((!libString.IsNullOrEmpty(_txtedWord.getText().toString())
						&& !libString.IsNullOrEmpty(_txtMeaning1.getText().toString())))
				{
					_vok.setWort(_txtedWord.getText().toString(),dontPrompt);
					_vok.setKommentar(_txtedKom.getText().toString());
					_vok.setBedeutung1(_txtMeaning1.getText().toString(),dontPrompt);
					_vok.setBedeutung2(_txtMeaning2.getText().toString());
					_vok.setBedeutung3(_txtMeaning3.getText().toString());
				}
				else
				{
					if (dontPrompt || lib.ShowMessageYesNo(context,context.getString(R.string.WordOrMeaningMissing), "" )==yesnoundefined.yes)
					{
						_vok.DeleteVokabel();
					}
					else
					{
						try
						{
							_vok.setWort(_txtedWord.getText().toString(), false);
							_vok.setKommentar(_txtedKom.getText().toString());
							_vok.setBedeutung1(_txtMeaning1.getText().toString(), false);
							_vok.setBedeutung2(_txtMeaning2.getText().toString());
							_vok.setBedeutung3(_txtMeaning3.getText().toString());
						}
						catch (Exception eex)
						{
							Log.e("EndEdit", eex.getMessage(), eex);
							return false;
						}
					}
				}
					
				EndEdit2();
			}
			catch (Exception ex)
			{
				lib.ShowMessage(context, ex.getMessage(), null);
				return false;
			}

		}
		return true;
	}
	
	void EndEdit2() throws Exception
	{
		if (_txtedWord==null)return;
		if (_txtedWord.getVisibility()== View.VISIBLE)
		{
			_txtWord.setVisibility(View.VISIBLE);
			_txtKom.setVisibility(View.VISIBLE);
			_txtedWord.setVisibility(View.GONE);
			_txtWord.setText(lib.getSpanableString(_txtedWord.getText().toString()));
			View LayWord = findViewById(R.id.LayWord);
			RelativeLayout.LayoutParams params = 
					(RelativeLayout.LayoutParams) LayWord.getLayoutParams();
			params.width = LayoutParams.WRAP_CONTENT;
			LayWord.setLayoutParams(params);
			_txtedKom.setVisibility(View.GONE);
			_txtKom.setText(lib.getSpanableString(_txtedKom.getText().toString()));
			_txtedWord.setImeOptions(EditorInfo.IME_ACTION_NONE);
			_txtedKom.setImeOptions(EditorInfo.IME_ACTION_NONE);
			_txtMeaning1.setImeOptions(EditorInfo.IME_ACTION_DONE);
			_txtMeaning2.setImeOptions(EditorInfo.IME_ACTION_DONE);
			_txtMeaning3.setImeOptions(EditorInfo.IME_ACTION_DONE);
			lib.setBgEditText(_txtMeaning1, _MeaningBG);
			lib.setBgEditText(_txtMeaning2, _MeaningBG);
			lib.setBgEditText(_txtMeaning3, _MeaningBG);
			/*_txtMeaning1.setBackgroundResource(0);
			_txtMeaning2.setBackgroundResource(0);
			_txtMeaning3.setBackgroundResource(0);*/
			if (!_vok.getCardMode())
			{
				_txtMeaning1.setLines(1);
				_txtMeaning1.setSingleLine();
				_txtMeaning2.setLines(1);
				_txtMeaning2.setSingleLine();
				_txtMeaning2.setVisibility(View.VISIBLE);
				_txtMeaning3.setLines(1);
				_txtMeaning3.setSingleLine();
				_txtMeaning3.setVisibility(View.VISIBLE);
			}
			else
			{
				_originalMovementmethod = _txtMeaning1.getMovementMethod();
				//_txtMeaning1.setAutoLinkMask(Linkify.ALL);
				_txtMeaning1.setMovementMethod(LinkMovementMethod.getInstance());
				//_txtMeaning1.setInputType(InputType.TYPE_NULL);
				//_txtMeaning1.requestFocus();
				//InputMethodManager Imn = (InputMethodManager) _main.getSystemService(Context.INPUT_METHOD_SERVICE);
				//Imn.hideSoftInputFromInputMethod(_txtMeaning1.getWindowToken(), 0);
			}
		}
		getVokabel(false, false);
	}

	private void InitMeanings() {
		_txtMeaning1.setOnEditorActionListener(EditorActionListener);
		_txtMeaning2.setOnEditorActionListener(EditorActionListener);
		_txtMeaning3.setOnEditorActionListener(EditorActionListener);
	}

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = _main.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = 
					(InputMethodManager) _main.getSystemService
					(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	private OnEditorActionListener EditorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

			if (event == null) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					hideKeyboard();
					String[] Antworten;
					org.de.jmg.learn.vok.Vokabel.Bewertung Bew;
					String meaning1 = _txtMeaning1.getText().toString();
					String meaning2 = _txtMeaning2.getVisibility() == View.VISIBLE ? _txtMeaning2
							.getText().toString() : null;
					String meaning3 = _txtMeaning3.getVisibility() == View.VISIBLE ? _txtMeaning3
							.getText().toString() : null;
					if (_txtWord.getVisibility()==View.VISIBLE)
					{
						Antworten = new String[] { meaning1, meaning2, meaning3 };
						try {
							Bew = _vok.CheckAntwort(Antworten);
							if (Bew == Bewertung.AllesRichtig) {
								lib.ShowToast(_main,
										getString(R.string.AnswerCorrect));
								_btnRight.performClick();
							} else if (Bew == Bewertung.AllesFalsch) {
								try {
									_vok.AntwortFalsch();
									lib.playSound(_main,
											_vok.getZaehler());
									_lastIsWrongVokID = _vok.getIndex();
									getVokabel(true, false);
									if (!_vok.getCardMode()) {
										setBtnsEnabled(false);
										flashwords();
										
									}
								} catch (Exception e) {

									lib.ShowException(_main, e);
								}
							} else if (Bew == Bewertung.aehnlich) {
								lib.ShowMessage(_main,
										getString(R.string.MeaningSimilar),"");
							} else if (Bew == Bewertung.TeilweiseRichtig) {
								lib.ShowMessage(_main,
										getString(R.string.MeaningPartiallyCorrect),"");
							} else if (Bew == Bewertung.enthalten) {
								lib.ShowMessage(_main,
										getString(R.string.MeaningIsSubstring),"");
							} else if (Bew == Bewertung.AehnlichEnthalten) {
								lib.ShowMessage(
										_main,
										getString(R.string.MeaningIsSubstringSimilar),"");
							} else if (Bew == Bewertung.TeilweiseRichtigAehnlich) {
								lib.ShowMessage(
										_main,
										getString(R.string.MeaningIsPartiallyCorrectSimilar),"");
							} else if (Bew == Bewertung.TeilweiseRichtigAehnlichEnthalten) {
								lib.ShowMessage(
										_main,
										getString(R.string.MeaningIsPartiallyCorrectSimilarSubstring),"");
							} else if (Bew == Bewertung.TeilweiseRichtigEnthalten) {
								lib.ShowMessage(
										_main,
										getString(R.string.MeaningIsPartiallyCorrectSubstring),"");
							}
	
						} catch (Exception e) {

							lib.ShowException(_main, e);
						}
					}
					else
					{
						try {
							EndEdit(false);
						} catch (Exception e) {

							lib.ShowException(_main, e);
						}
						
					}
					return true;
				}
				// Capture soft enters in a singleLine EditText that is the last
				// EditText.
				else if (actionId == EditorInfo.IME_ACTION_NEXT)
					return false;
				// Capture soft enters in other singleLine EditTexts

				else
					return false; // Let system handle all other null KeyEvents

			} else if (actionId == EditorInfo.IME_NULL) {
				// Capture most soft enters in multi-line EditTexts and all hard
				// enters.
				// They supply a zero actionId and a valid KeyEvent rather than
				// a non-zero actionId and a null event like the previous cases.
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					return false;
				// We capture the event when key is first pressed.
				else
					return false; // We consume the event when the key is
									// released.
			} else
				return false;
		}
	};

	void setTextColors() {
		libLearn.gStatus = "setTextColors";
		if (_txtMeaning1==null)return;
		_txtMeaning1.setTextColor(_main.Colors.get(ColorItems.meaning).ColorValue);
		_txtMeaning2.setTextColor(_main.Colors.get(ColorItems.meaning).ColorValue);
		_txtMeaning3.setTextColor(_main.Colors.get(ColorItems.meaning).ColorValue);
		_txtWord.setTextColor(_main.Colors.get(ColorItems.word).ColorValue);
		_txtKom.setTextColor(_main.Colors.get(ColorItems.comment).ColorValue);
		/*
		 * _txtMeaning1.setBackgroundColor(_main.Colors.get(ColorItems.background).
		 * ColorValue);
		 * _txtMeaning2.setBackgroundColor(_main.Colors.get(ColorItems.background
		 * ).ColorValue);
		 * _txtMeaning3.setBackgroundColor(_main.Colors.get(ColorItems.background
		 * ).ColorValue);
		 * _txtWord.setBackgroundColor(_main.Colors.get(ColorItems.background
		 * ).ColorValue);
		 * _txtKom.setBackgroundColor(_main.Colors.get(ColorItems.background
		 * ).ColorValue);
		 */
		findViewById(R.id.layoutMain).setBackgroundColor(
				_main.Colors.get(ColorItems.background).ColorValue);
	}

	public void setBtnsEnabled(boolean enable) {
		libLearn.gStatus = "setBtnsEnabled";
		_btnEdit.setEnabled(enable);
		_btnRight.setEnabled(enable);
		_btnSkip.setEnabled(enable);
		_btnView.setEnabled(enable);
		_btnWrong.setEnabled(enable);

	}
	MovementMethod _originalMovementmethod = null;
	public void SetViewsToVokMode()
	{
		if (_mBackgroundBack != null) _MeaningBG = _mBackgroundBack;
		// _txtWord.setMaxLines(3);
		// _txtWord.setLines(1);
		_txtWord.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(float) (60 * scale));
		_txtWord.setHorizontallyScrolling(false);
		
		// _txtKom.setMaxLines(3);
		// _txtKom.setLines(2);
		_txtKom.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(float) (35 * scale));
		_txtKom.setHorizontallyScrolling(false);

		_txtMeaning1.setLines(1);
		_txtMeaning1.setSingleLine();
		_txtMeaning1.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(float) (40 * scale));
		_txtMeaning1.setMaxLines(3);
		_txtMeaning1.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		_txtMeaning1.setHorizontallyScrolling(false);
		//_txtMeaning1.setAutoLinkMask(0);
		if (_originalMovementmethod!=null && _txtMeaning1.getMovementMethod() == LinkMovementMethod.getInstance())
		{
			_txtMeaning1.setMovementMethod(_originalMovementmethod);
		}
		//_txtMeaning1.setVerticalScrollBarEnabled(false);
		
		//_txtMeaning1.requestFocus();
		//InputMethodManager Imn = (InputMethodManager) _main.getSystemService(Context.INPUT_METHOD_SERVICE);
		//Imn.showSoftInputFromInputMethod(_txtMeaning1.getWindowToken(), 0);
	
		//_txtMeaning1.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

		_txtMeaning2.setVisibility(View.VISIBLE);
		_txtMeaning2.setLines(1);
		_txtMeaning2.setSingleLine();
		_txtMeaning2.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(float) (40 * scale));
		_txtMeaning2.setMaxLines(3);
		_txtMeaning2.setHorizontallyScrolling(false);

		_txtMeaning3.setVisibility(View.VISIBLE);
		_txtMeaning3.setLines(1);
		_txtMeaning3.setSingleLine();
		_txtMeaning3.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(float) (40 * scale));
		_txtMeaning3.setMaxLines(3);
		_txtMeaning3.setHorizontallyScrolling(false);

	}
	private Drawable _mBackgroundBack = null;
	public void SetViewsToCardmode()
	{
		// _txtWord.setMaxLines(3);
		// _txtWord.setLines(2);
		_txtWord.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(float) (40 * scale));

		// _txtKom.setMaxLines(3);
		// _txtKom.setLines(2);
		_txtKom.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(float) (30 * scale));

		_txtMeaning1.setSingleLine(false);
		_txtMeaning1.setMaxLines(1000);
		_txtMeaning1.setLines(16);
		//_txtMeaning1.setMinLines(2);
		_txtMeaning1.setGravity(Gravity.TOP|Gravity.LEFT);
		_txtMeaning1.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(float) (25 * scale));
		//_txtMeaning1.requestFocus();
		//InputMethodManager Imn = (InputMethodManager) _main.getSystemService(Context.INPUT_METHOD_SERVICE);
		//Imn.hideSoftInputFromInputMethod(_txtMeaning1.getWindowToken(), 0);
		_originalMovementmethod = _txtMeaning1.getMovementMethod();
		//_txtMeaning1.setAutoLinkMask(Linkify.ALL);
		_txtMeaning1.setMovementMethod(LinkMovementMethod.getInstance());
		//_txtMeaning1.setVerticalScrollBarEnabled(true);
		//_txtMeaning1.setInputType(InputType.TYPE_NULL);
		// _txtMeaning1.setImeOptions(EditorInfo.IME_NULL);
		// _txtMeaning1.setImeActionLabel(null, KeyEvent.KEYCODE_ENTER);
		// _txtMeaning1.setImeActionLabel("Custom text",
		// KeyEvent.KEYCODE_ENTER);
		_txtMeaning2.setVisibility(View.GONE);
		_txtMeaning3.setVisibility(View.GONE);
		if (_MeaningBG != null) _mBackgroundBack = _MeaningBG;
		_MeaningBG = null;
		//if (_mBackgroundBack!=null) _MeaningBG = _mBackgroundBack;
	
	}


	/*
	 * private Runnable runnableGetVok = new Runnable() {
	 * 
	 * @Override public void run() { // do what you need to do
	 * getVokabel(false,true); } };
	 */
	private Runnable runnableFalse = new Runnable() {
		@Override
		public void run() {
			/* do what you need to do */
			getVokabel(false, false);
		}
	};

	/*
	 * private void runFlashWords() { new Thread(new Runnable() {
	 * 
	 * @Override public void run() {  try {
	 * flashwords(); } catch (Exception e) {
	 * e.printStackTrace(); } } }).start(); }
	 */
	private ArrayList<Runnable> rFlashs = new ArrayList<Runnable>();
	private void flashwords() throws Exception {
		Runnable r;
		final RelativeLayout layout = (RelativeLayout) findViewById(R.id.layoutMainParent);
		layout.setBackgroundColor(_main.Colors.get(ColorItems.background_wrong).ColorValue);
		final ScrollView layoutScroll = (ScrollView) findViewById(R.id.layoutMain);
		layoutScroll.setBackgroundColor(_main.Colors.get(ColorItems.background_wrong).ColorValue);
		final RelativeLayout layoutButtons = (RelativeLayout) findViewById(R.id.layoutButtons);
		layoutButtons.setVisibility(View.GONE);
		View tb = _main.findViewById(R.id.action_bar);
		tb.setVisibility(View.GONE);

		_txtMeaning1.setBackgroundResource(0);
		_txtMeaning2.setBackgroundResource(0);
		_txtMeaning3.setBackgroundResource(0);
		
		if (_isSmallDevice)
		{
			_txtKom.setVisibility(View.GONE);
		}
		_txtWord.requestFocus();
		long delay = 0;
		for (int i = 0; i < _main.PaukRepetitions; i++) {
			// _txtWord.setBackgroundResource(R.layout.roundedbox);
			r = new showWordBordersTask();
			rFlashs.add(r);
			handler.postDelayed(r, delay);
			delay += _main.DisplayDurationWord * 1000;
			r = new hideWordBordersTask();
			rFlashs.add(r);
			handler.postDelayed(r, delay);
			BorderedEditText Beds[] = { _txtMeaning1, _txtMeaning2,
					_txtMeaning3 };
			for (int ii = 0; ii < _vok.getAnzBed(); ii++) {
				if (!libString.IsNullOrEmpty(_vok.getBedeutungen()[ii]))
				{
					r = new showBedBordersTask(Beds[ii]);
					rFlashs.add(r);
					handler.postDelayed(r, delay);
					delay += _main.DisplayDurationBed * 1000;
					r = new hideBedBordersTask(Beds[ii]);
					rFlashs.add(r);
					handler.postDelayed(r, delay);
				}
			}

		}
		r = new resetLayoutTask(layout);
		rFlashs.add(r);
		handler.postDelayed(r, delay);
		delay += 1000;

	}
	
	

	private class resetLayoutTask implements Runnable {
		public View view;

		public resetLayoutTask(View layout) {

			if (layout == null) layout = (RelativeLayout) findViewById(R.id.layoutMainParent);
			this.view = layout;
		}

		public void run() {
			if (view != null) {
				view.setBackgroundColor(_main.Colors.get(ColorItems.background).ColorValue);
				if (_isSmallDevice)
				{
					_txtKom.setVisibility(View.VISIBLE);
				}
				final RelativeLayout layoutButtons = (RelativeLayout) findViewById(R.id.layoutButtons);
				layoutButtons.setVisibility(View.VISIBLE);
				View tb = _main.findViewById(R.id.action_bar);
				tb.setVisibility(View.VISIBLE);
				final ScrollView layoutScroll = (ScrollView) findViewById(R.id.layoutMain);
				layoutScroll.setBackgroundColor(_main.Colors.get(ColorItems.background).ColorValue);
				runnableFalse.run();
				
			}
			rFlashs.clear();
		}
	}

	private class showWordBordersTask implements Runnable {
		public void run() {
			try {
				lib.playSound(_main, org.de.jmg.lib.lib.Sounds.Beep);
			} catch (Exception e) {

				e.printStackTrace();
			}
			showWordBorders();
		}
	}

	private class hideWordBordersTask implements Runnable {

		public void run() {

			hideWordBorders();
		}

	}

	private class showBedBordersTask implements Runnable {
		public BorderedEditText Bed;

		public showBedBordersTask(BorderedEditText Bed) {

			this.Bed = Bed;
		}

		public void run() {

			// Bed.setPadding(5, 5, 5, 5);
			Bed.setShowBorders(true,
					_main.Colors.get(ColorItems.box_meaning).ColorValue);
			try {
				lib.playSound(_main, org.de.jmg.lib.lib.Sounds.Beep);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	private class hideBedBordersTask implements Runnable {
		public BorderedEditText Bed;

		public hideBedBordersTask(BorderedEditText Bed) {

			this.Bed = Bed;
		}

		@Override
		public void run() {

			// Bed.setPadding(0, 0, 0, 0);
			Bed.setShowBorders(false,
					_main.Colors.get(ColorItems.background).ColorValue);
		}

	}

	/*
	 * private class CancelTimerTask extends TimerTask { public Timer T; public
	 * CancelTimerTask(Timer T) {  this.T
	 * = T; }
	 * 
	 * @Override public void run() {
	 * T.cancel(); }
	 * 
	 * }
	 */
	private void showWordBorders() {

		// _txtWord.setPadding(5, 5, 5, 5);
		_txtWord.setShowBorders(true,
				_main.Colors.get(ColorItems.box_word).ColorValue);
	}

	private void hideWordBorders() {

		// _txtWord.setPadding(0, 0, 0, 0);
		_txtWord.setShowBorders(false,
				_main.Colors.get(ColorItems.background).ColorValue);
	}

	public void SetActionBarTitle() throws Exception {
		if (mainView == null) return;
		if (_vok.getGesamtzahl() > 0) {
			String FName = "";
			if (! libString.IsNullOrEmpty(_vok.getFileName()))
			{
				FName = new File(_vok.getFileName()).getName();
			}
			else if (_vok.getURI()!=null)
			{
				String path = lib.dumpUriMetaData(_main, _vok.getURI());
				if(path.contains(":")) path = path.split(":")[0];
				int li=path.lastIndexOf("/");
				if (li>-1)
				{
					FName = path.substring(path.lastIndexOf("/"));
				}
				else
				{
					FName = "/" + path;
				}
			}
			else if (! libString.IsNullOrEmpty(_vok.getURIName()))
			{
				FName = _vok.getURIName();
			}
			if (FName.length()>15 && _isSmallDevice)
			{
				FName=FName.substring(0,15);
			}
			else if (FName.length()>30)
			{
				FName=FName.substring(0,30);
			}
			String title = "" + FName
					+ " " + getString(R.string.number) + ": " + _vok.getIndex()
					+ " " + getString(R.string.counter) + ": "
					+ _vok.getZaehler();
			String Right = " " + _vok.AnzRichtig;
			String Wrong = " " + _vok.AnzFalsch;
			SpannableString spnTitle = new SpannableString(title);
			SpannableString spnRight = new SpannableString(Right);
			SpannableString spnWrong = new SpannableString(Wrong);
			spnRight.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
					spnRight.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
			spnWrong.setSpan(new ForegroundColorSpan(Color.RED), 0,
					spnWrong.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
			TextView txtStatus = (TextView)(findViewById(R.id.txtStatus)); 
			if (txtStatus != null) txtStatus.setText(TextUtils.concat(spnTitle, spnRight, spnWrong));
			//getSupportActionBar().setTitle(
			//		TextUtils.concat(spnTitle, spnRight, spnWrong));

		} else {
			/*
			 * String title = "Learn " + "empty._vok" + " " +
			 * getString(R.string.number) + ": " + _vok.getIndex() + " " +
			 * getString(R.string.counter) + ": " + _vok.getZaehler(); String
			 * Right = " " + _vok.AnzRichtig; String Wrong = " " + _vok.AnzFalsch;
			 * SpannableString spnTitle = new SpannableString(title);
			 * SpannableString spnRight = new SpannableString(Right);
			 * SpannableString spnWrong = new SpannableString(Wrong);
			 * spnRight.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
			 * spnRight.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
			 * spnWrong.setSpan(new ForegroundColorSpan(Color.RED), 0,
			 * spnWrong.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
			 * 
			 * getSupportActionBar().setTitle( TextUtils.concat(spnTitle,
			 * spnRight, spnWrong));
			 */
		}
		resizeActionbar(0);
	}
	
	
	int _OriginalWidth = 0;
	public void resizeActionbar(final int width) {
		/*
		View tb = this.findViewById(R.id.action_bar);
		Paint p = new Paint();
		int SizeOther = 0;
		if (tb != null) {
			if (width == 0)
				width = tb.getWidth();
			if (width > 0) {
				ViewGroup g = (ViewGroup) tb;
				for (int i = 0; i < g.getChildCount(); i++) {
					View v = g.getChildAt(i);
					if (!(v instanceof TextView)) {
						SizeOther += v.getWidth();
					}
				}
				if (SizeOther == 0) SizeOther=lib.dpToPx(50);
				for (int i = 0; i < g.getChildCount(); i++) {
					View v = g.getChildAt(i);
					if (v instanceof TextView) {
						TextView t = (TextView) v;
						if (_main.ActionBarOriginalTextSize[i] == 0 )
						{
							_main.ActionBarOriginalTextSize[i] = t.getTextSize();
						}
						else
						{
							t.setTextSize(TypedValue.COMPLEX_UNIT_PX,_main.ActionBarOriginalTextSize[i]);
						}
						if (t.getText() instanceof SpannedString) {
							p.setTextSize(t.getTextSize());
							SpannedString s = (SpannedString) t.getText();
							width = width - SizeOther - lib.dpToPx(50);
							float measuredWidth = p.measureText(s.toString());
							if (measuredWidth > width)
							{
								float scaleA = (float)width / (float)measuredWidth;
								if (scaleA < .5f) scaleA = .5f;
								
								t.setTextSize(
										TypedValue.COMPLEX_UNIT_PX,
										(float) (t.getTextSize() * (scaleA)));
							}
							
						}
					}
				}

			}

		}
		*/
		if (mainView == null) return;
		TextView t = _txtStatus;
		if (_main.ActionBarOriginalTextSize==0)
		{
			_main.ActionBarOriginalTextSize = t.getTextSize();
			SetTxtStatusSize(width);
		}
		else if (t.getTextSize() != _main.ActionBarOriginalTextSize)
		{
			t.setTextSize(TypedValue.COMPLEX_UNIT_PX, _main.ActionBarOriginalTextSize);
			mainView.getViewTreeObserver().addOnGlobalLayoutListener(
					new ViewTreeObserver.OnGlobalLayoutListener() {

						@Override
						public void onGlobalLayout() {
							// Ensure you call it only once :
							lib.removeLayoutListener(mainView.getViewTreeObserver(), this);
							// Here you can get the size :)
							SetTxtStatusSize(width);
							//lib.ShowToast(SettingsActivity.this, "Resize End");
						}
					});
		}
		else
		{
			SetTxtStatusSize(width);
		}

	}

	void SetTxtStatusSize(int width)
	{
		if (width == 0)	width = mainView.getWidth();
		if (width == 0 && _OriginalWidth==0) return;
		if (width == 0) width = _OriginalWidth;
		_OriginalWidth = width;
		TextView t = _txtStatus;
		Paint p = new Paint();
		if (t.getText() instanceof SpannedString) {
			p.setTextSize(t.getTextSize());
			SpannedString s = (SpannedString) t.getText();
			width = width  - width/(_isSmallDevice?4:5);
			float measuredWidth = p.measureText(s.toString());
			if (measuredWidth != width)
			{
				float scaleA = (float)width / (float)measuredWidth;
				if(libString.IsNullOrEmpty(_vok.getFileName())) scaleA*=.75f;
				if (scaleA < .5f) scaleA = .5f;
				if (scaleA>2.0f) scaleA = 2.0f;
				float size = t.getTextSize();
				t.setTextSize(TypedValue.COMPLEX_UNIT_PX,size * scaleA);
			}

		}
	}
	
	



}
