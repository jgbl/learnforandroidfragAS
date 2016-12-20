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

//import android.support.v7.app.ActionBarActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnWindowFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.de.jmg.learn.MainActivity;
import org.de.jmg.learn.R;
import org.de.jmg.learn.libLearn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;

//import com.microsoft.live.*;
//import android.runtime.*;

public class lib
{

    public static final String TAG = "org.de.jmg.lib.lib";
    public static final int SELECT_FILE = 0xa3b4;
    // private static final String ONEDRIVE_APP_ID = "48122D4E";
    private static final String ClassName = "lib.lib";
    public static boolean sndEnabled = true;
    public static boolean AntwWasRichtig;
    public static ArrayList<DialogInterface> OpenDialogs = new ArrayList<>();
    public static MainActivity main;
    public static Handler YesNoHandler;
    public static String[] AssetSounds = new String[13];
    public static PrefsOnMultiChoiceClickListener cbListener = new PrefsOnMultiChoiceClickListener();
    static AlertDialog dlgOK;
    static Bitmap bmpimg;
    private static String _status = "";
    private static yesnoundefined DialogResultYes = yesnoundefined.undefined;
    private static DialogInterface.OnClickListener listenerYesNo = new DialogInterface.OnClickListener()
    {

        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    // Yes button clicked
                    DialogResultYes = yesnoundefined.yes;
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // No button clicked
                    DialogResultYes = yesnoundefined.no;
                    break;
            }
            if (YesNoHandler != null) YesNoHandler.sendMessage(YesNoHandler.obtainMessage());
        }
    };
    /*
     * public static String rtfToHtml(Reader rtf) throws IOException {
     * JEditorPane p = new JEditorPane(); p.setContentType("text/rtf");
     * EditorKit kitRtf = p.getEditorKitForContentType("text/rtf"); try {
     * kitRtf.read(rtf, p.getDocument(), 0); kitRtf = null; EditorKit kitHtml =
     * p.getEditorKitForContentType("text/html"); Writer writer = new
     * StringWriter(); kitHtml.write(writer, p.getDocument(), 0,
     * p.getDocument().getLength()); return writer.toString(); } catch
     * (BadLocationException e) { e.printStackTrace(); } return null; }
     */
    private static long SLEEP_TIME = 2; // for 2 second
    private static CountDownLatch latch;

    public lib()
    {
    }

    public static String getgstatus()
    {
        return _status;
    }

    public static Bitmap resizeBM(Bitmap b, float factX, float factY)
    {
        if (b != null)
        {
            Matrix matrix = new Matrix();
            matrix.postScale(factX, factY);
            return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
        }
        return null;
    }


    public static void removeDlg(DialogInterface dlg)
    {
        if (OpenDialogs.contains(dlg))
        {
            OpenDialogs.remove(dlg);
        }
    }

	/*
     * private static class ExStateInfo { public Context context; public
	 * RuntimeException ex; public ExStateInfo(Context context, RuntimeException
	 * ex) { this.context = context; this.ex = ex; } }
	 */

    public static void setgstatus(String value)
    {
        _status = value;
        System.out.println(value);
    }

    public static boolean ExtensionMatch(String value, String extension)
    {
        String ext;
        if (value.contains("."))
        {
            ext = value.substring(value.lastIndexOf("."));
        }
        else
        {
            return false;
        }

        if (extension.toLowerCase(Locale.getDefault()).contains(ext.toLowerCase(Locale.getDefault())))
            return true;

        String itext = extension;
        if (!itext.startsWith(".")) itext = "." + itext;
        itext = itext.replace(".", "\\.");
        itext = itext.toLowerCase(Locale.getDefault());
        ext = ext.toLowerCase(Locale.getDefault());
        return ext.matches(itext.replace("?", ".{1}").replace("*", ".*"));


    }

    public static boolean RegexMatchVok(String FileName)
    {
        String ext = lib.getExtension(FileName);
        if (!libString.IsNullOrEmpty(ext))
        {
            ext = ext.toLowerCase(Locale.getDefault());
            if (ext.startsWith(".k") || ext.startsWith(".v")) return true;
        }
        return false;
		/*
		if (FileName.toLowerCase().matches(".+\\.(v.{2})|(k.{2})$")) return true;
		if (FileName.toLowerCase().matches("\\/.+\\.(v.{2})|(k.{2})$")) return true;
		return false;
		*/
    }

    public static boolean NookSimpleTouch()
    {
        //return true;

        String MANUFACTURER = getBuildField("MANUFACTURER");
        @SuppressWarnings("unused")
        String MODEL = getBuildField("MODEL");
        String DEVICE = getBuildField("DEVICE");
        return (MANUFACTURER.equalsIgnoreCase("BarnesAndNoble") && DEVICE.equalsIgnoreCase("zoom2"));

    }

    private static String getBuildField(String fieldName)
    {

        try
        {
            return (String) Build.class.getField(fieldName).get(null);
        }
        catch (Exception e)
        {
            Log.d("cr3", "Exception while trying to check Build." + fieldName);
            return "";
        }
    }

    public static String getRealPathFromURI(Activity context,
                                            android.net.Uri contentURI) throws Exception
    {
        android.database.Cursor cursor = null;
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentURI, proj, null,
                    null, null);
            if (cursor != null)
            {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            else
            {
                return null;
            }

        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }

    public static String getSizeFromURI(Context context,
                                        android.net.Uri contentURI)
    {
        android.database.Cursor cursor = null;
        try
        {
            String[] proj = {MediaStore.Images.Media.SIZE};
            cursor = context.getContentResolver().query(contentURI, proj, null,
                    null, null);
            if (cursor != null)
            {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            else
            {
                return null;
            }

        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }

	/*
	 * public static <E> getEnumByOrdinal(<E> object, int ordinal) throws
	 * RuntimeException { E value; return value; }
	 */

    public static void StartViewer(Context context, android.net.Uri uri)
    {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(uri, "image/*");
        context.startActivity(i);
    }

    public static void copyFile(String Source, String Dest) throws IOException
    {
        File source = new File(Source);
        File dest = new File(Dest);
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try
        {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
        finally
        {
            sourceChannel.close();
            destChannel.close();
        }
    }

    public static float convertFromDp(Context context, float input)
    {
        int minWidth = context.getResources().getDisplayMetrics().widthPixels;
        int minHeight = context.getResources().getDisplayMetrics().heightPixels;
        if (minHeight < minWidth)
            minWidth = minHeight;
        final float scale = 768.0f / (float) minWidth;
        return ((input - 0.5f) / scale);
    }

    public static boolean like(final String str, final String expr)
    {
        String regex = quotemeta(expr);
        regex = regex.replace("_", ".").replace("*", ".*?");
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE
                | Pattern.DOTALL);
        return p.matcher(str).matches();
    }

    public static String quotemeta(String s)
    {
        if (s == null)
        {
            throw new IllegalArgumentException("String cannot be null");
        }

        int len = s.length();
        if (len == 0)
        {
            return "";
        }

        StringBuilder sb = new StringBuilder(len * 2);
        for (int i = 0; i < len; i++)
        {
            char c = s.charAt(i);
            if ("[](){}.+?$^|#\\".indexOf(c) != -1)
            {
                sb.append("\\");
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static int countMatches(String str, String sub)
    {
        if (libString.IsNullOrEmpty(str) || libString.IsNullOrEmpty(sub))
        {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1)
        {
            count++;
            idx += sub.length();
        }
        return count;

    }

    public static String MakeMask(String strBed)
    {
        try
        {
            int i = 0;
            int Len = strBed.length();
            if (Len == 0)
                return "";
            libLearn.gStatus = ClassName + ".MakeMask";
            for (i = 0; i <= Len - 1; i++)
            {
                if ((".,;/[]()".indexOf(strBed.charAt(i)) > -1))
                {
                    strBed = strBed.substring(0, i) + "*"
                            + (i < Len - 1 ? strBed.substring(i + 1) : "");
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return strBed;

    }

    /**
     * Returns a pseudo-random number between min and max, inclusive. The
     * difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value. Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int rndInt(int min, int max)
    {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static <T> T[] ResizeArray(T Array[], int newSize)
    {
        @SuppressWarnings("unchecked")
        T[] NewArr = (T[]) java.lang.reflect.Array.newInstance(
                Array.getClass(), newSize);
        int length = Array.length;
        if (length > newSize)
            length = newSize;
        System.arraycopy(Array, 0, NewArr, 0, length);
        return NewArr;
    }

    public static int[] ResizeArray(int[] Array, int newSize)
    {
        int[] NewArr = new int[newSize];
        int length = Array.length;
        if (length > newSize)
            length = newSize;
        System.arraycopy(Array, 0, NewArr, 0, length);
        return NewArr;
    }

    public static void ShowMessage(Context context, String msg, String title)
    {
        ShowMessage(context, new SpannableString(msg), title);
    }

    public static void ShowMessage(Context context, Spannable msg, String title)
    {
        // System.Threading.SynchronizationContext.Current.Post(new
        // System.Threading.SendOrPostCallback(DelShowException),new
        // ExStateInfo(context, ex));
        if (libString.IsNullOrEmpty(title)) title = context.getString(R.string.message);

        AlertDialog.Builder A = new AlertDialog.Builder(context);
        A.setPositiveButton("OK", new listener());
        A.setMessage(msg);
        A.setTitle(title);
        AlertDialog dlg = A.create();
        dlg.show();
        ((TextView) dlg.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        OpenDialogs.add(dlg);
    }

    public static boolean ShowMessageWithCheckbox(Context context, String title, String msg, String CheckboxTitle) throws Exception
    {
        // System.Threading.SynchronizationContext.Current.Post(new
        // System.Threading.SendOrPostCallback(DelShowException),new
        // ExStateInfo(context, ex));
        if (libString.IsNullOrEmpty(title)) title = context.getString(R.string.message);

        AlertDialog.Builder A = new AlertDialog.Builder(context);

        A.setTitle(title);
        A.setMessage(msg);

        View checkBoxView = View.inflate(context, R.layout.checkbox_layout, null);
        final CheckBox cbx = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        cbx.setText(CheckboxTitle);

        A.setView(checkBoxView);
        DialogResultYes = yesnoundefined.undefined;
        A.setPositiveButton(context.getString(R.string.ok), new OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //throw new MessageException();
                DialogResultYes = yesnoundefined.yes;
            }
        });
        //A.setMessage(msg);
        AlertDialog dlg = A.create();
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                throw new MessageException();
            }
        });
        dlg.show();
        OpenDialogs.add(dlg);
        try

        {
            Looper.loop();
        }
        catch (Exception e2)
        {
            // Looper.myLooper().quit();
            if (dlg.isShowing())
            {
                dlg.setOnDismissListener(null);
                dlg.dismiss();
            }
            removeDlg(dlg);
            dlg = null;
            if (!(e2 instanceof MessageException)) throw e2;
        }
        return DialogResultYes == yesnoundefined.yes && cbx.isChecked();
    }

    public static void ShowException(Context context, Throwable ex)
    {
        // System.Threading.SynchronizationContext.Current.Post(new
        // System.Threading.SendOrPostCallback(DelShowException),new
        // ExStateInfo(context, ex));
        AlertDialog.Builder A = new AlertDialog.Builder(context);
        A.setPositiveButton("OK", new listener());
        A.setMessage(ex.getMessage() + "\n"
                + (ex.getCause() == null ? "" : ex.getCause().getMessage())
                + "\nStatus: " + libLearn.gStatus
                + "\n" + Log.getStackTraceString(ex));
        A.setTitle("Error");
        dlgOK = A.create();
        dlgOK.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                removeDlg(dialog);
            }
        });
        dlgOK.show();
        OpenDialogs.add(dlgOK);
    }

    public static yesnoundefined ShowMessageYesNo(Context context,
                                                  String msg, String title)
    {
        return ShowMessageYesNo(context, msg, title, false);
    }

    public static yesnoundefined ShowMessageYesNo(Context context,
                                                  String msg, String title, boolean center)
    {
        // System.Threading.SynchronizationContext.Current.Post(new
        // System.Threading.SendOrPostCallback(DelShowException),new
        // ExStateInfo(context, ex));
        if (libString.IsNullOrEmpty(title)) title = context.getString(R.string.question);
        try
        {
            if (YesNoHandler == null)
            {
                YesNoHandler = new Handler()
                {
                    @Override
                    public void handleMessage(Message mesg)
                    {
                        //throw new MessageException();
                    }
                };
            }

            DialogResultYes = yesnoundefined.undefined;
            AlertDialog.Builder A = new AlertDialog.Builder(context);
            A.setPositiveButton(context.getString(R.string.yes), listenerYesNo);
            A.setNegativeButton(context.getString(R.string.no), listenerYesNo);
            A.setMessage(msg);
            A.setTitle(title);
            AlertDialog dlg = A.create();
            dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    throw new MessageException();
                }
            });

            dlg.show();
            OpenDialogs.add(dlg);
            if (center)
            {
                TextView messageView = (TextView) dlg.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.CENTER);
            }
            try

            {
                Looper.loop();
            }
            catch (Exception e2)
            {
                // Looper.myLooper().quit();
                YesNoHandler = null;
                if (dlg.isShowing())
                {
                    dlg.setOnDismissListener(null);
                    dlg.dismiss();
                }
                removeDlg(dlg);
                dlg = null;
                if (!(e2 instanceof MessageException)) throw e2;
            }
        }
        catch (Exception ex)
        {
            ShowException(context, ex);
        }
        return DialogResultYes;
    }

    public static yesnoundefined ShowMessageOKCancel(Context context,
                                                     String msg, String title, boolean center)
    {
        // System.Threading.SynchronizationContext.Current.Post(new
        // System.Threading.SendOrPostCallback(DelShowException),new
        // ExStateInfo(context, ex));
        if (libString.IsNullOrEmpty(title)) title = context.getString(R.string.question);
        try
        {
            if (YesNoHandler == null)
            {
                YesNoHandler = new Handler()
                {
                    @Override
                    public void handleMessage(Message mesg)
                    {
                        //throw new MessageException();
                    }
                };
            }

            DialogResultYes = yesnoundefined.undefined;
            AlertDialog.Builder A = new AlertDialog.Builder(context);
            A.setPositiveButton(context.getString(R.string.ok), listenerYesNo);
            A.setNegativeButton(context.getString(R.string.cancel), listenerYesNo);
            A.setMessage(msg);
            A.setTitle(title);
            AlertDialog dlg = A.create();
            dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    throw new MessageException();
                }
            });

            dlg.show();
            OpenDialogs.add(dlg);
            if (center)
            {
                TextView messageView = (TextView) dlg.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.CENTER);
            }
            try

            {
                Looper.loop();
            }
            catch (Exception e2)
            {
                // Looper.myLooper().quit();
                YesNoHandler = null;
                if (dlg.isShowing())
                {
                    dlg.setOnDismissListener(null);
                    dlg.dismiss();
                }
                removeDlg(dlg);
                dlg = null;
                if (!(e2 instanceof MessageException)) throw e2;
            }
        }
        catch (Exception ex)
        {
            ShowException(context, ex);
        }
        return DialogResultYes;
    }

    public static YesNoCheckResult ShowMessageYesNoWithCheckbox(Context context,
                                                                String title,
                                                                String msg,
                                                                String CheckBoxTitle,
                                                                boolean center)
    {
        // System.Threading.SynchronizationContext.Current.Post(new
        // System.Threading.SendOrPostCallback(DelShowException),new
        // ExStateInfo(context, ex));
        if (libString.IsNullOrEmpty(title)) title = context.getString(R.string.question);
        libLearn.gStatus = "ShowMessageYesNoWithCheckbox";
        try
        {
            if (YesNoHandler == null)
            {
                YesNoHandler = new Handler()
                {
                    @Override
                    public void handleMessage(Message mesg)
                    {
                        //throw new MessageException();
                    }
                };
            }

            DialogResultYes = yesnoundefined.undefined;
            AlertDialog.Builder A = new AlertDialog.Builder(context);
            A.setPositiveButton(context.getString(R.string.yes), listenerYesNo);
            A.setNegativeButton(context.getString(R.string.no), listenerYesNo);
            A.setMessage(msg);
            A.setTitle(title);
            View checkBoxView = View.inflate(context, R.layout.checkbox_layout, null);
            final CheckBox cbx = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
            cbx.setText(CheckBoxTitle);

            A.setView(checkBoxView);
            AlertDialog dlg = A.create();
            dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    throw new MessageException();
                }
            });

            dlg.show();
            OpenDialogs.add(dlg);
            if (center)
            {
                TextView messageView = (TextView) dlg.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.CENTER);
            }

            try

            {
                Looper.loop();
            }
            catch (Exception e2)
            {
                // Looper.myLooper().quit();
                YesNoHandler = null;
                if (dlg.isShowing())
                {
                    dlg.setOnDismissListener(null);
                    dlg.dismiss();
                }
                removeDlg(dlg);
                dlg = null;
                if (!(e2 instanceof MessageException)) throw e2;
            }
            return new YesNoCheckResult(DialogResultYes, cbx.isChecked());
        }
        catch (Exception ex)
        {
            ShowException(context, ex);
        }
        return null;
    }

    public static OkCancelStringResult InputBox(Context context,
                                                String title,
                                                String msg,
                                                String prompt,
                                                boolean center)
    {
        // System.Threading.SynchronizationContext.Current.Post(new
        // System.Threading.SendOrPostCallback(DelShowException),new
        // ExStateInfo(context, ex));
        if (libString.IsNullOrEmpty(title)) title = context.getString(R.string.question);
        libLearn.gStatus = "ShowMessageYesNoWithCheckbox";
        try
        {
            if (YesNoHandler == null)
            {
                YesNoHandler = new Handler()
                {
                    @Override
                    public void handleMessage(Message mesg)
                    {
                        //throw new MessageException();
                    }
                };
            }

            DialogResultYes = yesnoundefined.undefined;
            AlertDialog.Builder A = new AlertDialog.Builder(context);
            A.setPositiveButton(context.getString(R.string.ok), listenerYesNo);
            A.setNegativeButton(context.getString(R.string.cancel), listenerYesNo);
            A.setMessage(msg);
            A.setTitle(title);
            final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setText(prompt);
            A.setView(input);
            AlertDialog dlg = A.create();
            dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    throw new MessageException();
                }
            });

            dlg.show();
            OpenDialogs.add(dlg);
            if (center)
            {
                TextView messageView = (TextView) dlg.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.CENTER);
            }

            try

            {
                Looper.loop();
            }
            catch (Exception e2)
            {
                // Looper.myLooper().quit();
                YesNoHandler = null;
                if (dlg.isShowing())
                {
                    dlg.setOnDismissListener(null);
                    dlg.dismiss();
                }
                removeDlg(dlg);
                dlg = null;
                if (!(e2 instanceof MessageException)) throw e2;
            }
            okcancelundefined res;
            if (DialogResultYes == yesnoundefined.yes)
            {
                res = okcancelundefined.ok;
            }
            else if (DialogResultYes == yesnoundefined.no)
            {
                res = okcancelundefined.cancel;
            }
            else
            {
                res = okcancelundefined.undefined;
            }
            return new OkCancelStringResult(res, input.getText().toString());
        }
        catch (Exception ex)
        {
            ShowException(context, ex);
        }
        return null;
    }

    public static yesnoundefined ShowMessageYesNoWithCheckboxes(Context context,
                                                                String msg,
                                                                CharSequence[] items,
                                                                boolean[] checkedItems,
                                                                DialogInterface.OnMultiChoiceClickListener cbListener)
    {
        // System.Threading.SynchronizationContext.Current.Post(new
        // System.Threading.SendOrPostCallback(DelShowException),new
        // ExStateInfo(context, ex));

        try
        {
            if (YesNoHandler == null)
            {
                YesNoHandler = new Handler()
                {
                    @Override
                    public void handleMessage(Message mesg)
                    {
                        //throw new MessageException();
                    }
                };
            }

            DialogResultYes = yesnoundefined.undefined;
            AlertDialog.Builder A = new AlertDialog.Builder(context);
            A.setPositiveButton(context.getString(R.string.yes), listenerYesNo);
            A.setNegativeButton(context.getString(R.string.no), listenerYesNo);
            //A.setMessage(msg);
            A.setTitle(msg);
            A.setMultiChoiceItems(items, checkedItems, cbListener);
            AlertDialog dlg = A.create();
            dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    throw new MessageException();
                }
            });

            dlg.show();
            OpenDialogs.add(dlg);

            try

            {
                Looper.loop();
            }
            catch (Exception e2)
            {
                // Looper.myLooper().quit();
                YesNoHandler = null;
                if (dlg.isShowing())
                {
                    dlg.setOnDismissListener(null);
                    dlg.dismiss();
                }
                removeDlg(dlg);
                dlg = null;
                if (!(e2 instanceof MessageException)) throw e2;
            }
        }
        catch (Exception ex)
        {
            ShowException(context, ex);

        }
        return DialogResultYes;
    }

    public static void ShowToast(Context context, String msg)
    {
		/* Looper.prepare(); */
        Toast T = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        T.show();
    }

    public static String getExtension(String Filename)
    {
        return getExtension(new File(Filename));
    }

    public static String getExtension(java.io.File F)
    {
        String extension = "";

        int i = F.getName().lastIndexOf('.');
        if (i > 0)
        {
            extension = F.getName().substring(i);
            return extension;
        }
        else
        {
            return null;
        }
    }

    public static String getFilenameWithoutExtension(String Filename)
    {
        return getFilenameWithoutExtension(new File(Filename));
    }

    public static String getFilenameWithoutExtension(java.io.File F)
    {
        String Filename = F.getPath();

        int i = Filename.lastIndexOf('.');
        int ii = Filename.lastIndexOf(java.io.File.pathSeparatorChar);
        if (i > 0 && i > ii)
        {
            Filename = Filename.substring(0, i);
            return Filename;
        }
        else
        {
            return Filename;
        }
    }

    public static void Sleep(int Seconds) throws InterruptedException
    {
        latch = new CountDownLatch(1);
        SLEEP_TIME = Seconds;
        MyLauncher launcher = new MyLauncher();
        launcher.start();
        // latch.await();
    }

    public static int[] getColors()
    {
        int[] Colors = new int[4096];
        for (int i = 0; i < 16; i++)
        {
            for (int ii = 0; ii < 16; ii++)
            {
                for (int iii = 0; iii < 16; iii++)
                {
                    Colors[i + ii * 16 + iii * 16 * 16] = Color.rgb(i * 16,
                            ii * 16, iii * 16);
                }
            }
        }
        ;
        ;
        return Colors;
    }

    public static int[] getIntArrayFromPrefs(SharedPreferences prefs,
                                             String name)
    {
        int count = prefs.getInt(name, -1);
        if (count > -1)
        {
            int[] res = new int[count + 1];
            for (int i = 0; i <= count; i++)
            {
                res[i] = prefs.getInt(name + i, 0);
            }

            return res;
        }
        else
        {
            return null;
        }

    }

    public static void putIntArrayToPrefs(SharedPreferences prefs, int array[],
                                          String name)
    {
        Editor edit = prefs.edit();
        if (array == null)
        {
            edit.putInt(name, -1);
            //edit.putInt(name + 0, 1);
        }
        else
        {
            int count = array.length - 1;
            edit.putInt(name, count);
            for (int i = 0; i <= count; i++)
            {
                edit.putInt(name + i, array[i]);
            }
        }


        edit.commit();

    }

    public static void initSounds()
    {
        AssetSounds[0] = "snd/clapping_hurray.ogg";
        AssetSounds[1] = "snd/Fireworks Finale-SoundBible.com-370363529.ogg";
        AssetSounds[2] = "snd/Red_stag_roar-Juan_Carlos_-2004708707.ogg";
        AssetSounds[3] = "snd/Fireworks Finale-SoundBible.com-370363529.ogg";
        AssetSounds[4] = "snd/clapping_hurray.ogg";
        AssetSounds[5] = "snd/clapping_hurray.ogg";
        AssetSounds[6] = "snd/chickens_demanding_food.ogg";
        AssetSounds[7] = "snd/Cow And Bell-SoundBible.com-1243222141.ogg";
        AssetSounds[8] = "snd/gobbler_bod.ogg";
        AssetSounds[9] = "snd/Toilet_Flush.ogg";
        AssetSounds[10] = "snd/ziegengatter.ogg";
        AssetSounds[11] = "snd/ziegengatter.ogg";
        AssetSounds[12] = "snd/Pew_Pew-DKnight556-1379997159.ogg";

    }

    public static void playSound(Context context, Sounds s) throws Exception
    {
        MainActivity main = (MainActivity) context;
        AssetManager assets = context.getAssets();
        if (main.colSounds.size() > 0)
        {
            File F = new File(main.colSounds.get(s).SoundPath);
            if (F.exists())
                playSound(F);
            else if (F.getPath().startsWith("snd/"))
                playSound(assets, F.getPath());
        }
        else
        {
            if (AssetSounds[0] == null)
                initSounds();
            playSound(assets, AssetSounds[s.ordinal()]);
        }
    }

    public static void playSound(AssetManager assets, String name)
            throws Exception
    {
        if (!sndEnabled)
            return;
        AssetFileDescriptor afd = assets.openFd(name);
        try
        {
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                    afd.getLength());
            player.prepare();
            player.start();
        }
        catch (Exception ex)
        {
            Log.e("Error", "log.playSound", ex);
        }
    }

    public static Sounds getSoundByNumber(int Zaehler)
    {
        for (int i = 0; i < Sounds.values().length; i++)
        {
            if (Sounds.values()[i].ordinal() == Zaehler)
                return Sounds.values()[i];
        }
        return null;
    }

    public static void playSound(Context context, int Zaehler)
            throws Exception
    {
        MainActivity main = (MainActivity) context;
        if (main.colSounds.size() > 0)
        {
            if (Zaehler < -4)
                Zaehler = -4;
            else if (Zaehler > 5)
                Zaehler = 5;
            lib.Sounds Sound = null;
            if (Zaehler <= 0 && lib.AntwWasRichtig == false)
                Zaehler = Math.abs(Zaehler - 6);

            Sound = getSoundByNumber(Zaehler);

            File F = new File(main.colSounds.get(Sound).SoundPath);
            if (F.exists())
                playSound(F);
            else if (F.getPath().startsWith("snd/"))
                playSound(context.getAssets(), F.getPath());
        }
        else
        {
            AssetManager assets = context.getAssets();
            if (AssetSounds[0] == null)
                initSounds();
            if (Zaehler < -4)
                Zaehler = -4;
            else if (Zaehler > 5)
                Zaehler = 5;
            if (Zaehler > 0)
                playSound(assets, AssetSounds[Zaehler - 1]);
            else if (Zaehler <= 0)
                playSound(assets, AssetSounds[Math.abs(Zaehler - 5)]);
        }

    }

    public static void playSound(File F) throws Exception
    {
        if (!sndEnabled)
            return;
        try
        {
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(F.getPath());
            player.prepare();
            player.start();
        }
        catch (Exception ex)
        {
            Log.e("Error", "playSound File " + F.getPath(), ex);
        }
    }

    public static Drawable scaleImage(Context context, Drawable image,
                                      float scaleFactor)
    {

        if ((image == null) || !(image instanceof BitmapDrawable))
        {
            throw new RuntimeException("Not BitmapDrawable!");
        }

        Bitmap b = ((BitmapDrawable) image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap
                .createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(context.getResources(), bitmapResized);

        return image;

    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static Drawable getDefaultCheckBoxDrawable(Context context)
    {
        int resID = 0;

        if (Build.VERSION.SDK_INT <= 10)
        {
            // pre-Honeycomb has a different way of setting the CheckBox button
            // drawable
            resID = Resources.getSystem().getIdentifier("btn_check",
                    "drawable", "android");
        }
        else
        {
            // starting with Honeycomb, retrieve the theme-based indicator as
            // CheckBox button drawable
            TypedValue value = new TypedValue();
            context.getApplicationContext()
                    .getTheme()
                    .resolveAttribute(
                            android.R.attr.listChoiceIndicatorMultiple, value,
                            true);
            resID = value.resourceId;
        }
        if (Build.VERSION.SDK_INT < 22)
        {
            return context.getResources().getDrawable(resID);
        }
        else
        {
            return context.getResources().getDrawable(resID, null);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setBgCheckBox(CheckBox c, Drawable d)
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
        {
            c.setBackgroundDrawable(d);
        }
        else
        {
            c.setBackground(d);
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setBg(RelativeLayout l, ShapeDrawable rectShapeDrawable)
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
        {
            l.setBackgroundDrawable(rectShapeDrawable);
        }
        else
        {
            l.setBackground(rectShapeDrawable);
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setBgEditText(TextView e, Drawable drawable)
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
        {
            e.setBackgroundDrawable(drawable);
        }
        else
        {
            e.setBackground(drawable);
        }

    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    @SuppressLint("InlinedApi")
    public static void SelectFile(Activity context, Uri defaultURI) throws Exception
    {

        libLearn.gStatus = "Select File";
        Intent intent = new Intent();
        if (defaultURI != null)
        {
            intent.setData(defaultURI);
        }
        else
        {
            //intent.setType("file/*");
        }
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        String msg = context.getString(R.string.msgShowDocumentProvider);
        String key = "ShowAlwaysDocumentProvider";
        int ShowAlwaysDocumentProvider = prefs.getInt(key, 999);
        String checkBoxTitle = context.getString(R.string.msgRememberChoice);
        lib.YesNoCheckResult res = null;
        if (Build.VERSION.SDK_INT >= 19 && ShowAlwaysDocumentProvider == 999)
        {
            res = ShowMessageYesNoWithCheckbox(context, "", msg, checkBoxTitle, false);
            if (res.res == yesnoundefined.undefined) return;
        }

        if (Build.VERSION.SDK_INT < 19 || ShowAlwaysDocumentProvider == 0 ||
                (res != null && res.res == yesnoundefined.no))
        {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (res != null && res.checked)
            {
                prefs.edit().putInt(key, 0).commit();
            }

        }
        else
        {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            if (res != null && res.checked)
            {
                prefs.edit().putInt(key, -1).commit();
            }
        }
        intent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent chooser = Intent.createChooser(intent, "Open");
        if (intent.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivityForResult(chooser, SELECT_FILE);
        }
        else
        {
            intent.setData(null);
            context.startActivityForResult(chooser, SELECT_FILE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void removewfcListener(ViewTreeObserver observer,
                                         OnWindowFocusChangeListener wfcListener)
    {

        if (Build.VERSION.SDK_INT >= 18)
        {
            observer.removeOnWindowFocusChangeListener(wfcListener);
        }
    }

    public static void removeLayoutListener(ViewTreeObserver observer,
                                            OnGlobalLayoutListener listener)
    {
        if (Build.VERSION.SDK_INT < 16)
        {
            removeLayoutListenerPre16(
                    observer, listener);
        }
        else
        {
            removeLayoutListenerPost16(
                    observer, listener);
        }
    }

    @SuppressWarnings("deprecation")
    private static void removeLayoutListenerPre16(ViewTreeObserver observer,
                                                  OnGlobalLayoutListener listener)
    {
        observer.removeGlobalOnLayoutListener(listener);
    }

    @TargetApi(16)
    private static void removeLayoutListenerPost16(ViewTreeObserver observer,
                                                   OnGlobalLayoutListener listener)
    {
        observer.removeOnGlobalLayoutListener(listener);
    }

    public static String dumpUriMetaData(Activity context, Uri uri)
    {

        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor;
        String mimeType = null;
        try
        {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            mimeType = context.getContentResolver().getType(uri);
        }
        catch (Exception ex)
        {
            Log.e("DumpUri", "getContentResolver().query " + uri.toString(), ex);
            cursor = null;
        }
        try
        {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst())
            {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);
                String path = uri.getPath();
                path = URLDecoder.decode(path, "UTF-8");
                path = path.substring(path.lastIndexOf("/") + 1);
                int found = path.indexOf(displayName);
                if (found > -1 && (found + displayName.length() < path.length()))
                {
                    displayName += path.substring(found + displayName.length());
                }

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an
                // int can't be null in Java, the behavior is implementation-specific,
                // which is just a fancy term for "unpredictable".  So as
                // a rule, check if it's null before assigning to an int.  This will
                // happen often:  The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex))
                {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                }
                else
                {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);


                return displayName + ":" + size + ":" + mimeType;
            }
            else
            {
                MainActivity main = (MainActivity) context;
                if (!libString.IsNullOrEmpty(main.vok.getURIName()))
                {
                    return "/" + main.vok.getURIName();
                }
                else
                {
                    String p = uri.getPath();
                    if (!libString.IsNullOrEmpty(p))
                    {
                        p = p.substring(p.lastIndexOf("/") + 1);
                        return p;
                    }
                    else
                    {
                        return "";
                    }
                }
            }
        }
        catch (Exception ex)
        {
            lib.ShowException(context, ex);
        }
        finally
        {
            if (cursor != null) cursor.close();
        }
        return "";
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void getHitRect(View v, Rect rect)
    {
        rect.left = (int) (v.getLeft() + v.getTranslationX());
        rect.top = (int) (v.getTop() + v.getTranslationY());
        rect.right = rect.left + v.getWidth();
        rect.bottom = rect.top + v.getHeight();
        //rect.top -= v.dividerValue/2;
        //rect.bottom +=dividerValue/2;
    }

    @SuppressLint({"InlinedApi", "NewApi"})
    public static void CheckPermissions(Activity container, Uri uri, boolean blnShowMessage) throws Exception
    {
        try
        {
            int Flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            Flags = Flags | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            if (Build.VERSION.SDK_INT >= 19)
            {
                //Flags = Flags | FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
                container.getContentResolver().takePersistableUriPermission(uri, Flags);
            }


            //container.grantUriPermission("org.de.jmg.learn", uri , Flags);
        }
        catch (Exception ex)
        {
            try
            {
                Log.e("lib.GrantAllPermissions", ex.getMessage(), ex);
                int Flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                //container.grantUriPermission("org.de.jmg.learn", uri , Flags);
                if (Build.VERSION.SDK_INT >= 19)
                {
                    container.getContentResolver().takePersistableUriPermission(uri, Flags);
                }
            }
            catch (Exception ex2)
            {
                Log.e("lib.GrantAllPermissions", ex2.getMessage(), ex2);
                if (blnShowMessage)
                {
                    SharedPreferences prefs = container.getPreferences(Context.MODE_PRIVATE);
                    String key = "DontShowPersistableURIMessage";
                    int DontShowPersistableURIMessage = prefs.getInt(key, 999);
                    String CheckBoxTitle = container.getString(R.string.msgDontShowThisMessageAgain);
                    String msg = container.getString(R.string.msgNoPersistableUriPermissionCouldBeTaken);
                    String title = "";
                    if (DontShowPersistableURIMessage != -1)
                    {
                        DontShowPersistableURIMessage = ShowMessageWithCheckbox(container, title, msg, CheckBoxTitle) ? -1 : 0;
                        prefs.edit().putInt(key, DontShowPersistableURIMessage).commit();
                    }
                }
            }
            //if (force) lib.ShowException(container, ex);
            //throw new RuntimeException("CheckPermissions", ex);
        }
    }

    public static SpannableString getSpanableString(String txt) throws Exception
    {
        if (libString.IsNullOrEmpty(txt)) return new SpannableString("");
        final Pattern pattern = Pattern.compile("(?i)<a.*?</a>");
        final Pattern patternLI = Pattern.compile("(?i)<li>.*?<//li>", Pattern.DOTALL);
        Matcher matcherLI = patternLI.matcher(txt);
        if (txt.startsWith("{\\rtf1\\"))
        {
            // txt = Java2Html.convertToHtml(txt,
            // JavaSourceConversionOptions.getDefault());
            // return Html.fromHtml(txt);
            // return new SpannedString(stripRtf(txt));
            return new SpannableString(RichTextStripper.StripRichTextFormat(txt));
        }
        SpannableString span = null;
        if (txt.contains("<link://"))
        {
            ArrayList<String> urls = new ArrayList<String>();
            ArrayList<String> links = new ArrayList<String>();
            ArrayList<Integer> positions = new ArrayList<Integer>();
            int found = -1;
            while (txt.indexOf("<link://", found + 1) > -1)
            {
                found = txt.indexOf("<link://", found + 1);
                int Start = found + 8;
                int End = txt.indexOf("/>", Start);
                String repl = txt.substring(found, End + 2);
                if (End > 0)
                {
                    String Link = txt.substring(Start, End);
                    int LinkEnd = Link.indexOf(" ");
                    if (LinkEnd > -1)
                    {
                        String url = Link.substring(0, LinkEnd);
                        urls.add(url);
                        String linkText = Link.substring(LinkEnd + 1, Link.length());
                        links.add(linkText);
                        positions.add(found);
                        txt = txt.replace(repl, linkText);
                    }
                }
            }
            span = new SpannableString(txt);
            for (int i = 0; i < links.size(); i++)
            {
                int Start = positions.get(i);
                int End = Start + links.get(i).length();
                URLSpan spn = new URLSpan(urls.get(i));

                span.setSpan(spn, Start,
                        End, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        else
        {
            if (matcherLI.find())
            {
                int start = 0;
                do
                {
                    String LI = matcherLI.group();
                    LI = LI.replace("<//", "</");
                    SpannableString spnLI = new SpannableString(Html.fromHtml(LI));
                    spnLI.setSpan(new android.text.style.BulletSpan(), 0, spnLI.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (span == null || start == 0)
                    {
                        span = new SpannableString(txt.substring(start, matcherLI.start()));
                    }
                    else
                    {
                        span = new SpannableString(TextUtils.concat(span, txt.substring(start, matcherLI.start())));
                    }
                    start = matcherLI.end() + 1;
                    span = new SpannableString(TextUtils.concat(span, spnLI));
                }
                while (matcherLI.find());
                if (span != null) txt = span.toString();
            }
            Matcher matcher = pattern.matcher(txt);
            if (false && matcher.find())
            {
                int start = 0;
                boolean blnNewSpan = false;
                do
                {
                    String Anchor = matcher.group();
                    SpannableString spnAnchor = new SpannableString(Html.fromHtml(Anchor));
                    if (span == null)
                    {
                        span = new SpannableString(txt.substring(start, matcher.start()));
                        blnNewSpan = true;
                    }
                    else if (blnNewSpan)
                    {
                        span = new SpannableString(TextUtils.concat(span, txt.substring(start, matcher.start())));
                    }
                    start = matcher.end() + 1;
                    if (blnNewSpan)
                    {
                        span = new SpannableString(TextUtils.concat(span, spnAnchor));
                    }
                    else
                    {
                        span = new SpannableString(TextUtils.replace
                                (span, new String[]{Anchor}, new CharSequence[]{spnAnchor}));
                    }
                }
                while (matcher.find());

                if (span != null) txt = span.toString();
            }
        }
        if ((txt.contains("http://") || txt.contains("https://")))
        {

            int found = 0;
            int found1 = 0;
            int found2 = 0;
            //final String reg_exUrl = "\\b(?:(?:https?|ftp|file)://|www\\.|ftp\\.)(?:\\([-A-Z0-9+&@#/%=~_|$?!:,.]*\\)|[-A-Z0-9+&@#/%=~_|$?!:,.])*(?:\\([-A-Z0-9+&@#/%=~_|$?!:,.]*\\)|[A-Z0-9+&@#/%=~_|$])";
            //Pattern p = Pattern.compile(reg_exUrl);  // insert your pattern here
            //Matcher m = p.matcher(txt);
            found1 = txt.indexOf("http://");
            found2 = txt.indexOf("https://");
            if ((found1 > -1 && found1 < found2) || found2 == -1)
            {
                found = found1;
            }
            else
            {
                found = found2;
            }
            if (span == null) span = new SpannableString(txt);
            txt = txt.replace("\r", " ");
            txt = txt.replace("\n", " ");
            txt += " ";
            while (found != -1)
            {
                int start = found;
                int end = txt.indexOf(" ", found + 1);
                int endhyphen = txt.indexOf("\"", found + 1);
                int endbracked = txt.indexOf(")", found + 1);
                if (end == -1 || (endhyphen > -1 && endhyphen < end)) end = endhyphen;
                if (end == -1 || (endbracked > -1 && endbracked < end)) end = endbracked;
                if (end != -1)
                {
                    URLSpan urls[] = span.getSpans(start, end, URLSpan.class);
                    if (urls == null || urls.length == 0)
                    {
                        String url = txt.substring(start, end);
                        span.setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    found1 = txt.indexOf("http://", end + 1);
                    found2 = txt.indexOf("https://", end + 1);
                    if ((found1 > -1 && found1 < found2) || found2 == -1)
                    {
                        found = found1;
                    }
                    else
                    {
                        found = found2;
                    }
                }
                else
                {
                    found = end;
                }
            }
			/*
			URLSpan[] urls = span.getSpans(0, txt.length(), URLSpan.class);
	        for(URLSpan urlspan : urls) {
	            makeLinkClickable(span, urlspan);
	        }
	        */
            return span;

			/*
			Pattern pattern = Pattern.compile(
		            "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" +
		            "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" +
		            "|mil|biz|info|mobi|name|aero|jobs|museum" +
		            "|travel|[a-z]{2}))_txtMeaning1.setMovementMethod(LinkMovementMethod.getInstance());
		(:[\\d]{1,5})?" +
		            "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" +
		            "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
		            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" +
		            "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
		            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" +
		            "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

		        Matcher matcher = pattern.matcher(txt);
		        SpannableString span = new SpannableString(txt);

		        while (matcher.find()) {
		        	String url = txt.substring(matcher.start(),matcher.end());
					span.setSpan(new URLSpan(url), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		        }
		        */
        }
        if (span == null) return new SpannableString(txt);
        else return span;
    }

    public static Bitmap downloadpicture(final String Url)
    {
        final CountDownLatch l = new CountDownLatch(1);
        bmpimg = null;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                InputStream in = null;
                try
                {
                    URL url = new URL(Url);
                    URLConnection urlConn = url.openConnection();
                    HttpURLConnection httpConn = (HttpURLConnection) urlConn;
                    httpConn.connect();
                    in = httpConn.getInputStream();
                    bmpimg = BitmapFactory.decodeStream(in);
                    in.close();
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    l.countDown();
                }
            }
        }).start();
        try
        {
            l.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }


        return bmpimg;
    }

    protected static void makeLinkClickable(SpannableString strBuilder, final URLSpan span)
    {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan()
        {
            public void onClick(View view)
            {
                Log.d("Link", span.getURL());
                // Do something with span.getURL() to handle the link click...
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public static int getUIMode(Context context)
    {
        if (Build.VERSION.SDK_INT >= 8)
        {
            UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
            return uiModeManager.getCurrentModeType();
        }
        else
        {
            return 0;
        }
    }

    public static String toLanguageTag(Locale l)
    {
        String localeId = l.toString();//= MessageFormat.format("{0}-{1}",
        //        l.getLanguage(),
        //        l.getCountry());
        return localeId;
    }

    public static Locale forLanguageTag(String s)
    {
        /*
        StringTokenizer tempStringTokenizer = new StringTokenizer(s,"-");
        String l = null;
        String c = null;
        if(tempStringTokenizer.hasMoreTokens())
            l = tempStringTokenizer.nextToken();
        if(tempStringTokenizer.hasMoreTokens())
            c = tempStringTokenizer.nextToken();
        if (l!=null && c!=null)return new Locale(l,c);
        if (l!=null) return new Locale(l);
        */
        s = s.replace("-", "_");
        if (s.length() > 1 && s.endsWith("_")) s = s.substring(0, s.length() - 1);
        return new Locale(s);
        //return null;
    }

    public static String ReplaceLinks(String txt)
    {
        if (txt.contains("<link://"))
        {
            int found = -1;
            while (txt.indexOf("<link://", found + 1) > -1)
            {
                found = txt.indexOf("<link://", found + 1);
                int Start = found + 8;
                int End = txt.indexOf("/>", Start);
                String repl = txt.substring(found, End + 2);
                if (End > 0)
                {
                    String Link = txt.substring(Start, End);
                    int LinkEnd = Link.indexOf(" ");
                    if (LinkEnd > -1)
                    {
                        String url = Link.substring(0, LinkEnd);
                        String linkText = Link.substring(LinkEnd + 1, Link.length());
                        txt = txt.replace(repl, "<a href=\"" + url + "\">" + linkText + "</a>");
                    }
                }
            }
        }
        return txt;
    }

    public static void setLocale(Context c, String l) throws Exception
    {

        Resources res = c.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(l);
        res.updateConfiguration(conf, dm);

    }

    public static yesnoundefined AcceptPrivacyPolicy(Context context) throws IOException
    {
        InputStream is
        if ((context.Locale.getDefault().getDisplayLanguage() == Locale.GERMAN) || (Locale.getDefault().getDisplayLanguage() == Locale.GERMANY))
        {
            is = context.getAssets().open("PrivacyPolicy");
        }
        else
        {
            is = context.getAssets().open("PrivacyPolicy");
        }
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String strPrivacyPolicy = s.hasNext() ? s.next() : "";
        s.close();
        is.close();
        lib.yesnoundefined res2 = (lib.ShowMessageYesNo(context,
                strPrivacyPolicy,
                context.getString(R.string.PrivacyPolicy),
                true));
        return res2;
    }




    public enum yesnoundefined
    {
        yes, no, undefined
    }

    public enum okcancelundefined
    {
        ok, cancel, undefined
    }

    public enum Sounds
    {
        Richtig0, Richtig1, Richtig2, Richtig3, Richtig4, Richtig5, Falsch0, Falsch1, Falsch2, Falsch3, Falsch4, Falsch5, Beep
    }

    public static class YesNoCheckResult
    {

        public yesnoundefined res;
        public boolean checked;

        public YesNoCheckResult(yesnoundefined res, boolean checked)
        {
            this.res = res;
            this.checked = checked;
        }
    }

    public static class OkCancelStringResult
    {

        public okcancelundefined res;
        public String input;

        public OkCancelStringResult(okcancelundefined res, String input)
        {
            this.res = res;
            this.input = input;
        }
    }

    public static class libString
    {
        public static boolean IsNullOrEmpty(String s)
        {
            if (s == null || s == "" || s.length() == 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        public static int InStr(String s, String Search)
        {
            int Start = 1;
            return InStr(Start, s, Search);
        }

        public static int InStr(int Start, String s, String Search)
        {
            return s.indexOf(Search, Start - 1) + 1;
        }

        public static String Chr(int Code)
        {
            char c[] = {(char) Code};
            return new String(c);
        }

        public static String Left(String s, int length)
        {
            return s.substring(0, length);
        }

        public static int Len(String s)
        {
            return s.length();
        }

        public static String Right(String wort, int i)
        {

            return wort.substring(wort.length() - i);
        }

    }

    public static class MessageException extends RuntimeException
    {

    }

    private static class listener implements DialogInterface.OnClickListener
    {

        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            removeDlg(dialog);
        }
    }

    private static class MyLauncher extends Thread
    {
        @Override
        /**
         * Sleep for 2 seconds as you can also change SLEEP_TIME 2 to any.
         */
        public void run()
        {
            try
            {
                // Sleeping
                Thread.sleep(SLEEP_TIME * 1000);
                latch.countDown();
            }
            catch (Exception e)
            {
                Log.e(TAG, e.getMessage());
            }
            // do something you want to do
            // And your code will be executed after 2 second
        }
    }

    public static class PrefsOnMultiChoiceClickListener implements OnMultiChoiceClickListener
    {

        public SharedPreferences prefs;
        public String key;

        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked)
        {

            prefs.edit().putInt(key, isChecked ? -1 : 0).commit();
        }
    }

}
