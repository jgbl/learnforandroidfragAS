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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.de.jmg.learn.vok.Vokabel;
import org.de.jmg.lib.ColorSetting;
import org.de.jmg.lib.ColorSetting.ColorItems;
import org.de.jmg.lib.Path;
import org.de.jmg.lib.SoundSetting;
import org.de.jmg.lib.lib;
import org.de.jmg.lib.lib.Sounds;
import org.de.jmg.lib.lib.libString;
import org.de.jmg.lib.lib.yesnoundefined;
import org.liberty.android.fantastischmemo.downloader.quizlet.LoginQuizletActivity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import br.com.thinkti.android.filechooser.AdvFileChooser;
import br.com.thinkti.android.filechooser.FileChooser;
import br.com.thinkti.android.filechooserfrag.Data;
import br.com.thinkti.android.filechooserfrag.fragFileChooser;
import br.com.thinkti.android.filechooserfrag.fragFileChooserQuizlet;


public class MainActivity extends AppCompatActivity
{

    public static final int FILE_CHOOSER = 34823;
    public static final int Settings_Activity = 34824;
    public static final int FILE_CHOOSERADV = 34825;
    public static final int FILE_OPENINTENT = 34826;
    public static final int LOGINQUIZLETINTENT = 34827;
    private static final int EDIT_REQUEST_CODE = 0x3abd;
    public ViewPager mPager;
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
    public boolean isAndroidWear;
    public boolean isTV;
    public boolean isWatch;
    public boolean blnTextToSpeech;
    public float ActionBarOriginalTextSize;
    public String QuizletUser = null;
    public String QuizletAccessToken = null;
    public TextToSpeech tts;
    public String JMGDataDirectory;
    public boolean isSmallDevice = false;
    public Menu OptionsMenu;
    public MenuItem mnuAddNew;
    public MenuItem mnuUploadToQuizlet;
    boolean _blnUniCode = true;
    yesnoundefined _oldUniCode = yesnoundefined.undefined;
    AlertDialog dlg = null;
    private Thread.UncaughtExceptionHandler defaultErrorHandler;
    public UncaughtExceptionHandler ErrorHandler = new UncaughtExceptionHandler()
    {

        @Override
        public void uncaughtException(Thread thread, Throwable ex)
        {

            ex.printStackTrace();
            Log.e("uncaught", ex.getMessage(), ex);
            //lib.ShowException(MainActivity.this, ex);
			/*
			final Intent crashedIntent = new Intent(MainActivity.this, ExceptionActivity.class);
			//crashedIntent.setAction("org.de.jmg.errorintent");
			crashedIntent.putExtra("message", ex.getMessage() + "\n"
					+ (ex.getCause() == null ? "" : ex.getCause().getMessage())
					+ "\nStatus: " + libLearn.gStatus
					+ "\n" + Log.getStackTraceString(ex));
			//noinspection deprecation
			//crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			//crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			crashedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

			if(isUIThread()) {
				startActivity(crashedIntent);
			}else{  //handle non UI thread throw uncaught exception

				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						startActivity(crashedIntent);
					}
				});
			}
			*/
            defaultErrorHandler.uncaughtException(thread, ex);
        }
    };
    private Context context = this;
    private boolean _blnEink;
    private int _backPressed;
    private Handler handlerbackpressed = new Handler();
    private Runnable rSetBackPressedFalse = new Runnable()
    {
        @Override
        public void run()
        {
			/* do what you need to do */
            _backPressed = 0;
        }
    };
    private android.support.v7.widget.ActionMenuView ActionMenu = null;
    private boolean _blnReverse = false;
    private int _invisibleCount = 0;
    private boolean _blnPrivate = false;
    private boolean _blnVerifyToken = false;

    //Initialisation
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        lib.main = this;

        //getting the kind of userinterface: television or watch or else
        int UIMode = lib.getUIMode(this);
        switch (UIMode)
        {
            case Configuration.UI_MODE_TYPE_TELEVISION:
                isTV = true;
                break;
            case Configuration.UI_MODE_TYPE_WATCH:
                isWatch = true;
                break;
        }

        if (savedInstanceState != null)
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

        setPageChangedListener();

        /** Creating an instance of FragmentPagerAdapter */
        if (fPA == null) fPA = new MyFragmentPagerAdapter(fm, this, savedInstanceState != null);

        /** Setting the FragmentPagerAdapter object to the viewPager object */
        mPager.setAdapter(fPA);


        libLearn.gStatus = "onCreate getEink";
        try
        {
            _blnEink = getWindowManager().getDefaultDisplay().getRefreshRate() < 5.0;
            if (_blnEink)
                lib.ShowToast(this, "This is an Eink diplay!");
        }
        catch (Exception ex)
        {
            lib.ShowException(this, ex);
        }

        try
        {
            libLearn.gStatus = "onCreate setContentView";
            mainView = findViewById(Window.ID_ANDROID_CONTENT);
            defaultErrorHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(ErrorHandler);

            // View LayoutMain = findViewById(R.id.layoutMain);

            processPreferences();

            libLearn.gStatus = "onCreate Copy Assets";
            CopyAssets();


            try
            {
                processBundle(savedInstanceState);
            }
            catch (Exception e)
            {

                e.printStackTrace();
                lib.ShowException(this, e);
            }
            //InitSettings();
            Intent intent = getIntent();
            if (Intent.ACTION_VIEW.equals(intent.getAction()) )
            {
                Uri uri = intent.getData();
                if (uri.toString().startsWith("loginquizlet:/"))
                {
                    loginQuizlet = new LoginQuizletActivity();
                    this.onNewIntent(intent);
                }

                //finish();
            }


        }
        catch (Exception ex)
        {
            lib.ShowException(this, ex);
        }

    }

    private void processBundle(Bundle savedInstanceState) throws Exception
    {
        final String tmppath = Path.combine(getApplicationInfo().dataDir, "vok.tmp");

        boolean CardMode;

        if (savedInstanceState != null)
        {
            libLearn.gStatus = "onCreate Load SavedInstanceState";
            ActionBarOriginalTextSize = savedInstanceState.getFloat("ActionBarOriginalTextSize");
            String filename = savedInstanceState.getString("vokpath");
            Uri uri = null;
            String strURI = savedInstanceState.getString("URI");
            if (!libString.IsNullOrEmpty(strURI)) uri = Uri.parse(strURI);
            int index = savedInstanceState.getInt("vokindex");
            int[] Lernvokabeln = savedInstanceState
                    .getIntArray("Lernvokabeln");
            int Lernindex = savedInstanceState.getInt("Lernindex");
            CardMode = savedInstanceState.getBoolean("Cardmode", false);
            if (index > 0)
            {
                _blnUniCode = savedInstanceState.getBoolean(
                        "Unicode", true);
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
            String strURI = prefs.getString("URI", "");
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
                    lib.CheckPermissions(this, uri, false);
                }

                int index = prefs.getInt("vokindex", 1);
                int Lernindex = prefs.getInt("Lernindex", 0);
                _blnUniCode = prefs.getBoolean("Unicode", true);
                boolean isTmpFile = prefs
                        .getBoolean("isTmpFile", false);
                boolean aend = prefs.getBoolean("aend", true);
                CardMode = prefs.getBoolean("Cardmode", false);
                if (Lernvokabeln != null)
                {
                    if (isTmpFile)
                    {
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

    private void processPreferences()
    {
        libLearn.gStatus = "getting preferences";
        try
        {
            libLearn.gStatus = "onCreate getPrefs";
            prefs = this.getPreferences(Context.MODE_PRIVATE);
            String Installer = this.getPackageManager().getInstallerPackageName(this.getPackageName());
            if (prefs.getBoolean("play", true)
                    && (Installer == null
                    || (!Installer.equalsIgnoreCase("com.android.vending")
                    && !Installer.contains("com.google.android"))))
            {
                lib.YesNoCheckResult res = lib.ShowMessageYesNoWithCheckbox
                        (this, Installer != null ? Installer : "", this.getString(R.string.msgNotGooglePlay)
                                , this.getString(R.string.msgDontShowThisMessageAgain), false);
                if (res != null)
                {
                    prefs.edit().putBoolean("play", !res.checked).commit();
                    if (res.res == yesnoundefined.yes)
                    {
                        String url = "https://play.google.com/apps/testing/org.de.jmg.learn";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        finish();
                    }
                }
            }
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
            blnTextToSpeech = prefs.getBoolean("TextToSpeech", true);
            StartTextToSpeech();

            boolean blnLicenseAccepted = prefs.getBoolean("LicenseAccepted", false);
            if (!blnLicenseAccepted)
            {
                InputStream is = this.getAssets().open("LICENSE");
                java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                String strLicense = s.hasNext() ? s.next() : "";
                s.close();
                is.close();
                lib.yesnoundefined res = (lib.ShowMessageYesNo(this,
                        strLicense,
                        getString(R.string.license),
                        true));
					/*
					java.lang.CharSequence[] cbxs = {getString(R.string.gpl),getString(R.string.gplno)};
					boolean[] blns = {false,false};
					lib.yesnoundefined res = (lib.ShowMessageYesNoWithCheckboxes
							(this,
									strLicense,
									cbxs,
									blns,
									new DialogInterface.OnMultiChoiceClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which, boolean isChecked) {

										}
									})
					);
					*/
                if (res == lib.yesnoundefined.yes)
                {
                    prefs.edit().putBoolean("LicenseAccepted", true).commit();
                }
                else
                {
                    finish();
                }

            }
        }
        catch (Exception e)
        {

            lib.ShowException(this, e);
        }
    }

    private void StartTextToSpeech()
    {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                if (status == TextToSpeech.SUCCESS)
                {
                    int res = tts.setLanguage(Locale.US);
                    if (res < 0)
                    {
                        blnTextToSpeech = false;
                    }
                    else
                    {
                        tts.setSpeechRate(.75f);
                    }
                }
                else
                {
                    blnTextToSpeech = false;
                }
            }
        });
    }

    private void setPageChangedListener()
    {
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
                        if (fPA != null && fPA.fragSettings != null)
                        {
                            try
                            {
                                fPA.fragSettings.saveResultsAndFinish(true);
                            }
                            catch (Exception ex)
                            {
                                Log.e(".saveResultsAndFinish", ex.getMessage(), ex);
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

                        lib.ShowException(MainActivity.this, e);
                    }
                    mnuUploadToQuizlet.setEnabled(true);
                }
                else if (LastPosition == _MainActivity.fragID)
                {
                    if (fPA != null && fPA.fragMain != null)
                    {
                        fPA.fragMain.removeCallbacks();
                    }
                }

                if (position == fragFileChooser.fragID)
                {
                    mnuAddNew.setEnabled(false);
                        	/*
							try {
								if (!checkLoadFile())
								{
									mPager.setCurrentItem(_MainActivity.fragID);
								}
							} catch (Exception e) {

								lib.ShowException(MainActivity.this, e);
							}
							*/
                }
                else if (position == _MainActivity.fragID)
                {
                    mnuAddNew.setEnabled(true);
                    mnuUploadToQuizlet.setEnabled(true);
                    if (fPA != null && fPA.fragMain != null)
                    {
                        fPA.fragMain._txtMeaning1.setOnFocusChangeListener(new View.OnFocusChangeListener()
                        {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus)
                            {
                                fPA.fragMain._txtMeaning1.setOnFocusChangeListener(fPA.fragMain.FocusListenerMeaning1);
                                if (hasFocus)
                                {
                                    fPA.fragMain._scrollView.fullScroll(View.FOCUS_UP);
                                }

                            }
                        });
                    }
                }
                else if (position == SettingsActivity.fragID)
                {
                    mnuAddNew.setEnabled(false);
                    mnuUploadToQuizlet.setEnabled(false);
                    if (fPA != null && fPA.fragSettings != null)
                    {
                        try
                        {
                            int Language = fPA.fragSettings.getIntent().getIntExtra(
                                    "Language", org.de.jmg.learn.vok.Vokabel.EnumSprachen.undefiniert.ordinal());
                            fPA.fragSettings.spnLanguages.setSelection(Language);
                            fPA.fragSettings.setSpnMeaningPosition();
                            fPA.fragSettings.setSpnWordPosition();
                            fPA.fragSettings.setChkTSS();
                        }
                        catch (Exception ex)
                        {
                            Log.e(".saveResultsAndFinish", ex.getMessage(), ex);
                        }
                    }
                }
                else if (position == fragFileChooserQuizlet.fragID)
                {
                    if (fPA != null && fPA.fragQuizlet != null)
                    {
                        searchQuizlet();
                    }

                }
                else
                {
                    mnuAddNew.setEnabled(false);
                }

                LastPosition = position;


            }

        };

        /** Setting the pageChange listener to the viewPager */
        mPager.addOnPageChangeListener(pageChangeListener);

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
                Log.e(libLearn.gStatus, ex2.getMessage(), ex2);
            }
            fPA.fragSettings = null;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        try
        {
            boolean aend = vok.aend;
            String filename = vok.getFileName();
            Uri uri = vok.getURI();
            outState.putInt("SelFragID", mPager.getCurrentItem());
            outState.putString("JMGDataDirectory", JMGDataDirectory);
            outState.putFloat("ActionBarOriginalTextSize", ActionBarOriginalTextSize);
            if (vok.getGesamtzahl() > 0)
            {
                saveFilePrefs(true);
                if (uri != null)
                {
                    lib.CheckPermissions(this, uri, false);
                    //this.takePersistableUri(getIntent(), uri,true);
                }
                vok.SaveFile(
                        Path.combine(getApplicationInfo().dataDir, "vok.tmp"), uri,
                        vok.getUniCode(), true);
                outState.putString("vokpath", filename);
                outState.putInt("vokindex", vok.getIndex());
                outState.putInt("vokLastIndex", vok.getLastIndex());
                outState.putIntArray("Lernvokabeln", vok.getLernvokabeln());
                outState.putInt("Lernindex", vok.getLernIndex());
                outState.putBoolean("Unicode", vok.getUniCode());
                outState.putBoolean("Cardmode", vok.getCardMode());
                outState.putBoolean("aend", aend);
                if (uri != null) outState.putString("URI", uri.toString());
                vok.aend = aend;
                vok.setFileName(filename);
                vok.setURI(uri);

            }
            handlerbackpressed.removeCallbacks(rSetBackPressedFalse);
            for (DialogInterface dlg : lib.OpenDialogs)
            {
                dlg.dismiss();
            }
            lib.OpenDialogs.clear();

        }
        catch (Exception e)
        {

            Log.e("OnSaveInstanceState", e.getMessage(), e);
            e.printStackTrace();
        }
        // outState.putParcelable("vok", vok);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (mPager.getCurrentItem() == fragFileChooser.fragID)
        {
            boolean res = this.fPA.fragChooser.onKeyDown(keyCode, event);
            if (!res) return false;
        }
        else if (mPager.getCurrentItem() == _MainActivity.fragID)
        {
            this.fPA.fragMain.removeCallbacks();
        }
        if (keyCode == KeyEvent.KEYCODE_HOME)
        {
            try
            {
                saveVok(false);
            }
            catch (Exception e)
            {

                lib.ShowException(this, e);
                return true;
            }
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK && mPager.getCurrentItem() == 0)
        {
            try
            {
                if (_backPressed > 0 || saveVokAsync(false, false))
                {
                    handlerbackpressed.removeCallbacks(rSetBackPressedFalse);
                }
                else
                {
                    return true;
                }

            }
            catch (Exception e)
            {

                Log.e("onBackPressed", e.getMessage(), e);
                lib.ShowException(this, e);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onBackPressed()
    {
        if (mPager.getCurrentItem() == 0)
        {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        }
        else
        {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private boolean saveVokAsync(boolean dontPrompt, final boolean blnAsync) throws Exception
    {
        fPA.fragMain.EndEdit(true);
        if (vok.aend)
        {
            if (!dontPrompt)
            {

                AlertDialog.Builder A = new AlertDialog.Builder(context);
                A.setPositiveButton(getString(R.string.yes), new AlertDialog.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            if (libString.IsNullOrEmpty(vok.getFileName()) && vok.getURI() == null)
                            {
                                SaveVokAs(true, false);
                            }
                            else
                            {
                                if (blnAsync || (libString.IsNullOrEmpty(vok.getFileName()) && vok.getURI() != null))
                                {
                                    vok.SaveCurrentFileAsync();
                                }
                                else
                                {
                                    vok.SaveFile(vok.getFileName(), vok.getURI(), vok.getUniCode(), false);
                                }
                                vok.aend = false;
                                saveFilePrefs(false);
                            }


                        }
                        catch (Exception e)
                        {
                            try
                            {
                                SaveVokAs(true, false);
                            }
                            catch (Exception e1)
                            {

                                e1.printStackTrace();
                                lib.ShowException(MainActivity.this, e1);
                            }
                        }
                    }
                });
                A.setNegativeButton(getString(R.string.no), new AlertDialog.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                A.setMessage(getString(R.string.Save));
                A.setTitle(getString(R.string.question));
                Dialog dlg = A.create();
                dlg.show();
                dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        lib.ShowToast(MainActivity.this, MainActivity.this.getString(R.string.PressBackAgain));
                        _backPressed += 1;
                        handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);
                    }
                });
                if (_backPressed > 0)
                {
                    return true;
                }
            }

            if (dontPrompt)
            {
                try
                {
                    if (libString.IsNullOrEmpty(vok.getFileName()) && vok.getURI() == null)
                    {
                        SaveVokAs(true, false);
                    }
                    else
                    {
                        if (blnAsync || (libString.IsNullOrEmpty(vok.getFileName()) && vok.getURI() != null))
                        {
                            vok.SaveCurrentFileAsync();
                        }
                        else
                        {
                            vok.SaveFile(vok.getFileName(), vok.getURI(), vok.getUniCode(), true);
                        }
                        vok.aend = false;
                        saveFilePrefs(false);
                    }
                    _backPressed += 1;
                    handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);

                }
                catch (Exception e)
                {
                    lib.ShowException(this, e);
                }
            }
            return false;
        }
        else
        {
            return true;
        }

    }

    public boolean isUIThread()
    {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public void LoadVokabel(String fileSelected, Uri uri, int index, int[] Lernvokabeln,
                            int Lernindex, boolean CardMode)
    {
        try
        {
            if (fPA.fragMain != null && fPA.fragMain.mainView != null)
            {
                fPA.fragMain.setBtnsEnabled(false);
                if (!fPA.fragMain.EndEdit(false)) return;
            }
            if (uri == null)
            {
                if (!saveVok(false)) return;
            }
            try
            {
                vok.LoadFile(this, fileSelected, uri, false, false, _blnUniCode);
            }
            catch (RuntimeException ex)
            {
                if (ex.getCause() != null)
                {
                    if (ex.getCause().getMessage() != null
                            && ex.getCause().getMessage()
                            .contains("IsSingleline"))
                    {
                        vok.LoadFile(this, fileSelected, uri, true, false, _blnUniCode);
                    }
                    else
                    {
                        throw ex;
                    }
                }
                else
                {
                    throw ex;
                }
            }

            if (vok.getCardMode() || CardMode)
            {
                if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                    fPA.fragMain.SetViewsToCardmode();
            }
            else
            {
                if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                    fPA.fragMain.SetViewsToVokMode();
            }

            // if (index >0 ) vok.setIndex(index);
            if (Lernvokabeln != null)
                vok.setLernvokabeln(Lernvokabeln);
            if (vok.getGesamtzahl() > 0)
            {
                if (Lernindex > 0)
                    vok.setLernIndex((short) Lernindex);
                if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                    fPA.fragMain.setBtnsEnabled(true);
            }
            if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                fPA.fragMain.getVokabel(false, false, false);
        }
        catch (Exception e)
        {
            lib.ShowException(this, e);
            if (fPA.fragMain != null && fPA.fragMain.mainView != null)
            {
                try
                {
                    fPA.fragMain.getVokabel(true, true, false);
                }
                catch (Exception e1)
                {
                    lib.ShowException(this, e);
                }
            }
        }
    }

    public boolean saveVok(boolean dontPrompt) throws Exception
    {
        return saveVok(dontPrompt, true, false);
    }

    public boolean saveVok(boolean dontPrompt, boolean dontShowBackPressed) throws Exception
    {
        return saveVok(dontPrompt, dontShowBackPressed, false);
    }

    public boolean saveVok(boolean dontPrompt, boolean dontShowBackPressed, boolean blnAsync) throws Exception
    {
        if (fPA.fragMain != null && fPA.fragMain.mainView != null)
        {
            if (!fPA.fragMain.EndEdit(false)) return false;
        }
        if (vok.aend && vok.getGesamtzahl() > 0)
        {
            if (!dontPrompt)
            {
                yesnoundefined res = lib.ShowMessageYesNo(this,
                        getString(R.string.Save), "");
                if (res == yesnoundefined.undefined) return false;
                dontPrompt = res == yesnoundefined.yes;
                if (!dontPrompt)
                {
                    if (!dontShowBackPressed)
                    {
                        _backPressed += 1;
                        lib.ShowToast(MainActivity.this, MainActivity.this
                                .getString(R.string.PressBackAgain));
                        handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);
                    }
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

            try
            {

                if ((libString.IsNullOrEmpty(vok.getFileName())
                        || libString.IsNullOrEmpty(vok.getvok_Path())
                        || new File(vok.getFileName()).getParent() == null)
                        && vok.getURI() == null)
                {
                    SaveVokAs(true, false);
                    return false;
                }
                else
                {
                    if (blnAsync || (libString.IsNullOrEmpty(vok.getFileName()) && vok.getURI() != null))
                    {
                        vok.SaveCurrentFileAsync();
                    }
                    else
                    {
                        vok.SaveFile();
                    }
                    vok.aend = false;
                    if (!dontShowBackPressed)
                    {
                        _backPressed += 1;
                        handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);
                    }
                    saveFilePrefs(false);
                    return true;
                }
            }
            catch (Exception e)
            {

                //lib.ShowException(this, e);
                if (lib.ShowMessageYesNo(this, getString(R.string.msgFileCouldNotBeSaved), "") == yesnoundefined.yes)
                {
                    SaveVokAs(true, false);
                }
            }

            return false;
        }
        else
        {
            return true;
        }

    }

    private void saveFilePrefs(boolean isTmpFile)
    {
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
        if (vok.getURI() != null)
        {
            edit.putString("URI", vok.getURI().toString());
            try
            {
                takePersistableUri(vok.getURI(), false);
            }
            catch (Exception e)
            {

                e.printStackTrace();
                Log.e("saveFilePrefs", vok.getURI().toString(), e);
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
        if (Build.VERSION.SDK_INT >= 19)
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
                if (force) lib.ShowException(this, ex);
            }

        }
    }

    private void CopyAssets()
    {
        libLearn.gStatus = "Copy Assets";
        File F = android.os.Environment.getExternalStorageDirectory();
        boolean successful = false;
        for (int i = 0; i < 2; i++)
        {
            if (!F.exists() || i == 1)
            {
                F = new File(getApplicationInfo().dataDir);
            }
            String extPath = F.getPath();
            JMGDataDirectory = prefs.getString("JMGDataDirectory", Path.combine(extPath, "learnforandroid", "vok"));
            //JMGDataDirectory = Path.combine(extPath, "learnforandroid", "vok");

            if (F.isDirectory() && F.exists())
            {
                File F1 = new File(JMGDataDirectory);
                if (!F1.isDirectory() && !F1.exists())
                {
                    System.out.println(F1.mkdirs());
                }
                else
                {
                    int dontcopy = prefs.getInt("dontcopyorchoose", -2);
                    yesnoundefined rres;
                    if (dontcopy == -2)
                    {
                        rres = yesnoundefined.undefined;
                    }
                    else if (dontcopy == 0)
                    {
                        rres = yesnoundefined.yes;
                    }
                    else
                    {
                        rres = yesnoundefined.no;
                    }
                    lib.YesNoCheckResult res = new lib.YesNoCheckResult(rres, false);
                    if (rres == yesnoundefined.undefined)
                    {
                        res = lib.ShowMessageYesNoWithCheckbox(this, this.getString(R.string.copy),this.getString(R.string.copyfiles), this.getString(R.string.msgRememberChoice),false);
                    }
                    if (res.checked)
                    {
                        prefs.edit().putInt("dontcopyorchoose", (res.res == yesnoundefined.no?-1:0)).commit();
                    }
                    if (res.res == yesnoundefined.no) return;
                }
                AssetManager A = this.getAssets();
                try
                {
                    final String languages[] = new String[]{"Greek", "Hebrew",
                            "KAR", "Spanish"};
                    final String path = F1.getPath();
                    for (String language : languages)
                    {
                        F1 = new File(Path.combine(path, language));
                        for (String File : A.list(language))
                        {
                            InputStream myInput = A.open(Path.combine(language,
                                    File));
                            String outFileName = Path.combine(F1.getPath(), File);
                            if (!F1.isDirectory())
                            {
                                System.out.println(F1.mkdirs());
                            }
                            // Open the empty db as the output stream

                            File file = new File(outFileName);

                            if (file.exists())
                            {
                                // file.delete();
                                successful = true;
                            }
                            else
                            {
                                try
                                {
                                    System.out.println(file.createNewFile());
                                    OutputStream myOutput = new FileOutputStream(file);

                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = myInput.read(buffer, 0, 1024)) > 0)
                                    {
                                        myOutput.write(buffer, 0, length);
                                    }

                                    // Close the streams
                                    myOutput.flush();
                                    myOutput.close();
                                    myInput.close();
                                    successful = true;
                                }
                                catch (Exception eex)
                                {
                                    if (i == 0) throw eex;
                                    lib.ShowMessage(this, this.getString(R.string.fileCouldNotBeCreated) + " " + outFileName, this.getString(R.string.Error));
                                    //successful=false;
                                    throw eex;
                                }
                            }
                        }
                    }
                }
                catch (IOException e)
                {

                    // e.printStackTrace();
                    if (i > 0) lib.ShowException(this, e);
                    // lib.ShowMessage(this, "CopyAssets");
                    successful = false;
                }
            }
            if (successful) break;
        }

    }

    private HashMap<ColorItems, ColorSetting> getColorsFromPrefs()
    {
        HashMap<ColorItems, ColorSetting> res = new HashMap<>();
        for (int i = 0; i < ColorSetting.ColorItems.values().length; i++)
        {
            ColorItems ColorItem = ColorSetting.ColorItems.values()[i];
            String Name = getResources().getStringArray(R.array.spnColors)[i];
            int defValue;
            switch (ColorItem)
            {
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

    private HashMap<ColorItems, ColorSetting> getColorsFromIntent(Intent intent)
    {
        HashMap<ColorItems, ColorSetting> res = new HashMap<>();
        for (int i = 0; i < ColorSetting.ColorItems.values().length; i++)
        {
            ColorItems ColorItem = ColorSetting.ColorItems.values()[i];
            String Name = getResources().getStringArray(R.array.spnColors)[i];
            int defValue;
            switch (ColorItem)
            {
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

    private HashMap<Sounds, SoundSetting> getSoundsFromIntent(Intent intent)
    {
        HashMap<Sounds, SoundSetting> res = new HashMap<>();
        if (lib.AssetSounds[0] == null)
            lib.initSounds();
        for (int i = 0; i < lib.Sounds.values().length; i++)
        {
            Sounds SoundItem = Sounds.values()[i];
            String Name = getResources().getStringArray(R.array.spnSounds)[i];
            String defValue;
            defValue = lib.AssetSounds[SoundItem.ordinal()];
            String SoundPath = intent.getStringExtra(SoundItem.name());
            if (libString.IsNullOrEmpty(SoundPath))
            {
                SoundPath = defValue;
            }
            res.put(SoundItem, new SoundSetting(SoundItem, Name, SoundPath));
        }
        return res;

    }

    private HashMap<Sounds, SoundSetting> getSoundsFromPrefs()
    {
        HashMap<Sounds, SoundSetting> res = new HashMap<>();
        if (lib.AssetSounds[0] == null)
            lib.initSounds();
        for (int i = 0; i < lib.Sounds.values().length; i++)
        {
            Sounds SoundItem = Sounds.values()[i];
            String Name = getResources().getStringArray(R.array.spnSounds)[i];
            String defValue;
            defValue = lib.AssetSounds[SoundItem.ordinal()];
            String SoundPath = prefs.getString(SoundItem.name(), defValue);
            res.put(SoundItem, new SoundSetting(SoundItem, Name, SoundPath));
        }
        return res;

    }

    @SuppressLint("InlinedApi")
    public void SaveVokAs(boolean blnUniCode, boolean blnNew) throws Exception
    {
        boolean blnActionCreateDocument = false;
        try
        {
            if (fPA.fragMain != null && fPA.fragMain.mainView != null) fPA.fragMain.EndEdit(false);
            if (!libString.IsNullOrEmpty(vok.getFileName()) || vok.getURI() == null || Build.VERSION.SDK_INT < 19)
            {
                boolean blnSuccess;
                for (int i = 0; i < 2; i++)
                {
                    try
                    {
                        String key = "AlwaysStartExternalProgram";
                        int AlwaysStartExternalProgram = prefs.getInt(key, 999);
                        lib.YesNoCheckResult res;
                        if (AlwaysStartExternalProgram == 999 && !(vok.getURI() != null && i == 1))
                        {
                            res = lib.ShowMessageYesNoWithCheckbox(this, "", getString(R.string.msgStartExternalProgram), getString(R.string.msgRememberChoice), false);
                            if (res != null)
                            {
                                if (res.res == yesnoundefined.undefined) return;
                                if (res.checked)
                                    prefs.edit().putInt(key, res.res == yesnoundefined.yes ? -1 : 0).commit();
                            }
                            else
                            {
                                yesnoundefined par = yesnoundefined.undefined;
                                res = new lib.YesNoCheckResult(par, true);
                            }
                        }
                        else
                        {
                            yesnoundefined par = yesnoundefined.undefined;
                            if (AlwaysStartExternalProgram == -1) par = yesnoundefined.yes;
                            if (AlwaysStartExternalProgram == 0) par = yesnoundefined.no;
                            res = new lib.YesNoCheckResult(par, true);
                        }
                        if ((vok.getURI() != null && i == 1) || res.res == yesnoundefined.no)
                        {
                            Intent intent = new Intent(this, AdvFileChooser.class);
                            ArrayList<String> extensions = new ArrayList<>();
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
                                if (vok.getURI() != null)
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
                        else if (Build.VERSION.SDK_INT < 19)
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
                                blnSuccess = true;
                            }
                            else
                            {
                                lib.ShowToast(this, getString(R.string.InstallFilemanager));
                                intent.setData(null);
                                intent.removeExtra("org.openintents.extra.WRITEABLE_ONLY");
                                intent.removeExtra("org.openintents.extra.TITLE");
                                intent.removeExtra("org.openintents.extra.BUTTON_TEXT");

                                startActivityForResult(chooser, FILE_OPENINTENT);
                                blnSuccess = true;
                            }

                        }
                        else
                        {
                            blnActionCreateDocument = true;
                            blnSuccess = true;
                        }
                    }
                    catch (Exception ex)
                    {
                        blnSuccess = false;
                        Log.e("SaveAs", ex.getMessage(), ex);
                        if (i == 1)
                        {
                            lib.ShowException(this, ex);
                        }
                    }

                    if (blnSuccess) break;
                }


            }
            else if (Build.VERSION.SDK_INT >= 19)
            {
                blnActionCreateDocument = true;
            }
            if (blnActionCreateDocument)
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
                    String FName = "";
                    if (vok.getURI() != null)
                    {
                        String path2 = lib.dumpUriMetaData(this, vok.getURI());
                        if (path2.contains(":")) path2 = path2.split(":")[0];
                        FName = path2.substring(path2.lastIndexOf("/") + 1);
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
                    Log.d("empty", "empty");
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
            libLearn.gStatus = "SaveVokAs";
            lib.ShowException(this, ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        try
        {
            getMenuInflater().inflate(R.menu.main, menu);
            //findViewById(R.menu.main).setBackgroundColor(Color.BLACK);
            //.setBackgroundColor(Color.BLACK);
            //resize();
            OptionsMenu = menu;
            if (OptionsMenu != null)
            {
                mnuAddNew = menu.findItem(R.id.mnuAddWord);
                mnuUploadToQuizlet = menu.findItem(R.id.mnuUploadToQuizlet);
                Resources resources = context.getResources();
                DisplayMetrics metrics = resources.getDisplayMetrics();
                int height = metrics.heightPixels;
                int viewTop = this.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                height = height - viewTop;
                double scale = (double) height / (double) 950;
                if (scale < .5f)
                {
                    isSmallDevice = true;
                }
                if (Build.VERSION.SDK_INT < 11)
                {
                    menu.findItem(R.id.mnuOpenQuizlet).setVisible(false);
                    menu.findItem(R.id.mnuUploadToQuizlet).setVisible(false);
                }
                /*
                if (isSmallDevice)
                {
                    MenuItemCompat.setShowAsAction(menu.findItem(R.id.mnuSaveAs), MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
                }
                */
                MenuItem mnuQuizlet = menu.findItem(R.id.mnuLoginQuizlet);
                mnuQuizlet.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        throw new RuntimeException("Test");
                        //return true;
                    }
                });
                if (BuildConfig.DEBUG)
                {
                    mnuQuizlet.setVisible(true);
                }
                else
                {
                    mnuQuizlet.setVisible(false);
                }

                setMnuReverse();
                /*
                if (isSmallDevice)
                {
                    MenuItem m = menu.findItem(R.id.mnuHome);
                    MenuItemCompat.setShowAsAction(m,MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
                    m = menu.findItem(R.id.mnuFileOpen);
                    MenuItemCompat.setShowAsAction(m,MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
                    m = menu.findItem(R.id.mnuSaveAs);
                    MenuItemCompat.setShowAsAction(m,MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
                    m = menu.findItem(R.id.mnuAddWord);
                    MenuItemCompat.setShowAsAction(m,MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
                    //lib.ShowToast(this,"Menus set to IF_ROOM!");
                }
                */
                mainView.getViewTreeObserver().addOnGlobalLayoutListener
                        (
                                new ViewTreeObserver.OnGlobalLayoutListener() {

                                    @Override
                                    public void onGlobalLayout() {
                                        // Ensure you call it only once :
                                        lib.removeLayoutListener(mainView.getViewTreeObserver(), this);
                                        MainActivity.this.SetShowAsAction(mnuUploadToQuizlet);
                                    }
                                }
                        );

                return true;
            }
            throw new RuntimeException("menu is null!");
        }
        catch (Exception ex)
        {
            lib.ShowException(this, ex);
        }

        return false;
    }
    private boolean _hasBeenDownsized = false;
    public void SetShowAsAction(final MenuItem m)
    {
        _blnReverse = false;
        _hasBeenDownsized = false;
        _invisibleCount = Build.VERSION.SDK_INT >= 11 ? 0 : 2;
        _SetShowAsAction(m);

    }

    private void _SetShowAsAction(final MenuItem m)
    {
        final View tb = this.findViewById(R.id.action_bar);
        int SizeOther = 0;
        int width;
        ActionMenu = null;
        if (tb != null)
        {
            width = tb.getWidth();
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            double height = metrics.heightPixels;
            int viewTop = this.findViewById(Window.ID_ANDROID_CONTENT).getTop();
            double scale = (double) (height - viewTop) / (double) 950;

            if (scale < .5f)
            {
                isSmallDevice = true;
            }
            double ActionBarHeight = tb.getHeight();
            if (isSmallDevice && ActionBarHeight / height > .15f)
            {
                try
                {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tb.getLayoutParams();
                    layoutParams.height = (int)(height  * .15f);

                    tb.setMinimumHeight((int)(height * .15f));
                    tb.setLayoutParams(layoutParams);
                    tb.requestLayout();
                }
                catch (Exception ex)
                {
                    Log.e("SetToolbarHeight", ex.getMessage(),ex);
                }
            }
            if (width > 0)
            {
                ViewGroup g = (ViewGroup) tb;
                for (int i = 0; i < g.getChildCount(); i++)
                {
                    View v = g.getChildAt(i);
                    if ((v instanceof android.support.v7.widget.ActionMenuView))
                    {
                        SizeOther += v.getWidth();
                        ActionMenu = (android.support.v7.widget.ActionMenuView) v;
                    }
                }
                if (SizeOther < width * .7) _blnReverse = true;
                if ((_blnReverse || SizeOther > width * .7) && ActionMenu != null)
                {
                    if (_blnReverse)
                    {
                        if (!_hasBeenDownsized || _hasBeenDownsized)
                        {
                            MenuBuilder mm = (MenuBuilder) ActionMenu.getMenu();
                            int Actions = mm.getActionItems().size();
                            try
                            {
                                MenuItem mmm = ActionMenu.getMenu().getItem(Actions + _invisibleCount);
                                if (mmm.isVisible())
                                {
                                    MenuItemCompat.setShowAsAction(mmm, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
                                }
                                else
                                {
                                    _invisibleCount += 1;
                                    _SetShowAsAction(mmm);
                                }
                            }
                            catch (IndexOutOfBoundsException ex)
                            {
                                return;
                            }
                        }

                    }
                    else
                    {
                        MenuItemCompat.setShowAsAction(m, MenuItemCompat.SHOW_AS_ACTION_NEVER);
                        _hasBeenDownsized = true;
                    }
                    ActionMenu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
                    {
                        @Override
                        public void onGlobalLayout()
                        {
                            if (ActionMenu != null)
                            {
                                lib.removeLayoutListener(ActionMenu.getViewTreeObserver(), this);
                                int SizeNew = ActionMenu.getWidth();
                                Log.v("Test", "" + SizeNew);
                                MenuBuilder mm = (MenuBuilder) ActionMenu.getMenu();
                                int count = mm.getActionItems().size();
                                if (count >= 1 && !(_blnReverse && SizeNew > tb.getWidth() * .7))
                                {
                                    MenuItem m = mm.getActionItems().get(count - 1);
                                    _SetShowAsAction(m);
                                }

                            }

                        }
                    });

                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        try
        {
            int id = item.getItemId();
            if (id == R.id.action_settings)
            {
                mPager.setCurrentItem(SettingsActivity.fragID);
            }
            else if (id == R.id.mnuCredits)
            {
                String txt = "android-file-chooser";
                URLSpan spanurl = new URLSpan("https://github.com/mypapit/android-file-chooser");
                Spannable spn = new SpannableString(txt);
                spn.setSpan(spanurl,0,spn.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                lib.ShowMessage(this,spn,"Credits");
            }ff
            else if (id == R.id.mnuContact)
            {
                Intent intent = new Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", "jhmgbl@gmail.com", null));
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jhmgbl@gmail.com"});
                String versionName = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionName;
                intent.putExtra(Intent.EXTRA_SUBJECT, "learnforandroid " + versionName);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.ConvertVok));
                this.startActivity(Intent.createChooser(intent, getString(R.string.SendMail)));
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
            else if (id == R.id.mnuOpenQuizlet)
            {
                //LoginQuizlet();
                if (mPager.getCurrentItem() != fragFileChooserQuizlet.fragID)
                {
                    mPager.setCurrentItem(fragFileChooserQuizlet.fragID);
                }
                else
                {
                    if (fPA != null && fPA.fragQuizlet != null)
                    {
                        searchQuizlet();
                    }
                }
            }
            else if (id == R.id.mnuUploadToQuizlet)
            {
                uploadtoQuizlet();

            }
            else if (id == R.id.mnuAskReverse)
            {
                item.setChecked(!item.isChecked());
                vok.reverse = item.isChecked();
                setMnuReverse();
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
                    lib.SelectFile(this, def);
                }
            }
            else if (id == R.id.mnuNew)
            {
                newVok();

            }
            else if (id == R.id.mnuAddWord)
            {
                mPager.setCurrentItem(_MainActivity.fragID);
                if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                {
                    if (fPA.fragMain.EndEdit(false))
                    {
                        vok.AddVokabel();
                        fPA.fragMain.getVokabel(true, false, true);
                        fPA.fragMain.StartEdit();
                    }
                }

            }
            else if (id == R.id.mnuFileOpenASCII)
            {
                LoadFile(false);
            }
            else if (id == R.id.mnuConvMulti)
            {
                if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                {
                    vok.ConvertMulti();
                    fPA.fragMain.getVokabel(false, false);
                }
            }
            else if (id == R.id.mnuFileSave)
            {
                saveVok(false);
            }
            else if (id == R.id.mnuSaveAs)
            {
                SaveVokAs(true, false);
            }
            else if (id == R.id.mnuRestart)
            {
                vok.restart();
            }
            else if (id == R.id.mnuDelete)
            {
                if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                {
                    vok.DeleteVokabel();
                    fPA.fragMain.EndEdit2();
                }
            }
            else if (id == R.id.mnuReverse)
            {
                if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                {
                    vok.revert();
                    fPA.fragMain.getVokabel(false, false);
                }
            }
            else if (id == R.id.mnuReset)
            {
                if (lib.ShowMessageYesNo(this,
                        this.getString(R.string.ResetVocabulary), "") == yesnoundefined.yes)
                {
                    vok.reset();
                }

            }
            else if (id == R.id.mnuStatistics)
            {
                if (vok.getGesamtzahl() > 5)
                {
                    try
                    {
						/*
						IDemoChart chart = new org.de.jmg.learn.chart.LearnBarChart();
					int UIMode = lib.getUIMode(this);

		switch (UIMode)
		{
			case Configuration.UI_MODE_TYPE_TELEVISION:
				isTV = true;
				break;
			case Configuration.UI_MODE_TYPE_WATCH:
				isWatch = true;
				break;
		}
	Intent intent = chart.execute(this);
						this.startActivity(intent);
						*/
                        mPager.setCurrentItem(fragStatistics.fragID);
                    }
                    catch (Exception ex)
                    {
                        lib.ShowException(this, ex);
                    }

                }
            }
            else if (id == R.id.mnuEdit)
            {
                if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                {
                    fPA.fragMain.edit();
                }
            }
            else if (id == R.id.mnuLoginQuizlet)
            {
                LoginQuizlet(false);
            }

        }
        catch (Throwable ex)
        {
            lib.ShowException(this, ex);
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean newVok() throws Exception
    {
        if (saveVok(false))
        {
            newvok();
            return true;
        }
        return false;
    }

    private void uploadtoQuizlet()
    {
        if (vok.getGesamtzahl() < 3) return;
        try
        {
            if (this.QuizletAccessToken == null)
            {
                this.QuizletAccessToken = prefs.getString("QuizletAccessToken", null);
                this.QuizletUser = prefs.getString("QuizletUser", null);
                if (QuizletAccessToken != null)
                {
                    final CountDownLatch l = new CountDownLatch(1);
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                _blnVerifyToken = org.liberty.android.fantastischmemo.downloader.quizlet.lib
                                        .verifyAccessToken(new String[]{QuizletAccessToken, QuizletUser});
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                _blnVerifyToken = false;
                            }
                            l.countDown();
                        }
                    }).start();
                    l.await();
                    if (!_blnVerifyToken)
                    {
                        QuizletAccessToken = null;
                        QuizletUser = null;
                    }
                }
            }
            if (this.QuizletAccessToken == null)
            {
                this.LoginQuizlet(true);
            }
            else
            {

                final AlertDialog.Builder A = new AlertDialog.Builder(context);
                final CharSequence[] items = {MainActivity.this.getString(R.string.Private), MainActivity.this.getString(R.string.Public)};
                A.setSingleChoiceItems(items, _blnPrivate ? 0 : 1, new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        _blnPrivate = which == 0;

                    }
                });

                final EditText input = new EditText(context);
                //final LinearLayout ll = new LinearLayout(context);
                //ll.addView(input);
                A.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String userId = MainActivity.this.QuizletUser;
                        if (!_blnPrivate)
                        {
                            userId = "";
                        }
                        else
                        {
                            if (lib.ShowMessageOKCancel
                                    (MainActivity.this
                                            , MainActivity.this.getString(R.string.msgPrivateNotSupported)
                                            , ""
                                            , false)
                                    == yesnoundefined.no) return;
                        }
                        new UploadToQuzletTask().execute(input.getText().toString(), userId);
                        lib.removeDlg(dlg);

                    }
                });
                A.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        lib.removeDlg(dlg);
                    }
                });

                A.setTitle(String.format(getString(R.string.UploadToQuizlet), new File(vok.getFileName()).getName()));
                //A.setTitle(getString(R.string.Search));
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText("");
				/*
				android.widget.LinearLayout.LayoutParams params
						= (android.widget.LinearLayout.LayoutParams) input.getLayoutParams();
				params.topMargin = lib.dpToPx(20);
				input.setLayoutParams(params);
				*/
                A.setView(input);
				/*
				int PT =  input.getPaddingTop();
				int PL = input.getPaddingLeft();
				int PR = input.getPaddingRight();
				int PB = input.getPaddingBottom();
				//int PE = input.getPaddingEnd();
				//int PS = input.getPaddingStart();
				input.setPadding(PL,PT*3,PR,PB);
				*/
                dlg = A.create();
                dlg.show();

                dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        lib.removeDlg(dlg);
                    }
                });
                lib.OpenDialogs.add(dlg);
            }

        }
        catch (Exception ex)
        {
            lib.ShowException(this, ex);
        }

    }

    private void searchQuizlet()
    {
        try
        {
            if (this.QuizletAccessToken == null)
            {
                this.QuizletAccessToken = prefs.getString("QuizletAccessToken", null);
                this.QuizletUser = prefs.getString("QuizletUser", null);
                if (QuizletAccessToken != null)
                {
                    final CountDownLatch l = new CountDownLatch(1);
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                _blnVerifyToken = org.liberty.android.fantastischmemo.downloader.quizlet.lib
                                        .verifyAccessToken(new String[]{QuizletAccessToken, QuizletUser});
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                _blnVerifyToken = false;
                            }
                            l.countDown();
                        }
                    }).start();
                    l.await();
                    if (!_blnVerifyToken)
                    {
                        QuizletAccessToken = null;
                        QuizletUser = null;
                    }
                }
            }
            if (this.QuizletAccessToken == null)
            {
                this.LoginQuizlet(false);
            }
            else
            {

                final AlertDialog.Builder A = new AlertDialog.Builder(context);
                final CharSequence[] items = {MainActivity.this.getString(R.string.Private), MainActivity.this.getString(R.string.Public)};
                A.setSingleChoiceItems(items, _blnPrivate ? 0 : 1, new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        _blnPrivate = which == 0;

                    }
                });

                final EditText input = new EditText(context);
                //final LinearLayout ll = new LinearLayout(context);
                //ll.addView(input);
                A.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (_blnPrivate || !libString.IsNullOrEmpty(input.getText().toString()))
                        {
                            fPA.fragQuizlet.setSearchPhrase(input.getText().toString());
                            fPA.fragQuizlet.blnPrivate = _blnPrivate;
                            fPA.fragQuizlet.Load();
                            lib.removeDlg(dlg);
                            //fPA.fragQuizlet.Login();
                        }
                    }
                });
                A.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        lib.removeDlg(dlg);
                    }
                });

                A.setTitle(getString(R.string.SearchQuizlet));
                //A.setTitle(getString(R.string.Search));
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(fPA.fragQuizlet.getSearchPhrase());
				/*
				android.widget.LinearLayout.LayoutParams params
						= (android.widget.LinearLayout.LayoutParams) input.getLayoutParams();
				params.topMargin = lib.dpToPx(20);
				input.setLayoutParams(params);
				*/
                A.setView(input);
				/*
				int PT =  input.getPaddingTop();
				int PL = input.getPaddingLeft();
				int PR = input.getPaddingRight();
				int PB = input.getPaddingBottom();
				//int PE = input.getPaddingEnd();
				//int PS = input.getPaddingStart();
				input.setPadding(PL,PT*3,PR,PB);
				*/
                dlg = A.create();
                dlg.show();

                dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        lib.removeDlg(dlg);
                    }
                });
                lib.OpenDialogs.add(dlg);
            }

        }
        catch (Exception ex)
        {
            lib.ShowException(this, ex);
        }

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
        intent.putExtra("langword", lib.toLanguageTag(vok.getLangWord()));
        intent.putExtra("langmeaning", lib.toLanguageTag(vok.getLangMeaning()));
        //fPA.fragSettings.init(intent, Settings_Activity);
        return intent;
    }

    public boolean checkLoadFile() throws Exception
    {
        boolean blnLoadFile;
        if (fPA.fragMain != null && fPA.fragMain.mainView != null)
        {
            boolean res = fPA.fragMain.EndEdit(false);
            if (!res) return false;
        }
        if (vok.getGesamtzahl() > 0 && vok.aend && libString.IsNullOrEmpty(vok.getFileName()) && vok.getURI() == null)
        {
            if (lib.ShowMessageYesNo(this, getString(R.string.SaveNewVokabularyAs), "") == yesnoundefined.yes)
            {
                //Crashes
                //SaveVokAs(true,false);
                blnLoadFile = false;
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

    public void LoadFile(boolean blnUniCode) throws Exception
    {

        if (checkLoadFile())
        {
            Intent intent = getFileChooserIntent(blnUniCode);
            this.startActivityForResult(intent, FILE_CHOOSER);

        }
    }

    public Intent getFileChooserIntent(boolean blnUniCode)
    {
        Intent intent = new Intent(this, FileChooser.class);
        ArrayList<String> extensions = new ArrayList<>();
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
                (JMGDataDirectory != null && new File(JMGDataDirectory).exists()) ? JMGDataDirectory
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
        yesnoundefined res = lib.ShowMessageYesNo(this, getString(R.string.txtFlashCardFile), "");
        if (res == yesnoundefined.undefined) return;
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
        fPA.fragMain.getVokabel(true, false, true);
        fPA.fragMain.StartEdit();
    }

    public void setJMGDataDirectory(String value)
    {
        JMGDataDirectory = value;
        if (fPA.fragChooser != null) fPA.fragChooser.setCurrentDir((value));
    }

    LoginQuizletActivity loginQuizlet;

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if (loginQuizlet!=null) loginQuizlet.onNewIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            if ((requestCode == FILE_CHOOSER)
                    && (resultCode == Activity.RESULT_OK))
            {
                mPager.setCurrentItem(_MainActivity.fragID);
                String fileSelected = data.getStringExtra("fileSelected");
                _blnUniCode = data.getBooleanExtra("blnUniCode", true);
                LoadVokabel(fileSelected, null, 1, null, 0, false);

            }
            else if (requestCode == SettingsActivity.FILE_CHOOSERSOUND)
            {
                if (fPA.fragSettings != null && fPA.fragSettings.SettingsView != null)
                    fPA.fragSettings.onActivityResult(requestCode, resultCode, data);
            }
            else if (requestCode == SettingsActivity.FILE_CHOOSERDATADIR)
            {
                if (fPA.fragSettings != null && fPA.fragSettings.SettingsView != null)
                    fPA.fragSettings.onActivityResult(requestCode, resultCode, data);
            }
            else if ((requestCode == FILE_CHOOSERADV)
                    && (resultCode == Activity.RESULT_OK))
            {
                final String fileSelected = data.getStringExtra("fileSelected");
                _blnUniCode = data.getBooleanExtra("blnUniCode", true);
                final boolean blnNew = data.getBooleanExtra("blnNew", false);
                if (!libString.IsNullOrEmpty(fileSelected))
                {
                    mPager.setCurrentItem(_MainActivity.fragID);
                    String value = fileSelected;
                    value = value.replace("\n", "");
                    try
                    {
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
                                        getString(R.string.Overwrite), "") == yesnoundefined.yes))
                        {
                            File ParentDir = F.getParentFile();
                            if (!ParentDir.exists())
                                System.out.println(ParentDir.mkdirs());
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
                            if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                                fPA.fragMain.SetActionBarTitle();
                        }

                    }
                    catch (Exception e)
                    {

                        lib.ShowException(MainActivity.this, e);
                        e.printStackTrace();
                    }
                }
					/* AlertDialog.Builder alert = new AlertDialog.Builder(this);

					alert.setTitle(getString(R.string.SaveAs));
					alert.setMessage(getString(R.string.EnterNewFilename)
							+ ": " + fileSelected);
Intent i = new Intent(this, org.de.jmg.learn.MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);

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
                    if (blnNew)
                        try
                        {
                            newvok();
                        }
                        catch (Exception e)
                        {
                            lib.ShowException(MainActivity.this, e);
                        }
                }

            }
            else if (resultCode == RESULT_OK && requestCode == LOGINQUIZLETINTENT && data != null)
            {
                String AuthCode = data.getStringExtra("AuthCode");
                String user = data.getStringExtra("user");
                String accessToken = data.getStringExtra("accessToken");
                boolean upload = data.getBooleanExtra("upload", false);
                QuizletUser = user;
                QuizletAccessToken = accessToken;
                prefs.edit().putString("QuizletAccessToken", QuizletAccessToken).commit();
                prefs.edit().putString("QuizletUser", QuizletUser).commit();
                if (upload)
                {
                    uploadtoQuizlet();
                }
                else
                {
                    searchQuizlet();
                }
                //lib.ShowMessage(this,"Code: " + AuthCode + " User: " + user + "accessToken: "+ accessToken,"");
            }
            else if (resultCode == RESULT_OK && requestCode == lib.SELECT_FILE && data != null)
            {
                Uri selectedUri = data.getData();
                String strUri = selectedUri.toString();
                String path = lib.dumpUriMetaData(this, selectedUri);
                if (path.contains(":")) path = path.split(":")[0];
                if (lib.RegexMatchVok(path) || lib.ShowMessageYesNo(this, getString(R.string.msgWrongExtLoad), "") == yesnoundefined.yes)
                {
                    mPager.setCurrentItem(_MainActivity.fragID);
                    new TaskOpenUri(selectedUri).execute();
                    //LoadVokabel(null,selectedUri, 1, null, 0, false);

                }

            }
            else if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE && data != null)
            {
                Uri selectedUri = data.getData();
                String strUri = selectedUri.toString();
                vok.setURIName("");
                String path = lib.dumpUriMetaData(this, selectedUri);
                if (path.contains(":")) path = path.split(":")[0];
                boolean blnWrongExt = false;
                if (vok.getCardMode())
                {
                    if (!lib.ExtensionMatch(path, "k??"))
                    {
                        blnWrongExt = true;
                        //value += ".kar";
                    }
                }
                else
                {
                    if (!lib.ExtensionMatch(path, "v??"))
                    {
                        //value += ".vok";
                        blnWrongExt = true;
                    }
                }


                if (!blnWrongExt || lib.ShowMessageYesNo(this, getString(R.string.msgWrongExt), "") == yesnoundefined.yes)
                {
                    try
                    {
                        mPager.setCurrentItem(_MainActivity.fragID);
                        takePersistableUri(selectedUri, false);
                        vok.SaveFileAsync(null, selectedUri,
                                _blnUniCode); //, false);
                        saveFilePrefs(false);
                        //if (blnNew) newvok();
                        if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                            fPA.fragMain.SetActionBarTitle();
                        prefs.edit().putString("defaultURI", strUri).commit();
                    }
                    catch (Exception ex)
                    {
                        if (ex.getMessage().equalsIgnoreCase("SaveVokError") && ex.getCause() != null)
                        {
                            lib.ShowMessage(this, getString(R.string.msgFileCouldNotBeSaved2)
                                    + "\n" + selectedUri.toString() + "\n" + ex.getCause().getMessage()
                                    , getString(R.string.Error));
                        }
                        else
                        {
                            lib.ShowMessage(this, getString(R.string.msgFileCouldNotBeSaved2)
                                    + "\n" + selectedUri.toString() + "\n" + ex.getMessage()
                                    , getString(R.string.Error));
                            //lib.ShowMessage(this, ex.getMessage(), this.getString(R.string.Error));
                        }
                    }
                }
                else
                {
                    _backPressed = 0;
                    // vok.aend=true;
                }
            }
            else if (resultCode == RESULT_OK && requestCode == FILE_OPENINTENT && data != null)
            {
                Uri selectedUri = data.getData();
                try
                {

                    String strUri = selectedUri.toString();
                    vok.setURIName("");
                    String path = lib.dumpUriMetaData(this, selectedUri);
                    if (path.contains(":")) path = path.split(":")[0];
                    boolean blnWrongExt = false;
                    if (vok.getCardMode())
                    {
                        if (!lib.ExtensionMatch(path, "k??"))
                        {
                            blnWrongExt = true;
                            //value += ".kar";
                        }
                    }
                    else
                    {
                        if (!lib.ExtensionMatch(path, "v??"))
                        {
                            //value += ".vok";
                            blnWrongExt = true;
                        }
                    }


                    if (!blnWrongExt || lib.ShowMessageYesNo(this, getString(R.string.msgWrongExt), "") == yesnoundefined.yes)
                    {
                        try
                        {
                            mPager.setCurrentItem(_MainActivity.fragID);
                            takePersistableUri(selectedUri, false);
                            vok.SaveFileAsync(null, selectedUri,
                                    _blnUniCode); //, false);
                            saveFilePrefs(false);
                            //if (blnNew) newvok();
                            if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                                fPA.fragMain.SetActionBarTitle();
                            prefs.edit().putString("defaultURI", strUri).commit();
                        }
                        catch (Exception ex)
                        {
                            if (ex.getMessage().equalsIgnoreCase("SaveVokError") && ex.getCause() != null)
                            {
                                lib.ShowMessage(this, getString(R.string.msgFileCouldNotBeSaved2)
                                        + "\n" + selectedUri.toString() + "\n" + ex.getCause().getMessage()
                                        , getString(R.string.Error));
                            }
                            else
                            {
                                lib.ShowMessage(this, getString(R.string.msgFileCouldNotBeSaved2)
                                        + "\n" + selectedUri.toString() + "\n" + ex.getMessage()
                                        , getString(R.string.Error));
                                //lib.ShowMessage(this, ex.getMessage(), this.getString(R.string.Error));
                            }
                        }
                    }
                    else
                    {
                        _backPressed = 0;
                        //vok.aend=true;
                    }
                }
                catch (Exception e)
                {
                    if (e.getMessage().equalsIgnoreCase("SaveVokError") && e.getCause() != null && e.getCause() instanceof IOException)
                    {
                        lib.ShowMessage(this, getString(R.string.msgFileCouldNotBeSaved2)
                                + "\n" + selectedUri.toString() + "\n" + e.getCause().getMessage()
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
        if (data.getStringExtra("OK").equalsIgnoreCase("OK"))
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
            if (Schrittweite != vok.getSchrittweite())
            {
                vok.setSchrittweite(Schrittweite);
                vok.InitAbfrage();
            }
            vok.CharsetASCII = (data.getExtras().getString("CharsetASCII"));

            String langword = data.getExtras().getString("langword");
            String langmeaning = data.getExtras().getString("langmeaning");
            if (!libString.IsNullOrEmpty(langword)
                    && !langword.equalsIgnoreCase(lib.toLanguageTag(vok.getLangWord())))
            {
                vok.aend = true;
                vok.setLangWord(lib.forLanguageTag(langword));
            }
            if (!libString.IsNullOrEmpty(langmeaning)
                    && !langmeaning.equalsIgnoreCase(lib.toLanguageTag(vok.getLangMeaning())))
            {
                vok.setLangMeaning(lib.forLanguageTag(langmeaning));
                vok.aend = true;
            }

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
            if (data.getExtras().containsKey("tts"))
                blnTextToSpeech = data.getExtras().getBoolean("tts");
            if (blnTextToSpeech) StartTextToSpeech();
            int Language = data.getExtras().getInt("Language", Vokabel.EnumSprachen.undefiniert.ordinal());
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
            editor.putBoolean("TextToSpeech", blnTextToSpeech);
            editor.putInt(keyProvider, ShowAlwaysDocumentProvider);
            editor.putInt(keyURIMessage, DontShowPersistableURIMessage);
            editor.putInt(key, AlwaysStartExternalProgram);
            editor.putBoolean("nghs", data.getExtras().getBoolean("nghs"));
            editor.putBoolean("fora", data.getExtras().getBoolean("fora"));
            editor.putBoolean("translate", data.getExtras().getBoolean("translate"));
            editor.putInt("RestartInterval", vok.RestartInterval);
            for (ColorItems item : Colors.keySet())
            {
                editor.putInt(item.name(), Colors.get(item).ColorValue);
            }

            for (Sounds item : colSounds.keySet())
            {
                editor.putString(item.name(), colSounds.get(item).SoundPath);
            }

            editor.commit();
            libLearn.gStatus = "setTextColors";
            if (fPA.fragMain != null && fPA.fragMain.mainView != null) fPA.fragMain.setTextColors();
            libLearn.gStatus = "getVokabel";
            //if (fPA.fragMain!=null && fPA.fragMain.mainView!=null) fPA.fragMain.getVokabel(false, false);
        }

    }

    public void setSoundDir(String dir)
    {
        SoundDir = dir;
        prefs.edit().putString("SoundDir", dir).commit();
    }

    public void LoginQuizlet(boolean blnUpload)
    {
        if (loginQuizlet==null) loginQuizlet = new LoginQuizletActivity();
        Intent login = new Intent();
        login.putExtra("upload", blnUpload);
        loginQuizlet.doLogin(this, login);
        //this.startActivityForResult(login, LOGINQUIZLETINTENT);
    }

    public void setMnuReverse()
    {
        if (OptionsMenu != null)
        {
            MenuItem item = OptionsMenu.findItem(R.id.mnuAskReverse);
            if (vok != null)
            {
                item.setChecked(vok.reverse);
                if (vok.reverse)
                {
                    item.setIcon(android.R.drawable.ic_menu_revert);
                }
                else
                {
                    item.setIcon(android.R.drawable.ic_media_rew);
                }
            }
            else
            {
                item.setIcon(android.R.drawable.ic_media_rew);
            }
        }

    }

    class UploadToQuzletTask extends AsyncTask<String, Void, Exception>
    {

        ProgressDialog p;
        String res;

        @Override
        protected Exception doInBackground(String... params)
        {
            try
            {
                String userId = params[1];

                res = org.liberty.android.fantastischmemo.downloader
                        .quizlet.lib.uploadToQuizlet
                                (MainActivity.this
                                        .vok, MainActivity.this.QuizletAccessToken
                                        , userId, params[0]);
            }
            catch (Exception e)
            {
                return e;
            }

            return null;
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(Exception ex)
        {

            if (p.isShowing()) p.dismiss();
            if (ex != null)
            {
                Log.d("UploadToQuizlet", ex.getMessage(), ex);
                res = ex.getMessage().substring(ex.getMessage().lastIndexOf(" \n" +
                        "Error: ")) + 9;
                if (!libString.IsNullOrEmpty(res))
                {
                    InputStream is = new ByteArrayInputStream(res.getBytes(Charset.forName("UTF-8")));
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader r = new BufferedReader(isr);
                    String x;
                    String resout = "";
                    try
                    {
                        while ((x = r.readLine()) != null)
                        {
                            String[] Tokens = x.split(": ");
                            switch (Tokens[0])
                            {
                                case "error_title":
                                    //resout += MainActivity.this.getString(R.string.Error);
                                    resout += Tokens[1] + "\n";
                                    break;
                                case "error_description":
                                    resout += MainActivity.this.getString(R.string.error_description);
                                    resout += ": " + Tokens[1] + "\n";
                                    break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    lib.ShowMessage(MainActivity.this, resout, MainActivity.this.getString(R.string.Error));
                }
                else
                {
                    lib.ShowException(MainActivity.this, ex);
                }
            }
            else
            {
                if (!libString.IsNullOrEmpty(res))
                {
                    InputStream is = new ByteArrayInputStream(res.getBytes(Charset.forName("UTF-8")));
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader r = new BufferedReader(isr);
                    String x;
                    String resout = "";
                    try
                    {
                        while ((x = r.readLine()) != null)
                        {
                            String[] Tokens = x.split(": ");
                            switch (Tokens[0])
                            {
                                case "id":
                                    //resout += MainActivity.this.getString(R.string.Error);
                                    resout += "ID: " + Tokens[1] + "\n";
                                    break;
                                case "url":
                                    //resout += MainActivity.this.getString(R.string.error_description);
                                    resout += "URL: " + Tokens[1] + "\n";
                                    break;
                                case "created_by":
                                    resout += MainActivity.this.getString(R.string.created_by);
                                    resout += ": " + Tokens[1] + "\n";
                                    break;
                                case "term_count":
                                    resout += MainActivity.this.getString(R.string.term_count);
                                    resout += ": " + Tokens[1] + "\n";
                                    break;
                                case "created_date":
                                    resout += MainActivity.this.getString(R.string.created_date);
                                    Date dtCreated = new Date(Long.parseLong(Tokens[1]) * 1000);
                                    resout += ": " + Data.SHORT_DATE_FORMAT.format(dtCreated) + "\n";
                                    break;
                                case "modified_date":
                                    resout += MainActivity.this.getString(R.string.modified_date);
                                    Date dtModified = new Date(Long.parseLong(Tokens[1]) * 1000);
                                    resout += ": " + Data.SHORT_DATE_FORMAT.format(dtModified) + "\n";
                                    break;
                                case "published_date":
                                    resout += MainActivity.this.getString(R.string.published_date);
                                    Date dtPublished = new Date(Long.parseLong(Tokens[1]) * 1000);
                                    resout += ": " + Data.SHORT_DATE_FORMAT.format(dtPublished) + "\n";
                                    break;
                                case "visibility":
                                    resout += MainActivity.this.getString(R.string.visibility);
                                    resout += ": " + Tokens[1] + "\n";
                                    break;
                                case "editable":
                                    resout += MainActivity.this.getString(R.string.editable);
                                    resout += ": " + Tokens[1] + "\n";
                                    break;
                                case "description":
                                    resout += MainActivity.this.getString(R.string.description);
                                    resout += ": " + Tokens[1] + "\n";
                                    break;
                                case "lang_terms":
                                    resout += MainActivity.this.getString(R.string.lang_terms);
                                    resout += ": " + Tokens[1] + "\n";
                                    break;
                                case "lang_definitions":
                                    resout += MainActivity.this.getString(R.string.lang_definitions);
                                    resout += ": " + Tokens[1] + "\n";
                                    break;
								/*password_use: 0
								password_edit: 0
								access_type: 2
								creator_id: 30990258
								set_id: 102155109*/
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    lib.ShowMessage(MainActivity.this, resout, MainActivity.this.getString(R.string.upload_successful));
                }
            }
        }

        @Override
        protected void onPreExecute()
        {
            p = new ProgressDialog(MainActivity.this);
            p.setMessage(MainActivity.this.getString(R.string.saving));
            p.show();
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
        }


    }

    class TaskOpenUri extends AsyncTask<Void, Void, Exception>
    {
        ProgressDialog p;
        Uri uri;

        TaskOpenUri(Uri uri)
        {
            super();
            this.uri = uri;
        }

        @Override
        protected Exception doInBackground(Void... params)
        {
            try
            {
                try
                {
                    vok.LoadFile(MainActivity.this, null, uri, false, false, _blnUniCode, true);
                }
                catch (RuntimeException ex)
                {
                    if (ex.getCause() != null)
                    {
                        if (ex.getCause().getMessage() != null
                                && ex.getCause().getMessage()
                                .contains("IsSingleline"))
                        {
                            vok.LoadFile(MainActivity.this, null, uri, true, false, _blnUniCode);
                        }
                        else
                        {
                            throw ex;
                        }
                    }
                    else
                    {
                        throw ex;
                    }
                }
            }

            catch (Exception e2)
            {
                return e2;
            }


            return null;
        }

        @Override
        protected void onPostExecute(Exception ex)
        {

            if (p.isShowing()) p.dismiss();
            try
            {
                if (ex == null)
                {
                    try
                    {

                        if (vok.getCardMode())
                        {
                            if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                                fPA.fragMain.SetViewsToCardmode();
                        }
                        else
                        {
                            if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                                fPA.fragMain.SetViewsToVokMode();
                        }

                        // if (index >0 ) vok.setIndex(index);
                        if (vok.getGesamtzahl() > 0)
                        {
                            if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                                fPA.fragMain.setBtnsEnabled(true);
                        }
                        if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                            fPA.fragMain.getVokabel(false, false, false);
                    }
                    catch (Exception e)
                    {
                        lib.ShowException(MainActivity.this, e);
                        if (fPA.fragMain != null && fPA.fragMain.mainView != null)
                        {
                            try
                            {
                                fPA.fragMain.getVokabel(true, true, false);
                            }
                            catch (Exception e1)
                            {
                                lib.ShowException(MainActivity.this, e1);
                            }
                        }
                    }
                    takePersistableUri(uri, false);
                    prefs.edit().putString("defaultURI", uri.toString()).commit();

                }
                else
                {
                    throw ex;
                }
            }
            catch (Exception e2)
            {
                lib.ShowException(MainActivity.this, ex);
            }


        }


        @Override
        protected void onPreExecute()
        {
            try
            {
                lib.CheckPermissions(MainActivity.this, uri, true);
                p = new ProgressDialog(MainActivity.this);
                p.setMessage(MainActivity.this.getString(R.string.loading));
                p.show();
            }
            catch (Exception e)
            {
                lib.ShowException(MainActivity.this, e);
                this.cancel(false);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
        }


    }


}