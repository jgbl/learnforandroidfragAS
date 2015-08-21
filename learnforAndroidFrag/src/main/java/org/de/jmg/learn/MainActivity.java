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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;

import org.de.jmg.learn.R;
import org.de.jmg.learn.MyFragmentPagerAdapter;
import org.de.jmg.learn.libLearn;
import org.de.jmg.learn.vok.Vokabel;
import org.de.jmg.lib.ColorSetting;
import org.de.jmg.lib.Path;
import org.de.jmg.lib.SoundSetting;
import org.de.jmg.lib.lib;
import org.de.jmg.lib.ColorSetting.ColorItems;
import org.de.jmg.lib.lib.Sounds;
import org.de.jmg.lib.lib.libString;
import org.de.jmg.lib.lib.yesnoundefined;

import br.com.thinkti.android.filechooser.AdvFileChooser;
import br.com.thinkti.android.filechooser.FileChooser;
import br.com.thinkti.android.filechooserfrag.fragFileChooser;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

	public ViewPager mPager;	
	public static final int FILE_CHOOSER = 34823;
	public static final int Settings_Activity = 34824;
	public static final int FILE_CHOOSERADV = 34825;
	public static final int FILE_OPENINTENT = 34826;
	private Context context = this;
	private boolean _blnEink;
	boolean _blnUniCode = true;
	yesnoundefined _oldUniCode = yesnoundefined.undefined;
	
	public float DisplayDurationWord;
	public float DisplayDurationBed;
	public int PaukRepetitions = 3;
	
	
	public HashMap<ColorItems, ColorSetting> Colors;
	public HashMap<Sounds, SoundSetting> colSounds;
	public Vokabel vok;
	public String CharsetASCII = "Windows-1252";
	public View mainView;
	public ViewGroup Layout;
	public SharedPreferences prefs; // =
	public MyFragmentPagerAdapter fPA;
	public String SoundDir;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState!=null)
		{
			JMGDataDirectory = savedInstanceState.getString("JMGDataDirectory");
		}
				
		setContentView(R.layout.activity_main_viewpager);
		        /** Getting a reference to ViewPager from the layout */
        View pager = this.findViewById(R.id.pager);
        Layout = (ViewGroup) pager;
		mPager = (ViewPager) pager;

        /** Getting a reference to FragmentManager */
		FragmentManager fm = getSupportFragmentManager();
        
        /** Defining a listener for pageChange */
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener()
        {
            int LastPosition = -1;    
        	@Override
                public void onPageSelected(int position)
                {
                        super.onPageSelected(position);
                        if (LastPosition == SettingsActivity.fragID)
                        {
                        		try 
                        		{
                    				if(fPA!=null && fPA.fragSettings!=null)
                    				{
                    					try
                    					{
                    						fPA.fragSettings.saveResultsAndFinish(true);
                    					}
                    					catch (Exception ex)
                    					{
                    						Log.e(".saveResultsAndFinish",ex.getMessage(),ex);
                    					}
                    					/*
                    					if (lib.NookSimpleTouch()) 
                    					{
                    						RemoveFragSettings();
                    					}
                    					*/
                    				}
                    				
                    			} 
                        		catch (Exception e) 
                        		{
                    				// TODO Auto-generated catch block
                    				lib.ShowException(MainActivity.this, e);
                    			}
                    	}
                        else if (LastPosition == _MainActivity.fragID)
                        {
                        	if (fPA!=null && fPA.fragMain!=null)
                        	{
                        		fPA.fragMain.removeCallbacks();
                        	}
                        }
                        LastPosition=position;
                        
                        if (position == fragFileChooser.fragID)
                    	{
                        	mnuAddNew.setEnabled(false);
                        	try {
								checkLoadFile();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								lib.ShowException(MainActivity.this, e);
							}
                    	}
                        else if (position == _MainActivity.fragID)
                        {
                        	mnuAddNew.setEnabled(true);
                        }
                        else
                        {
                        	mnuAddNew.setEnabled(false);
                        }
                        
        				
        				
                }

        };
        
        /** Setting the pageChange listener to the viewPager */
        mPager.setOnPageChangeListener(pageChangeListener);
        
        /** Creating an instance of FragmentPagerAdapter */
        if(fPA==null)fPA = new MyFragmentPagerAdapter(fm, this, savedInstanceState!=null);
                
        /** Setting the FragmentPagerAdapter object to the viewPager object */
        mPager.setAdapter(fPA);
        
        
        libLearn.gStatus = "onCreate getEink";
		try {
			_blnEink = getWindowManager().getDefaultDisplay().getRefreshRate() < 5.0;
			if (_blnEink)
				lib.ShowToast(this, "This is an Eink diplay!");
		} catch (Exception ex) {
			lib.ShowException(this, ex);
		}

		try {
			libLearn.gStatus = "onCreate setContentView";
			mainView = findViewById(Window.ID_ANDROID_CONTENT);
			Thread.setDefaultUncaughtExceptionHandler(ErrorHandler);

			// View LayoutMain = findViewById(R.id.layoutMain);
			libLearn.gStatus = "getting preferences";
			try {
				libLearn.gStatus = "onCreate getPrefs";
				prefs = this.getPreferences(Context.MODE_PRIVATE);
				vok = new Vokabel(this,
						(TextView) this.findViewById(R.id.txtStatus));
				if (fPA.fragMain != null) fPA.fragMain._vok = vok;
				vok.setSchrittweite((short) prefs.getInt("Schrittweite", 6));
				CharsetASCII = prefs.getString("CharsetASCII", "Windows-1252");
				vok.CharsetASCII = CharsetASCII;
				vok.setAbfragebereich((short) prefs
						.getInt("Abfragebereich", -1));
				DisplayDurationWord = prefs.getFloat("DisplayDurationWord",
						1.5f);
				DisplayDurationBed = prefs.getFloat("DisplayDurationBed", 2.5f);
				PaukRepetitions = prefs.getInt("PaukRepetitions", 3);
				vok.ProbabilityFactor = prefs
						.getFloat("ProbabilityFactor", -1f);
				vok.setAbfrageZufaellig(prefs.getBoolean("Random",
						vok.getAbfrageZufaellig()));
				vok.setAskAll(prefs.getBoolean("AskAll", vok.getAskAll()));
				vok.RestartInterval = prefs.getInt("RestartInterval", 10);
				lib.sndEnabled = prefs.getBoolean("Sound", lib.sndEnabled);
				SoundDir = prefs.getString("SoundDir", Environment.getExternalStorageDirectory().getPath());
				Colors = getColorsFromPrefs();
				colSounds = getSoundsFromPrefs();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				lib.ShowException(this, e);
			}
			libLearn.gStatus = "onCreate Copy Assets";
			CopyAssets();
			
			
			try {
				
				String tmppath = Path.combine(getApplicationInfo().dataDir,
						"vok.tmp");
				// SetActionBarTitle();
				boolean CardMode = false;
				if (savedInstanceState != null) 
				{
					/*
					if (fPA.fragMain!=null && fPA.fragMain.mainView!=null)
					{
						fPA.fragMain.onCreateView(LayoutInflater.from(this), Layout, null);
					}
					*/
					libLearn.gStatus = "onCreate Load SavedInstanceState";
					String filename = savedInstanceState.getString("vokpath");
					Uri uri = null;
					String strURI = savedInstanceState.getString("URI");
					if (!libString.IsNullOrEmpty(strURI)) uri = Uri.parse(strURI);
					int index = savedInstanceState.getInt("vokindex");
					int[] Lernvokabeln = savedInstanceState
							.getIntArray("Lernvokabeln");
					int Lernindex = savedInstanceState.getInt("Lernindex");
					CardMode = savedInstanceState.getBoolean("Cardmode", false);
					if (index > 0) {
						boolean Unicode = savedInstanceState.getBoolean(
								"Unicode", true);
						_blnUniCode = Unicode;
						LoadVokabel(tmppath, uri, index, Lernvokabeln, Lernindex,
								CardMode);
						vok.setLastIndex(savedInstanceState.getInt(
								"vokLastIndex", vok.getLastIndex()));
						vok.setFileName(filename);
						vok.setURI(uri);
						vok.setCardMode(CardMode);
						vok.aend = savedInstanceState.getBoolean("aend", true);
						/*
						if (fPA.fragMain!=null && fPA.fragMain.mainView!=null)
						{
							fPA.fragMain.getVokabel(false, false);
						}
						*/
					}
					
					/*
					if (fPA.fragSettings!=null)
					{
						fPA.fragSettings.onCreateView(LayoutInflater.from(this), Layout, null);
					}
					*/
					
					//mPager.setCurrentItem(savedInstanceState.getInt("SelFragID", 0));
				} 
				else 
				{
					libLearn.gStatus = "getting lastfile";
					String strURI = prefs.getString("URI","");
					String filename = prefs.getString("LastFile", "");
					String UriName = prefs.getString("FileName", "");
					int[] Lernvokabeln = lib.getIntArrayFromPrefs(prefs,
							"Lernvokabeln");
					if (!libString.IsNullOrEmpty(strURI)
							|| !libString.IsNullOrEmpty(filename)
							|| (vok.checkLernvokabeln(Lernvokabeln))) 
					{
						libLearn.gStatus = "onCreate Load Lastfile";
						
						Uri uri = null;
						if (!libString.IsNullOrEmpty(strURI))
						{
							uri = Uri.parse(strURI);
							lib.CheckPermissions(this, uri,false);
						}
						
						int index = prefs.getInt("vokindex", 1);
						int Lernindex = prefs.getInt("Lernindex", 0);
						boolean Unicode = prefs.getBoolean("Unicode", true);
						_blnUniCode = Unicode;
						boolean isTmpFile = prefs
								.getBoolean("isTmpFile", false);
						boolean aend = prefs.getBoolean("aend", true);
						CardMode = prefs.getBoolean("Cardmode", false);
						if (Lernvokabeln != null) {
							if (isTmpFile) {
								libLearn.gStatus = "getting lastfile load tmpfile";
								LoadVokabel(tmppath, uri, index, Lernvokabeln,
										Lernindex, CardMode);
								vok.setFileName(filename);
								vok.setURI(uri);
								vok.setURIName(UriName);
								vok.setCardMode(CardMode);
								vok.setLastIndex(prefs.getInt("vokLastIndex",
										vok.getLastIndex()));
								vok.aend = aend;
							} 
							else 
							{
								libLearn.gStatus = "getting lastfile load file";
								LoadVokabel(filename, uri, index, Lernvokabeln,
										Lernindex, CardMode);
								vok.setLastIndex(prefs.getInt("vokLastIndex",
										vok.getLastIndex()));
							}
						} 
						else 
						{
							if (isTmpFile) 
							{
								libLearn.gStatus = "getting lastfiletmp no Lernvokablen";
								LoadVokabel(tmppath, uri, 1, null, 0, CardMode);
								vok.setFileName(filename);
								vok.setURI(uri);
								vok.setCardMode(CardMode);
								vok.aend = aend;
							} 
							else 
							{
								libLearn.gStatus = "getting lastfile no Lernvokablen";
								LoadVokabel(filename, uri, 1, null, 0, CardMode);
							}
						}

					}
				}

			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				lib.ShowException(this, e);
			}
			//InitSettings();
						

		} catch (Exception ex) {
			lib.ShowException(this, ex);
		}

        

    }
	
	
	
	
	public void RemoveFragSettings()
	{
		if (fPA.fragSettings != null && mPager.getCurrentItem() != SettingsActivity.fragID)
		{
			try
			{
				libLearn.gStatus = "getSupportFragmentmanager remove fragment";	
				getSupportFragmentManager().beginTransaction().remove(fPA.fragSettings).commitAllowingStateLoss();
			}
			catch (IllegalStateException ex2)
			{
				Log.e(libLearn.gStatus, ex2.getMessage(),ex2);
			}
			fPA.fragSettings=null;
		}
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		try {
			boolean aend = vok.aend;
			String filename = vok.getFileName();
			Uri uri = vok.getURI();
			outState.putInt("SelFragID", mPager.getCurrentItem());
			outState.putString("JMGDataDirectory", JMGDataDirectory);
			if (vok.getGesamtzahl() > 0 ) {
				saveFilePrefs(true);
				if(uri!=null)
				{
					lib.CheckPermissions(this, uri,false);
					//this.takePersistableUri(getIntent(), uri,true);
				}
				vok.SaveFile(
						Path.combine(getApplicationInfo().dataDir, "vok.tmp"),uri,
						vok.getUniCode(), true);
				outState.putString("vokpath", filename);
				outState.putInt("vokindex", vok.getIndex());
				outState.putInt("vokLastIndex", vok.getLastIndex());
				outState.putIntArray("Lernvokabeln", vok.getLernvokabeln());
				outState.putInt("Lernindex", vok.getLernIndex());
				outState.putBoolean("Unicode", vok.getUniCode());
				outState.putBoolean("Cardmode", vok.getCardMode());
				outState.putBoolean("aend", aend);
				if (uri!= null) outState.putString("URI", uri.toString());
				vok.aend = aend;
				vok.setFileName(filename);
				vok.setURI(uri);
				
			}
			handlerbackpressed.removeCallbacks(rSetBackPressedFalse);
			for (AlertDialog dlg: lib.OpenDialogs)
			{
				dlg.dismiss();
			}
			lib.OpenDialogs.clear();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("OnSaveInstanceState", e.getMessage(), e);
			e.printStackTrace();
		}
		// outState.putParcelable("vok", vok);

	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mPager.getCurrentItem() == fragFileChooser.fragID)
		{
			boolean res = this.fPA.fragChooser.onKeyDown(keyCode, event);
			if (res == false) return res;
		}
		else if (mPager.getCurrentItem() == _MainActivity.fragID)
		{
			this.fPA.fragMain.removeCallbacks();
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			try {
				saveVok(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				lib.ShowException(this, e);
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK && mPager.getCurrentItem()==0) {
			try {
				if (_backPressed > 0 || saveVokAsync(false)) 
				{
					handlerbackpressed.removeCallbacks(rSetBackPressedFalse);
				} else 
				{
					return true;
				}

			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				Log.e("onBackPressed", e.getMessage(), e);
				lib.ShowException(this, e);
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);

	};

	
	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
	}
	
	private boolean saveVokAsync(boolean dontPrompt) throws Exception {
		fPA.fragMain.EndEdit(true);
		if (vok.aend) {
			if (!dontPrompt) 
			{
				
				 AlertDialog.Builder A = new AlertDialog.Builder(context);
				 A.setPositiveButton(getString(R.string.yes), new
				 AlertDialog.OnClickListener() 
				 {				  
				  @Override public void onClick(DialogInterface dialog, int which) 
				  { 
					  try 
					  { 
						  if (libString.IsNullOrEmpty(vok.getFileName()) && vok.getURI()==null)
						  {
							SaveVokAs(true,false);
						  }
							else
						  {
							vok.SaveFile(vok.getFileName(),vok.getURI(), vok.getUniCode(),	false);
							vok.aend = false; 
							_backPressed += 1;
							handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);
							saveFilePrefs(false);
						  }
					  } 
					  catch (Exception e) 
					  { 
						  try 
						  {
							SaveVokAs(true,false);
						  } 
						  catch (Exception e1) 
						  {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							lib.ShowException(MainActivity.this, e1); 
						  }
					  } 
				  }
				 });
				 A.setNegativeButton(getString(R.string.no), new
				 AlertDialog.OnClickListener() 
				  {
					  @Override 
					  public void onClick(DialogInterface dialog, int which) 
					  { 
						  lib.ShowToast( MainActivity.this, 
								  MainActivity.this.getString(R.string.PressBackAgain)); 
						  _backPressed += 1;
						  handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000); 
					  }
				  }); 
				  A.setMessage(getString(R.string.Save));
				  A.setTitle("Question"); 
				  A.show(); 
				  if (!dontPrompt) 
				  { 
					  if (_backPressed > 0) 
					  { 
						  return true; 
					  } 
					  else 
					  {
						  lib.ShowToast(this, this.getString(R.string.PressBackAgain));
						  _backPressed += 1; 
						  handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000); }
				  
				  }
				 
			}

			if (dontPrompt) {
				try {
					if (libString.IsNullOrEmpty(vok.getFileName()) && vok.getURI()==null)
					{
						SaveVokAs(true,false);
					}
					else
					{
						vok.SaveFile();
						vok.aend = false;
						_backPressed += 1;
						handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);
						saveFilePrefs(false);
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					lib.ShowException(this, e);
				}
			}
			return false;
		} else {
			return true;
		}

	}

	
	public UncaughtExceptionHandler ErrorHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// TODO Auto-generated method stub
			lib.ShowException(MainActivity.this, ex);
		}
	};
	public void LoadVokabel(String fileSelected, Uri uri, int index, int[] Lernvokabeln,
			int Lernindex, boolean CardMode) {
		try 
		{
			if (fPA.fragMain!=null && fPA.fragMain.mainView!=null)
			{
				fPA.fragMain.setBtnsEnabled(false);
				fPA.fragMain.EndEdit(false);
			}
			if (uri==null) saveVok(false);
			try 
			{
				vok.LoadFile(this, fileSelected, uri, false, false, _blnUniCode);
			} 
			catch (RuntimeException ex) 
			{
				if (ex.getCause() != null) {
					if (ex.getCause().getMessage() != null
							&& ex.getCause().getMessage()
									.contains("IsSingleline")) {
						vok.LoadFile(this, fileSelected, uri, true, false, _blnUniCode);
					} else {
						throw ex;
					}
				} else {
					throw ex;
				}
			}

			if (vok.getCardMode() || CardMode) {
				if (fPA.fragMain!=null && fPA.fragMain.mainView!=null)fPA.fragMain.SetViewsToCardmode();
			} 
			else 
			{
				if (fPA.fragMain!=null && fPA.fragMain.mainView!=null)fPA.fragMain.SetViewsToVokMode();
			}

			// if (index >0 ) vok.setIndex(index);
			if (Lernvokabeln != null)
				vok.setLernvokabeln(Lernvokabeln);
			if (Lernindex > 0)
				vok.setLernIndex((short) Lernindex);
			if (vok.getGesamtzahl() > 0)
				{
				if (fPA.fragMain!=null && fPA.fragMain.mainView!=null)fPA.fragMain.setBtnsEnabled(true);
				}
			if (fPA.fragMain!=null && fPA.fragMain.mainView!=null) fPA.fragMain.getVokabel(false, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			lib.ShowException(this, e);
			if (fPA.fragMain!=null && fPA.fragMain.mainView!=null) fPA.fragMain.getVokabel(true, true);
		}
	}
	
	private int _backPressed;
	private Handler handlerbackpressed = new Handler();

	private synchronized boolean saveVok(boolean dontPrompt) throws Exception {
		if (fPA.fragMain!=null && fPA.fragMain.mainView!=null)fPA.fragMain.EndEdit(false);
		if (vok.aend) 
		{
			if (!dontPrompt) 
			{
				yesnoundefined res = lib.ShowMessageYesNo(this,
						getString(R.string.Save),"");
				if (res==yesnoundefined.undefined) return false;
				dontPrompt = res==yesnoundefined.yes;
				if (!dontPrompt) 
				{
					_backPressed += 1;
					lib.ShowToast(MainActivity.this, MainActivity.this
							.getString(R.string.PressBackAgain));
					handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);
					return true;
				}
				/*
				 * AlertDialog.Builder A = new AlertDialog.Builder(context);
				 * A.setPositiveButton(getString(R.string.yes), new
				 * AlertDialog.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { try { vok.SaveFile(vok.getFileName(),
				 * vok.getUniCode()); vok.aend = false; _backPressed += 1;
				 * Handler handler = new Handler();
				 * handler.postDelayed(rSetBackPressedFalse, 10000);
				 * saveFilePrefs(); } catch (Exception e) { // TODO
				 * Auto-generated catch block
				 * lib.ShowException(MainActivity.this, e); } } });
				 * A.setNegativeButton(getString(R.string.no), new
				 * AlertDialog.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { lib.ShowToast( MainActivity.this, MainActivity.this
				 * .getString(R.string.PressBackAgain)); _backPressed += 1;
				 * Handler handler = new Handler();
				 * handler.postDelayed(rSetBackPressedFalse, 10000); }
				 * 
				 * }); A.setMessage(getString(R.string.Save));
				 * A.setTitle("Question"); A.show(); if (!dontPrompt) { if
				 * (_backPressed > 0) { return true; } else {
				 * lib.ShowToast(this, this.getString(R.string.PressBackAgain));
				 * _backPressed += 1; handler = new Handler();
				 * handler.postDelayed(rSetBackPressedFalse, 10000); }
				 * 
				 * }
				 */
			}

			if (dontPrompt) 
			{
				try 
				{
					
					if (libString.IsNullOrEmpty(vok.getFileName()) && vok.getURI()==null)
					{
						SaveVokAs(true,false);
					}
					else
					{
						vok.SaveFile();
						vok.aend = false;
						_backPressed += 1;
						handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);
						saveFilePrefs(false);
						return true;
					}
				} 
				catch (Exception e) 
				{
					// TODO Auto-generated catch block
					//lib.ShowException(this, e);
					if (lib.ShowMessageYesNo(this, getString(R.string.msgFileCouldNotBeSaved),"")==yesnoundefined.yes)
					{
						SaveVokAs(true,false);
					}
				}
			}
			return false;
		} 
		else 
		{
			return true;
		}

	}
	
	private void saveFilePrefs(boolean isTmpFile) {
		Editor edit = prefs.edit();
		edit.putString("LastFile", vok.getFileName());
		edit.putInt("vokindex", vok.getIndex());
		edit.putInt("vokLastIndex", vok.getLastIndex());
		lib.putIntArrayToPrefs(prefs, vok.getLernvokabeln(), "Lernvokabeln");
		edit.putInt("Lernindex", vok.getLernIndex());
		edit.putBoolean("Unicode", vok.getUniCode());
		edit.putBoolean("isTmpFile", isTmpFile);
		edit.putBoolean("Cardmode", vok.getCardMode());
		edit.putBoolean("aend", vok.aend);
		if (vok.getURI()!= null) 
		{
			edit.putString("URI", vok.getURI().toString());
			try 
			{
				takePersistableUri(vok.getURI(),false);
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("saveFilePrefs",vok.getURI().toString(),e);
			}
			String FileName = lib.dumpUriMetaData(this, vok.getURI());
			if (FileName.contains(":")) FileName = FileName.split(":")[0];
			edit.putString("FileName", FileName);
		}
		else
		{
			edit.putString("URI", "");
			edit.putString("FileName", "");
		}
		edit.commit();
	}
	
	@SuppressLint("NewApi")
	private void takePersistableUri(Uri selectedUri, boolean force) throws Exception
	{
		if(Build.VERSION.SDK_INT>=19)
		{
			try
			{
				final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
			            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
				// Check for the freshest data.
				getContentResolver().takePersistableUriPermission(selectedUri, takeFlags);
			}
			catch (Exception ex)
			{
				Log.e("takePersistableUri", "Error", ex);
				if (force)lib.ShowException(this, ex);
			}

		}
	}


	
	private Runnable rSetBackPressedFalse = new Runnable() {
		@Override
		public void run() {
			/* do what you need to do */
			_backPressed = 0;
		}
	};

		
	public String JMGDataDirectory;
	public boolean isSmallDevice = false;
	
	private void CopyAssets() {
		libLearn.gStatus = "Copy Assets";
		File F = android.os.Environment.getExternalStorageDirectory();
		boolean successful = false;
		for (int i = 0; i<2; i++)
		{
			if(!F.exists()||i==1)
			{
				F= new File(getApplicationInfo().dataDir);
			}
			String extPath = F.getPath();
			JMGDataDirectory = Path.combine(extPath, "learnforandroid", "vok");
	
			if (F.isDirectory() && F.exists()) {
				File F1 = new File(JMGDataDirectory);
				if (F1.isDirectory() == false && !F1.exists()) {
					F1.mkdirs();
				}
				AssetManager A = this.getAssets();
				try {
					final String languages[] = new String[] { "Greek", "Hebrew",
							"KAR", "Spanish" };
					final String path = F1.getPath();
					for (String language : languages) {
						F1 = new File(Path.combine(path, language));
						for (String File : A.list(language)) {
							InputStream myInput = A.open(Path.combine(language,
									File));
							String outFileName = Path.combine(F1.getPath(), File);
							if (F1.isDirectory() == false) {
								F1.mkdirs();
							}
							// Open the empty db as the output stream
	
							File file = new File(outFileName);
	
							if (file.exists()) {
								// file.delete();
								successful = true;
							} else {
								file.createNewFile();
								OutputStream myOutput = new FileOutputStream(file);
	
								byte[] buffer = new byte[1024];
								int length;
								while ((length = myInput.read(buffer, 0, 1024)) > 0) {
									myOutput.write(buffer, 0, length);
								}
	
								// Close the streams
								myOutput.flush();
								myOutput.close();
								myInput.close();
								successful = true;
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					lib.ShowMessage(this, e.getMessage(),null);
					// lib.ShowMessage(this, "CopyAssets");
					successful = false;
				}
			}
			if (successful) break;
		}

	}
	private HashMap<ColorItems, ColorSetting> getColorsFromPrefs() {
		HashMap<ColorItems, ColorSetting> res = new HashMap<ColorItems, ColorSetting>();
		for (int i = 0; i < ColorSetting.ColorItems.values().length; i++) {
			ColorItems ColorItem = ColorSetting.ColorItems.values()[i];
			String Name = getResources().getStringArray(R.array.spnColors)[i];
			int defValue = 0;
			switch (ColorItem) {
			case word:
				defValue = 0xff000000;
				break;
			case meaning:
				defValue = 0xff000000;
				break;
			case comment:
				defValue = 0xff000000;
				break;
			case background:
				defValue = 0xffffffff;
				break;
			case background_wrong:
				defValue = 0xffc0c0c0;
				break;
			case box_word:
				defValue = 0xffffffff;
				break;
			case box_meaning:
				defValue = 0xffffffff;
				break;
			default:
				defValue = 0xff000000;
				break;
			}
			int Color = prefs.getInt(ColorItem.name(), defValue);
			res.put(ColorItem, new ColorSetting(ColorItem, Name, Color));
		}
		return res;

	}

	private HashMap<ColorItems, ColorSetting> getColorsFromIntent(Intent intent) {
		HashMap<ColorItems, ColorSetting> res = new HashMap<ColorItems, ColorSetting>();
		for (int i = 0; i < ColorSetting.ColorItems.values().length; i++) {
			ColorItems ColorItem = ColorSetting.ColorItems.values()[i];
			String Name = getResources().getStringArray(R.array.spnColors)[i];
			int defValue = 0;
			switch (ColorItem) {
			case word:
				defValue = 0xff000000;
				break;
			case meaning:
				defValue = 0xff000000;
				break;
			case comment:
				defValue = 0xff000000;
				break;
			case background:
				defValue = 0xffffffff;
				break;
			case background_wrong:
				defValue = 0xffc0c0c0;
				break;
			case box_word:
				defValue = 0xffffffff;
				break;
			case box_meaning:
				defValue = 0xffffffff;
				break;
			default:
				defValue = 0xff000000;
				break;
			}
			int Color = intent.getIntExtra(ColorItem.name(), defValue);
			res.put(ColorItem, new ColorSetting(ColorItem, Name, Color));
		}
		return res;

	}

	private HashMap<Sounds, SoundSetting> getSoundsFromIntent(Intent intent) {
		HashMap<Sounds, SoundSetting> res = new HashMap<Sounds, SoundSetting>();
		if (lib.AssetSounds[0] == null)
			lib.initSounds();
		for (int i = 0; i < lib.Sounds.values().length; i++) {
			Sounds SoundItem = Sounds.values()[i];
			String Name = getResources().getStringArray(R.array.spnSounds)[i];
			String defValue = "";
			defValue = lib.AssetSounds[SoundItem.ordinal()];
			String SoundPath = intent.getStringExtra(SoundItem.name());
			if (libString.IsNullOrEmpty(SoundPath)) {
				SoundPath = defValue;
			}
			res.put(SoundItem, new SoundSetting(SoundItem, Name, SoundPath));
		}
		return res;

	}

	private HashMap<Sounds, SoundSetting> getSoundsFromPrefs() {
		HashMap<Sounds, SoundSetting> res = new HashMap<Sounds, SoundSetting>();
		if (lib.AssetSounds[0] == null)
			lib.initSounds();
		for (int i = 0; i < lib.Sounds.values().length; i++) {
			Sounds SoundItem = Sounds.values()[i];
			String Name = getResources().getStringArray(R.array.spnSounds)[i];
			String defValue = "";
			defValue = lib.AssetSounds[SoundItem.ordinal()];
			String SoundPath = prefs.getString(SoundItem.name(), defValue);
			res.put(SoundItem, new SoundSetting(SoundItem, Name, SoundPath));
		}
		return res;

	}
	
	private static final int EDIT_REQUEST_CODE = 0x3abd;
	@SuppressLint("InlinedApi")
	public void SaveVokAs(boolean blnUniCode, boolean blnNew) throws Exception 
	{
		boolean blnActionCreateDocument = false;
		try
		{
			fPA.fragMain.EndEdit(false);
			if (!libString.IsNullOrEmpty(vok.getFileName()) || vok.getURI()==null || Build.VERSION.SDK_INT<19)
			{
				boolean blnSuccess = false;
				for (int i = 0; i<2; i++)
				{
					try
					{
						String key = "AlwaysStartExternalProgram";
						int AlwaysStartExternalProgram = prefs.getInt(key, 999);
						lib.YesNoCheckResult res = null;
						if (AlwaysStartExternalProgram==999 && !(vok.getURI()!=null && i == 1))
						{
							res = lib.ShowMessageYesNoWithCheckbox(this, "", getString(R.string.msgStartExternalProgram), getString(R.string.msgRememberChoice));
							if (res.res==yesnoundefined.undefined) return;
							if (res.checked) prefs.edit().putInt(key, res.res==yesnoundefined.yes?-1:0).commit();
						}
						else
						{
							yesnoundefined par = yesnoundefined.undefined;
							if (AlwaysStartExternalProgram==-1)par=yesnoundefined.yes;
							if (AlwaysStartExternalProgram==0)par=yesnoundefined.no;
							res = new lib.YesNoCheckResult(par, true);
						}
						if ((vok.getURI()!=null && i == 1) || res.res==yesnoundefined.no)
						{
							Intent intent = new Intent(this, AdvFileChooser.class);
							ArrayList<String> extensions = new ArrayList<String>();
							extensions.add(".k??");
							extensions.add(".v??");
							extensions.add(".K??");
							extensions.add(".V??");
							extensions.add(".KAR");
							extensions.add(".VOK");
							extensions.add(".kar");
							extensions.add(".vok");
							extensions.add(".dic");
							extensions.add(".DIC");
							
							if (libString.IsNullOrEmpty(vok.getFileName()))
							{
								if (vok.getURI()!=null)
								{
									intent.setData(vok.getURI());
								}
							}
							else
							{
								File F = new File(vok.getFileName());
								Uri uri = Uri.fromFile(F);
								intent.setData(uri);
							}
							intent.putExtra("URIName", vok.getURIName());
							intent.putStringArrayListExtra("filterFileExtension", extensions);
							intent.putExtra("blnUniCode", blnUniCode);
							intent.putExtra("DefaultDir",
									new File(JMGDataDirectory).exists() ? JMGDataDirectory
											: "/sdcard/");
							intent.putExtra("selectFolder", false);
							intent.putExtra("blnNew", blnNew);
							if (_blnUniCode)
								_oldUniCode = yesnoundefined.yes;
							else
								_oldUniCode = yesnoundefined.no;
							_blnUniCode = blnUniCode;
				
							this.startActivityForResult(intent, FILE_CHOOSERADV);
							blnSuccess = true;
						}
						else if (Build.VERSION.SDK_INT<19)
						{
							//org.openintents.filemanager
							Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
							intent.setData(vok.getURI());
							intent.putExtra("org.openintents.extra.WRITEABLE_ONLY", true);
							intent.putExtra("org.openintents.extra.TITLE", getString(R.string.SaveAs));
			                intent.putExtra("org.openintents.extra.BUTTON_TEXT", getString(R.string.btnSave));
			                intent.setType("*/*");
			                Intent chooser = Intent.createChooser(intent, getString(R.string.SaveAs));
			        		if (intent.resolveActivity(context.getPackageManager()) != null) 
			        		{ 
			        			startActivityForResult(chooser, FILE_OPENINTENT);
			        			blnSuccess=true;
			        		}
			        		else
			        		{
			        			lib.ShowToast(this, getString(R.string.InstallFilemanager));
			        			intent.setData(null);
			        			intent.removeExtra("org.openintents.extra.WRITEABLE_ONLY");
								intent.removeExtra("org.openintents.extra.TITLE");
				                intent.removeExtra("org.openintents.extra.BUTTON_TEXT");
				                	
			        			startActivityForResult(chooser, FILE_OPENINTENT);
			        			blnSuccess=true;
			        		}
			                       
						}
						else
						{
							blnActionCreateDocument = true;
							blnSuccess = true;
						}
					}
					catch(Exception ex)
					{
						blnSuccess=false;
						Log.e("SaveAs",ex.getMessage(),ex);
						if (i==1)
						{
							lib.ShowException(this, ex);
						}
					}
					
					if (blnSuccess) break;
				}
				
	
			}
			else if (Build.VERSION.SDK_INT>=19)
			{
				blnActionCreateDocument = true;
			}
			if (blnActionCreateDocument == true)
			{
				/**
				 * Open a file for writing and append some text to it.
				 */
			    // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
			    // file browser.
				Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
			    // Create a file with the requested MIME type.
			    String defaultURI = prefs.getString("defaultURI", "");
			    if (!libString.IsNullOrEmpty(defaultURI))
				{
			    	String FName="";
			    	if (vok.getURI()!=null)
			    	{
			    		String path2 = lib.dumpUriMetaData(this, vok.getURI());
						if(path2.contains(":")) path2 = path2.split(":")[0];
						FName = path2.substring(path2.lastIndexOf("/")+1);    	
			    	}
			    	else if (!libString.IsNullOrEmpty(vok.getFileName()))
			    	{
			    		FName = new File(vok.getFileName()).getName();
			    	}
			    	intent.putExtra(Intent.EXTRA_TITLE, FName);
				    //defaultURI = (!defaultURI.endsWith("/")?defaultURI.substring(0,defaultURI.lastIndexOf("/")+1):defaultURI);
					Uri def = Uri.parse(defaultURI);
					intent.setData(def);
				}
				else
				{
					//intent.setType("file/*");
				}
	
			    // Filter to only show results that can be "opened", such as a
			    // file (as opposed to a list of contacts or timezones).
			    intent.addCategory(Intent.CATEGORY_OPENABLE);
	
			    // Filter to show only text files.
			    intent.setType("*/*");
	
			    startActivityForResult(intent, EDIT_REQUEST_CODE);
				
			}
		}
		catch (Exception ex)
		{
			libLearn.gStatus= "SaveVokAs";
			lib.ShowException(this, ex);
		}
	}
	public Menu OptionsMenu;
	public MenuItem mnuAddNew;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		try {
			getMenuInflater().inflate(R.menu.main, menu);
			//findViewById(R.menu.main).setBackgroundColor(Color.BLACK);
			//.setBackgroundColor(Color.BLACK);
			//resize();
			OptionsMenu = menu;
			mnuAddNew = menu.findItem(R.id.mnuAddWord);
			return true;
		} catch (Exception ex) {
			lib.ShowException(this, ex);
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		try {
			if (id == R.id.action_settings) {
				mPager.setCurrentItem(SettingsActivity.fragID);
			} 
			else if (id == R.id.mnuContact)
			{
				Intent intent = new Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", "jhmgbl@gmail.com", null));
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jhmgbl@gmail.com"});
				String versionName = context.getPackageManager()
					    .getPackageInfo(context.getPackageName(), 0).versionName;
				intent.putExtra(Intent.EXTRA_SUBJECT, "learnforandroid " + versionName);
				intent.putExtra(Intent.EXTRA_TEXT, "If you send me a vocabulary file, I will convert it for you.");
				this.startActivity(Intent.createChooser(intent, "Send mail..."));
			}
			else if (id == R.id.mnuFileOpen) 
			{
				mPager.setCurrentItem(fragFileChooser.fragID);
				//LoadFile(true);
			} 
			else if (id == R.id.mnuHome)
			{
				mPager.setCurrentItem(_MainActivity.fragID);
			}
			else if (id == R.id.mnuOpenUri) 
			{
				if (saveVok(false))
				{
					String defaultURI = prefs.getString("defaultURI", "");
					Uri def;
					if (libString.IsNullOrEmpty(defaultURI))
					{
						File F = new File(JMGDataDirectory);
						def = Uri.fromFile(F);
					}
					else
					{
						def = Uri.parse(defaultURI);								
					}
					lib.SelectFile(this,def);
				}
			} else if (id == R.id.mnuNew) {
				if (saveVok(false))
				{
					newvok();
				}
				
			} else if (id == R.id.mnuAddWord) {
				mPager.setCurrentItem(_MainActivity.fragID);
				if (fPA.fragMain!=null && fPA.fragMain.mainView!=null)
				{
					fPA.fragMain.EndEdit(false);
					vok.AddVokabel();
					fPA.fragMain.getVokabel(true, false);
					fPA.fragMain.StartEdit();
				}
				
			} else if (id == R.id.mnuFileOpenASCII) {
				LoadFile(false);
			} else if (id == R.id.mnuConvMulti) {
				if (fPA.fragMain!=null && fPA.fragMain.mainView!=null)
				{
					vok.ConvertMulti();
					fPA.fragMain.getVokabel(false, false);
				}
			} else if (id == R.id.mnuFileSave) {
				saveVok(false);
			} else if (id == R.id.mnuSaveAs) {
				SaveVokAs(true,false);
			} else if (id == R.id.mnuRestart) {
				vok.restart();
			} 
			else if (id == R.id.mnuDelete) 
			{
				if (fPA.fragMain!=null && fPA.fragMain.mainView!=null)
				{
					vok.DeleteVokabel();
					fPA.fragMain.EndEdit2();
				}
			} 
			else if (id == R.id.mnuReverse) 
			{
				if (fPA.fragMain!=null && fPA.fragMain.mainView!=null)
				{
					vok.revert();
					fPA.fragMain.getVokabel(false, false);
				}
			} else if (id == R.id.mnuReset) {
				if (lib.ShowMessageYesNo(this,
						this.getString(R.string.ResetVocabulary),"")==yesnoundefined.yes) {
					vok.reset();
				}

			} else if (id == R.id.mnuStatistics) {
				if (vok.getGesamtzahl() > 5) {
					try {
						/*
						IDemoChart chart = new org.de.jmg.learn.chart.LearnBarChart();
						Intent intent = chart.execute(this);
						this.startActivity(intent);
						*/
						mPager.setCurrentItem(fragStatistics.fragID);
					} catch (Exception ex) {
						lib.ShowException(this, ex);
					}

				}
			}

		} catch (Exception ex) {
			lib.ShowException(this, ex);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public Intent getSettingsIntent() 
	{
		libLearn.gStatus = "initializing SettingsFragment";
		Intent intent = new Intent(this, SettingsActivity.class);
		intent.putExtra("Abfragebereich", vok.getAbfragebereich());
		intent.putExtra("CharsetASCII", vok.CharsetASCII);
		intent.putExtra("Step", vok.getSchrittweite());
		intent.putExtra("DisplayDurationWord", DisplayDurationWord);
		intent.putExtra("DisplayDurationBed", DisplayDurationBed);
		intent.putExtra("PaukRepetitions", PaukRepetitions);
		float ProbFact = vok.ProbabilityFactor;
		intent.putExtra("ProbabilityFactor", ProbFact);
		intent.putExtra("RestartInterval", vok.RestartInterval);
		intent.putExtra("Random", vok.getAbfrageZufaellig());
		intent.putExtra("AskAll", vok.getAskAll());
		intent.putExtra("Sound", lib.sndEnabled);
		intent.putExtra("Language", vok.getSprache().ordinal());
		int ShowAlwaysDocumentProvider = prefs.getInt("ShowAlwaysDocumentProvider", 999);
		intent.putExtra("ShowAlwaysDocumentProvider", ShowAlwaysDocumentProvider);
		String key = "DontShowPersistableURIMessage";
		int DontShowPersistableURIMessage = prefs.getInt(key, 999);
		intent.putExtra(key, DontShowPersistableURIMessage);
		key = "AlwaysStartExternalProgram";
		int AlwaysStartExternalProgram = prefs.getInt(key, 999);
		intent.putExtra(key, AlwaysStartExternalProgram);
		intent.putExtra("nghs", prefs.getBoolean("nghs", true));
		intent.putExtra("fora", prefs.getBoolean("fora", true));
		intent.putExtra("translate", prefs.getBoolean("translate", true));
		//fPA.fragSettings.init(intent, Settings_Activity);
		return intent;
	}
	public boolean checkLoadFile() throws Exception
	{
		boolean blnLoadFile = false;
		if (vok.aend && libString.IsNullOrEmpty(vok.getFileName()) && vok.getURI()==null)
		{
			if (lib.ShowMessageYesNo(this, getString(R.string.SaveNewVokabularyAs),"")==yesnoundefined.yes)
			{
				SaveVokAs(true,false);
			}
			else
			{
				vok.aend = false;
				blnLoadFile = true;
			}
		}
		else
		{
			blnLoadFile = true;
		}
		return blnLoadFile;
	}
	
	public void LoadFile(boolean blnUniCode) throws Exception {
		
		if (checkLoadFile())
		{			
			Intent intent = getFileChooserIntent(blnUniCode);
			this.startActivityForResult(intent, FILE_CHOOSER);

		}
	}

	public Intent getFileChooserIntent(boolean blnUniCode)
	{
		Intent intent = new Intent(this, FileChooser.class);
		ArrayList<String> extensions = new ArrayList<String>();
		extensions.add(".k??");
		extensions.add(".v??");
		extensions.add(".K??");
		extensions.add(".V??");
		extensions.add(".KAR");
		extensions.add(".VOK");
		extensions.add(".kar");
		extensions.add(".vok");
		extensions.add(".dic");
		extensions.add(".DIC");

		intent.putStringArrayListExtra("filterFileExtension", extensions);
		intent.putExtra("blnUniCode", blnUniCode);
		intent.putExtra("DefaultDir",
				(JMGDataDirectory!= null && new File(JMGDataDirectory).exists()) ? JMGDataDirectory
						: "/sdcard/");
		if (_blnUniCode)
			_oldUniCode = yesnoundefined.yes;
		else
			_oldUniCode = yesnoundefined.no;
		_blnUniCode = blnUniCode;
		return intent;
	}
	
	private void newvok() throws Exception
	{
		mPager.setCurrentItem(_MainActivity.fragID);
		vok.NewFile();
		yesnoundefined res = lib.ShowMessageYesNo(this, getString(R.string.txtFlashCardFile),"");
		if (res==yesnoundefined.undefined)return;
		if (res == yesnoundefined.yes)
		{
			vok.setCardMode(true);
			fPA.fragMain.SetViewsToCardmode();
		}
		else
		{
			vok.setCardMode(false);
			fPA.fragMain.SetViewsToVokMode();
		}
		vok.AddVokabel();
		fPA.fragMain.getVokabel(true, false);
		fPA.fragMain.StartEdit();
	}
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if ((requestCode == FILE_CHOOSER)
					&& (resultCode == Activity.RESULT_OK)) {
				mPager.setCurrentItem(_MainActivity.fragID);
				String fileSelected = data.getStringExtra("fileSelected");
				_blnUniCode = data.getBooleanExtra("blnUniCode", true);
				LoadVokabel(fileSelected, null, 1, null, 0, false);

			}
			else if (requestCode == SettingsActivity.FILE_CHOOSERSOUND)
			{
				if (fPA.fragSettings!=null && fPA.fragSettings.SettingsView!=null)
					fPA.fragSettings.onActivityResult(requestCode, resultCode, data);
			}
			else if ((requestCode == FILE_CHOOSERADV)
					&& (resultCode == Activity.RESULT_OK)) {
				final String fileSelected = data.getStringExtra("fileSelected");
				_blnUniCode = data.getBooleanExtra("blnUniCode", true);
				final boolean blnNew = data.getBooleanExtra("blnNew",false);
				if (!libString.IsNullOrEmpty(fileSelected)) 
				{
					mPager.setCurrentItem(_MainActivity.fragID);
					String value = fileSelected;
					value = value.replace("\n", "");
					try {
						if (vok.getCardMode())
						{
							if (!lib.ExtensionMatch(value, "k??") && !lib.ExtensionMatch(value, "v??"))
							{
								value += ".kar";
							}
						}
						else
						{
							if (!lib.ExtensionMatch(value, "v??") && !lib.ExtensionMatch(value, "k??"))
							{
								value += ".vok";
							}
						}
						File F = new File(value);
						if (!F.isDirectory()
								&& (!F.exists() || lib
										.ShowMessageYesNo(
												MainActivity.this,
												getString(R.string.Overwrite),"")==yesnoundefined.yes)) {
							File ParentDir = F.getParentFile();
							if (!ParentDir.exists())
								ParentDir.mkdirs();
							libLearn.gStatus = "onActivityResult SaveFile";
							vok.SaveFile(F.getPath(), vok.getURI(),
									_blnUniCode, false);
							libLearn.gStatus = "onActivityResult SaveFilePrefs";
							saveFilePrefs(false);
							if (blnNew)
							{
								newvok();
							}
							libLearn.gStatus = "onActivityResult SetActionbarTitle";
							if(fPA.fragMain!=null && fPA.fragMain.mainView!=null) 
								fPA.fragMain.SetActionBarTitle();
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						lib.ShowException(MainActivity.this, e);
						e.printStackTrace();
					}
				}
					/* AlertDialog.Builder alert = new AlertDialog.Builder(this);

					alert.setTitle(getString(R.string.SaveAs));
					alert.setMessage(getString(R.string.EnterNewFilename)
							+ ": " + fileSelected);

					// Set an EditText view to get user input
					final EditText input = new EditText(this);
					input.setLines(1);
					input.setSingleLine();
					alert.setView(input);
					if (vok.getURI()!=null && libString.IsNullOrEmpty(vok.getFileName()))
					{
						String path = lib.dumpUriMetaData(this, vok.getURI());
						if (path.contains(":")) path = path.split(":")[0];
						input.setText(path);
					}
					else
					{
						input.setText(new File(vok.getFileName()).getName());
					}
					
					alert.setPositiveButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String value = input.getText().toString();
									value = value.replace("\n", "");
									try {
										if (vok.getCardMode())
										{
											if (!lib.ExtensionMatch(value, "k??"))
											{
												value += ".kar";
											}
										}
										else
										{
											if (!lib.ExtensionMatch(value, "v??"))
											{
												value += ".vok";
											}
										}
										File F = new File(Path.combine(
												fileSelected, value));
										if (!F.isDirectory()
												&& (!F.exists() || lib
														.ShowMessageYesNo(
																MainActivity.this,
																getString(R.string.Overwrite),""))) {
											File ParentDir = F.getParentFile();
											if (!ParentDir.exists())
												ParentDir.mkdirs();
											
											vok.SaveFile(F.getPath(), vok.getURI(),
													_blnUniCode, false);
											saveFilePrefs(false);
											if (blnNew)
											{
												newvok();
											}
											SetActionBarTitle();
										}

									} catch (Exception e) {
										// TODO Auto-generated catch block
										lib.ShowException(MainActivity.this, e);
										e.printStackTrace();
									}
								}
							});

					alert.setNegativeButton(getString(R.string.btnCancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) 
								{
									if(blnNew)
										try {
											newvok();
										} catch (Exception e) {
											lib.ShowException(MainActivity.this, e);
										}
								}
							});

					alert.show();
				*/
				
				else
				{
					if(blnNew)
						try {
							newvok();
						} catch (Exception e) {
							lib.ShowException(MainActivity.this, e);
						}
				}

			}

			else if (resultCode == RESULT_OK && requestCode == lib.SELECT_FILE && data!=null) 
			{
				Uri selectedUri = data.getData();
				String strUri = selectedUri.toString();
				String path = lib.dumpUriMetaData(this, selectedUri);
				if(path.contains(":")) path = path.split(":")[0];
				if (lib.RegexMatchVok(path) || lib.ShowMessageYesNo(this, getString(R.string.msgWrongExtLoad),"")==yesnoundefined.yes)
				{
					mPager.setCurrentItem(_MainActivity.fragID);
					LoadVokabel(null,selectedUri, 1, null, 0, false);
					takePersistableUri(selectedUri,false);
					prefs.edit().putString("defaultURI",strUri).commit();
				}
				
			}
			else if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE && data!=null) 
			{
				Uri selectedUri = data.getData();
				String strUri = selectedUri.toString();
				vok.setURIName("");
				String path = lib.dumpUriMetaData(this, selectedUri);
				if(path.contains(":")) path = path.split(":")[0];
				boolean blnWrongExt = false;
				if (vok.getCardMode())
				{
					if (!lib.ExtensionMatch(path, "k??"))
					{
						blnWrongExt=true;
						//value += ".kar";
					}
				}
				else
				{
					if (!lib.ExtensionMatch(path, "v??"))
					{
						//value += ".vok";
						blnWrongExt=true;
					}
				}
				
				
				if (!blnWrongExt||lib.ShowMessageYesNo(this, getString(R.string.msgWrongExt),"")==yesnoundefined.yes)
				{
					try
					{
						mPager.setCurrentItem(_MainActivity.fragID);
						takePersistableUri(selectedUri,false);
						vok.SaveFile(null, selectedUri,
								_blnUniCode, false);
						saveFilePrefs(false);
						//if (blnNew) newvok();
						if (fPA.fragMain!=null&&fPA.fragMain.mainView!=null)fPA.fragMain.SetActionBarTitle();
						prefs.edit().putString("defaultURI",strUri).commit();
					}
					catch (Exception ex)
					{
						if (ex.getMessage().equalsIgnoreCase("SaveVokError") && ex.getCause()!=null )
						{
							lib.ShowMessage(this, getString(R.string.msgFileCouldNotBeSaved2) 
									+ "\n"  + selectedUri.toString() + "\n" + ex.getCause().getMessage()
									, getString(R.string.Error));
						}
						else
						{
							lib.ShowMessage(this, getString(R.string.msgFileCouldNotBeSaved2) 
									+ "\n"  + selectedUri.toString() + "\n" + ex.getMessage()
									, getString(R.string.Error));
							//lib.ShowMessage(this, ex.getMessage(), this.getString(R.string.Error));
						}
					}
				}
			}
			else if (resultCode == RESULT_OK && requestCode == FILE_OPENINTENT && data!=null) 
			{
				Uri selectedUri = data.getData();
				try
				{
					
					String strUri = selectedUri.toString();
					vok.setURIName("");
					String path = lib.dumpUriMetaData(this, selectedUri);
					if(path.contains(":")) path = path.split(":")[0];
					boolean blnWrongExt = false;
					if (vok.getCardMode())
					{
						if (!lib.ExtensionMatch(path, "k??"))
						{
							blnWrongExt=true;
							//value += ".kar";
						}
					}
					else
					{
						if (!lib.ExtensionMatch(path, "v??"))
						{
							//value += ".vok";
							blnWrongExt=true;
						}
					}
					
					
					if (!blnWrongExt||lib.ShowMessageYesNo(this, getString(R.string.msgWrongExt),"")==yesnoundefined.yes)
					{
						try
						{
							mPager.setCurrentItem(_MainActivity.fragID);
							takePersistableUri(selectedUri,false);
							vok.SaveFile(null, selectedUri,
									_blnUniCode, false);
							saveFilePrefs(false);
							//if (blnNew) newvok();
							if(fPA.fragMain!=null&&fPA.fragMain.mainView!=null)fPA.fragMain.SetActionBarTitle();
							prefs.edit().putString("defaultURI",strUri).commit();
						}
						catch (Exception ex)
						{
							if (ex.getMessage().equalsIgnoreCase("SaveVokError") && ex.getCause()!=null )
							{
								lib.ShowMessage(this, getString(R.string.msgFileCouldNotBeSaved2) 
										+ "\n"  + selectedUri.toString() + "\n" + ex.getCause().getMessage()
										, getString(R.string.Error));
							}
							else
							{
								lib.ShowMessage(this, getString(R.string.msgFileCouldNotBeSaved2) 
										+ "\n"  + selectedUri.toString() + "\n" + ex.getMessage()
										, getString(R.string.Error));
								//lib.ShowMessage(this, ex.getMessage(), this.getString(R.string.Error));
							}
						}
					}
				}
				catch(Exception e)
				{
					if (e.getMessage().equalsIgnoreCase("SaveVokError") && e.getCause()!=null && e.getCause() instanceof IOException)
					{
						lib.ShowMessage(this, getString(R.string.msgFileCouldNotBeSaved2) 
								+ "\n"  + selectedUri.toString() + "\n" + e.getCause().getMessage()
								, getString(R.string.Error));
					}
					else
					{
						lib.ShowException(MainActivity.this, e);
					}
				}
			}
		}
		catch (Exception e) 
		{
			lib.ShowException(MainActivity.this, e);
		}
	}

	void processSettingsIntent(Intent data) throws Exception
	{
		if (data.getStringExtra("OK")=="OK")
		{
			libLearn.gStatus = "getting values from intent";
			int oldAbfrage = vok.getAbfragebereich();
			vok.setAbfragebereich(data.getExtras().getShort(
					"Abfragebereich"));
			if (oldAbfrage != vok.getAbfragebereich())
			{
				vok.ResetAbfrage();
			}
			short Schrittweite = data.getExtras().getShort("Step");
			if (Schrittweite != vok.getSchrittweite()) {
				vok.setSchrittweite(Schrittweite);
				vok.InitAbfrage();
			}
			vok.CharsetASCII = (data.getExtras().getString("CharsetASCII"));
			DisplayDurationWord = data.getExtras().getFloat(
					"DisplayDurationWord");
			DisplayDurationBed = data.getExtras().getFloat(
					"DisplayDurationBed");
			PaukRepetitions = data.getExtras().getInt("PaukRepetitions");
			vok.RestartInterval = data.getExtras().getInt("RestartInterval");
			vok.ProbabilityFactor = data.getExtras().getFloat(
					"ProbabilityFactor");
			vok.setAbfrageZufaellig(data.getExtras().getBoolean("Random"));
			vok.setAskAll(data.getExtras().getBoolean("AskAll"));
			int Language = data.getExtras().getInt("Language",Vokabel.EnumSprachen.undefiniert.ordinal());
			for (int i = 0; i < Vokabel.EnumSprachen.values().length; i++)
			{
				if (Vokabel.EnumSprachen.values()[i].ordinal() == Language)
				{
					vok.setSprache(Vokabel.EnumSprachen.values()[i]);
					break;
				}
			}
			lib.sndEnabled = data.getExtras().getBoolean("Sound");
			Colors = getColorsFromIntent(data);
			colSounds = getSoundsFromIntent(data);
			final String keyProvider = "ShowAlwaysDocumentProvider";
			int ShowAlwaysDocumentProvider = data.getExtras().getInt(keyProvider, 999);
			final String keyURIMessage = "DontShowPersistableURIMessage";
			int DontShowPersistableURIMessage = data.getExtras().getInt(keyURIMessage, 999);
			final String key = "AlwaysStartExternalProgram";
			int AlwaysStartExternalProgram = data.getExtras().getInt(key, 999);
			libLearn.gStatus = "writing values to prefs";
			Editor editor = prefs.edit();
			editor.putInt("Schrittweite", vok.getSchrittweite());
			editor.putString("CharsetASCII", vok.CharsetASCII);
			editor.putInt("Abfragebereich", vok.getAbfragebereich());
			editor.putFloat("DisplayDurationWord", DisplayDurationWord);
			editor.putFloat("DisplayDurationBed", DisplayDurationBed);
			editor.putInt("PaukRepetitions", PaukRepetitions);
			editor.putFloat("ProbabilityFactor", vok.ProbabilityFactor);
			editor.putBoolean("Random", vok.getAbfrageZufaellig());
			editor.putBoolean("AskAll", vok.getAskAll());
			editor.putBoolean("Sound", lib.sndEnabled);
			editor.putInt(keyProvider, ShowAlwaysDocumentProvider);
			editor.putInt(keyURIMessage, DontShowPersistableURIMessage);
			editor.putInt(key, AlwaysStartExternalProgram);
			editor.putBoolean("nghs", data.getExtras().getBoolean("nghs"));
			editor.putBoolean("fora", data.getExtras().getBoolean("fora"));
			editor.putBoolean("translate", data.getExtras().getBoolean("translate"));
			editor.putInt("RestartInterval", vok.RestartInterval);
			for (ColorItems item : Colors.keySet()) {
				editor.putInt(item.name(), Colors.get(item).ColorValue);
			}
	
			for (Sounds item : colSounds.keySet()) {
				editor.putString(item.name(), colSounds.get(item).SoundPath);
			}
	
			editor.commit();
			libLearn.gStatus = "setTextColors";
			if (fPA.fragMain!=null && fPA.fragMain.mainView!=null) fPA.fragMain.setTextColors();
			libLearn.gStatus = "getVokabel";
			if (fPA.fragMain!=null && fPA.fragMain.mainView!=null) fPA.fragMain.getVokabel(false, false);
		}

	}

	public void setSoundDir(String dir) 
	{
		SoundDir = dir;
		prefs.edit().putString("SoundDir", dir).commit();
	}

}