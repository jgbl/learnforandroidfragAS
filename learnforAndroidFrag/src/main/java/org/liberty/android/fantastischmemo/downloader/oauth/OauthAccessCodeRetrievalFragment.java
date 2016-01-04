/*
Copyright (C) 2012 Haowen Ning
Modified 2015 J.M.Goebel

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package org.liberty.android.fantastischmemo.downloader.oauth;

import java.io.IOException;


//import roboguice.RoboGuice;
//import roboguice.fragment.RoboDialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;


import org.de.jmg.learn.R;
import org.de.jmg.lib.lib;

public abstract class OauthAccessCodeRetrievalFragment extends DialogFragment
{
    static {
        //RoboGuice.setUseAnnotationDatabases(false);
    }
    private Activity mActivity;

    private AuthCodeReceiveListener authCodeReceiveListener = null;

    private WebView webview;

    private View loadingText;

    private View progressDialog;

    private LinearLayout rootView;

    public View v;

    public OauthAccessCodeRetrievalFragment() { }

    // Return the URL that show the web login
    protected abstract String getLoginUrl();

    // The token request for oauth 1, retrieve the oauth token
    // and the token secret
    protected abstract void requestToken() throws IOException;

    // Process the callback for oauth 1 and 2
    // For Oauth 2, the access token are retrieve from here.
    // return true if the url is handled
    protected abstract boolean processCallbackUrl(String url);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        authCodeReceiveListener.onCancelled();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            v = inflater.inflate(R.layout.oauth_login_layout, container, false);
            webview = (WebView)v.findViewById(R.id.login_page);
            loadingText = v.findViewById(R.id.auth_page_load_text);
            progressDialog = v.findViewById(R.id.auth_page_load_progress);
            rootView = (LinearLayout)v.findViewById(R.id.ll);

            // We have to set up the dialog's webview size manually or the webview will be zero size.
            // This should be a bug of Android.
            Rect displayRectangle = new Rect();
            Window window = mActivity.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

            rootView.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
            rootView.setMinimumHeight((int)(displayRectangle.height() * 0.8f));

            RequestTokenTask task = new RequestTokenTask();
            task.execute((Void)null);

        }
        catch (Exception ex)
        {
            AlertDialog.Builder A = new AlertDialog.Builder(mActivity);
            A.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            A.setMessage(ex.getMessage());
            A.setTitle(mActivity.getString(R.string.Error));
            AlertDialog dlg = A.create();
            dlg.show();
            v = super.onCreateView(inflater,container,savedInstanceState);
            //lib.ShowMessage(mActivity, ex.getMessage(), "Error");
        }
        return v;
    }


    // For subclass to call method in the authCodeReceiveListener
    protected AuthCodeReceiveListener getAuthCodeReceiveListener() {
        return authCodeReceiveListener;
    }

    private class RequestTokenTask extends AsyncTask<Void, Void, Void> {

    	private Exception backgroundTaskException;

        @Override
        protected void onPreExecute() {
            loadingText.setVisibility(View.VISIBLE);
            progressDialog.setVisibility(View.VISIBLE);
            webview.setVisibility(View.INVISIBLE);
        }

        @Override
		protected Void doInBackground(Void... params) {
        	try {
        		requestToken();
        	} catch (Exception e) {
        		backgroundTaskException = e;
        	}
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        	if (backgroundTaskException != null) {
        		lib.ShowException(mActivity, backgroundTaskException);
        	}
            webview.getSettings().setJavaScriptEnabled(true);

            webview.loadUrl(getLoginUrl());

            // This is workaround to show input on some android version.
            webview.requestFocus(View.FOCUS_DOWN);
            webview.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_UP:
                            if (!v.hasFocus()) {
                                v.requestFocus();
                            }
                            break;
                    }
                    return false;
                }
            });


            webview.setWebViewClient(new WebViewClient() {

                // Make sure the callback is used only once
                // Sometimes, the website will call this method
                // twice.
                private boolean authenticated = false;

                @Override
                public void onPageFinished(WebView view, String url)  {
                    loadingText.setVisibility(View.GONE);
                    progressDialog.setVisibility(View.GONE);
                    webview.setVisibility(View.VISIBLE);
                    if (authenticated) {
                        return;
                    }
                    if (processCallbackUrl(url)) {
                        authenticated = true;
                        dismiss();
                    }
                }
            });
        }

    }

    public void setAuthCodeReceiveListener(AuthCodeReceiveListener listener) {
        authCodeReceiveListener = listener;
    }

    public static interface AuthCodeReceiveListener {
        // the auth code received are different for oauth1 an oauth2
        // so this mehtod just has a list of possible codes
        void onAuthCodeReceived(String... codes);

        void onAuthCodeError(String error);
        void onCancelled();
    }
}
