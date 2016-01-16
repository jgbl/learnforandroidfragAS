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
package org.liberty.android.fantastischmemo.downloader.quizlet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import org.de.jmg.learn.MainActivity;
import org.de.jmg.learn.R;
import org.de.jmg.lib.lib;

//import roboguice.RoboGuice;
//import roboguice.activity.RoboActionBarActivity;

public class LoginQuizletActivity
{//extends AppCompatActivity {
    static {
        //RoboGuice.setUseAnnotationDatabases(false);
    }
    public String AccessToken;
    public boolean blnUpload;
    public QuizletOAuth2AccessCodeRetrievalFragment _dlg;
    private MainActivity _main;
    /*
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
    @Override
    public void onActivityReenter(int i, Intent in)
    {
        super.onActivityReenter(i, in);
    }
    @Override
    public void onResume()
    {
        super.onResume();
    }
    @Override
     public void onStart()
    {
        super.onStart();
    }
    */
    //@Override
    public void onNewIntent(Intent intent)
    {
        //super.onNewIntent(intent);
        if (Intent.ACTION_VIEW.equals(intent.getAction()) && _dlg != null)
        {
            Uri uri = intent.getData();
            getDlg().processCallbackUrl(uri.toString());
            //finish();
        }
    }

    private QuizletOAuth2AccessCodeRetrievalFragment getDlg()
    {
        if (_dlg == null)
        {
            try
            {
                _dlg = new QuizletOAuth2AccessCodeRetrievalFragment();
                _dlg.setAuthCodeReceiveListener(new QuizletOAuth2AccessCodeRetrievalFragment.AuthCodeReceiveListener()
                {
                    @Override
                    public void onAuthCodeReceived(String... codes)
                    {
                        if (codes != null && codes.length > 0)
                        {
                            AccessToken = codes[0];
                            new GetAccessTokenTask().execute(codes);
                        }
                        else
                        {
                            //setResult(Activity.RESULT_CANCELED);
                            dofinish();
                        }

                    }

                    @Override
                    public void onAuthCodeError(String error)
                    {
                        lib.ShowMessage(_main, error, "");
                        //setResult(Activity.RESULT_CANCELED);
                        dofinish();
                    }

                    @Override
                    public void onCancelled()
                    {
                        //setResult(Activity.RESULT_CANCELED);
                        dofinish();
                    }
                });

                //dlg.show(this.getSupportFragmentManager(), "OauthAccessCodeRetrievalFragment");
            }
            catch (Exception ex)
            {
                AlertDialog.Builder A = new AlertDialog.Builder(_main);
                A.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                A.setMessage(ex.getMessage());
                A.setTitle(_main.getString(R.string.Error));
                AlertDialog dlg = A.create();
                dlg.show();
            }

        }
        return _dlg;
    }

    public void doLogin(MainActivity main, Intent intent)
    {
        _main = main;
        blnUpload = intent.getBooleanExtra("upload",false);
        String url = getDlg().getLoginUrl();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        _main.startActivity(i);
    }
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            getDlg().processCallbackUrl(uri.toString());
            finish();
        }
        else
        {
            blnUpload = intent.getBooleanExtra("upload",false);
            String url = getDlg().getLoginUrl();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }


    }
    */
    private class GetAccessTokenTask extends AsyncTask<String, Void, String[]> {

        private ProgressDialog progressDialog;

        private Exception backgroundTaskException = null;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(_main);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(_main.getString(R.string.loading));
            progressDialog.setMessage(_main.getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        public String[] doInBackground(String... requests) {
            try {
                return org.liberty.android.fantastischmemo.downloader.quizlet.lib.getAccessTokens(requests);
            } catch (Exception e) {
                backgroundTaskException = e;
                return null;
            }
        }

        @Override
        public void onPostExecute(String[] accessTokens){
            progressDialog.dismiss();

            if (backgroundTaskException != null) {
                lib.ShowException(_main, backgroundTaskException);
                //setResult(Activity.RESULT_CANCELED);
                dofinish();
            }

            if (accessTokens!=null && accessTokens.length == 2)
            {
                Intent intent = new Intent();
                intent.putExtra("AuthCode", AccessToken);
                intent.putExtra("user", accessTokens[1]);
                intent.putExtra("accessToken", accessTokens[0]);
                intent.putExtra("upload",blnUpload);
                //setResult(Activity.RESULT_OK, intent);
                _main.onActivityResult(MainActivity.LOGINQUIZLETINTENT, Activity.RESULT_OK,intent);
                dofinish();
            }
            else
            {
                //setResult(Activity.RESULT_CANCELED);
                dofinish();
            }
        }
    }
    private void dofinish()
    {
        _dlg = null;
        //finish();
    }

}