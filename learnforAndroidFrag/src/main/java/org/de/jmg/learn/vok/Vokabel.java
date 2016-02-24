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
package org.de.jmg.learn.vok;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.TextView;

import org.de.jmg.learn.MainActivity;
import org.de.jmg.learn.R;
import org.de.jmg.learn.libLearn;
import org.de.jmg.lib.RefSupport;
import org.de.jmg.lib.RichTextStripper;
import org.de.jmg.lib.WindowsBufferedReader;
import org.de.jmg.lib.lib;
import org.de.jmg.lib.lib.libString;
import org.de.jmg.lib.lib.yesnoundefined;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Vokabel
{

    // Learn For All New Version
    // By J.M.Goebel (jhmgbl2@t-online.dee)
    //
    // This program is free software; you can redistribute it and/or
    // modify it under the terms of the GNU General Public License
    // as published by the Free Software Foundation; either version 2
    // of the License, or (at your option) any later version.
    //
    // This program is distributed in the hope that it will be useful,
    // but WITHOUT ANY WARRANTY; without even the implied warranty of
    // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    // GNU General Public License for more details.
    //
    // You should have received a copy of the GNU General Public License
    // along with this program; if not, write to the Free Software
    // Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307,
    // USA.
    //

    final static String className = "Vokabel";

    // ******** Events ************
    interface FehlerEventHandler
    {
        void Fehler(String strText);
    }

    List<FehlerEventHandler> FehlerEventlisteners = new ArrayList<>();

    public void addFehlerEventListener(FehlerEventHandler toAdd)
    {
        FehlerEventlisteners.add(toAdd);
    }

    // Set of functions that Throw Events.
    public void ThrowFehlerEvent(String strText)
    {
        for (FehlerEventHandler hl : FehlerEventlisteners)
            hl.Fehler(strText);
        System.out.println("Something thrown");
    }

    interface AbfrageEndeEventHandler
    {
        void AbfrageEnde(String strText);
    }

    List<AbfrageEndeEventHandler> AbfrageEndeEventlisteners = new ArrayList<>();

    public void addAbfrageEndeEventListener(AbfrageEndeEventHandler toAdd)
    {
        AbfrageEndeEventlisteners.add(toAdd);
    }

    // Set of functions that Throw Events.
    public void ThrowAbfrageEndeEvent(String strText)
    {
        for (AbfrageEndeEventHandler hl : AbfrageEndeEventlisteners)
            hl.AbfrageEnde(strText);
        System.out.println("Something thrown");
    }

    // ********** Public ***********
    public boolean aend;
    public boolean varHebr;
    public boolean reverse;
    public int AnzRichtig;
    public float ProbabilityFactor = -1;
    public int RestartInterval = 10;
    public Typeface TypefaceCardo;
    public String title;
    public int AnzFalsch;

    public enum Bewertung
    {
        AllesRichtig, TeilweiseRichtig, aehnlich, AllesFalsch, enthalten, TeilweiseRichtigEnthalten, TeilweiseRichtigAehnlich, AehnlichEnthalten, TeilweiseRichtigAehnlichEnthalten, undefiniert
    }

    public enum EnumSprachen
    {
        Normal, Hebrew, Griechisch, Sonst1, Sonst2, Sonst3, undefiniert
    }

    public class structFonts
    {
        public typFonts Normal;
        public typFonts Hebrew;
        public typFonts Griechisch;
        public typFonts Sonst1;
        public typFonts Sonst2;
        public typFonts Sonst3;
    }

    public Activity Container;

    // ******** Types ******

    public class typFonts
    {
        // FÃ¼r Fonts
        public clsFont Wort;
        public clsFont Bed;
        public clsFont Kom;
    }

    // ************* final ************

    final short ErrLernindex = 1005;
    final short ErrWrongfilename = 1001;

    final short ErrNoFileHandle = 1002;
    private Locale mLangWord = Locale.getDefault();
    private Locale mLangMeaning = Locale.getDefault();
    private ArrVok mVok;
    // enthÃ¤lt die Indexwerte der Lernvokabeln
    private int[] mLernVokabeln;
    // gibt an welcher Index zuletzt bei den Lernvokabeln verwendet wurde
    private int mLastIndex;
    // Abfragen ist mÃ¶glich
    private boolean mblnLernInit;
    // lokale Kopie
    private int mIndex;
    // lokale Kopie
    private int mGesamtzahl;
    // lokale Kopie
    private boolean mConfirmChanges;
    // Private mChanged As booleanean 'lokale Kopie
    // lokale Kopie
    private short mSchrittweite;
    // lokale Kopie
    private clsFont mWortFont;// = new clsFont(Container);
    // lokale Kopie
    private clsFont mBedFont;// = new clsFont(Container);
    // lokale Kopie
    private clsFont mKomFont;// = new clsFont(Container);
    // lokale Kopie
    private short mAbfragebereich;
    // lokale Kopie
    private boolean mAbfrageZufaellig;
    // lokale Kopie
    private short mLerngeschwindigkeit;
    // lokale Kopie
    private EnumSprachen mSprache = EnumSprachen.undefiniert;
    // lokale Kopie
    private short mLernindex;
    // lokale Kopie
    private String mFileName;
    private String mVokPath;
    private TextView mSTatusO;
    private String[] mOldBed = new String[3];
    private String mSTatus;
    private structFonts mFonts;
    private boolean _cardmode;
    private boolean _UniCode;
    private boolean _AskAll;
    private Uri _uri = null;


    public Uri getURI()
    {
        return _uri;
    }

    public void setURI(Uri value)
    {
        _uri = value;
    }

    public boolean getAskAll()
    {
        return _AskAll;
    }

    public void setAskAll(boolean value)
    {
        _AskAll = value;
    }

    public boolean getUniCode()
    {
        return _UniCode;
    }

    private String[] mAntworten;

    public String[] getAntworten()
    {
        return mAntworten;
    }

    public String[] getBedeutungen() throws Exception
    {
        return new String[]{getBedeutung1(), getBedeutung2(), getBedeutung3()};
    }

    public ArrVok getVokabeln()
    {
        return mVok;
    }

    public String getProperties() throws Exception
    {
        String txt;
        txt = getContext().getString(R.string.TotalNumber) + ": "
                + this.getGesamtzahl();
        for (int i = -6; i <= 6; i++)
        {
            txt += "\r\n" + "z = " + i + ": "
                    + this.Select(null, null, i).size();
        }
        return txt;
    }

    public String getvok_Path()
    {
        return mVokPath;
    }

    public void setvok_Path(String value)
    {
        mVokPath = value;
    }

    public boolean getCardMode()
    {
        return _cardmode;
    }

    public void setCardMode(boolean value)
    {
        _cardmode = value;
    }

    public String getFileName()
    {
        return mFileName;
    }

    public void setFileName(String value)
    {
        mFileName = value;
        if (value != null)
        {
            File fname = new File(value);
            this.mVokPath = fname.getParent();
        }
        else
        {
            this.mVokPath = null;
        }
    }

    public Locale getLangWord()
    {
        return mLangWord;
    }

    public Locale getLangMeaning()
    {
        return mLangMeaning;
    }

    public void setLangWord(Locale l)
    {
        mLangWord = l;
    }

    public void setLangMeaning(Locale l)
    {
        mLangMeaning = l;
    }

    public String[] getOldBed()
    {
        return (mOldBed);
    }

    public structFonts getfonts()
    {
        return mFonts;
    }

    public void setFonts(structFonts value)
    {
        mFonts = value;
    }

    public short getLernIndex()
    {
        return mLernindex;
    }

    public String CharsetASCII = "windows-1252";

    public void setLernIndex(short value) throws Exception
    {
        libLearn.gStatus = "Vokabel.LernIndex Start";
        if (value > mSchrittweite)
            value = 1;
        if (!mblnLernInit)
            InitAbfrage();
        if (value > mSchrittweite) value = mSchrittweite;
        if (value > 0 && value <= mSchrittweite && mblnLernInit)
        {
            mLernindex = value;
            mIndex = mLernVokabeln[mLernindex];
        }
        else
        {
            throw new Exception(
                    "Die Abfrage konnte nicht aktualisiert werden oder Fehler!");
        }

    }

    public EnumSprachen getSprache()
    {
        return mSprache;
    }

    public void setSprache(EnumSprachen value)
    {
        libLearn.gStatus = "Vokabel.Sprache Start";
        if (mSprache.ordinal() != value.ordinal())
        {
            mSprache = value;
            aend = true;
        }

    }

    private String[] _Trennzeichen = new String[6];


    public String getTrennzeichen()
    {
        libLearn.gStatus = "Vokabel.Trennzeichen Start";
        return _Trennzeichen[mSprache.ordinal()];
    }

    public void setTrennzeichen(String value)
    {
        libLearn.gStatus = "Vokabel.Trennzeichen Start";
        _Trennzeichen[mSprache.ordinal()] = value;

    }

    public short getLerngeschwindigkeit()
    {
        return mLerngeschwindigkeit;
    }

    public void setLerngeschwindigkeit(short value)
    {
        mLerngeschwindigkeit = value;
    }

    public boolean getAbfrageZufaellig()
    {
        return mAbfrageZufaellig;
    }

    public void setAbfrageZufaellig(boolean value)
    {
        mAbfrageZufaellig = value;
    }

    // Der Abfragebereicht wird durch eine Zahl zwischen -1 und 6 reprÃ¤sentiert
    // <=0 reprÃ¤sentiert alle Vokabeln, die ein oder mehrmals nicht gewuÃŸt
    // wurden
    // 1 alle Zahlen, die noch gar nicht gewuÃŸt wurden, bei jeder richtigen
    // Antwort wird
    // der ZÃ¤hler um eins erhÃ¶ht.
    public short getAbfragebereich()
    {
        return mAbfragebereich;
    }

    public void setAbfragebereich(short value)
    {
        mAbfragebereich = value;
    }

    public clsFont getFontKom()
    {
        return mKomFont;
    }

    public void setFontKom(clsFont value)
    {
        mKomFont = value;
    }

    public clsFont getFontBed()
    {
        return mBedFont;
    }

    public void setFontBed(clsFont value)
    {
        mBedFont = value;
    }

    public clsFont getFontWort()
    {
        return mWortFont;
    }

    public void setFontWort(clsFont value)
    {
        mWortFont = value;
    }

    public short getSchrittweite()
    {
        return mSchrittweite;
    }

    public void setSchrittweite(short value) throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Schrittweite Start";
        // wird beim Zuweisen eines Werts in eine Eigenschaft auf der linken
        // Seite der Gleichung, verwendet.
        // Syntax: X.Schrittweite = 5
        if (value < mGesamtzahl || mGesamtzahl <= 0)
        {
            mSchrittweite = value;
        }
        else
        {
            mSchrittweite = (short) mGesamtzahl;
        }

    }

    public String getStatus() throws Exception
    {
        String functionReturnValue;
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.libLearn.gStatus Start";
        //
        //
        functionReturnValue = mSTatus;
        return functionReturnValue;
    }

    public void setStatus(String value) throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.libLearn.gStatus Start";
        if (mSTatusO != null)
        {
            mSTatusO.setText(value);
        }

        mSTatus = value;

    }

    public boolean getConfirmChanges() throws Exception
    {
        boolean functionReturnValue;
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.ConfirmChanges Start";
        // wird beim Ermitteln einer Eignschaft auf der rechten Seite der
        // Gleichung verwendet.
        // Syntax: Debug.Print X.ConfirmChanges
        functionReturnValue = mConfirmChanges;
        return functionReturnValue;
    }

    public void setConfirmChanges(boolean value)
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.ConfirmChanges Start";
        // wird beim Zuweisen eines Werts in eine Eigenschaft auf der linken
        // Seite der Gleichung, verwendet.
        // Syntax: X.ConfirmChanges = 5
        mConfirmChanges = value;

    }

    // wird beim Ermitteln einer Eignschaft auf der rechten Seite der Gleichung
    // verwendet.
    // Syntax: Debug.Print X.Gesamtzahl
    public int getGesamtzahl()
    {
        if (mGesamtzahl == 0 && mVok != null)
        {
            mGesamtzahl = mVok.size();
        }
        return mGesamtzahl;
    }

    public int getIndex()
    {
        return mIndex;
    }

    public void setIndex(int value) throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Index Start";
        // ERROR: Not supported in C#: OnErrorStatement
        if (value >= 0 & value <= mGesamtzahl)
        {
            mIndex = value;
        }

    }

    public int getLastIndex()
    {
        return mLastIndex;
    }

    public void setLastIndex(int value) throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Index Start";
        // ERROR: Not supported in C#: OnErrorStatement
        if (value >= 0 & value <= mGesamtzahl)
        {
            mLastIndex = value;
        }

    }

    public int[] getLernvokabeln()
    {
        return mLernVokabeln;
    }

    public void setLernvokabeln(int[] Lernvokabeln)
    {
        if (checkLernvokabeln(Lernvokabeln)) mLernVokabeln = Lernvokabeln;
    }

    public boolean checkLernvokabeln(int[] Lernvokabeln)
    {
        if (Lernvokabeln == null) return false;

        boolean found = false;
        for (int i = 1; i < Lernvokabeln.length; i++)
        {
            if (Lernvokabeln[i] > 0)
            {
                found = true;
                break;
            }
        }
        return found;
    }

    public short getZaehler() throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.ZÃƒÆ’Ã‚Â¤hler Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Ermitteln einer Eignschaft auf der rechten Seite der
        // Gleichung verwendet.
        // Syntax: Debug.Print X.ZÃƒÆ’Ã‚Â¤hler
        if (mIndex > mVok.size() - 1) mIndex = mVok.size() - 1;
        return mVok.get(mIndex).z;
        //return functionReturnValue;
    }

    public void setZaehler(short value) throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.ZÃƒÆ’Ã‚Â¤hler Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Zuweisen eines Werts in eine Eigenschaft auf der linken
        // Seite der Gleichung, verwendet.
        // Syntax: X.ZÃƒÆ’Ã‚Â¤hler = 5
        mVok.get(mIndex).z = value;
        aend = true;
    }

    public String getKommentar() throws Exception
    {
        String functionReturnValue = null;
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Kommentar Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Ermitteln einer Eignschaft auf der rechten Seite der
        // Gleichung verwendet.
        // Syntax: Debug.Print X.Kommentar
        if (mIndex > mVok.size() - 1) mIndex = mVok.size() - 1;
        if (mIndex < 0) return "";
        if (mVok.get(mIndex).Kom != null) functionReturnValue = mVok.get(mIndex).Kom;
        return functionReturnValue;
    }

    public void setKommentar(String value) throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Kommentar Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Zuweisen eines Werts in eine Eigenschaft auf der linken
        // Seite der Gleichung, verwendet.
        // Syntax: X.Kommentar = 5https://banking.ing-diba.de/app/rd/auftrag_an_die_diba?x=shITyvzDn9yY

        mVok.get(mIndex).Kom = value;
        aend = true;
    }

    public String getBedeutung3() throws Exception
    {
        String functionReturnValue = null;
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Bedeutung3 Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Ermitteln einer Eignschaft auf der rechten Seite der
        // Gleichung verwendet.
        // Syntax: Debug.Print X.Bedeutung3
        if (mIndex > mVok.size() - 1) mIndex = mVok.size() - 1;
        if (mIndex < 0) return "";
        if (mVok.get(mIndex).Bed3 != null) functionReturnValue = mVok.get(mIndex).Bed3.trim();
        return functionReturnValue;
    }

    public void setBedeutung3(String value) throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Bedeutung3 Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Zuweisen eines Werts in eine Eigenschaft auf der linken
        // Seite der Gleichung, verwendet.
        // Syntax: X.Bedeutung3 = 5
        mVok.get(mIndex).Bed3 = value;
        aend = true;
    }

    public String getBedeutung2() throws Exception
    {
        String functionReturnValue = null;
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Bedeutung2 Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Ermitteln einer Eignschaft auf der rechten Seite der
        // Gleichung verwendet.
        // Syntax: Debug.Print X.Bedeutung2
        if (mIndex > mVok.size() - 1) mIndex = mVok.size() - 1;
        if (mIndex < 0) return "";
        if (mVok.get(mIndex).Bed2 != null) functionReturnValue = (mVok.get(mIndex).Bed2).trim();
        return functionReturnValue;
    }

    public void setBedeutung2(String value) throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Bedeutung2 Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Zuweisen eines Werts in eine Eigenschaft auf der linken
        // Seite der Gleichung, verwendet.
        // Syntax: X.Bedeutung2 = 5
        mVok.get(mIndex).Bed2 = value;
        aend = true;
    }

    public String getBedeutung1() throws Exception
    {
        String functionReturnValue = null;
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Bedeutung1 Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Ermitteln einer Eignschaft auf der rechten Seite der
        // Gleichung verwendet.
        // Syntax: Debug.Print X.Bedeutung1
        if (mIndex > mVok.size() - 1) mIndex = mVok.size() - 1;
        if (mIndex < 0) return "";
        if (mVok.get(mIndex).Bed1 != null) functionReturnValue = (mVok.get(mIndex).Bed1).trim();
        return functionReturnValue;
    }

    public void setBedeutung1(String value, boolean blnDontThrow) throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Bedeutung1 Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Zuweisen eines Werts in eine Eigenschaft auf der linken
        // Seite der Gleichung, verwendet.
        // Syntax: X.Bedeutung1 = 5
        if ((value).length() > 0)
        {
            aend = true;
            mVok.get(mIndex).Bed1 = value;
        }
        else if (!blnDontThrow)
        {
            throw new Exception(Container.getString(R.string.MeaningMustContainText));
        }
    }

    // Inserted by CodeCompleter
    public String getWort() throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Wort Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Ermitteln einer Eignschaft auf der rechten Seite der
        // Gleichung verwendet.
        // Syntax: Debug.Print X.Wort
        if (mIndex > mVok.size() - 1) mIndex = mVok.size() - 1;
        if (mIndex < 0) return "";
        return mVok.get(mIndex).Wort;

    }

    public void setWort(String value, boolean blnDontThow) throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.Wort Start";
        // ERROR: Not supported in C#: OnErrorStatement
        // wird beim Zuweisen eines Werts in eine Eigenschaft auf der linken
        // Seite der Gleichung, verwendet.
        // Syntax: X.Wort = 5
        if (value != null && (value).length() > 0)
        {
            mVok.get(mIndex).Wort = value;
            aend = true;
        }
        else if (!blnDontThow)
        {
            throw new Exception(Container.getString(R.string.EmptyWord));
        }
    }

    // Inserted by CodeCompleter
    public void Init(TextView StatusO)
    {
        // ERROR: Not supported in C#: OnErrorStatement

        libLearn.gStatus = "Vokabel.Init Start";

        mSTatusO = StatusO;


    }

    public void SkipVokabel() throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement

        libLearn.gStatus = "Vokabel.SkipVokabel Start";
        ReorderLernVokabeln();
        mLernindex += 1;
        InitAbfrage();
        //

    }

    public Bewertung CheckAntwort(String[] Antworten) throws Exception
    {
        Bewertung functionReturnValue;
        final String CodeLoc = className + ".CheckAntwort";
        functionReturnValue = Bewertung.undefiniert;
        try
        {
            libLearn.gStatus = "Vokabel.CheckAnwort Start";
            String[] Bedeutungen;
            short i;
            short ii;
            short Loesungen = 0;
            // Anzahl der eingegebenen Antworten
            short anzBedeutungen = 0;

            short Enthalten = 0;
            // Anzahl der Antworten die nur einen TeilString enthalten
            short aehnlich = 0;
            // Anzahl der aehnlichen Antworten
            short TeilweiseRichtig = 0;

			/*
             * short TeilweiseRichtigAehnlich = 0;
			 * 
			 * short TeilweiseRichtigAehnlichEnthalten = 0;
			 * 
			 * short TeilweiseRichtigEnhalten = 0;
			 * 
			 * short aehnlichenthalten = 0;
			 */

            libLearn.gStatus = "Vokabel.CheckAnwort Line 248";
            // Inserted by CodeCompleter
            Bewertung TeilErgebnis;

            mOldBed[0] = "";
            mOldBed[1] = "";
            mOldBed[2] = "";
            libLearn.gStatus = "Vokabel.CheckAnwort Line 258";
            // Inserted by CodeCompleter
            Bedeutungen = new String[]{getBedeutung1(), getBedeutung2(),
                    getBedeutung3()};
            mAntworten = Antworten;
            libLearn.gStatus = CodeLoc + " Gleichheit ÃœberprÃ¼fen";

            for (ii = 0; ii <= (Bedeutungen.length - 1); ii++)
            {
                Bedeutungen[ii] = (Bedeutungen[ii]).trim();

                if (!libString.IsNullOrEmpty(Bedeutungen[ii]))
                {
                    anzBedeutungen += 1;
                }
            }
            libLearn.gStatus = "Vokabel.CheckAnwort Line 268";
            // Inserted by CodeCompleter
            for (i = 0; i <= (Antworten.length - 1); i++)
            {
                if (!(Antworten[i] == null))
                {
                    Antworten[i] = (Antworten[i]).trim();
                    String Antwort = RemoveKomment(Antworten[i]);
                    for (ii = 0; ii <= (Bedeutungen.length - 1); ii++)
                    {
                        Bedeutungen[ii] = (Bedeutungen[ii]).trim();

                        if (!libString.IsNullOrEmpty(Bedeutungen[ii]))
                        {
                            libLearn.gStatus = CodeLoc + " call MakeVergl";
                            boolean CheckVergl;
                            try
                            {
                                CheckVergl = lib.like(Antwort,
                                        MakeVergl(Bedeutungen[ii]));
                            }
                            catch (Exception ex)
                            {
                                throw new Exception(ex.getMessage() + "\n"
                                        + CodeLoc + " CheckVergl");
                            }
                            if (CheckVergl)
                            {
                                libLearn.gStatus = "Vokabel.CheckAnwort Line 278";
                                // Inserted by CodeCompleter
                                mOldBed[ii] = Bedeutungen[ii];
                                Bedeutungen[ii] = "";
                                Loesungen += 1;

                                break;
                                // For
                                // Falls eine Antwort mehrere Teilantworten
                                // enthÃ¤lt
                            }
                            else
                            {
                                String[] s = EnthaeltTrennzeichen(RemoveKomment(Bedeutungen[ii]));
                                RefSupport<String> refVar1 = new RefSupport<>(Antwort);
                                RefSupport<String[]> refVar2 = new RefSupport<>(s);
                                short[] refii = new short[]{ii};
                                RefSupport<short[]> refVar3 = new RefSupport<>(refii);
                                TeilErgebnis = TeileUeberpruefen(refVar1,
                                        refVar2, refVar3);
                                Antwort = refVar1.getValue();
                                //s = refVar2.getValue();
                                ii = refVar3.getValue()[0];
                                if (TeilErgebnis == Bewertung.AllesRichtig)
                                {
                                    libLearn.gStatus = "Vokabel.CheckAnwort Line 288";
                                    // Inserted by CodeCompleter
                                    Loesungen += 1;
                                    mOldBed[ii] = Bedeutungen[ii];
                                    Bedeutungen[ii] = "";
                                    //Antwort = "";
                                    break;
                                    // Exit For
                                }
                                else if (TeilErgebnis == Bewertung.TeilweiseRichtig)
                                {
                                    TeilweiseRichtig += 1;

                                    // Bedeutungen(ii) = ""
                                    // Antwort = ""
                                    // Exit For
                                    libLearn.gStatus = "Vokabel.CheckAnwort Line 298";
                                }
                                // Inserted by CodeCompleter
                                else if (TeilErgebnis == Bewertung.enthalten)
                                {
                                    Enthalten += 1;
                                }
                                else if (TeilErgebnis == Bewertung.aehnlich)
                                {
                                    aehnlich += 1;
                                }
                                else if (TeilErgebnis == Bewertung.AehnlichEnthalten)
                                {
                                    aehnlich += 1;
                                    Enthalten += 1;
                                }
                                else if (TeilErgebnis == Bewertung.TeilweiseRichtigAehnlich)
                                {
                                    TeilweiseRichtig += 1;
                                    aehnlich += 1;
                                    // TeilweiseRichtigAehnlich += 1;
                                }
                                else if (TeilErgebnis == Bewertung.TeilweiseRichtigAehnlichEnthalten)
                                {
                                    TeilweiseRichtig += 1;
                                    aehnlich += 1;
                                    Enthalten += 1;
                                    // TeilweiseRichtigAehnlichEnthalten += 1;
                                }
                                else if (TeilErgebnis == Bewertung.TeilweiseRichtigEnthalten)
                                {
                                    TeilweiseRichtig += 1;
                                    Enthalten += 1;
                                    // TeilweiseRichtigEnhalten += 1;
                                }
                                else
                                {
                                    CheckVergl = false;
                                    float SizeRelation = ((float) Antwort
                                            .length() / Bedeutungen[ii]
                                            .length());
                                    if (SizeRelation > 0.5)
                                    {
                                        CheckVergl = lib.like(Bedeutungen[ii],
                                                "*" + MakeVergl(Antwort) + "*");

                                    }
                                    if (CheckVergl)
                                    {
                                        Enthalten += 1;
                                    }
                                    else
                                    {
                                        short refII[] = new short[]{ii};
                                        RefSupport<short[]> refVar___ii = new RefSupport<>(refII);
                                        boolean boolVar___0 = Aehnlichkeit(
                                                Bedeutungen[ii], Antwort,
                                                refVar___ii) > 0.5;
                                        ii = refVar___ii.getValue()[0];
                                        if (boolVar___0)
                                        {
                                            aehnlich += 1;
                                        }
                                    }

                                }

                            }
                        }
                    }
                    libLearn.gStatus = "Vokabel.CheckAnwort Line 308";
                    // Inserted by CodeCompleter
                }
            }
            libLearn.gStatus = CodeLoc + " Auswertung";
            if (Loesungen > 0 && Loesungen == anzBedeutungen)
            {
                functionReturnValue = Bewertung.AllesRichtig;
            }
            else if (Loesungen < anzBedeutungen)
            {
                if (Loesungen > 0 || TeilweiseRichtig > 0)
                {
                    functionReturnValue = Bewertung.TeilweiseRichtig;
                    libLearn.gStatus = "Vokabel.TeileUeberpruefen Line 450";
                    // Inserted by CodeCompleter
                    if (aehnlich > 0)
                    {
                        functionReturnValue = Bewertung.TeilweiseRichtigAehnlich;
                        if (Enthalten > 0)
                        {
                            functionReturnValue = Bewertung.TeilweiseRichtigAehnlichEnthalten;
                        }
                    }
                    else if (Enthalten > 0)
                    {
                        functionReturnValue = Bewertung.TeilweiseRichtigEnthalten;
                    }
                }
                else
                {
                    functionReturnValue = Bewertung.AllesFalsch;
                    if (Enthalten > 0)
                    {
                        functionReturnValue = Bewertung.enthalten;
                        if (aehnlich > 0)
                        {
                            functionReturnValue = Bewertung.AehnlichEnthalten;
                        }
                    }
                    else if (aehnlich > 0)
                    {
                        functionReturnValue = Bewertung.aehnlich;

                    }

                }
            }
        }
        catch (Exception ex)
        {
            throw new Exception(CodeLoc, ex);
        }
        return functionReturnValue;

    }

    private float Aehnlichkeit(String Bedeutung, String Antwort,
                               RefSupport<short[]> BedNR) throws Exception
    {

        final String CodeLoc = className + ".Aehnlichkeit";
        libLearn.gStatus = CodeLoc + " Start";
        short Size1;
        Bedeutung = Bedeutung.toLowerCase(Locale.getDefault());
        Antwort = Antwort.toLowerCase(Locale.getDefault());
        Size1 = (short) RemoveKomment(Bedeutung).length();
        libLearn.gStatus = CodeLoc + " RemoveKomment";
        // Antwort = RemoveKomment(Antwort)
        libLearn.gStatus = CodeLoc + " Levenshtein";
        int levenshtein = LevenshteinDistance(Bedeutung, Antwort);
        boolean blnOldBed = !libString
                .IsNullOrEmpty(mOldBed[BedNR.getValue()[0]]);
        // TODO: Dim locs(Size1) As Integer
        int LastPos = 0;
        String Test;
        // Bedeutung = Me.Bedeutungen(BedNR)
        // mOldBed(BedNR) = ""
        // Antwort = mAntworten(BedNR)
        Test = new String(new char[Bedeutung.length()]).replace('\0', '*'); // new
        // String('*',
        // Bedeutung.length());
        char[] tst = Test.toCharArray();
        for (int ii = 0; ii <= Antwort.length() - 1; ii++)
        {
            int Pos;
            int Pos2 = -1;
            int LastLastPos = LastPos;
            do
            {
                String sub = Antwort.substring(ii, ii + 1);
                Pos = Bedeutung.indexOf(sub, LastPos);
                if (Pos == -1)
                    break;


                if (Pos > -1 && ii < Antwort.length() - 1)
                {
                    sub = Antwort.substring(ii + 1, ii + 2);
                    Pos2 = Bedeutung.indexOf(sub, Pos + 1);
                }

                if (Pos2 != Pos + 1)
                    LastPos = Pos + 1;

            }
            while (!(Pos2 == Pos + 1 || LastPos >= Antwort.length() - 1));
            if (Pos > -1)
            {
                if (ii == Antwort.length() - 1 || Pos2 == Pos + 1)
                {
                    tst[Pos] = Bedeutung.charAt(Pos); // Bedeutung.substring(Pos,
                    // 1).toCharArray()[0];
                    if (Pos2 == Pos + 1)
                    {
                        tst[Pos2] = Bedeutung.charAt(Pos2);// Bedeutung.substring(Pos2,
                        // 1).;
                    }

                    LastPos = Pos + 1;
                }
                else
                {
                    LastPos = LastLastPos;
                }
            }
            else
            {
                LastPos = LastLastPos;
            }
        }
        Test = new String(tst);
        if (libString.IsNullOrEmpty(mOldBed[BedNR.getValue()[0]])
                || !blnOldBed)
        {
            mOldBed[BedNR.getValue()[0]] = Test;
        }
        else
        {
            // For iii As Integer = 0 To test.Length - 1
            // If test.SubString(iii, 1) <> "*" Then
            // Mid(mOldBed(BedNR), iii + 1, 1) = test.SubString(iii, 1)
            // End If
            // Next
            mOldBed[BedNR.getValue()[0]] = mOldBed[BedNR.getValue()[0]] + ","
                    + Test;
        }
        int AnzFalsch = lib.countMatches(Test, "*");// ClsGlobal.CountChar(Test,
        // "*");
        float Aehn1 = (float) (Size1 - AnzFalsch) / Size1;
        float Aehn2 = (float) (Size1 - levenshtein) / Size1;
        if (Aehn1 < Aehn2)
        {
            return Aehn1;
        }
        else
        {
            return Aehn2;
        }
    }

    private static int minimum(int a, int b, int c)
    {
        return Math.min(Math.min(a, b), c);
    }

    public static int computeLevenshteinDistance(String str1, String str2)
    {
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= str2.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= str1.length(); i++)
            for (int j = 1; j <= str2.length(); j++)
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1]
                                + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                : 1));

        return distance[str1.length()][str2.length()];
    }

    public int LevenshteinDistance(String Bedeutung, String Antwort)
    {

        Bedeutung = RemoveKomment(Bedeutung);
        Antwort = RemoveKomment(Antwort);
        return computeLevenshteinDistance(Bedeutung, Antwort);
    }

    private String MakeVergl(String Bed) throws Exception
    {
        String functionReturnValue;
        final String CodeLoc = className + ".MakeVergl";
        libLearn.gStatus = CodeLoc + " Start";
        short i;
        int f1;
        int f2;
        int intAsc;
        // Optionale Teile herausfiltern
        try
        {
            f1 = Bed.indexOf("(", 0);// libString.InStr(1, Bed, "(");
            libLearn.gStatus = CodeLoc + " Klammern verarbeiten";
            while (f1 > -1)
            {
                f2 = Bed.indexOf(")", f1 + 1); // libString.InStr(f1 + 1, Bed,
                // ")");
                if (f2 > -1)
                {
                    Bed = Bed.substring(0, f1) + "*"
                            + Bed.substring(f2 + 1, Bed.length()); // libString.Left(Bed,
                    // f1 - 1) +
                    // "*" +
                    // libString.Mid(Bed,
                    // f2 + 1,
                    // libString.Len(Bed)
                    // - f2);
                    f1 = Bed.indexOf("(", f1 + 1); // libString.InStr(f2 + 1,
                    // Bed, "(");
                }
                else
                {
                    f1 = f2;
                }
            }
            libLearn.gStatus = CodeLoc + "Kommentare herausfiltern";
            Bed = RemoveKomment(Bed);
            libLearn.gStatus = CodeLoc + " UngÃ¼ltige Zeichen ersetzen";
            if (Bed.length() > 0)
            {
                Bed = (Bed).toUpperCase(Locale.getDefault());

                for (i = 1; i <= (Bed).length(); i++)
                {
                    try
                    {
                        intAsc = Bed.charAt(i - 1);// libString.Asc(libString.Mid(Bed,
                        // i, 1));
                        if (intAsc < 65 | intAsc > 90)
                        {
                            Bed = Bed.substring(0, i - 1) + "*"
                                    + Bed.substring(i, Bed.length());
                        }
                    }
                    catch (Exception ex)
                    {
                        throw new Exception(
                                "Fehler bei MakeVergl UngÃ¼ltige Zeichen: \n"
                                        + ex.getMessage());
                    }
                }
            }
            functionReturnValue = Bed;
        }
        catch (Exception ex)
        {
            throw new Exception("Fehler bei MakeVergl: \n" + ex.getMessage());
        }
        return functionReturnValue;

    }

    public static String RemoveKomment(String Bed)
    {
        final String CodeLoc = className + ".RemoveKomment";
        libLearn.gStatus = CodeLoc + " Start";
        int f1;
        int f2;
        f1 = Bed.indexOf("[", 0); // libString.InStr(1, Bed, "[");
        while (f1 > -1)
        {
            f2 = Bed.indexOf("]", f1 + 1);// libString.InStr(f1 + 1, Bed, "]");
            libLearn.gStatus = "Vokabel.MakeVergl Line 392";
            // Inserted by CodeCompleter
            if (f2 > f1)
            {
                Bed = Bed.substring(0, f1)
                        + Bed.substring(f2 + 1, Bed.length());
                // 'libString.Left(Bed, f1 - 1) + "" + libString.Mid(Bed, f2 +
                // 1, libString.Len(Bed) - f2);
                f1 = Bed.indexOf("[", f1 + 1);// libString.InStr(f2 + 1, Bed,
                // "[");
            }
            else
            {
                f1 = f2;
            }
        }
        return Bed;
    }

    private Bewertung TeileUeberpruefen(RefSupport<String> Antwort,
                                        RefSupport<String[]> teile, RefSupport<short[]> BedNR)
            throws Exception
    {
        Bewertung functionReturnValue = Bewertung.undefiniert;
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.TeileUeberpruefen Start";
        short i;
        short ii;
        short richtig = 0;
        short Bedeutungen = 0;
        short aehnlich = 0;
        short enthalten = 0;
        String Antworten[];
        if ((teile.getValue() == null || teile.getValue().length == 0))
        {
            functionReturnValue = Bewertung.AllesFalsch;
            return functionReturnValue;
        }

        Antworten = EnthaeltTrennzeichen(Antwort.getValue());
        for (i = 0; i <= (teile.getValue()).length - 1; i++)
        {
            // Richtige Teilantworten finden
            libLearn.gStatus = "Vokabel.TeileUeberpruefen Line 420";
            // Inserted by CodeCompleter
            if (!libString.IsNullOrEmpty(teile.getValue()[i]))
            {
                Bedeutungen += 1;
                for (ii = 0; ii <= (Antworten).length - 1; ii++)
                {
                    Antworten[ii] = (Antworten[ii]).trim();
                    if (!libString.IsNullOrEmpty(Antworten[ii]))
                    {
                        String comp = MakeVergl(teile.getValue()[i]);
                        if (!comp.equalsIgnoreCase("*") && lib.like(Antworten[ii], comp)) // If
                        // Antworten(ii)
                        // Like
                        // MakeVergl(teile(i))
                        // Then
                        {

                            richtig += 1;
                            int intBedNR = BedNR.getValue()[0];
                            if ((mOldBed[intBedNR]).length() > 0)
                            {
                                mOldBed[intBedNR] = mOldBed[intBedNR] + ","
                                        + Antworten[ii];
                                libLearn.gStatus = "Vokabel.TeileUeberpruefen Line 430";
                            }
                            else
                            {
                                // Inserted by CodeCompleter
                                mOldBed[intBedNR] = Antworten[ii];
                            }
                            Antworten[ii] = "";
                            teile.getValue()[i] = "";
                            break;
                        }

                    }

                }
            }

        }

        // Erst in zweitem Schritt aehnlichkeiten feststellen!
        boolean Aehn;
        float lAehnlichkeit;
        for (i = 0; i <= (teile.getValue()).length - 1; i++)
        {
            libLearn.gStatus = "Vokabel.TeileUeberpruefen Line 420";
            // Inserted by CodeCompleter
            Aehn = false;
            if (!libString.IsNullOrEmpty(teile.getValue()[i]))
            {
                for (ii = 0; ii <= (Antworten).length - 1; ii++)
                {
                    Antworten[ii] = (Antworten[ii]).trim();
                    if (!libString.IsNullOrEmpty(Antworten[ii]))
                    {
                        boolean CheckVergl = false;
                        float SizeRelation = ((float) Antworten[ii].length() / teile
                                .getValue()[i].length());
                        if (SizeRelation > 0.5)
                        {
                            CheckVergl = lib.like(teile.getValue()[i], "*"
                                    + MakeVergl(Antworten[ii]) + "*");

                        }
                        if (!CheckVergl)
                        {
                            RefSupport<short[]> refVar___0 = new RefSupport<>(BedNR.getValue());
                            lAehnlichkeit = Aehnlichkeit(teile.getValue()[i],
                                    Antworten[ii], refVar___0);
                            BedNR.setValue(refVar___0.getValue());
                            if (lAehnlichkeit > 0.2)
                                Aehn = true;

                            if (lAehnlichkeit > 0.5)
                            {
                                aehnlich += 1;
                                break;
                            }
                        }
                        else
                        {
                            enthalten += 1;
                        }
                    }

                }
                // Inserted by CodeCompleter
                if (!Aehn)
                {
                    if (libString.IsNullOrEmpty(mOldBed[BedNR.getValue()[0]]))
                    {
                        mOldBed[BedNR.getValue()[0]] = lib.MakeMask(teile
                                .getValue()[i]);
                    }
                    else
                    {
                        mOldBed[BedNR.getValue()[0]] = mOldBed[BedNR.getValue()[0]]
                                + "," + lib.MakeMask(teile.getValue()[i]);
                    }
                }

            }

        }
        // OldWord.AnzTeilBed(BedNR) = Bedeutungen
        if (richtig > 0 && richtig == Bedeutungen)
        {
            functionReturnValue = Bewertung.AllesRichtig;
        }
        else if (richtig < Bedeutungen)
        {
            if (richtig > 0)
            {
                functionReturnValue = Bewertung.TeilweiseRichtig;
                libLearn.gStatus = "Vokabel.TeileUeberpruefen Line 450";
                // Inserted by CodeCompleter
                if (aehnlich > 0)
                {
                    functionReturnValue = Bewertung.TeilweiseRichtigAehnlich;
                    if (enthalten > 0)
                    {
                        functionReturnValue = Bewertung.TeilweiseRichtigAehnlichEnthalten;
                    }
                }
                else if (enthalten > 0)
                {
                    functionReturnValue = Bewertung.TeilweiseRichtigEnthalten;
                }
            }
            else
            {
                functionReturnValue = Bewertung.AllesFalsch;
                if (enthalten > 0)
                {
                    functionReturnValue = Bewertung.enthalten;
                    if (aehnlich > 0)
                    {
                        functionReturnValue = Bewertung.AehnlichEnthalten;
                    }
                }
                else if (aehnlich > 0)
                {
                    functionReturnValue = Bewertung.aehnlich;

                }

            }

        }

        return functionReturnValue;

    }

    private String[] EnthaeltTrennzeichen(String Antwort)
    {
        String[] functionReturnValue;
        // RÃ¼ckgabewert ist Anzahl der Teilbedeutungen
        // ERROR: Not supported in C#: OnErrorStatement

        libLearn.gStatus = "Vokabel.EnthaeltTrennzeichen Start";
        String Trenn;
        ArrayList<String> teile = new ArrayList<>();
        // teile.add("");
        short i;
        short lastTrenn = -1;
        short Trennz = 0;
        Trenn = "/,.;:-\\";
        Antwort = RemoveKomment(Antwort);
        Antwort = Antwort + ",";

        for (i = 0; i <= (Antwort.length() - 1); i++)
        {
            if (Trenn.contains(Antwort.substring(i, i + 1)))
            {
                libLearn.gStatus = "Vokabel.EnthaeltTrennzeichen Line 464";
                // Inserted by CodeCompleter
                // String[] newteile = new String[Trennz +1];
                // System.arraycopy(teile, 0, newteile, 0, teile.length)
                teile.add(Antwort.substring(lastTrenn + 1, i));
                lastTrenn = i;
                Trennz += 1;
            }
        }

        if (Trennz > 0)
        {
            functionReturnValue = new String[teile.size() - 1];
            functionReturnValue = teile.toArray(functionReturnValue);
        }
        else
        {
            return null;
        }
        return functionReturnValue;
    }

    public int AntwortRichtig() throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement
        int res;
        libLearn.gStatus = "Vokabel.AntwortRichtig Start";
        if (mVok.get(mIndex).z < -1)
            mVok.get(mIndex).z = -1;
        mVok.get(mIndex).z = (short) (mVok.get(mIndex).z + 1);
        aend = true;
        lib.AntwWasRichtig = true;
        res = mVok.get(mIndex).z;
        if (mVok.get(mIndex).z > -1)
        {
            if (mLernVokabeln == null)
            {
                this.InitAbfrage();
            }
            else
            {
                ReorderLernVokabeln();
            }
        }
        AnzRichtig += 1;
        InitAbfrage();

        return res;

    }

    private void ReorderLernVokabeln()
    {
        mLernVokabeln[mLernindex] = 0;
		/*
		 * funkioniert nicht if (mLernindex < mSchrittweite) { for (int i =
		 * mLernindex;i < mSchrittweite;i++) { mLernVokabeln[i] =
		 * mLernVokabeln[i+1]; } mLernVokabeln[mSchrittweite] = 0; mLernindex -=
		 * 1; }
		 */
    }

    public void AntwortFalsch()
    {
        // ERROR: Not supported in C#: OnErrorStatement

        libLearn.gStatus = "Vokabel.AntwortFalsch Start";
        if (mVok.get(mIndex).z <= 0)
        {
            mVok.get(mIndex).z = (short) (mVok.get(mIndex).z - 1);
        }
        else
        {
            mVok.get(mIndex).z = 0;
        }
        aend = true;
        AnzFalsch += 1;
        lib.AntwWasRichtig = false;
        if (RestartInterval > -1 && (AnzFalsch % RestartInterval) == 0)
        {
            restart();
        }
        // InitAbfrage
    }

    public void InitAbfrage() throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement

        libLearn.gStatus = "Vokabel.InitAbfrage Start";
        // ERROR: Not supported in C#: OnErrorStatement

        short i;
        short voknr;
        boolean blnDurch = false;
        boolean blnDurch2 = false;

        // mLastIndex = 0
        voknr = (short) mLastIndex;

        libLearn.gStatus = "Init Abfrage";
        if (mGesamtzahl > 0)
        {
            libLearn.gStatus = "Vokabel.InitAbfrage Line 499";
            // Inserted by CodeCompleter
            if (mSchrittweite < 5)
                mSchrittweite = 5;
            if (mSchrittweite >= mGesamtzahl)
            {
                if (mGesamtzahl >= 5)
                {
                    mSchrittweite = (short) (mGesamtzahl - 1);
                }
                else
                {
                    mSchrittweite = (short) mGesamtzahl;
                }
            }

            mLernVokabeln = lib.ResizeArray(mLernVokabeln, mSchrittweite + 1);
            // ÃœberprÃ¼fen ob Cache mit Lernvokabeln gefÃ¼llt ist
            for (i = 1; i <= mSchrittweite; i++)
            {
                if (mLernVokabeln[i] == 0)
                {
                    // falls Lernvokabel gelÃƒÂ¶scht ist neue holen
                    if (!_AskAll)
                    {
                        if (mAbfragebereich == -1 || blnDurch2)
                        {
                            RefSupport<Object> refVar___0 = new RefSupport<Object>(
                                    voknr);
                            RefSupport<Object> refVar___1 = new RefSupport<Object>(
                                    i);
                            vokabelVonAllenHolen(refVar___0, refVar___1);
                            voknr = (Short) refVar___0.getValue();
                            i = (Short) refVar___1.getValue();
                        }
                        else
                        {
                            libLearn.gStatus = "Vokabel.InitAbfrage Line 509";
                            // Inserted by CodeCompleter
                            RefSupport<Object> refVar___2 = new RefSupport<Object>(
                                    voknr);
                            RefSupport<Object> refVar___3 = new RefSupport<Object>(
                                    i);
                            RefSupport<Object> refVar___4 = new RefSupport<Object>(
                                    blnDurch);
                            RefSupport<Object> refVar___5 = new RefSupport<Object>(
                                    blnDurch2);
                            Get_Vok(refVar___2, refVar___3, refVar___4,
                                    refVar___5);
                            voknr = (Short) refVar___2.getValue();
                            i = (Short) refVar___3.getValue();
                            blnDurch = (Boolean) refVar___4.getValue();
                            blnDurch2 = (Boolean) refVar___5.getValue();
                        }
                    }
                    else
                    {
                        if (voknr < mGesamtzahl)
                        {
                            voknr += 1;

                        }
                        else
                        {
                            voknr = 1;
                        }
                        if (mAbfrageZufaellig)
                        {
                            do
                            {
                                voknr = (short) lib.rndInt(0, mGesamtzahl);
                            }
                            while (!CheckIfNotContained(voknr));
                        }
                        mLernVokabeln[i] = voknr;
                    }
                }

            }
            // NÃ¤chste Vokabel im Puffer einstellen
            mLernindex += 1;
            // falls wir am Ende sind wieder an den Anfang gehen
            if (mLernindex > mSchrittweite)
                mLernindex = 1;
            // Vokabelnummer aus dem Puffer holen
            libLearn.gStatus = "Vokabel.InitAbfrage Line 519";
            // Inserted by CodeCompleter
            mIndex = mLernVokabeln[mLernindex];
            mOldBed[0] = lib.MakeMask(getBedeutung1());
            mOldBed[1] = lib.MakeMask(getBedeutung2());
            mOldBed[2] = lib.MakeMask(getBedeutung3());

        }
        else
        {
            Log.d("Initabfrage","Gesamtzahl=0");
            // If mLernindex < mGesamtzahl Then mLernindex += 1 Else mLernindex
            // = 1
        }
        if (mLernVokabeln != null && mLernVokabeln[mLernindex] > 0)
        {
            mblnLernInit = true;
            mLastIndex = voknr;
            libLearn.gStatus = "Vokabel.InitAbfrage Line 529";
            // Inserted by CodeCompleter
        }
        else
        {
            mblnLernInit = false;
        }
        libLearn.gStatus = "";
    }

    public ArrayList<typVok> Select(String Wort, String Bedeutung)
    {
        int Zaehler = -100;
        return Select(Wort, Bedeutung, Zaehler);
    }

    public ArrayList<typVok> Select(String Wort, String Bedeutung, int Zaehler)
    {
        ArrayList<typVok> Sel = new ArrayList<>();
        for (typVok vok : mVok)
        {
            if (!libString.IsNullOrEmpty(Wort))
            {
                if (vok.Wort.contains(Wort))
                {
                    Sel.add(vok);
                }
            }
            else if (!libString.IsNullOrEmpty(Bedeutung))
            {
                if (vok.Bed1.contains(Bedeutung)
                        || vok.Bed2.contains(Bedeutung)
                        || vok.Bed3.contains(Bedeutung))
                {
                    Sel.add(vok);
                }
            }
            else if (Zaehler != -100)
            {
                if (Zaehler > -6)
                {
                    if (Zaehler < 6)
                    {
                        if (vok.z == Zaehler)
                        {
                            Sel.add(vok);
                        }
                    }
                    else if (vok.z >= 6)
                    {
                        Sel.add(vok);
                    }
                }
                else if (vok.z <= -6)
                {
                    Sel.add(vok);
                }
            }

        }
        return Sel;
    }

    public int getLearned(int Zaehler)
    {
        int res = 0;
        boolean first = true;
        for (typVok vok : mVok)
        {
            if (first)
            {
                first = false;
                continue;
            }
            if (Zaehler > -6)
            {
                if (Zaehler < 6)
                {
                    if (vok.z == Zaehler)
                    {
                        res += 1;
                    }
                }
                else if (vok.z >= 6)
                {
                    res += 1;
                }
            }
            else if (vok.z <= -6)
            {
                res += 1;
            }
        }
        return res;
    }

    public void Get_Vok(RefSupport<Object> refvokNr, RefSupport<Object> refi,
                        RefSupport<Object> refblnDurch, RefSupport<Object> refblnDurch2)
            throws Exception
    {
        // Get_Vok:
        refblnDurch.setValue(false);
        short vokNr = (Short) refvokNr.getValue();
        short i = (Short) refi.getValue();
        do
        {
            if (vokNr < mVok.size() - 1)
            {
                vokNr += 1;
            }
            else
            {
                vokNr = 1;
                if ((Boolean) refblnDurch.getValue())
                {
                    refblnDurch2.setValue(true);
                    // UPGRADE_ISSUE: Die Anweisung GoSub wird nicht
                    // unterstÃ¼tzt. Klicken Sie hier fÃ¼r weitere Informationen:
                    // 'ms-help://MS.VSExpressCC.v80/dv_commoner/local/redirect.htm?keyword="C5A1A479-AB8B-4D40-AAF4-DB19A2E5E77F"'
                    RefSupport<Object> refVar___0 = new RefSupport<Object>(
                            vokNr);
                    RefSupport<Object> refVar___1 = new RefSupport<Object>(i);
                    vokabelVonAllenHolen(refVar___0, refVar___1);
                    vokNr = (Short) (refVar___0.getValue());
                    i = (Short) (refVar___1.getValue());
                    break;
                }


                refblnDurch.setValue(true);
            }
            if (CheckIfNotContained(vokNr)
                    && ((mVok.get(vokNr).z == mAbfragebereich)
                    || (mAbfragebereich >= 6 && mVok.get(vokNr).z >= 6) || (mAbfragebereich == 0 && mVok
                    .get(vokNr).z <= 0)))
            {
                mLernVokabeln[i] = vokNr;
                break;
            }

        }
        while (true);
		
		/*
		 * RefSupport<Object> refVar___2 = new RefSupport<Object>(vokNr);
		 * RefSupport<Object> refVar___3 = new RefSupport<Object>(i);
		 * vokabelVonAllenHolen(refVar___2,refVar___3);
		 */
        refvokNr.setValue(vokNr);
        refi.setValue(i);

    }

    public void vokabelVonAllenHolen(RefSupport<Object> refvokNr,
                                     RefSupport<Object> refi) throws Exception
    {
        short intVokNr = (Short) refvokNr.getValue();
        short i = (Short) refi.getValue();
        Random rnd = new Random();
        do
        {

            if (mAbfrageZufaellig)
            {
                do
                {
                    intVokNr = (short) lib.rndInt(0, mGesamtzahl);
                }
                while (!CheckIfNotContained(intVokNr));
            }
            else
            {
                do
                {
                    if (intVokNr < mVok.size() - 1)
                    {
                        intVokNr += 1;
                    }
                    else
                    {
                        intVokNr = 1;
                    }
                }
                while (!CheckIfNotContained(intVokNr));

            }

            if (mVok.get(intVokNr).z <= 1)
            {
                if (CheckIfNotContained(intVokNr))
                {
                    mLernVokabeln[i] = intVokNr;
                    break;
                }
            }
            else
            {

                double r = rnd.nextDouble();
                double z = (double) mVok.get(intVokNr).z;
                double p;
                if (ProbabilityFactor <= 0)
                {
                    p = 1 / (z * (mGesamtzahl > 50 ? (float) mGesamtzahl
                            / (float) 50 : 1));
                }
                else
                {
                    p = 1 / (z * ProbabilityFactor);
                }
                if (r < p && CheckIfNotContained(intVokNr))
                {
                    mLernVokabeln[i] = intVokNr;
                    break;
                }

            }
        }
        while (true);
        refvokNr.setValue(intVokNr);
        refi.setValue(i);
    }

    private boolean CheckIfNotContained(int vokNr)
    {
        if (mGesamtzahl > mSchrittweite * 2)
        {
            for (int i = 1; i <= mSchrittweite; i++)
            {
                if (mLernVokabeln[i] == vokNr)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public void DeleteVokabel()
    {
        DeleteVokabel(-1);
    }

    public void DeleteVokabel(int index)
    {
        // ERROR: Not supported in C#: OnErrorStatement

        libLearn.gStatus = "Vokabel.DeleteVokabel Start";
        //
        //
        //
        if (index == -1)
            index = mIndex;
        if (mVok.size() <= index || index < 1) return;
        mVok.remove(index);
        aend = true;
        mGesamtzahl = mVok.size();
        if (mIndex >= mGesamtzahl) mIndex = mGesamtzahl - 1;
        mblnLernInit = false;
        // mVok = lib.ResizeArray(mVok, mGesamtzahl +1 );
    }


    public void AddVokabel()
    {
        libLearn.gStatus = "Vokabel.AddVokabel Start";
        //
        mVok.add(new typVok("", "", "", "", "", (short) 0));
        mGesamtzahl = mVok.size();
        mIndex = mGesamtzahl - 1;
        // mVok = lib.ResizeArray(mVok, mGesamtzahl +1 );
    }

    public void SaveFile() throws Exception
    {
        SaveFile(mFileName, _uri, true, false);
    }

    public void SaveCurrentFileAsync() throws Exception
    {
        SaveFileAsync(mFileName, _uri, _UniCode);
    }

    public void SaveFileAsync(String strFileName, Uri uri, boolean blnUniCode) throws Exception
    {
        TaskSaveVok T = new TaskSaveVok(strFileName, uri, blnUniCode);
        T.execute();
        try
        {
            Looper.loop();
        }
        catch (EndLooperException ex)
        {
            Log.d("SaveAsync","Finished");
        }
        T.Latch.await();
        if (T.ex != null) throw T.ex;
    }

    class EndLooperException extends RuntimeException
    {

    }

    class TaskSaveVok extends AsyncTask<Void, Void, Exception>
    {
        ProgressDialog p;
        Exception ex;
        CountDownLatch Latch = new CountDownLatch(1);
        String strFileName;
        Uri uri;
        boolean blnUnicode;

        TaskSaveVok(String strFileName, Uri uri, boolean blnUnUniCade)
        {
            super();
            this.strFileName = strFileName;
            this.uri = uri;
            this.blnUnicode = blnUnUniCade;
        }

        @Override
        protected Exception doInBackground(Void... params)
        {
            try
            {
                SaveFile(strFileName, uri, blnUnicode, true);
            }

            catch (Exception e2)
            {
                this.ex = e2;
            }

            Latch.countDown();
            return ex;
        }

        @Override
        protected void onPostExecute(Exception ex)
        {
            if (p.isShowing()) p.dismiss();
            throw new EndLooperException();
        }


        @Override
        protected void onPreExecute()
        {
            if (!blnUnicode)
            {
                yesnoundefined res;
                res = lib.ShowMessageYesNo(getContext(), getContext()
                        .getString(R.string.SaveAsUniCode), "");

                if (res == lib.yesnoundefined.yes)
                {
                    blnUnicode = true;
                }
            }
            p = new ProgressDialog(getContext());
            p.setMessage(getContext().getString(R.string.saving));
            p.show();

        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
        }


    }


    public void SaveFile(String strFileName, Uri uri, boolean blnUniCode,
                         boolean dontPrompt) throws Exception
    {
        if ((libString.IsNullOrEmpty(strFileName) && uri == null) || mVok.size() < 2)
        {
            if (mVok.size() < 2) aend = false;
            return;
        }

        ParcelFileDescriptor pfd = null;
        java.io.OutputStreamWriter sWriter = null;
        FileOutputStream os = null;
        libLearn.gStatus = "Vokabel.SaveFile Start";
        //
        String LWort;
        short h;
        short spr = 0;
        short einst = 0;
        short tasta = 0;
        short varbed = 0;
        String fontfil;
        String tastbel;
        Exception finallyEx = null;
        fontfil = "";
        tastbel = "";

        try
        {
            Charset enc = Charset.forName("UTF-8");
            Charset CharsetWindows = null;
            try
            {
                CharsetWindows = Charset.forName(CharsetASCII);
            }
            catch (Exception ex)
            {
                this.setStatus(ex.getMessage());
            }
            if (CharsetWindows == null)
                CharsetWindows = Charset.defaultCharset();
			/*
			 * if (fname.exists()) { java.io.InputStream in = new
			 * java.io.FileInputStream(fname); java.io.InputStreamReader r = new
			 * java.io.InputStreamReader(in,enc); enc =
			 * Charset.forName(r.getEncoding()); r.close(); in.close(); }
			 */
            if (blnUniCode)
            {
                enc = Charset.forName("UTF-8");
            }
            else
            {
                yesnoundefined res = yesnoundefined.undefined;
                if (!dontPrompt)
                {
                    res = lib.ShowMessageYesNo(getContext(), getContext()
                            .getString(R.string.SaveAsUniCode), "");
                    if (res == yesnoundefined.undefined) return;
                }

                if (dontPrompt
                        || res == lib.yesnoundefined.no)
                {
                    enc = CharsetWindows;
                }
                else
                {
                    blnUniCode = true;
                    enc = Charset.forName("UTF-8");
                }
            }
            File fname;
            if (!libString.IsNullOrEmpty(strFileName))
            {
                fname = new File(strFileName);
                os = new java.io.FileOutputStream(fname);
                this.mVokPath = fname.getParent();
            }
            else if (uri != null)
            {
                lib.CheckPermissions(Container, uri, false);
                try
                {
                    pfd = Container.getContentResolver().
                            openFileDescriptor(uri, "w");
                    if (pfd != null)
                    {
                        os = new FileOutputStream(pfd.getFileDescriptor());
                    }
                    else
                    {
                        File F = new File(uri.getPath());
                        os = new java.io.FileOutputStream(F);
                        this.mVokPath = F.getParent();
                    }
                }
                catch (Exception ex)
                {
                    File F = new File(uri.getPath());
                    os = new java.io.FileOutputStream(F);
                    this.mVokPath = F.getParent();
                }
            }
            else
            {
                throw new RuntimeException("Filename is missing!");
            }
            sWriter = new java.io.OutputStreamWriter(os, enc);

            // System.Windows.Forms.Cursor.Current =
            // System.Windows.Forms.Cursors.WaitCursor;
            spr = (short) (spr | -(tasta) * 32);
            spr = (short) (spr | -(varbed) * 64);
            spr = (short) (spr | (varHebr ? 16 : 0));
            spr = (short) (spr | this.mSprache.ordinal());

            if ((fontfil).length() < 15)
            {
                fontfil = (this.mSprache.ordinal() + 1) + ","
                        + (this.mSprache.name());
                fontfil += "," + (getFontWort().getSize());
                fontfil += "," + getFontWort().getName();
                fontfil += "," + (getFontBed().getSize());
                fontfil += "," + getFontBed().getName();
                fontfil += "," + (getFontKom().getSize());
                fontfil += "," + getFontKom().getName();

            }
            spr = (short) (spr | 256);
            if (reverse) spr = (short) (spr | 512);
            if (!libString.IsNullOrEmpty(tastbel)
                    | !libString.IsNullOrEmpty(fontfil))
            {
                sWriter.write((spr | 128 | einst) + "\n");
                sWriter.write(tastbel + "\n");
                sWriter.write(fontfil + "\n");
            }
            else
            {
                sWriter.write((spr | einst) + "\n");
            }

            sWriter.write(lib.toLanguageTag(mLangWord) + "," + lib.toLanguageTag(mLangMeaning) + "\n");

            for (h = 1; h <= mVok.size() - 1; h++)
            {
                if (!libString.IsNullOrEmpty(mVok.get(h).Wort))
                {
                    LWort = mVok.get(h).Wort;
                    if (!libString.IsNullOrEmpty(mVok.get(h).Kom))
                        LWort += (char) 8 + mVok.get(h).Kom;
                    if (!libString.IsNullOrEmpty(LWort))
                        LWort = LWort.replace("\r", "{CR}").replace("\n",
                                "{LF}");
                    sWriter.write(LWort + "\n");
                    LWort = mVok.get(h).Bed1;
                    if (!libString.IsNullOrEmpty(LWort))
                        LWort = LWort.replace("\r", "{CR}").replace("\n",
                                "{LF}");
                    sWriter.write(LWort + "\n");
                    LWort = mVok.get(h).Bed2;
                    if (!libString.IsNullOrEmpty(LWort))
                        LWort = LWort.replace("\r", "{CR}").replace("\n",
                                "{LF}");
                    sWriter.write(LWort + "\n");
                    LWort = mVok.get(h).Bed3;
                    if (!libString.IsNullOrEmpty(LWort))
                        LWort = LWort.replace("\r", "{CR}").replace("\n",
                                "{LF}");
                    sWriter.write(LWort + "\n");
                    sWriter.write((mVok.get(h).z) + "\n");
                }

            }
        }
        catch (Exception ex)
        {
            throw new Exception("SaveVokError", ex);
        }
        finally
        {
            libLearn.gStatus = "savevok close sWriter";
            try
            {
                if (sWriter != null)
                {
                    sWriter.flush();
                    sWriter.close();
                    sWriter = null;
                }
            }
            catch (Exception ex)
            {
                finallyEx = ex;
            }
            try
            {
                libLearn.gStatus = "savevok close os";
                if (os != null)
                {
                    os.flush();
                    os.close();
                    os = null;
                }
                libLearn.gStatus = "savevok close pfd";
                if (pfd != null)
                {
                    pfd.close();
                    pfd = null;
                }
            }
            catch (Exception ex)
            {
                finallyEx = ex;
            }

        }

        mFileName = strFileName;
        _uri = uri;
        spr = (short) (spr & 7);
        _UniCode = blnUniCode;
        // System.Windows.Forms.Cursor.Current =
        // System.Windows.Forms.Cursors.Default;
        if (finallyEx != null) throw finallyEx;
        aend = false;
    }

    public void revert()
    {

        for (int h = 0; h <= mVok.size() - 1; h++)
        {
            String vok = mVok.get(h).Wort;
            mVok.get(h).Wort = mVok.get(h).Bed1;
            if (!libString.IsNullOrEmpty(mVok.get(h).Bed2))
            {
                mVok.get(h).Wort += "/" + mVok.get(h).Bed2;
                mVok.get(h).Bed2 = "";
                if (!libString.IsNullOrEmpty(mVok.get(h).Bed3))
                {
                    mVok.get(h).Wort += "/" + mVok.get(h).Bed3;
                    mVok.get(h).Bed3 = "";
                }
            }
            mVok.get(h).Bed1 = vok;
            mVok.get(h).z = 0;
        }
        Locale lang = this.getLangWord();
        this.setLangWord(this.getLangMeaning());
        this.setLangMeaning(lang);

        if (!libString.IsNullOrEmpty(mFileName))
        {
            File F = new File(mFileName);
            mFileName = lib.getFilenameWithoutExtension(F) + "rev.vok";
            F = new File(mFileName);
            if (F.exists())
            {
                for (int i = 0; i < 1000; i++)
                {
                    mFileName = lib.getFilenameWithoutExtension(F) + "rev" + i
                            + ".vok";
                    F = new File(mFileName);
                    if (!F.exists())
                        break;
                }
            }
            else
            {
                setURI(null);
            }

        }

    }

    public void reset()
    {
        for (int h = 0; h <= mVok.size() - 1; h++)
        {
            mVok.get(h).z = 0;
        }
        aend = true;
    }

    public void restart()
    {
        mLastIndex = 0;
    }

    int static_GetNextLineFromString_startLine;

    // Private Function GetNextLineFromString(ByRef strContent As String,
    // Optional ByRef strRef As String = "nihxyz", Optional ByRef FirstLine As
    // Single = 0) As Boolean
    private boolean GetNextLineFromString(String strContent) throws Exception
    {
        int FirstLine = 0;
        RefSupport<String> strRef = new RefSupport<String>("nihxyz");
        return GetNextLineFromString(strContent, strRef, FirstLine);
    }

    private boolean GetNextLineFromString(String strContent,
                                          RefSupport<String> strRef) throws Exception
    {
        int FirstLine = 0;
        return GetNextLineFromString(strContent, strRef, FirstLine);
    }

    private boolean GetNextLineFromString(String strContent,
                                          RefSupport<String> strRef, int FirstLine) throws Exception
    {
        boolean functionReturnValue = false;
        // ERROR: Not supported in C#: OnErrorStatement
        libLearn.gStatus = "Vokabel.GetNextLineFromString Start";
        int crFound = 0;
        if (libString.IsNullOrEmpty(strContent))
        {
            throw new RuntimeException("GetNextLineFromString\n"
                    + "String ist empty!");
        }

        if ((strRef.getValue().equals("nihxyz")))
        {
            functionReturnValue = !(static_GetNextLineFromString_startLine > (strContent
                    .length()));
            return functionReturnValue;
        }

        // Inserted by CodeCompleter
        if (FirstLine != 0)
            static_GetNextLineFromString_startLine = 1;

        crFound = strContent.indexOf("\r",
                static_GetNextLineFromString_startLine - 1) + 1;
        if (crFound == 0)
            crFound = strContent.length();

        strRef.setValue(strContent.substring(
                static_GetNextLineFromString_startLine, crFound - 1));
        static_GetNextLineFromString_startLine = crFound + 2;
        return functionReturnValue;
    }

    public void LoadFromString(String strContent) throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement

        libLearn.gStatus = "Vokabel.LoadFromString Start";

        short qf = 0;
        short hh = 0;
        short h = 0;
        short sp = 0;
        short n = 0;
        short lad = 0;
        short indexlang = 0;
        String fontfil = null;
        String tastbel = null;
        String strTmp = null;
        mLernVokabeln = new int[mSchrittweite + 1];
        mLastIndex = 0;
        // ERROR: Not supported in C#: OnErrorStatement

        mFileName = "";
        fontfil = "";
        tastbel = "";
        strTmp = "";

        libLearn.gStatus = "Vokabel.LoadFromString Line 669";
        // Inserted by CodeCompleter

        RefSupport<String> refStrTmp = new RefSupport<String>(strTmp);
        GetNextLineFromString(strContent, refStrTmp, -1);
        strTmp = refStrTmp.getValue();
        // SPRACHE LADEN
        sp = (short) Integer.parseInt(strTmp.trim());
        indexlang = (short) (sp & 7);
        libLearn.gStatus = "Vokabel.LoadFromString Line 679";
        // Inserted by CodeCompleter
        // lib.setEnumOrdinal(mSprache, indexlang);
        for (EnumSprachen Sprache : EnumSprachen.values())
        {
            if (Sprache.ordinal() == indexlang)
            {
                mSprache = Sprache;
                break;
            }
        }
        if ((sp & 128) != 0)
        {
            refStrTmp.setValue(tastbel);
            GetNextLineFromString(strContent, refStrTmp);
            tastbel = refStrTmp.getValue();
            refStrTmp.setValue(fontfil);
            GetNextLineFromString(strContent, refStrTmp);
            fontfil = refStrTmp.getValue();
            RefSupport<String> refVar___0 = new RefSupport<String>(fontfil);
            RefSupport<Object> refVar___1 = new RefSupport<Object>(hh);
            RefSupport<Object> refVar___2 = new RefSupport<Object>(h);
            RefSupport<Object> refVar___3 = new RefSupport<Object>(indexlang);
            RefSupport<Object> refVar___4 = new RefSupport<Object>(qf);
            RefSupport<Object> refVar___5 = new RefSupport<Object>(lad);
            Getfonts(refVar___0, refVar___1, refVar___2, refVar___3,
                    refVar___4, refVar___5);
            fontfil = (String) refVar___0.getValue();
            hh = (Short) refVar___1.getValue();
            h = (Short) refVar___2.getValue();
            indexlang = (Short) refVar___3.getValue();
            qf = (Short) refVar___4.getValue();
            lad = (Short) refVar___5.getValue();
            // Windows Fonts extrahieren
        }
        else
        {
            lad = 0;
        }
        while ((GetNextLineFromString(strContent)))
        {
            libLearn.gStatus = "Vokabel.LoadFromString Line 689";
            // Inserted by CodeCompleter
            // System.Windows.Forms.Application.DoEvents();
            mVok.add(new typVok());
            n = (short) (mVok.size() - 1);
            // lib.ResizeArray(mVok, n + 1);
            RefSupport<String> refWort = new RefSupport<String>(
                    mVok.get(n).Wort);
            GetNextLineFromString(strContent, refWort);
            mVok.get(n).Wort = refWort.getValue();
            qf = (short) (mVok.get(n).Wort.indexOf(0) + 1);
            if (qf == 0)
                qf = (short) (mVok.get(n).Wort.indexOf(8) + 1);
            if (qf != 0)
            {
                mVok.get(n).Kom = mVok.get(n).Wort.substring(qf);
                libLearn.gStatus = "Vokabel.LoadFromString Line 699";
                // Inserted by CodeCompleter
                mVok.get(n).Wort = mVok.get(n).Wort.substring(qf - 1);
            }
            refStrTmp.setValue(mVok.get(n).Bed1);
            GetNextLineFromString(strContent, refStrTmp);
            mVok.get(n).Bed1 = refStrTmp.getValue();

            refStrTmp.setValue(mVok.get(n).Bed2);
            GetNextLineFromString(strContent, refStrTmp);
            mVok.get(n).Bed2 = refStrTmp.getValue();

            refStrTmp.setValue(mVok.get(n).Bed3);
            GetNextLineFromString(strContent, refStrTmp);
            mVok.get(n).Bed3 = refStrTmp.getValue();

            refStrTmp.setValue(strTmp);
            GetNextLineFromString(strContent, refStrTmp);
            strTmp = refStrTmp.getValue();

            mVok.get(n).z = (short) Integer.parseInt(strTmp.trim());
            if (libString.IsNullOrEmpty(mVok.get(n).Wort))
            {
                libLearn.gStatus = "Vokabel.LoadFromString Line 709";
                // Inserted by CodeCompleter
                mVok.remove(n);
                n = (short) (mVok.size() - 1);
                // lib.ResizeArray(mVok, n + 1);
            }

        }
        mGesamtzahl = n;
        mIndex = 1;

        // ******** Hier gehts hin wenn ein Fehler auftrit oder wenn _
        // ' ******** SchluÃŸ ist.....

        // Defmouse 0
        sp = (short) (sp & 7);
        if (sp >= 0 & sp <= 3)
        {
            indexlang = sp;
        }
        switch (indexlang)
        {
            // Case 0: mSprache = "Deutsch"
            // Case 1: mSprache = "HebrÃ¤isch"
            // Case 2: mSprache = "Griechisch"
            // Case Is > 2: Sprache = "Sonstige"
        }
        // If Sprache <> "" Then mSprache = Sprache
        if (mGesamtzahl > 5)
        {
            InitAbfrage();
            mFileName = "String";
        }
        else
        {
            mblnLernInit = false;
        }
        aend = false;
        return;
    }

    public void Getfonts(RefSupport<String> fontfil, RefSupport<Object> refhh,
                         RefSupport<Object> refh, RefSupport<Object> refindexLang,
                         RefSupport<Object> refqf, RefSupport<Object> reflad)
            throws Exception
    {
        // getfonts:,// ********** Hier werden die Fonts 'extrahiert'
        short h = (Short) refh.getValue();
        short hh = (Short) refhh.getValue();
        short indexLang = (Short) refindexLang.getValue();
        short qf = (Short) refqf.getValue();
        hh = 1;
        fontfil.setValue(fontfil.getValue().trim());
        if ((fontfil.getValue().indexOf(",")) > -1)
        {
            fontfil.setValue(fontfil.getValue() + ",");
            h = (short) ((fontfil.getValue().indexOf(",", hh - 1)) + 1);
            if (h != 0 && h - hh > 0)
                indexLang = (short) (Integer.parseInt(fontfil.getValue()
                        .substring(hh - 1, h - 1).trim()));

            hh = (short) (h + 1);
            h = (short) ((fontfil.getValue().indexOf(",", hh - 1)) + 1);
            try
            {
                if (h != 0 && h - hh > 0)
                    mSprache = EnumSprachen.valueOf((fontfil.getValue()
                            .substring(hh - 1, h - 1)));
            }
            catch (Exception __dummyCatchVar0)
            {
                if (fontfil.getValue().substring(hh - 1, h - 1)
                        .equals("Hebräisch"))
                {
                    mSprache = EnumSprachen.Hebrew;
                }
                else
                {
                    mSprache = EnumSprachen.Normal;
                }
            }

            hh = (short) (h + 1);
            for (qf = 1; qf <= 3; qf++)
            {
                h = (short) ((fontfil.getValue().indexOf(",", hh - 1)) + 1);
                if (h != 0 && h - hh > 0)
                {
                    switch (qf)
                    {
                        case 1:
                            mWortFont.setSize(Integer.parseInt(fontfil.getValue()
                                    .substring(hh - 1, h - 1).trim()));
                            break;
                        case 2:
                            mBedFont.setSize(Integer.parseInt(fontfil.getValue()
                                    .substring(hh - 1, h - 1).trim()));
                            break;
                        case 3:
                            mKomFont.setSize(Integer.parseInt(fontfil.getValue()
                                    .substring(hh - 1, h - 1).trim()));
                            break;

                    }
                }

                hh = (short) (h + 1);
                h = (short) ((fontfil.getValue().indexOf(",", hh - 1)) + 1);
                if (h != 0 & h - hh > 0)
                {
                    switch (qf)
                    {
                        case 1:
                            mWortFont.setName(fontfil.getValue()
                                    .substring(hh - 1, h - 1).trim());
                            break;
                        case 2:
                            mBedFont.setName(fontfil.getValue()
                                    .substring(hh - 1, h - 1).trim());
                            break;
                        case 3:
                            mKomFont.setName(fontfil.getValue()
                                    .substring(hh - 1, h - 1).trim());
                            break;

                    }
                }

                hh = (short) (h + 1);
            }
            reflad.setValue((short) -1);
        }
        refh.setValue(h);
        refhh.setValue(hh);
        refindexLang.setValue(indexLang);
        refqf.setValue(qf);
    }

    public void NewFile()
    {
        mLernVokabeln = new int[mSchrittweite + 1];
        mVok = new ArrVok();
        mVok.add(new typVok());
        mLastIndex = 0;
        mLernindex = 0;
        mGesamtzahl = 0;
        mIndex = 0;
        mFileName = "";
        _URIName = "";
        _uri = null;
        mSprache = EnumSprachen.Normal;
        AnzRichtig = 0;
        AnzFalsch = 0;
        mLangMeaning = Locale.getDefault();
        mLangWord = Locale.getDefault();
    }

    public void LoadFile(String strFileName) throws Exception
    {
        LoadFile(Container, strFileName, null, false, false, true);
    }

    public void LoadFile(Context context, String strFileName, Uri uri, boolean blnSingleLine,
                         boolean blnAppend, boolean blnUnicode) throws Exception
    {
        LoadFile(context, strFileName, uri, blnSingleLine, blnAppend, blnUnicode, false);
    }

    public void LoadFile(Context context, String strFileName, Uri uri, boolean blnSingleLine,
                         boolean blnAppend, boolean blnUnicode, boolean blnDontPrompt) throws Exception
    {
        try
        {
            final String CodeLoc = "Vokabel.LoadFile";
            libLearn.gStatus = CodeLoc + " Start";

            short sp = 0;
            short h = 0;
            short hh = 0;
            short qf = 0;
            short n = 0;
            short lad = 0;
            short indexlang = 0;
            boolean canBeSingleLine = false;
            String fontfil = null;
            String tastbel = null;
            String strTmp = null;
            java.io.InputStreamReader isr = null;
            InputStream is = null;
            WindowsBufferedReader sr = null;
            String tmp = null;
            fontfil = "";
            strTmp = "";
            _URIName = "";
            mLernVokabeln = new int[mSchrittweite + 1];
            mLastIndex = 0;
            // ERROR: Not supported in C#: OnErrorStatement

            libLearn.gStatus = "Load File: " + strFileName;

            //mFileName = "";

            libLearn.gStatus = CodeLoc + " Open Stream";
            // Inserted by CodeCompleter
            java.io.File F = null;

            //_uri = uri;

            Charset CharsetWindows = null;
            try
            {
                CharsetWindows = Charset.forName(CharsetASCII);
            }
            catch (Exception ex)
            {
                if (!blnDontPrompt) this.setStatus(ex.getMessage());
            }
            if (CharsetWindows == null)
                CharsetWindows = Charset.defaultCharset();
            Charset CharSetUnicode = null;

            boolean blnWrongNumberFormat = false;
            if (!blnDontPrompt && libString.IsNullOrEmpty(strFileName) && uri != null)
            {
                lib.CheckPermissions(Container, uri, true);
            }
            do
            {
                CharSetUnicode = (sp >= -1 ? Charset
                        .forName("UTF-8") : Charset.forName("UTF-16"));
                boolean finished = false;
                for (int i = 0; i <= 2; i++)
                {

                    if (libString.IsNullOrEmpty(strFileName) && uri != null)
                    {
                        //lib.CheckPermissions(Container, uri,true);
                        is = context.getContentResolver().openInputStream(uri);
                        aend = true;
                    }
                    else
                    {
                        F = new java.io.File(strFileName);
                        uri = null;
                        is = new java.io.FileInputStream(F);
                    }
                    if (is != null)
                    {
                        isr = new java.io.InputStreamReader(is,
                                (blnUnicode ? CharSetUnicode : CharsetWindows));
                        sr = new WindowsBufferedReader(isr);
                        if (!(finished || i == 2 || blnWrongNumberFormat))
                        {
                            int ii = 0;
                            do
                            {
                                String s = sr.readLine();
                                ii++;
                                if (s == null || ii >= 500)
                                {
                                    finished = true;
                                    break;
                                }
                                if (s.contains("�"))
                                {
                                    finished = false;
                                    blnUnicode = !blnUnicode;
                                    break;
                                }
                            }
                            while (true);
                            sr.close();
                            isr.close();
                            is.close();
                            continue;
                        }
                        else
                        {
                            break;
                        }

                    }
                    else
                    {
                        if (blnDontPrompt) throw new FileNotFoundException
                                (getContext().getString(R.string.FileDoesNotExist));
                        lib.ShowMessage(getContext(),
                                getContext().getString(R.string.FileDoesNotExist), "");
                        // Call Err.Raise(vbObjectError + ErrWrongfilename, CodeLoc
                        // & "", "Dateiname_ungÃ¼ltig", "", "")
                        return;
                    }
                }

                if (F != null)
                {
                    if (lib.getExtension(F).toLowerCase(Locale.getDefault())
                            .indexOf(".k") != -1)
                        _cardmode = true;
                    else
                        _cardmode = false;
                }
                else if (uri != null)
                {
                    String path = lib.dumpUriMetaData(Container, uri);
                    if (path.contains(":")) path = path.split(":")[0];
                    if (path.toLowerCase().lastIndexOf(".k") > path.length() - 5)
                    {
                        _cardmode = true;
                    }
                    else
                    {
                        _cardmode = false;
                    }
                }
                libLearn.gStatus = CodeLoc + " ReadLine1";
                tmp = sr.readLine();
                if (tmp == null)
                {
                    sp = -2;
                    break;
                }
                try
                {
                    sp = (short) Integer.parseInt(tmp.trim()); // .replaceAll("[^\\d]",
                    // "")'
                }
                catch (NumberFormatException ex)
                {
                    // lib.ShowException(getContext(), ex);
                    if (!blnDontPrompt) this.setStatus(ex.getMessage());
                    sp -= 1;
                    blnWrongNumberFormat = true;
                    blnUnicode = (sp > -2 ? !blnUnicode : true);
                    if (sr != null)
                    {
                        sr.close();
                        sr = null;
                    }

                    if (isr != null)
                    {
                        isr.close();
                        isr = null;
                    }

                    if (is != null)
                    {
                        is.close();
                        is = null;
                    }
                }
            }
            while (sp < 0 && sp >= -2);
            if (sp < -1)
            {
                if (blnDontPrompt) throw new RuntimeException(getContext()
                        .getString(R.string.FileFormatNotRecognized));
                lib.ShowMessage(getContext(),
                        getContext()
                                .getString(R.string.FileFormatNotRecognized), "");
            }
            else
            {
                varHebr = (sp & 16) != 0;
                libLearn.gStatus = CodeLoc + " Line 819";
                // Inserted by CodeCompleter
                indexlang = (short) (sp & 7);
                if (!blnAppend)
                {
                    for (EnumSprachen Sprache : EnumSprachen.values())
                    {
                        if (Sprache.ordinal() == indexlang)
                        {
                            mSprache = Sprache;
                            break;
                        }
                    }
                }
                if ((sp & 128) != 0)
                {
                    String x;
                    tastbel = sr.readLine();
                    x = sr.readLine();
                    // while ((x = sr.readLine()).length()==0 && x!=null);
                    fontfil = x.replaceAll("\"$|^\"", "");
                    ;
                    if (!blnAppend)
                    {
                        RefSupport<String> refVar___0 = new RefSupport<String>(
                                fontfil);
                        RefSupport<Object> refVar___1 = new RefSupport<Object>(hh);
                        RefSupport<Object> refVar___2 = new RefSupport<Object>(h);
                        RefSupport<Object> refVar___3 = new RefSupport<Object>(
                                indexlang);
                        RefSupport<Object> refVar___4 = new RefSupport<Object>(qf);
                        RefSupport<Object> refVar___5 = new RefSupport<Object>(lad);
                        Getfonts(refVar___0, refVar___1, refVar___2, refVar___3,
                                refVar___4, refVar___5);
                        fontfil = (String) refVar___0.getValue();
                        hh = (Short) refVar___1.getValue();
                        h = (Short) refVar___2.getValue();
                        indexlang = (Short) refVar___3.getValue();
                        qf = (Short) refVar___4.getValue();
                        lad = (Short) refVar___5.getValue();
                    }
                    // Windows Fonts extrahieren
                }
                else
                {
                    lad = 0;
                }
                if ((sp & 256) != 0 && sp < 1024)
                {
                    String x = sr.readLine();
                    String[] Sprachen = x.split(",");
                    mLangWord = lib.forLanguageTag(Sprachen[0]);
                    mLangMeaning = lib.forLanguageTag(Sprachen[1]);
                }
                if (sp < 1024) reverse = (sp & 512) != 0;
                if (Container != null) ((MainActivity) Container).setMnuReverse();
                libLearn.gStatus = CodeLoc + " Line 829";
                // Inserted by CodeCompleter
                if (blnAppend)
                {
                    n = (short) mGesamtzahl;
                }
                else
                {
                    mVok.clear();
                    mFileName = "";
                    _uri = null;
                    mVok.add(new typVok("empty", "empty", "empty", "empty",
                            "empty", (short) 0));
                }
                String[][] csvall = new String[5][5];
                int csvFound = 0;
                boolean csvRegognized = false;
                typVok CurVok = null;
                for (String x = sr.readLine(); x != null; x = sr.readLine())
                {
                    csvFound = 0;
                    int Len = x.length();
                    if (Len == 0)
                        continue;
                    String[] csv = x.split(",");
                    if (csv.length == 5)
                    {
                        csvFound++;
                        csvall[0] = csv;
                    }
                    CurVok = new typVok();
                    mVok.add(CurVok);
                    n = (short) (mVok.size() - 1);
                    // mVok = lib.ResizeArray(mVok, n + 1);
                    libLearn.gStatus = CodeLoc + " ReadLine2";
                    SetWord(CurVok, x);
                    libLearn.gStatus = CodeLoc + " ReadLine3";
                    if (!((x = sr.readLine()) == null))
                    {
                        CurVok.Bed1 = x.replace("{CR}", "\r").replace("{LF}", "\n");
                        csv = x.split(",");
                        if (csv.length == 5)
                        {
                            csvFound++;
                            csvall[1] = csv;
                        }
                    }
                    else
                    {
                        break;
                    }

                    if (!blnSingleLine)
                    {
                        if (!((x = sr.readLine()) == null))
                        {
                            libLearn.gStatus = CodeLoc + " ReadLine4";
                            CurVok.Bed2 = x.replace("{CR}", "\r").replace("{LF}",
                                    "\n");
                            csv = x.split(",");
                            if (csv.length == 5)
                            {
                                csvFound++;
                                csvall[2] = csv;
                            }
                            strTmp = x;
                            short tmpZ = -100;
                            try
                            {
                                tmpZ = (short) Integer.parseInt(strTmp.trim());
                            }
                            catch (Exception ex)
                            {
                                tmpZ = -100;
                            }
                            if (tmpZ > -100)
                                canBeSingleLine = true;

                        }
                        else
                        {
                            break;
                        }
                        libLearn.gStatus = CodeLoc + " Line 849";
                        // Inserted by CodeCompleter
                        if (!((x = sr.readLine()) == null))
                        {
                            libLearn.gStatus = CodeLoc + " ReadLine5";
                            CurVok.Bed3 = x.replace("{CR}", "\r").replace("{LF}",
                                    "\n");
                            csv = x.split(",");
                            if (csv.length == 5)
                            {
                                csvFound++;
                                csvall[3] = csv;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        CurVok.Bed2 = "";
                        CurVok.Bed3 = "";
                    }
                    if (!((x = sr.readLine()) == null))
                    {
                        libLearn.gStatus = CodeLoc + " ReadLine6";
                        strTmp = x;
                        try
                        {
                            CurVok.z = (short) Integer.parseInt(strTmp.trim());
                        }
                        catch (Exception ex)
                        {
                            if (canBeSingleLine)
                            {
                                blnSingleLine = true;
                                throw new RuntimeException("IsSingleline", ex);
                            }
                            else
                            {
                                csv = x.split(",");
                                if (csv.length == 5)
                                {
                                    csvFound++;
                                    csvall[4] = csv;
                                }
                                if (csvFound == 5 || csvRegognized)
                                {
                                    csvRegognized = true;
                                    for (int iii = 0; iii < csvFound; iii++)
                                    {
                                        for (int iiii = 0; iiii < 5; iiii++)
                                        {
                                            csvall[iii][iiii] = csvall[iii][iiii].replaceAll("^\"|\"$", "");
                                        }
                                        SetWord(CurVok, csvall[iii][0]);
                                        CurVok.Bed1 = csvall[iii][1];
                                        CurVok.Bed2 = csvall[iii][2];
                                        CurVok.Bed3 = csvall[iii][3];
                                        CurVok.z = (short) Integer.parseInt(csvall[iii][4].trim());
                                        if (iii < 4)
                                        {
                                            CurVok = new typVok();
                                            mVok.add(CurVok);
                                            n = (short) (mVok.size() - 1);
                                        }
                                    }
                                }
                                else
                                {
                                    throw ex;
                                }
                            }

                        }
                    }
                    else
                    {
                        break;
                    }
                    if (libString.IsNullOrEmpty(CurVok.Wort))
                    {
                        mVok.remove(n);
                        n = (short) (mVok.size() - 1);
                        libLearn.gStatus = CodeLoc + " Line 859";
                        // Inserted by CodeCompleter
                        // mVok = lib.ResizeArray(mVok, n + 1);
                    }
                    else
                    {
                        CurVok.Wort = CurVok.Wort.replace("ùú", "\r\n");
                        CurVok.Kom = CurVok.Kom.replace("ùú", "\r\n");
                        CurVok.Bed1 = CurVok.Bed1.replace("ùú", "\r\n");
                        CurVok.Bed2 = CurVok.Bed2.replace("ùú", "\r\n");
                        CurVok.Bed3 = CurVok.Bed3.replace("ùú", "\r\n");
                        if (!blnUnicode && CharsetASCII.equalsIgnoreCase("IBM437"))
                        {
                            CurVok.Wort = CurVok.Wort.replace("∙", "\r\n");
                            CurVok.Kom = CurVok.Kom.replace("∙", "\r\n");
                            CurVok.Bed1 = CurVok.Bed1.replace("∙", "\r\n");
                            CurVok.Bed2 = CurVok.Bed2.replace("∙", "\r\n");
                            CurVok.Bed3 = CurVok.Bed3.replace("∙", "\r\n");
                        }
                        if (blnSingleLine)
                        {
                            CurVok.Wort = ConvMulti(CurVok.Wort);
                            CurVok.Kom = ConvMulti(CurVok.Kom); // .Kom.replace("Ã¹",
                            // "\r\n");
                            CurVok.Bed1 = ConvMulti(CurVok.Bed1); // .replace("Ã¹",
                            // "\r\n");
                            CurVok.Bed2 = ConvMulti(CurVok.Bed2); // .replace("Ã¹",
                            // "\r\n");
                            CurVok.Bed3 = ConvMulti(CurVok.Bed3); // .replace("Ã¹",
                            // "\r\n");
                            CurVok.Wort = ConvMulti(CurVok.Wort); // .replace("Â„",
                            // "Ã¤");

                        }
                    }
                    libLearn.gStatus = CodeLoc + " End While";

                }
                if (csvFound == 5 || csvRegognized)
                {
                    for (int iii = 0; iii < csvFound; iii++)
                    {

                        for (int iiii = 0; iiii < 5; iiii++)
                        {
                            csvall[iii][iiii] = csvall[iii][iiii].replaceAll("^\"|\"$", "");
                        }

                        SetWord(CurVok, csvall[iii][0]);
                        CurVok.Bed1 = csvall[iii][1];
                        CurVok.Bed2 = csvall[iii][2];
                        CurVok.Bed3 = csvall[iii][3];
                        CurVok.z = (short) Integer.parseInt(csvall[iii][4].trim());
                        if (iii < 4)
                        {
                            CurVok = new typVok();
                            mVok.add(CurVok);
                            n = (short) (mVok.size() - 1);
                        }
                    }
                }
                mGesamtzahl = n;
                if (!blnAppend)
                    mIndex = 1;

                // ******** Hier gehts hin wenn ein Fehler auftrit oder wenn _
                // ' ******** SchluÃŸ ist.....
                libLearn.gStatus = CodeLoc + " CloseFile";
                // closefile:
                // Inserted by CodeCompleter
                sr.close();
                isr.close();
                is.close();
                // Defmouse 0
                sp = (short) (sp & 7);
                if (sp >= 0 & sp <= 3)
                {
                    indexlang = sp;
                }
                switch (indexlang)
                {
                    // Case 0: mSprache = "Deutsch"

                    // Case 1: mSprache = "HebrÃ¤isch"
                    // Case 2: mSprache = "Griechisch"
                    // Case Is > 2: Sprache = "Sonstige"
                }
                // If Sprache <> "" Then mSprache = Sprache
                if (mGesamtzahl > 0)
                {
                    _UniCode = blnUnicode;
                    InitAbfrage();
                    if (!blnAppend)
                        mFileName = strFileName;
                    _uri = uri;
                }
                else
                {
                    libLearn.gStatus = CodeLoc + " Line 889";
                    // Inserted by CodeCompleter
                    mblnLernInit = false;
                    mFileName = "";
                    _uri = null;
                }
                if (blnUnicode)
                    aend = false;
                else
                    aend = true;
                //if (blnUriOpened) aend = true;
                return;
            }

        }
        catch (Exception ex)
        {
            throw new RuntimeException("Error in Loadfile", ex);
        }
    }

    private void SetWord(typVok CurVok, String x)
    {
        final String CodeLoc = "SetWord ";
        CurVok.Wort = x.replace("{CR}", "\r").replace("{LF}", "\n");
        short qf = (short) libString.InStr(CurVok.Wort, "\0");
        if (qf == 0)
            qf = (short) libString.InStr(CurVok.Wort, libString.Chr(8));
        if (qf != 0)
        {
            CurVok.Kom = libString.Right(CurVok.Wort,
                    libString.Len(CurVok.Wort) - qf);

            libLearn.gStatus = CodeLoc + " Line 839";
            // Inserted by CodeCompleter
            CurVok.Wort = libString.Left(CurVok.Wort, qf - 1);
        }
        else
        {
            CurVok.Kom = "";
        }


    }

    public void ConvertMulti()
    {
        for (typVok CurVok : mVok)
        {
            CurVok.Wort = ConvMulti(CurVok.Wort);
            CurVok.Kom = ConvMulti(CurVok.Kom); // .Kom.replace("Ã¹", "\r\n");
            CurVok.Bed1 = ConvMulti(CurVok.Bed1); // .replace("Ã¹", "\r\n");
            CurVok.Bed2 = ConvMulti(CurVok.Bed2); // .replace("Ã¹", "\r\n");
            CurVok.Bed3 = ConvMulti(CurVok.Bed3); // .replace("Ã¹", "\r\n");
            CurVok.Wort = ConvMulti(CurVok.Wort); // .replace("Â„", "Ã¤");
            aend = true;
        }
    }

    private String ConvMulti(String s)
    {
        s = s.replace((char) 0xf9, "\n".charAt(0));
        s = s.replace((char) 0x84, "ä".charAt(0));
        s = s.replace((char) 0x9e, "ß".charAt(0));
        s = s.replace((char) 0x81, "ü".charAt(0));
        s = s.replace((char) 0x9a, "Ü".charAt(0));
        s = s.replace((char) 0x94, "ö".charAt(0));
        s = s.replace((char) 0x8e, "Ä".charAt(0));
        s = s.replace((char) 0x80, "€".charAt(0));
        s = s.replace("€1", "");
        s = s.replace("€U", "");
        s = s.replace("€u", "");
        s = s.replace("€B", "");
        s = s.replace("€b", "");
        s = s.replace("€q", "");
        s = s.replace("€C", "");
        s = s.replace("€a", "");
        s = s.replace("€0", "");
        s = s.replace("€2", "");
        s = s.replace("€3", "");
        s = s.replace("€4", "");
        s = s.replace("€8", "");
        s = s.replace("€R", "");
        s = s.replace("€h", "");
        s = s.replace("€t", "");
        s = s.replaceAll("€\\d", "");

        return s;

    }

    public void ResetAbfrage() throws Exception
    {
        mLernVokabeln = new int[mSchrittweite + 1];
        InitAbfrage();
    }

	/*
	 * public void LoadFileAndConvert(String strFileName) { boolean
	 * blnSingleLine = false; boolean blnAppend = false;
	 * LoadFileAndConvert(strFileName,blnSingleLine,blnAppend); } public void
	 * LoadFileAndConvert(String strFileName, boolean blnSingleLine, boolean
	 * blnAppend) { // ERROR: Not supported in C#: OnErrorStatement
	 * 
	 * libLearn.gStatus = "Vokabel.LoadFile Start";
	 * 
	 * short sp = 0; short h = 0; short hh = 0; short qf = 0; short einst = 0;
	 * short tasta = 0; String ext = new String(' ', 3); short n = 0; short lad
	 * = 0; short indexlang = 0; short varbed = 0; String fontfil = null; String
	 * Sprache = null; String tastbel = null; String strTmp = null;
	 * System.IO.StreamReader sr = null; fontfil = ""; Sprache = ""; tastbel =
	 * ""; strTmp = ""; mLernVokabeln = new int[mSchrittweite + 1]; mLastIndex =
	 * 0; // ERROR: Not supported in C#: OnErrorStatement
	 * 
	 * libLearn.gStatus = "Load File: " + strFileName; mFileName = "";
	 * 
	 * libLearn.gStatus = "Vokabel.LoadFile Line 799"; // Inserted by
	 * CodeCompleter
	 * 
	 * if (!libString.IsNullOrEmpty(FileSystem.Dir(strFileName))) { sr = new
	 * System.IO.StreamReader(strFileName,
	 * System.Text.Encoding.GetEncoding(1252)); } else {
	 * Interaction.MsgBox(ClsGlobal.GetLang("FileDoesNotExist",
	 * "Dateiname existiert nicht!", )); //Call Err.Raise(vbObjectError +
	 * ErrWrongfilename, "Vokabel.Loadfile", "Dateiname_ungÃ¼ltig", "", "")
	 * return; } if (System.IO.Path.GetExtension(strFileName).IndexOf(".k",
	 * System.StringComparison.CurrentCultureIgnoreCase) != -1) _cardmode =
	 * true; else _cardmode = false;
	 * 
	 * sp = sr.readLine(); einst = sp & ((Math.Pow(2, 16)) - 256); varHebr = (sp
	 * & 16) != 0; varbed = (sp & 64) != 0; tasta = (sp & 32) != 0;
	 * libLearn.gStatus = "Vokabel.LoadFile Line 819"; // Inserted by
	 * CodeCompleter indexlang = sp & 7; if (!blnAppend) mSprache = indexlang;
	 * if (sp & 128) { tastbel = sr.readLine(); fontfil = sr.readLine(); if
	 * (!blnAppend) getfonts(ref fontfil, ref hh, ref h, ref indexlang, ref qf,
	 * ref lad); //Windows Fonts extrahieren } else { lad = false; }
	 * libLearn.gStatus = "Vokabel.LoadFile Line 829"; // Inserted by
	 * CodeCompleter if (blnAppend) n = mGesamtzahl; while (!sr.EndOfStream) { n
	 * = n + 1; Array.Resize(ref mVok, n + 1); mVok.get(n).Wort =
	 * sr.readLine().replace("{CR}", "\r").replace("{LF}", "\n"); qf =
	 * libString.InStr(mVok.get(n).Wort, libString.Chr(0)); if (qf == 0) qf =
	 * libString.InStr(mVok.get(n).Wort, libString.Chr(8)); if (qf != 0) {
	 * mVok.get(n).Kom = libString.Right(mVok.get(n].Wort,
	 * libString.Len(mVok[n).Wort) - qf); libLearn.gStatus =
	 * "Vokabel.LoadFile Line 839"; // Inserted by CodeCompleter
	 * mVok.get(n).Wort = libString.Left(mVok.get(n).Wort, qf - 1); } else {
	 * mVok.get(n).Kom = ""; } String WordConvert = ""; char cTest = '\0';
	 * foreach (char c in mVok.get(n).Wort) { String cConv = c; if (this.Sprache
	 * == EnumSprachen.Hebrew) { this.aend = true; cConv = ConvHebreÂ„w(c);
	 * WordConvert = cConv + WordConvert; } else if (this.Sprache ==
	 * EnumSprachen.Griechisch) { this.aend = true; cConv = ConvGreek(c);
	 * WordConvert += cConv; }
	 * 
	 * }
	 * 
	 * mVok.get(n).Wort = WordConvert; if (!sr.EndOfStream) { mVok.get(n).Bed1 =
	 * sr.readLine().replace("{CR}", "\r").replace("{LF}", "\n"); } if
	 * (!blnSingleLine) { if (!sr.EndOfStream) { mVok.get(n).Bed2 =
	 * sr.readLine().replace("{CR}", "\r").replace("{LF}", "\n"); }
	 * libLearn.gStatus = "Vokabel.LoadFile Line 849"; // Inserted by
	 * CodeCompleter if (!sr.EndOfStream) { mVok.get(n).Bed3 =
	 * sr.readLine().replace("{CR}", "\r").replace("{LF}", "\n"); } } else {
	 * mVok.get(n).Bed2 = ""; mVok.get(n).Bed3 = ""Â„; } if (!sr.EndOfStream) {
	 * strTmp = sr.readLine(); mVok.get(n).z = Conversion.Val(strTmp); } if
	 * (libString.IsNullOrEmpty(mVok.get(n).Wort)) { n = n - 1; libLearn.gStatus
	 * = "Vokabel.LoadFile Line 859"; // Inserted by CodeCompleter
	 * Array.Resize(ref mVokÂ„, n + 1); } else { mVok.get(n).Wort =
	 * mVok.get(n).Wort.replace("Ã¹Ãº", "\r\n"); mVok.get(n).Kom =
	 * mVok.get(n).Kom.replace("Ã¹Ãº", "\r\n"); mVok.get(n).Bed1 =
	 * mVok.get(n).Bed1.replace("Ã¹Ãº", "\r\n"); mVok.get(n).Bed2 =
	 * mVok.get(n).Bed2.replace("Ã¹Ãº", "\r\n"); mVok.get(n).Bed3 =
	 * mVok.get(n).Bed3.replace("Ã¹Ãº", "\r\n"); }
	 * 
	 * } if (this.aend == true) { if (this.Sprache == EnumSprachen.Hebrew ||
	 * this.Sprache == EnumSprachen.Griechisch) { this.FontWort.Name = "Cardo";
	 * varHebr = false; } } mGesamtzahl = n; if (!blnAppend) mIndex = 1;
	 * 
	 * // ******** Hier gehts hin wenn ein Fehler auftrit oder wenn _ //'
	 * ******** SchluÃŸ ist..... libLearn.gStatus = "Vokabel.LoadFile Line 869";
	 * closefile: // Inserted by CodeCompleter sr.Close(); sr.Dispose(); sr =
	 * null; //Defmouse 0 sp = sp & 7; if (sp >= 0 & sp <= 3) { indexlang = sp;
	 * } switch (indexlang) { // Case 0: mSprache = "Deutsch"
	 * 
	 * // Case 1: mSprache = "HebrÃ¤isch" // Case 2: mSprache = "Griechisch" //
	 * Case Is > 2: Sprache = "Sonstige" } //If Sprache <> "" Then mSprache =
	 * Sprache if (mGesamtzahl > 5) { InitAbfrage(); if (!blnAppend) mFileName =
	 * strFileName; } else { libLearn.gStatus = "Vokabel.LoadFile Line 889"; //
	 * Inserted by CodeCompleter mblnLernInit = false; } aend = false; return;
	 * 
	 * // FErr: if (Err().Number == 59){Interaction.MsgBox("Wort zu lang!"); //
	 * ERROR: Not supported in C#: ResumeStatement }
	 * 
	 * if (Interaction.MsgBox("Fileerror " + "\r\n" + Err().Description,
	 * MsgBoxStyle.RetryCancel) == MsgBoxResult.Retry) { // ERROR: Not supported
	 * in C#: ResumeStatement
	 * 
	 * } // ERROR: Not supported in C#: OnErrorStatement
	 * 
	 * goto closefile; return; } public String ConvHebrew(char c) {Â„ char cTest
	 * = '\0'; String cConv = c; if (c != 'I') { cTest = (char.ToLower(c)); }
	 * else { cTest = c; } switch (libString.Asc(cTest)) { case 0x61: case 0x62:
	 * cConv = libString.ChrW(libString.Asc(c) - 0x61 + 0x5d0); break; case
	 * 0x63: cConv = libString.ChrW(0x5e1); break; case 0x64: cConv =
	 * libString.ChrW(0x5d3); break; case 0x65: cConv = libString.ChrW(0x5b6);
	 * break; case 0x66: cConv = libString.ChrW(0x5b8); break; case 0x67:Â„ cConv
	 * = libString.ChrW(0x5d2); break; case 0x68: cConv = libString.ChrW(0x5d4);
	 * break; case 0x69: cConv = libString.ChrW(0x5e2); break; case 0x6a: cConv
	 * = libString.ChrW(0x5e6); break; case 0x6b: cConv = libString.ChrW(0x5db);
	 * break; case 0x6c: cConv = libString.ChrW(0x5dc); break; case 0x6d: cConv
	 * = libString.ChrW(0x5de); break; case 0x6e:Â„ cConv =
	 * libString.ChrW(0x5e0); break; case 0x6f: //o cConv =
	 * libString.ChrW(0x5b9); break; case libString.Asc('p'): cConv =
	 * libString.ChrW(0x5e4); break; case libString.Asc('q'): cConv =
	 * libString.ChrW(0x5e7); break; case libString.Asc('r'): cConv =
	 * libString.ChrW(0x5e8); break; case libString.Asc('s'): cConv =
	 * libString.ChrW(0x5e9) + libString.ChrW(0x5c2); break; case
	 * libString.Asc('t'): cConv = libString.ChrW(0x5ea); break; case
	 * libString.Asc('u'): cConv = libString.ChrW(0x5d8); break; case
	 * libString.Asc('v'): cConv = libString.ChrW(0x5d5); break; case
	 * libString.Asc('w'): cConv = libString.ChrW(0x5e9) +
	 * libString.ChrW(0x5c1); break; case libString.Asc('x'): cConv =
	 * libString.ChrW(0x5d7); break; case libString.Asc('y'): cConv =
	 * libString.ChrW(0x5d9); break; case libString.Asc('z'): cConv =
	 * libString.ChrW(0x5d6); break; case libString.Asc('('): cConv =
	 * libString.ChrW(0x5e2); break; case libString.Asc(')'): cConv =
	 * libString.ChrW(0x5d0); break; case libString.Asc('+'): cConv =
	 * libString.ChrW(0x5b7); break; case libString.Asc('-'): cConv =
	 * libString.ChrW(0x5b7); break; case libString.Asc('0'): cConv =
	 * libString.ChrW(0x5c2); break; case libString.Asc('1'): cConv =
	 * libString.ChrW(0x5c5); break; case libString.Asc('2'): cConv =
	 * libString.ChrWÂ„(0x5b0); break; case libString.Asc('3'): cConv =
	 * libString.ChrW(0x5a6); break; case libString.Asc('4'): cConv = ""; break;
	 * case libString.Asc('5'): cConv = ""; break; case libString.Asc('6'):
	 * cConv = ""; break; case libString.Asc('7'): cConv = ""; break; case
	 * libString.Asc('9'): cConv = libString.ChrW(0x5bf); break; case
	 * libString.Asc('"'): cConv = libString.ChrW(0x5b5); break; case
	 * libString.Asc('I'): cConv = libString.ChrW(0x5b4); break; case
	 * libString.Asc('f'): cConv = libString.ChrW(0x5b8); break; case
	 * libString.Asc('o'): cConv = libString.ChrW(0x5c1); break; case
	 * libString.Asc('e'): cConv = libString.ChrW(0x5b6); break; case
	 * libString.Asc(':'): cConv = libString.ChrW(0x5b0); break; case
	 * libString.Asc('_'): cConv = libString.ChrW(0x5b2); break; case
	 * libString.Asc('.'): cConv = libString.ChrW(0x5c3); break; case
	 * libString.Asc('^'): cConv = libString.ChrW(0x5ab); break; case
	 * libString.Asc(']'): cConv = libString.ChrW(0x5df); break; case
	 * libString.Asc('%'): cConv = libString.ChrW(0x5da) + libString.ChrW(0x5bc)
	 * + libString.ChrW(0x5b3);
	 * 
	 * break;
	 * 
	 * } return cConv; } public String ConvGreek(char c) { char cTest = '\0';
	 * String cConv = c; //If c <> "I"c Then // cTest = (Char.ToLower(c)) //Else
	 * cTest = c; // End If switch (libString.Asc(cTest)) { case
	 * libString.Asc('A'): cConv = libString.ChrW(0x391); break; case
	 * libString.Asc('B'): cConv = libString.ChrW(0x392); break; case
	 * libString.Asc('C'): cConv = libString.ChrW(0x3a7); break; case
	 * libString.Asc('D'): cConv = libString.ChrW(0x394); break; case
	 * libString.Asc('E'): cConv = libString.ChrW(0x395); break; case
	 * libString.Asc('F'): cConv = libString.ChrW(0x3a6); break; case
	 * libString.Asc('G'): cConv = libString.ChrW(0x393); break; case
	 * libString.Asc('H'): cConv = libString.ChrW(0x397); break; case
	 * libString.Asc('I'): cConv = libString.ChrW(0x399); break; case
	 * libString.Asc('J'): cConv = "á¿³Í…"; break; case libString.Asc('K'): cConv =
	 * libString.ChrW(0x39a); break; case libString.Asc('L'): cConv =
	 * libString.ChrW(0x39b); break; case libString.Asc('M'): cConv =
	 * libString.ChrW(0x39c); break; case libString.Asc('N'): cConv =
	 * libString.ChrW(0x39d); break; case libString.Asc('O'): cConv =
	 * libString.ChrW(0x39f); break; case libString.Asc('P'): cConv =
	 * libString.ChrW(0x3a0); break; case libString.Asc('Q'): cConv =
	 * libString.ChrW(0x398); break; case libString.Asc('R'): cConv =
	 * libString.ChrW(0x3a1); break; case libString.Asc('S'): cConv =
	 * libString.ChrW(0x3a3); break; case libString.Asc('T'): cConv =
	 * libString.ChrW(0x3a4); break; case libString.Asc('U'): cConv =
	 * libString.ChrW(0x3a5); break; case libString.Asc('V'): cConv = "á¿ƒ";
	 * break; case libString.Asc('W'): cConv = libString.ChrW(0x3a9); break;
	 * case libString.Asc('X'): cConv = libString.ChrW(0x39e); break; case
	 * libString.Asc('Y'): cConv = libString.ChrW(0x3a8); break; case
	 * libString.Asc('Z'): cConv = libString.ChrW(0x396); break; case
	 * libString.Asc('â€¦'): cConv = "á½·"; break; case libString.Asc('Å¡'): cConv =
	 * "á½³"; break; case libString.Asc('Å’'): cConv = "á¼·"; break; case
	 * libString.Asc('Æ’'): cConv = "á¼±"; break;Â„ case libString.Asc('â€ '): cConv =
	 * "á¼µ"; break; case libString.Asc('â€ž'): cConv = "á¼°"; break; case
	 * libString.Asc('Ë†'): cConv = "á½¶"; break; case libString.Asc('$'): cConv =
	 * "Ï›"; break; case libString.Asc('%'): cConv = "Ï™"; break; case
	 * libString.Asc('#'): cConv = "Ï�"; break; case 0x61: // TODO: to 0x7a cConv
	 * =
	 * "Î±Î²Ï‡Î´ÎµÏ†Î³Î·Î¹Ï‚ÎºÎ»Î¼Î½Î¿Ï€Î¸Ï�ÏƒÏ„Ï…á¾³Ï‰Î¾ÏˆÎ¶".SubString("abcdefghijklmnopqrstuvwxyz".IndexOf
	 * (cTest), 1); break; case libString.Asc('Â·'): // TODO: to
	 * libString.Asc('Ã�') cConv =
	 * "á¿¥á¿¤á¼¡á¼ á½µá¼¥á¼¤á½´á¼£á¼¢á¿†á¼§á¼¦Íºá¾‘á¾�á¿„á¾•á¾”á¿‚á¾“á¾’á¿‡á¾—á¾–".SubString("Â·Â¸Â¹ÂºÂ»Â¼Â½Â¾Â¿Ã€Ã�Ã‚ÃƒÃ„Ã…Ã†Ã‡ÃˆÃ‰ÃŠÃ‹ÃŒÃ�ÃŽÃ�"
	 * .IndexOf(cTest), 1); break; case libString.Asc('\u008d'): // TODO: to
	 * libString.Asc('Â¶') int Index =
	 * "\u008d\u008e\u008f\u0090\u009d\u009eÂ¡Â¢Â£Â¤Â¥Â¦Â§Â¨Â©ÂªÂ«Â¬Â®Â¯Â°Â±Â²Â³Â´ÂµÂ¶"
	 * .IndexOf(cTest); if (Index > -1) { cConv =
	 * "á¼¶á¿‘á¿“á¿’á½²á¼“á¼�á¼€á½±á¼…á¼„á½°á¼ƒá¼‚á¾¶á¼‡á¼†á¾�á¾´á¾…á¾„á¾²á¾ƒá¾„á¾·á¾‡á¾†".SubString(Index, 1); } break; case
	 * libString.Asc('Ã�'): // TODO: to libString.Asc('Ã¥') int Index =
	 * "Ã�Ã‘Ã’Ã“Ã”Ã•Ã–Ã—Ã˜Ã™ÃšÃ›ÃœÃ�ÃžÃŸÃ Ã¡Ã¢Ã£Ã¤Ã¥".IndexOf(cTest); if (Index > -1) { cConv =
	 * "á½�á½€á½¹á½…á½„á½¸á½ƒá½‚á½‘á½�á½»á½•á½”á½ºá½“á½”á¿¦á½—á½–Ï‹á¿£á¿¢".SubString(Index, 1); } break; case
	 * libString.Asc('Ã¦'): // TODO: to libString.Asc('Ã»') int Index =
	 * "Ã¦Ã§Ã¨Ã©ÃªÃ«Ã¬Ã­Ã®Ã¯Ã°Ã±Ã²Ã³Ã´ÃµÃ¶Ã·Ã¸Ã¹ÃºÃ»".IndexOf(cTest); if (Index > -1) { cConv =
	 * "á½¡á½ á½½á½¥á½¤á½¼á½£á½¢á¿¶á½§á½¦á¾¡á¾ á¿´á¾¥á¾¤á¿²á¾£á¾¢á¿·á¾§á¾¦".SubString(Index, 1); } break; case
	 * libString.Asc('Ã¼'): // TODO: to libString.Asc('âˆ™') int Index =
	 * "Ã¼Ã½Å’Å“Å Å¡Å¸Æ’Ë†Ëœâ€“â€”â€˜â€™\"â€žâ€ â€¡â€¢â€¦â€°â€¹â€ºâ„¢âˆ™".IndexOf(cTest); if (Index > -1) { cConv =
	 * "ÎµÎ¿á¼·á¼”á¼’á¼²á½³á¼’á¼±á½¶á¼‘á¿�á¿®á¿Ÿá¿�á¿žá¿Žá¼°á¼µá¼´á¿�á½·á¼³á¿–á¼•á¼�á¿¥".SubString(Index, 1); }
	 * 
	 * break;
	 * 
	 * } return cConv; }
	 */

    // UPGRADE_NOTE: Class_Initialize wurde aktualisiert auf Init. Klicken Sie
    // hier fÃ¼r weitere Informationen:
    // 'ms-help://MS.VSExpressCC.v80/dv_commoner/local/redirect.htm?keyword="A9E4979A-37FA-4718-9994-97DD76ED70A7"'
    private void Init() throws Exception
    {
        // ERROR: Not supported in C#: OnErrorStatement

        libLearn.gStatus = "Vokabel.Class_Initialize Start";
        mVok = new ArrVok();

        mVok.add(new typVok("empty", "empty", "empty", "empty", "empty",
                (short) 0));
        libLearn.gStatus = "Vokabel.Class_Initialize Line 1228";
        // Inserted by CodeCompleter
        mConfirmChanges = true;
        mSchrittweite = 6;
        mAbfragebereich = -1;
        mAbfrageZufaellig = false;
        mLerngeschwindigkeit = 1;

        return;
    }

    // Public Sub New()
    // MyBase.New()
    // Init()
    // End Sub
    public Vokabel(Activity Container, TextView txtStatus) throws Exception
    {

        this.Container = Container;
        Init(txtStatus);
        mWortFont = new clsFont(Container);
        // lokale Kopie
        mBedFont = new clsFont(Container);
        // lokale Kopie
        mKomFont = new clsFont(Container);
        // lokale Kopie
        TypefaceCardo = Typeface.createFromAsset(getContext().getAssets(),
                "Cardo104s.ttf");
        Init();
    }

    public Context getContext()
    {
        if (Container != null)
        {
            return Container;
        }
        else
        {
            return null;
        }
    }

    public void OpenURL(String strLocation) throws Exception
    {
        URL toDownload = new URL(strLocation);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try
        {
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = toDownload.openStream();

            while ((bytesRead = stream.read(chunk)) > 0)
            {
                outputStream.write(chunk, 0, bytesRead);
            }
            stream.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
        String b = new String(outputStream.toByteArray());
        if (libString.Len(b) < 50)
        {
            libLearn.gStatus = "Vok.OpenURL Line 39";
            // Inserted by CodeCompleter
            throw new RuntimeException("OpenUrl: Could not load URL "
                    + strLocation);
        }
        if (libString.InStr(1, b, "<html>") > 0)
        {
            throw new RuntimeException("OpenUrl:" + b);
        }
    }

    public Vokabel()
    {
        // TODO Auto-generated finalructor stub
    }

    /*
	 * @Override public int describeContents() { // TODO Auto-generated method
	 * stub return 0; }
	 * 
	 * @Override public void writeToParcel(Parcel dest, int flags) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 */
    public int getAnzBed() throws Exception
    {

        return mVok.get(mIndex).getAnzBed();
    }


    private String _URIName = "";

    public void setURIName(String uriName)
    {

        if (uriName != null && uriName.length() > 1)
        {
            do
            {
                if (uriName.startsWith("/"))
                {
                    uriName = uriName.substring(1);
                }
                else
                {
                    break;
                }
            }
            while (uriName.length() > 1);
        }
        _URIName = uriName;
    }

    public String getURIName()
    {
        return _URIName;
    }

    public static String getComment(String vok) throws Exception
    {
        if (!libString.IsNullOrEmpty(vok))
        {
            if (vok.startsWith("{\\rtf1\\"))
            {
                // txt = Java2Html.convertToHtml(txt,
                // JavaSourceConversionOptions.getDefault());
                // return Html.fromHtml(txt);
                // return new SpannedString(stripRtf(txt));
                vok = RichTextStripper.StripRichTextFormat(vok);
            }

            int Start1 = vok.indexOf("[");
            if (Start1 > -1)
            {
                int Start2 = vok.indexOf("]", Start1 + 1);
                if (Start2 > Start1)
                {
                    return vok.substring(Start1, Start2 + 1);
                }

            }
        }
        return "";
    }


}
