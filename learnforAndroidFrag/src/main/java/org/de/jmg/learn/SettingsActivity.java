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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.EditText;

import br.com.thinkti.android.filechooser.AdvFileChooser;
import br.com.thinkti.android.filechooser.FileChooser;

import org.de.jmg.learn.R;
import org.de.jmg.lib.AbstractScaledArrayAdapter;
import org.de.jmg.lib.ColorsArrayAdapter;
import org.de.jmg.lib.DisplayLocale;
import org.de.jmg.lib.ScaledArrayAdapter;
import org.de.jmg.lib.SoundSetting;
import org.de.jmg.lib.SoundsArrayAdapter;
import org.de.jmg.lib.lib;
import org.de.jmg.lib.lib.libString;
import org.de.jmg.lib.lib.yesnoundefined;
import org.de.jmg.lib.ColorSetting;
import yuku.ambilwarna.*;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;


public class SettingsActivity extends Fragment 
{
	
	public static final int FILE_CHOOSERSOUND = 0x42FA;
	public static final int FILE_CHOOSERDATADIR = 0x42FB;
	public final static int fragID = 3;
	public Spinner spnAbfragebereich;
	public Spinner spnASCII;
	public Spinner spnStep;
	public Spinner spnDisplayDurationWord;
	public Spinner spnDisplayDurationBed;
	public Spinner spnPaukRepetitions;
	public Spinner spnProbabilityFactor;
	public Spinner spnLanguages;
	public Spinner spnRestartInterval;
	public Spinner spnLangWord;
	public Spinner spnLangMeaning;
	public org.de.jmg.lib.NoClickSpinner spnColors;
	public org.de.jmg.lib.NoClickSpinner spnSounds;
	public Button btnColors;
	public Button btnResetDefaultVok;
	public Button btnResetGooglePlay;
	public CheckBox chkRandom;
	public CheckBox chkAskAll;
	public CheckBox chkSound;
	public CheckBox chkDocumentProvider;
	public CheckBox chkDontShowPersistableURIMessage;
	public CheckBox chkFora;
	public CheckBox chkNGHS;
	public CheckBox chkTranslate;
	public EditText edDataDir;
	public CheckBox chkAlwaysStartExternalProgram;
	public CheckBox chkTextToSpeech;
	public ColorsArrayAdapter Colors;
	public SoundsArrayAdapter Sounds;
	public SharedPreferences prefs;
	public ScaledArrayAdapter<DisplayLocale> adapterLangMeaning;
	public ScaledArrayAdapter<DisplayLocale> adapterLangWord;
	private View mainView;
	private Intent intent;
	private boolean blnLayouted = false;
	private int langinitialized = 0;

	MainActivity _main;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		langinitialized = 0;
		_main = (MainActivity) getActivity();	
		SettingsView = null;
		_blnInitialized = false;
		if (_main.fPA!=null && _main.fPA.fragSettings==null)
		{
			_main.fPA.fragSettings=this;
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) 
	{
		/*		
		{
			_main.RemoveFragSettings();
		}
		*/
		super.onSaveInstanceState(outState);
		try {
			saveResultsAndFinish(true);
		} catch (Exception e) {

			lib.ShowException(_main, e);
		}
	}

	View SettingsView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater,container,savedInstanceState);
		langinitialized = 0;
		if (lib.NookSimpleTouch())
		{
			if (blnLayouted)
			{
				//SettingsView = inflater.inflate(R.layout.activity_settings_nook, container,false);
			}
			else
			{
				blnLayouted = true;
			}
			SettingsView = inflater.inflate(R.layout.activity_settings_nook, container,false);
			
		}
		else if (_main.isSmallDevice)
		{
			SettingsView = inflater.inflate(R.layout.activity_settings_wear, container,false);
			
		}
		else
		{
			SettingsView = inflater.inflate(R.layout.activity_settings, container,false);
		}
		_blnInitialized=false;
		mainView = SettingsView;
		if (_main == null) _main=(MainActivity)getActivity();
		if (_main!=null)
		{
			_Intent=_main.getSettingsIntent();
			intent = new Intent();
		}
		try {
			init(savedInstanceState != null);
		} catch (Exception e) {
			lib.ShowException(_main,e);
		}
		//langinitialized = true;
		return SettingsView;
	}
	
	@Override
	public void onDestroyView ()
	{
		super.onDestroyView();
		SettingsView = null;
	}
	

	Intent _Intent;
	boolean _blnInitialized = false;	
	public void init(Intent mainintent, MainActivity main) {

		
		_Intent = mainintent;
		_main = main;
	}
	
	public void init(boolean blnRestart) throws Exception
	{
		if (_Intent == null || _main == null || SettingsView == null || _blnInitialized)
		{
			return;
		}
		try
		{
			//lib.ShowToast(_main, "Settings Start");
			
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.layoutSettings); // id fetch from xml
			ShapeDrawable rectShapeDrawable = new ShapeDrawable(); // pre defined class
			int pxPadding = lib.dpToPx(10);
			rectShapeDrawable.setPadding(pxPadding, pxPadding, pxPadding, pxPadding * ((lib.NookSimpleTouch()) ? 2 : 1));
			Paint paint = rectShapeDrawable.getPaint();
			paint.setColor(Color.BLACK);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(5); // you can change the value of 5
			lib.setBg(layout, rectShapeDrawable);
			
			mainView = _main.findViewById(Window.ID_ANDROID_CONTENT);
			//Thread.setDefaultUncaughtExceptionHandler(ErrorHandler);
			prefs = _main.getPreferences(Context.MODE_PRIVATE);
			
					
			TextView txtSettings = (TextView) findViewById(R.id.txtSettings);
			SpannableString Settings = new SpannableString(txtSettings.getText());
			Settings.setSpan(new UnderlineSpan(), 0, Settings.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			txtSettings.setText(Settings);
			initCheckBoxes();
			initSpinners(blnRestart);
			initButtons();
			initHelp();
			edDataDir = (EditText) findViewById(R.id.edDataDir);
			edDataDir.setSingleLine(true);
			edDataDir.setText(_main.JMGDataDirectory);
			edDataDir.setImeOptions(EditorInfo.IME_ACTION_DONE);
			edDataDir.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if(actionId == EditorInfo.IME_ACTION_DONE)
					{
						String strDataDir = edDataDir.getText().toString();
						File fileSelected = new File(strDataDir);
						if (fileSelected.isDirectory() && fileSelected.exists())
						{
							_main.setJMGDataDirectory(fileSelected.getPath());
							edDataDir.setText(_main.JMGDataDirectory);
							Editor editor = prefs.edit();
							editor.putString("JMGDataDirectory", fileSelected.getPath());
							editor.commit();
						}
					}
					return true;
				}
			});
			edDataDir.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					ShowDataDirDialog();
					return true;
				}
			});
			if (!(lib.NookSimpleTouch()) && !_main.isSmallDevice)
			{
				SettingsView.getViewTreeObserver().addOnGlobalLayoutListener
				(
						new ViewTreeObserver.OnGlobalLayoutListener() 
						{

							@Override
							public void onGlobalLayout() 
							{
								// Ensure you call it only once :
								lib.removeLayoutListener(SettingsView.getViewTreeObserver(), this);
								
								// Here you can get the size :)
								resize(0);
								//lib.ShowToast(_main, "Resize End");
							}
						}
				);

			}
			else
			{
				//resize(1.8f);
				mScale = 1.0f;
			}
			_blnInitialized = true;
		}
		catch (Exception ex)
		{
			lib.ShowException(_main, ex);
		}
		
	}

	

	private View findViewById(int id) {

		return SettingsView.findViewById(id);
	}




	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.mnuResize && !lib.NookSimpleTouch()) resize(0);
		return super.onOptionsItemSelected(item);
	}
	
	private void initCheckBoxes() {
		chkRandom = (CheckBox) findViewById(R.id.chkRandom);
		chkAskAll = (CheckBox) findViewById(R.id.chkAskAll);
		chkSound = (CheckBox) findViewById(R.id.chkSound);
		chkDocumentProvider = (CheckBox) findViewById(R.id.chkDocumentProvider);
		chkDontShowPersistableURIMessage = (CheckBox) findViewById(R.id.chkDontShowPersistableURIMessage);
		chkAlwaysStartExternalProgram = (CheckBox) findViewById(R.id.chkAlwaysStartExternalProgram);
		chkNGHS = (CheckBox) findViewById(R.id.chkNGHS);
		chkFora = (CheckBox) findViewById(R.id.chkFora);
		chkTranslate = (CheckBox) findViewById(R.id.chkTranslate);
		chkTextToSpeech = (CheckBox) findViewById((R.id.chkTextToSpeech));
		boolean checked = getIntent().getBooleanExtra("nghs", true);
		chkNGHS.setChecked(checked);
		intent.putExtra("nghs", checked);

		chkNGHS.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {

				intent.putExtra("nghs", isChecked);
				intent.putExtra("OK", "OK");
			}

		});
		
		checked = getIntent().getBooleanExtra("fora", true);
		chkFora.setChecked(checked);
		intent.putExtra("fora", checked);

		chkFora.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {

				intent.putExtra("fora", isChecked);
				intent.putExtra("OK", "OK");
			}

		});

		checked = getIntent().getBooleanExtra("translate", true);
		chkTranslate.setChecked(checked);
		intent.putExtra("translate", checked);

		chkTranslate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {

				intent.putExtra("translate", isChecked);
				intent.putExtra("OK", "OK");
			}

		});

		
		checked = getIntent().getBooleanExtra("Random", false);
		chkRandom.setChecked(checked);
		intent.putExtra("Random", checked);

		chkRandom.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {

				intent.putExtra("Random", isChecked);
				intent.putExtra("OK", "OK");
			}

		});

		//chkTextToSpeech.setChecked(_main.blnTextToSpeech);
		chkTextToSpeech.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				intent.putExtra("tts", isChecked);
				intent.putExtra("OK","OK");
			}
		});


		checked = getIntent().getBooleanExtra("AskAll", false);
		chkAskAll.setChecked(checked);
		intent.putExtra("AskAll", checked);

		chkAskAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				intent.putExtra("AskAll", isChecked);
				intent.putExtra("OK","OK");
			}

		});

		checked = getIntent().getBooleanExtra("Sound", true);
		chkSound.setChecked(checked);
		intent.putExtra("Sound", checked);

		chkSound.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				intent.putExtra("Sound", isChecked);
				intent.putExtra("OK","OK");
			}

		});
		if (Build.VERSION.SDK_INT< 19)
		{
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) chkRandom
					.getLayoutParams();
			params.bottomMargin = (int) lib.dpToPx(10);
			chkRandom.setLayoutParams(params);
			
			chkDocumentProvider.setVisibility(View.GONE);
			chkDontShowPersistableURIMessage.setVisibility(View.GONE);
		}
		else
		{
			final String keyProvider = "ShowAlwaysDocumentProvider";
			int ShowAlwaysDocumentProvider = getIntent().getIntExtra(keyProvider, 999);
			intent.putExtra(keyProvider, ShowAlwaysDocumentProvider);
			final String keyURIMessage = "DontShowPersistableURIMessage";
			int DontShowPersistableURIMessage = getIntent().getIntExtra(keyURIMessage, 999);
			intent.putExtra(keyURIMessage, DontShowPersistableURIMessage);
			
			if (ShowAlwaysDocumentProvider==-1) 
			{
				chkDocumentProvider.setChecked(true);
			}
			else if (ShowAlwaysDocumentProvider==0)
			{
				chkDocumentProvider.setChecked(false);
			}
			else
			{
				chkDocumentProvider.setEnabled(false);
			}
			chkDocumentProvider.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					int ShowAlwaysDocumentProvider = isChecked?-1:0;
					if(!isChecked)
					{
						String msg=_main.getString(R.string.msgResetSetting);
						if(lib.ShowMessageYesNo(_main, msg, "")==yesnoundefined.yes)
						{
							ShowAlwaysDocumentProvider=999;	
						}
					}
					intent.putExtra(keyProvider, ShowAlwaysDocumentProvider);
					intent.putExtra("OK","OK");
				}
			});
			
			
			
			if (DontShowPersistableURIMessage==-1) 
			{
				chkDontShowPersistableURIMessage.setChecked(true);
			}
			else if (DontShowPersistableURIMessage==0)
			{
				chkDontShowPersistableURIMessage.setChecked(false);
			}
			else
			{
				chkDontShowPersistableURIMessage.setEnabled(false);
			}
			
			chkDontShowPersistableURIMessage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					int DontShowPersistableURIMessage = isChecked?-1:0;
					intent.putExtra(keyURIMessage, DontShowPersistableURIMessage);
					intent.putExtra("OK","OK");
				}
			});
		}
		
		final String key = "AlwaysStartExternalProgram";
		int AlwaysStartInternalProgram = getIntent().getIntExtra(key, 999);
		intent.putExtra(key, AlwaysStartInternalProgram);
		
		
		if (AlwaysStartInternalProgram==-1) 
		{
			chkAlwaysStartExternalProgram.setChecked(true);
		}
		else if (AlwaysStartInternalProgram==0)
		{
			chkAlwaysStartExternalProgram.setChecked(false);
		}
		else
		{
			chkAlwaysStartExternalProgram.setEnabled(false);
		}
		chkAlwaysStartExternalProgram.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				int AlwaysStartExternalProgram = isChecked?-1:0;
				if(!isChecked)
				{
					String msg=_main.getString(R.string.msgResetSetting);
					if(lib.ShowMessageYesNo(_main, msg, "")==yesnoundefined.yes)
					{
						AlwaysStartExternalProgram=999;	
					}
				}
				intent.putExtra(key, AlwaysStartExternalProgram);
				intent.putExtra("OK","OK");
			}
		});
		
		

	}

	private void initSpinners(boolean blnRestart) {
		libLearn.gStatus = "initSpinners";
		try {
			spnAbfragebereich = (Spinner) findViewById(R.id.spnAbfragebereich);
			spnASCII = (Spinner) findViewById(R.id.spnASCII);
			spnStep = (Spinner) findViewById(R.id.spnStep);
			spnDisplayDurationWord = (Spinner) findViewById(R.id.spnAnzeigedauerWord);
			spnDisplayDurationBed = (Spinner) findViewById(R.id.spnAnzeigedauerBed);
			spnPaukRepetitions = (Spinner) findViewById(R.id.spnRepetitions);
			spnProbabilityFactor = (Spinner) findViewById(R.id.spnProbabilityFactor);
			spnRestartInterval = (Spinner) findViewById(R.id.spnRestartInterval);
			spnLanguages = (Spinner) findViewById(R.id.spnLanguages);
			spnColors = (org.de.jmg.lib.NoClickSpinner) findViewById(R.id.spnColors);
			spnSounds = (org.de.jmg.lib.NoClickSpinner) findViewById(R.id.spnSounds);
			spnLangWord = (Spinner) findViewById(R.id.spnLangWord);
			spnLangMeaning = (Spinner) findViewById(R.id.spnLangMeaning);
			if (!blnRestart) langinitialized = 0; else langinitialized = 0;
			if (spnAbfragebereich.getAdapter()!= null && spnAbfragebereich.getAdapter().getCount()>0) return; 
			if (Colors == null || Colors != null)
			{
				Colors = new ColorsArrayAdapter(_main,
						R.layout.spinnerrow);
				Colors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				Sounds = new SoundsArrayAdapter(_main,
						R.layout.soundsspinnerrow);
				Sounds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			}
			
			spnASCII.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnStep.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnDisplayDurationBed.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnDisplayDurationWord.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnAbfragebereich.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnPaukRepetitions.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnProbabilityFactor.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnRestartInterval.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnLanguages.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			//spnColors.getBackground().setColorFilter(Color.BLACK,	PorterDuff.Mode.SRC_ATOP);
			//spnSounds.getBackground().setColorFilter(Color.BLACK,	PorterDuff.Mode.SRC_ATOP);

			// Create an ArrayAdapter using the string array and a default
			// spinner layout
			ScaledArrayAdapter<CharSequence> adapter = ScaledArrayAdapter
					.createFromResource(_main, R.array.spnAbfragebereichEntries,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch() && mScale==1) adapter.Scale = 1.8f;
			// Apply the adapter to the spinner
			spnAbfragebereich.setAdapter(adapter);
			spnAbfragebereich.setSelection(getIntent().getShortExtra(
					"Abfragebereich", (short) -1) + 1);

			spnAbfragebereich
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							intent.putExtra("Abfragebereich",
									(short) (position - 1));
							intent.putExtra("OK","OK");
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

							
						}

						
					});
			ScaledArrayAdapter<CharSequence> adapterStep = ScaledArrayAdapter
					.createFromResource(_main, R.array.spnStepEntries,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterStep
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			if (lib.NookSimpleTouch()&& mScale==1) adapterStep.Scale = 1.8f;
			spnStep.setAdapter(adapterStep);
			spnStep.setSelection(adapterStep.getPosition(""
					+ getIntent().getShortExtra("Step", (short) 5)));
			spnStep.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {

					intent.putExtra("Step", (short) (Integer
							.parseInt((String) parent
									.getItemAtPosition(position))));
					intent.putExtra("OK","OK");
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

					
				}

				
			});

			
			ScaledArrayAdapter<String> adapterASCII = new ScaledArrayAdapter<String>(
					_main, android.R.layout.simple_spinner_item);
			// adapterASCII.addAll(Charset.availableCharsets().values());
			ArrayList<String>charsets = new ArrayList<String>();
			for (Charset c : Charset.availableCharsets().values()) {
				charsets.add(c.name());
			}
			adapterASCII.addAll(charsets);
			// Specify the layout to use when the list of choices appears
			adapterASCII
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			if (lib.NookSimpleTouch() && mScale==1) adapterASCII.Scale = 1.8f;
			spnASCII.setAdapter(adapterASCII);
			String CharsetASCII = getIntent().getStringExtra("CharsetASCII");
			if (!libString.IsNullOrEmpty(CharsetASCII)) {
				int i = 0;
				for (Charset c : Charset.availableCharsets().values()) {
					if (c.name().equalsIgnoreCase(CharsetASCII)) {
						break;
					}
					i++;
				}
				if (i < adapterASCII.getCount()) {
					spnASCII.setSelection(i);
				}

			}
			spnASCII.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {

					intent.putExtra("CharsetASCII",
							((String) (parent.getSelectedItem())));
					intent.putExtra("OK","OK");
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

					
				}

				
			});

			final ScaledArrayAdapter<CharSequence> adapterDDWord = ScaledArrayAdapter
					.createFromResource(_main, R.array.spnDurations,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterDDWord
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch() && mScale==1) adapterDDWord.Scale = 1.8f;
			// Apply the adapter to the spinner
			spnDisplayDurationWord.setAdapter(adapterDDWord);
			String strDD = ""
					+ getIntent().getFloatExtra("DisplayDurationWord", 1.5f);
			strDD = strDD.replace(".0", "");
			int Pos = adapterDDWord.getPosition(strDD);
			spnDisplayDurationWord.setSelection(Pos);
			spnDisplayDurationWord
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							intent.putExtra("DisplayDurationWord", (Float
									.parseFloat((String) parent
											.getItemAtPosition(position))));
							intent.putExtra("OK","OK");
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

							
						}

						
					});

			ScaledArrayAdapter<CharSequence> adapterDDBed = ScaledArrayAdapter
					.createFromResource(_main, R.array.spnDurations,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterDDBed
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch() && mScale==1) adapterDDBed.Scale = 1.8f;
			// Apply the adapter to the spinner
			spnDisplayDurationBed.setAdapter(adapterDDBed);
			strDD = "" + getIntent().getFloatExtra("DisplayDurationBed", 2.5f);
			strDD = strDD.replace(".0", "");
			Pos = adapterDDBed.getPosition(strDD);
			spnDisplayDurationBed.setSelection(Pos);
			spnDisplayDurationBed
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							intent.putExtra("DisplayDurationBed", (Float
									.parseFloat((String) parent
											.getItemAtPosition(position))));
							intent.putExtra("OK","OK");
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

							
						}

						
					});

			ScaledArrayAdapter<CharSequence> adapterPaukRepetitions = ScaledArrayAdapter
					.createFromResource(_main, R.array.spnRepetitions,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterPaukRepetitions
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch() && mScale==1) adapterPaukRepetitions.Scale = 1.8f;
			spnPaukRepetitions.setAdapter(adapterPaukRepetitions);
			Pos = getIntent().getIntExtra("PaukRepetitions", 3) - 1;
			spnPaukRepetitions.setSelection(Pos);
			spnPaukRepetitions
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							intent.putExtra("PaukRepetitions", (Integer
									.parseInt((String) parent
											.getItemAtPosition(position))));
							intent.putExtra("OK","OK");
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

							
						}

						
					});

			ScaledArrayAdapter<CharSequence> adapterProbabilityFactor = ScaledArrayAdapter
					.createFromResource(_main, R.array.spnProbabilityFactors,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterProbabilityFactor
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch() && mScale==1) adapterProbabilityFactor.Scale = 1.8f;
			spnProbabilityFactor.setAdapter(adapterProbabilityFactor);
			float ProbabilityFactor = getIntent().getFloatExtra(
					"ProbabilityFactor", -1f);
			if (ProbabilityFactor == -1) {
				strDD = _main.getString((R.string.auto));
			} else {
				strDD = "" + ProbabilityFactor;
				strDD = strDD.replace(".0", "");
			}
			
			ArrayAdapter<CharSequence> a1 = adapterProbabilityFactor;
			if (a1 != null) {
				try {
					libLearn.gStatus = "get Spinneradapter ProbabilityFactor";
					Pos = (a1.getPosition(strDD));
					spnProbabilityFactor.setSelection(Pos);
				} catch (Exception ex) {
					lib.ShowException(_main, ex);
				}

			}

			spnProbabilityFactor
					.setOnItemSelectedListener(new OnItemSelectedListener() 
					{

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							String strDD = (String) parent
									.getItemAtPosition(position);
							if (strDD.equalsIgnoreCase(_main.getString(R.string.auto)))
								strDD = "-1";
							intent.putExtra("ProbabilityFactor",
									(Float.parseFloat(strDD)));
							intent.putExtra("OK","OK");
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

							
						}

						
					});

			
			ScaledArrayAdapter<CharSequence> adapterRestartInterval = ScaledArrayAdapter
					.createFromResource(_main, R.array.spnRestartInterval,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterRestartInterval
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch() && mScale==1) adapterRestartInterval.Scale = 1.8f;
			spnRestartInterval.setAdapter(adapterRestartInterval);
			int RestartInterval = getIntent().getIntExtra(
					"RestartInterval", -1);
			if (RestartInterval == -1) {
				strDD = _main.getString(R.string.off);
			} else {
				strDD = "" + RestartInterval;
			}

			a1 = adapterRestartInterval;
			if (a1 != null) {
				try {
					libLearn.gStatus = "get Spinneradapter ProbabilityFactor";
					Pos = (a1.getPosition(strDD));
					spnRestartInterval.setSelection(Pos);
				} catch (Exception ex) {
					lib.ShowException(_main, ex);
				}

			}

			spnRestartInterval
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							String strDD = (String) parent
									.getItemAtPosition(position);
							if (strDD.equalsIgnoreCase(_main.getString(R.string.off)))
								strDD = "-1";
							intent.putExtra("RestartInterval",
									(Integer.parseInt(strDD)));
							intent.putExtra("OK","OK");
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

							
						}

						
					});
			
			ScaledArrayAdapter<CharSequence> adapterLanguages = ScaledArrayAdapter
					.createFromResource(_main, R.array.spnLanguages,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterLanguages
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch() && mScale==1) adapterLanguages.Scale = 1.8f;
			spnLanguages.setAdapter(adapterLanguages);
			int Language = getIntent().getIntExtra(
					"Language", org.de.jmg.learn.vok.Vokabel.EnumSprachen.undefiniert.ordinal());
			spnLanguages.setSelection(Language);
	        spnLanguages
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
												   View view, int position, long id) {
							intent.putExtra("Language", position);
							intent.putExtra("OK", "OK");
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {


						}


					});

			adapterLangWord = new ScaledArrayAdapter<>(
					_main, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterLangWord
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch() && mScale==1) adapterLangWord.Scale = 1.8f;

			adapterLangWord.add(new DisplayLocale(new Locale("","")));
			adapterLangWord.add(new DisplayLocale(new Locale("_off")));
			for (Locale l : Locale.getAvailableLocales())
			{
				DisplayLocale dl = new DisplayLocale(l);
				adapterLangWord.add(dl);
			}
			sortLangWord();

			spnLangWord.setAdapter(adapterLangWord);
			/*
			if (selectedLocale != null)
			{
				int pos = adapterLangWord.getPosition(selectedLocale);
				spnLangWord.setSelection (-1);
				spnLangWord.setSelection(pos);
			}*/
			spnLangWord
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
												   View view, int position, long id) {
							if (position <= 0 || langinitialized == 0)
							{
								langinitialized += 1;
								return;
							}
							Locale l = adapterLangWord.getItem(position).locale;
							int res = 0;
							if (_main.tts.isLanguageAvailable(Locale.US) >= 0)
							{
								res = _main.tts.setLanguage(l);
							}
							if (!chkTextToSpeech.isChecked()
									|| res >= 0
									|| l.toString().equalsIgnoreCase("_off")
									|| !_main.blnTextToSpeech
									|| lib.ShowMessageYesNo(_main,
									String.format(_main.getString
											(R.string.msgLanguageNotavailable)
											, l.getDisplayLanguage() + " " + l.getDisplayCountry()), "")
									== yesnoundefined.yes)
							{
								intent.putExtra("langword", lib.toLanguageTag
										(l));
								intent.putExtra("OK", "OK");
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {


						}


					});

			adapterLangMeaning = new ScaledArrayAdapter<>(
					_main, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterLangMeaning
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch() && mScale==1) adapterLangMeaning.Scale = 1.8f;
			adapterLangMeaning.add(new DisplayLocale(new Locale("","")));
			adapterLangMeaning.add(new DisplayLocale(new Locale("_off")));
			for (Locale l : Locale.getAvailableLocales()) {
				DisplayLocale dl = new DisplayLocale((l));
				adapterLangMeaning.add(dl);
			}
			sortLangMeaning();

			spnLangMeaning.setAdapter(adapterLangMeaning);
			/*
			if (selectedLocale != null)
			{
				int pos = adapterLangMeaning.getPosition(selectedLocale);
				spnLangMeaning.setSelection(-1);
				spnLangMeaning.setSelection(pos);
			}
			*/
			spnLangMeaning
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
												   View view, int position, long id) {
							if (position <= 0  || langinitialized < 2)
							{
								langinitialized += 1;
								return;
							}
							Locale l = adapterLangMeaning.getItem(position).locale;
							int res = 0;
							if (_main.tts.isLanguageAvailable(Locale.US) >= 0)
							{
								res = _main.tts.setLanguage(l);
							}
							if (!chkTextToSpeech.isChecked()
									|| res >= 0
									|| l.toString().equalsIgnoreCase("_off")
									|| !_main.blnTextToSpeech
									|| lib.ShowMessageYesNo(_main, String.format
									(_main.getString(R.string.msgLanguageNotavailable),
											l.getDisplayLanguage() + " "
													+ l.getDisplayCountry()),"")
									== yesnoundefined.yes)
							{
								intent.putExtra("langmeaning", lib.toLanguageTag(l));
								intent.putExtra("OK", "OK");
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {


						}


					});

			if (lib.NookSimpleTouch() && mScale==1) Colors.Scale = 1.8f;
			spnColors.setAdapter(Colors);
			spnColors
					.setOnLongClickListener(new android.widget.AdapterView.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {

							spnColors.blnDontCallOnClick = true;
							ShowColorDialog();
							return false;
						}
					});
			spnColors
					.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {

							spnColors.blnDontCallOnClick = true;
							ShowColorDialog();
							return false;
						}
					});
			
			if (lib.NookSimpleTouch() && mScale==1) Sounds.Scale = 1.8f;
			spnSounds.setAdapter(Sounds);
			spnSounds
					.setOnLongClickListener(new android.widget.AdapterView.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {

							spnSounds.blnDontCallOnClick = true;
							ShowSoundsDialog();
							return false;
						}
					});
			spnSounds
					.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {

							spnSounds.blnDontCallOnClick = true;
							ShowSoundsDialog();
							return false;
						}
					});

			spnSounds.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {

					SoundSetting item = (SoundSetting) parent
							.getItemAtPosition(position);
					File F = new File(item.SoundPath);
					try {
						if (F.exists())
							lib.playSound(F);
						else
							lib.playSound(_main.getAssets(), item.SoundPath);
					} catch (Exception e) {

						e.printStackTrace();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

					
				}

				
			});

		} catch (Exception ex) {
			lib.ShowException(_main, ex);
		}
	}

	void sortLangWord()
	{
		adapterLangWord.sort(new Comparator<DisplayLocale>() {

			@Override
			public int compare(DisplayLocale lhs, DisplayLocale rhs) {
				int res = lhs.compareTo(rhs);
				return res;
			}
		});
	}

	void sortLangMeaning()
	{
		adapterLangMeaning.sort(new Comparator<DisplayLocale>() {

			@Override
			public int compare(DisplayLocale lhs, DisplayLocale rhs) {
				int res = lhs.compareTo(rhs);
				return res;
			}
		});

	}

	public Intent getIntent() {

		return _Intent;
	}




	private void initButtons() {
		btnResetDefaultVok = (Button) findViewById(R.id.btnResetCopyVok);
		btnResetDefaultVok.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				prefs.edit().putInt("dontcopyorchoose", -2).commit();

			}
		});
		btnResetGooglePlay = (Button) findViewById(R.id.btnResetGooglePlay);
		btnResetGooglePlay.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				prefs.edit().putBoolean("play", true).commit();
			}
		});

		/*
		Button b = (Button) findViewById(R.id.btnOK);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					saveResultsAndFinish(false);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		});
		b = (Button) findViewById(R.id.btnCancel);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				_main.mPager.setCurrentItem(_MainActivity.fragID);
			}
		});
		*/
	}
	
	void saveResultsAndFinish(boolean blnDontSetCurrentItem) throws Exception
	{
		for (int i = 0; i < Colors.getCount(); i++) {
			intent.putExtra(Colors.getItem(i).ColorItem.name(),
					Colors.getItem(i).ColorValue);
		}

		for (int i = 0; i < Sounds.getCount(); i++) {
			intent.putExtra(Sounds.getItem(i).Sound.name(),
					Sounds.getItem(i).SoundPath);
		}
		if (intent.getStringExtra("OK") == "OK")
		{
			_main.processSettingsIntent(intent);
		}
		if (!blnDontSetCurrentItem) _main.mPager.setCurrentItem(_MainActivity.fragID);
	}

	public float mScale = 1;
	private OnLongClickListener ViewOnLongClickCD = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {

			if ( v.getContentDescription()!=null && v.getContentDescription().length()>0)
			{
				lib.ShowMessage(_main, (String) v.getContentDescription(), _main.getString(R.string.help));
			}
			return false;
		}
	};
	private void resize(float scale) {
		//if (scale == 0 && mScale!=1) return;
		Resources resources = _main.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		@SuppressWarnings("unused")
		int Density = metrics.densityDpi;
		try
		{
			int width = mainView.getWidth();
			if (width>500)
			{
				width -= lib.dpToPx(40);
			}
			else
			{
				width -= lib.dpToPx(40);
			}
			if (scale == 0)
			{
				mainView.setVisibility(View.INVISIBLE);
				libLearn.gStatus="Calculating Scale";
					
				float scale1 = width
						/ (float) (_main.isSmallDevice?0:(findViewById(R.id.txtCharsetASCII)).getWidth()
								+ spnASCII.getWidth() + width / 30);
				float scale2 = width
						/ (float) (_main.isSmallDevice?0:(findViewById(R.id.txtSounds)).getWidth()
								+ spnSounds.getWidth() + width / 30);
				float scale3 = width / (float)(_main.isSmallDevice?0:(findViewById(R.id.txtCharsetASCII)).getWidth()
						+ spnASCII.getWidth() + width / 30);
				scale = (scale1 < scale2) ? scale1 : scale2;
				scale = (scale3 < scale) ? scale3 : scale;
			}
			mScale = scale;
			
			ViewGroup Settings = (ViewGroup) findViewById(R.id.layoutSettings);
			libLearn.gStatus = "Enumerating ChildViews";
			int ChildCount = Settings.getChildCount();
			for (int i = 0; i < ChildCount; i++) {
				if (i>100)break;
				libLearn.gStatus="getting view "+i;
				View V = Settings.getChildAt(i);
				/*
				CharSequence cs = V.getContentDescription();
				if (cs != null && cs.length()>0 )
				{
					V.setOnLongClickListener(ViewOnLongClickCD);
				}
				*/
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) V
						.getLayoutParams();

				params.topMargin = (int) (params.topMargin * scale);
				params.bottomMargin = (int) (params.bottomMargin * scale);
				
				if (params.height>0) params.height = (int) (params.height * scale);
				if (V instanceof CheckBox)
				{
					if (params.width>0) params.width = ((width - lib.dpToPx(10))/3);
				}
				else
				{
					if (params.width>0)params.width = (int) (params.width * scale);
				}
				/*
				if (V == spnSounds) {
					int soundsHeight = spnSounds.getHeight();
					float margin = (float) ((soundsHeight * scale) / 5.25);
					params.topMargin = params.topMargin + (int) margin;
				}
				*/
				libLearn.gStatus="Setting Layoutparams";
				V.setLayoutParams(params);
				// }
				if (V instanceof TextView && !(V instanceof CheckBox)) {
					libLearn.gStatus="TextView set size";
					TextView t = (TextView) V;
					t.setTextSize(TypedValue.COMPLEX_UNIT_PX, t.getTextSize()
							* scale);
				} else if (V instanceof Spinner) {
					Spinner spn = (Spinner) V;
					SpinnerAdapter A = spn.getAdapter();
					if (A instanceof AbstractScaledArrayAdapter<?>) {
						libLearn.gStatus="Scaling Adapter";
						AbstractScaledArrayAdapter<?> AA = (AbstractScaledArrayAdapter<?>) A;
						AA.Scale = AA.Scale * scale;
						if (spn.getSelectedItemPosition()>-1)
						{
							AA.notifyDataSetChanged();
						}
						

					}
				} else if (V instanceof CheckBox) {
					libLearn.gStatus="CheckBox";
					CheckBox c = (CheckBox) V;
					//c.setScaleX(scale);
					//c.setScaleY(scale);
					// c.setle
					c.setTextSize(TypedValue.COMPLEX_UNIT_PX, c.getTextSize()
							* scale);
					
					/*
					int p1 = c.getPaddingTop();
					int p2 = c.getPaddingBottom();
					int p3 = c.getPaddingLeft();
					int p4 = c.getPaddingRight();
					c.setPadding((int) (p3/scale), p1, p4, p2);
					*/
					//LevelListDrawable D = (LevelListDrawable) c.getBackground();
					/*
					Drawable d = c.getBackground();
					Log.d("bounds", d.getBounds().toString());
					*/
					//d.setTargetDensity((int) (Density * scale));
					//d.setBounds(0, 0, c.getHeight(), c.getHeight());
					//lib.setBgCheckBox(c,d);
					/*
					ViewGroup check = (ViewGroup) V;
					for (int ii = 0; ii<check.getChildCount(); ii++)
					{
						View cv = check.getChildAt(ii);
						String cls = cv.getClass().getName();
						Log.d("Classs", cls);
					}
					*/
					// Drawable d = lib.getDefaultCheckBoxDrawable(_main);
					// d = new ScaleDrawable(d, 0, c.getHeight()*scale,
					// c.getHeight()*scale).getDrawable();
					// float scaleC = (float)c.getHeight()/d.getBounds().height();
					// d.setBounds(0, 0,(int) (c.getHeight()*scale),(int)
					// (c.getHeight()*scale));
					// LayerDrawable L = new LayerDrawable(new Drawable[]{d});
					// d = lib.scaleImage(_main, d, scaleC);
					// /c.setButtonDrawable(d);
				}
			}
			/*
			libLearn.gStatus="Buttons";
			Button b = (Button) findViewById(R.id.btnOK);
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) b
					.getLayoutParams();
			params.topMargin = (int) (params.topMargin * scale);
			if (params.height>0)params.height = (int) (params.height * scale);
			if (params.width>0)params.width = (int) (params.width * scale);
			b.setLayoutParams(params);
			b.setTextSize(TypedValue.COMPLEX_UNIT_PX, b.getTextSize() * scale);

			b = (Button) findViewById(R.id.btnCancel);
			params = (android.widget.RelativeLayout.LayoutParams) b
					.getLayoutParams();
			params.topMargin = (int) (params.topMargin * scale);
			if (params.height>0)params.height = (int) (params.height * scale);
			if (params.width>0)params.width = (int) (params.width * scale);
			b.setLayoutParams(params);
			b.setTextSize(TypedValue.COMPLEX_UNIT_PX, b.getTextSize() * scale);
			*/
			SettingsView.getViewTreeObserver().addOnGlobalLayoutListener
			(
					new ViewTreeObserver.OnGlobalLayoutListener() 
					{

						@Override
						public void onGlobalLayout() 
						{
							// Ensure you call it only once :
							lib.removeLayoutListener(SettingsView.getViewTreeObserver(), this);
							int pos = spnASCII.getSelectedItemPosition();
							spnASCII.setSelection(-1);
							spnASCII.setSelection(pos);
						}
					}
			);

			
		}
		catch (Exception ex)
		{
			lib.ShowException(_main, ex);
		}
		finally
		{
			mainView.setVisibility(View.VISIBLE);
		}
		
		
	}
	
	private void initHelp()
	{
		ViewGroup Settings = (ViewGroup) findViewById(R.id.layoutSettings);
		int ChildCount = Settings.getChildCount();
		for (int i = 0; i < ChildCount; i++) {
			if (i>100)break;
			libLearn.gStatus="getting view "+i;
			View V = Settings.getChildAt(i);
			CharSequence cs = V.getContentDescription();
			if (cs != null && cs.length()>0 )
			{
				V.setOnLongClickListener(ViewOnLongClickCD);
			}
		}

	}
	
	private void ShowColorDialog() {
		spnColors.blnDontCallOnClick = true;
		final ColorSetting item = Colors.getItem(spnColors
				.getSelectedItemPosition());
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(_main, item.ColorValue,
				new OnAmbilWarnaListener() {

					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {

						//ColorSetting item = Colors
						//		.getItem(spnColors.getSelectedItemPosition());
						item.ColorValue = color;
						Editor editor = prefs.edit();
						editor.putInt(item.ColorItem.name(), item.ColorValue);
						intent.putExtra(item.ColorItem.name(), item.ColorValue);
						intent.putExtra("OK","OK");
						
						editor.commit();
						if (lib.NookSimpleTouch())
						{
							int pos = spnColors.getSelectedItemPosition();
							spnColors.setAdapter(null);
							spnColors.setAdapter(Colors);
							spnColors.setSelection(pos);
						}
						else
						{
							Colors.notifyDataSetChanged();
						}
						
						spnColors.blnDontCallOnClick = false;
					}

					@Override
					public void onCancel(AmbilWarnaDialog dialog) {

						spnColors.blnDontCallOnClick = false;
					}
				});
		dialog.show();

	}

	private void ShowSoundsDialog() {
		spnSounds.blnDontCallOnClick = true;
		SoundSetting item = Sounds.getItem(spnSounds
				.getSelectedItemPosition());
		File F = new File(item.SoundPath);
		String dir = _main.SoundDir;
		if (F.exists())
			dir = F.getParent();
		Intent intent = new Intent(_main, FileChooser.class);
		ArrayList<String> extensions = new ArrayList<String>();
		extensions.add(".wav");
		extensions.add(".mp3");
		extensions.add(".ogg");
		extensions.add(".flv");

		intent.putStringArrayListExtra("filterFileExtension", extensions);
		intent.putExtra("DefaultDir", dir);

		_main.startActivityForResult(intent, FILE_CHOOSERSOUND);
	}

	private void ShowDataDirDialog() {
		String  dir = edDataDir.getText().toString();
		Intent intent = new Intent(_main, AdvFileChooser.class);
		intent.putExtra("DefaultDir", dir);
		intent.putExtra("selectFolder",true);
		_main.startActivityForResult(intent, FILE_CHOOSERDATADIR);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == FILE_CHOOSERSOUND) {
				if (resultCode == Activity.RESULT_OK) {
					String fileSelected = data.getStringExtra("fileSelected");
					SoundSetting item = Sounds
							.getItem(spnSounds.getSelectedItemPosition());
					item.SoundPath = fileSelected;
					File F = new File(item.SoundPath);
					try {
						if (F.exists()) {
							lib.playSound(F);
							_main.setSoundDir(F.getParent());
						} else
							lib.playSound(_main.getAssets(), item.SoundPath);
					} catch (IOException e) {

						e.printStackTrace();
					}
					Editor editor = prefs.edit();
					editor.putString(item.Sound.name(), item.SoundPath);
					intent.putExtra(item.Sound.name(), item.SoundName);
					intent.putExtra("OK", "OK");
					editor.commit();
					Sounds.notifyDataSetChanged();
				}
				spnSounds.blnDontCallOnClick = false;
			}
			else if (requestCode == FILE_CHOOSERDATADIR && resultCode == Activity.RESULT_OK)
			{
				String strDataDir = data.getStringExtra("fileSelected");
				File fileSelected = new File(strDataDir);
				if (fileSelected.isDirectory() && fileSelected.exists())
				{
					_main.setJMGDataDirectory(fileSelected.getPath());
					edDataDir.setText(_main.JMGDataDirectory);
					Editor editor = prefs.edit();
					editor.putString("JMGDataDirectory", fileSelected.getPath());
					editor.commit();
				}
			}
		} catch (Exception ex) {
			lib.ShowException(_main, ex);
		}
	}

	public void setChkTSS()
	{
		chkTextToSpeech.setChecked(_main.blnTextToSpeech);
		//intent.putExtra("tts", _main.blnTextToSpeech);
	}

	public void setSpnWordPosition()
	{
		langinitialized = 0;
		DisplayLocale selectedLocale = null;
		for (int i = 0; i < adapterLangWord.getCount(); i++)
		{
			DisplayLocale l = adapterLangWord.getItem(i);
			if (l.locale.toString().equalsIgnoreCase(_main.vok.getLangWord().toString()))
			{
				spnLangWord.setSelection(i);
				selectedLocale = l;
				break;
			}
		}
		if (selectedLocale == null)
		{
			selectedLocale = new DisplayLocale(_main.vok.getLangWord());
			adapterLangWord.add(selectedLocale);
			sortLangWord();
			spnLangWord.setSelection(adapterLangWord.getPosition(selectedLocale));

		}
		//langinitialized = true;
	}

	public void setSpnMeaningPosition()
	{
		langinitialized = 0;
		DisplayLocale selectedLocale = null;
		for (int i = 0; i < adapterLangMeaning.getCount(); i++)
		{
			DisplayLocale l = adapterLangMeaning.getItem(i);
			if (l.locale.toString().equalsIgnoreCase(_main.vok.getLangMeaning().toString()))
			{
				spnLangMeaning.setSelection(i);
				selectedLocale = l;
				break;
			}
		}
		if (selectedLocale == null)
		{
			selectedLocale = new DisplayLocale(_main.vok.getLangMeaning());
			adapterLangMeaning.add(selectedLocale);
			sortLangMeaning();
			spnLangMeaning.setSelection(adapterLangMeaning.getPosition(selectedLocale));
		}
		//langinitialized = true;
	}


	

}

	
