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
package org.liberty.android.fantastischmemo.downloader.quizlet;

import android.annotation.TargetApi;
import android.util.Base64;
import android.util.Log;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import br.com.thinkti.android.filechooserfrag.Data;
//import roboguice.util.Ln;
@TargetApi(11)
public final class QuizletOAuth2AccessCodeRetrievalFragment
{

    public final static int fragID = 5;


    public QuizletOAuth2AccessCodeRetrievalFragment() { }


    //@Override
    protected void requestToken() throws IOException {
        // Do nothing.
    }

    //@Override
    String getLoginUrl() {
        final String TAG = "getLoginUrl";
        try {
            String uri = String
                .format("https://quizlet.com/authorize/?response_type=%s&client_id=%s&scope=%s&state=%s&redirect_uri=%s",
                    URLEncoder.encode("code", "UTF-8"), URLEncoder
                    .encode(lib.QUIZLET_CLIENT_ID, "UTF-8"),
                    URLEncoder.encode("read write_set", "UTF-8"),
                    URLEncoder.encode("login", "UTF-8"),
                    URLEncoder.encode(Data.RedirectURI,
                        "UTF-8"));
            Log.i(TAG,"Oauth request uri is " + uri);
            return uri;
        } catch (UnsupportedEncodingException e) {
            // This is unlikely to happen
            Log.e(TAG, "The URL encodeing UTF-8 is not supported ",e);
            return null;
        }
    }

    //@Override
    boolean processCallbackUrl(String url) {
        final String TAG = "ProcessCallbackUrl";
        Log.i(TAG,"Callback url is " + url);

        if (!url.startsWith(Data.RedirectURI.replace("/","///"))) {
            return false;
        }

        int index = url.indexOf("code=");
        // If there is access token
        if (index != -1) {
            // Move index through "code="
            index += 5;
            String accessToken = url.substring(index);
            getAuthCodeReceiveListener().onAuthCodeReceived(accessToken);
            return true;
        }

        index = url.indexOf("error=");
        if (index != -1) {
            // Move index through "error="
            index += 6;
            String errorString = url.substring(index);
            getAuthCodeReceiveListener().onAuthCodeError(errorString);
            return true;
        }
        return false;
    }

    void setAuthCodeReceiveListener(AuthCodeReceiveListener listener) {
        authCodeReceiveListener = listener;
    }

    AuthCodeReceiveListener authCodeReceiveListener;
    private AuthCodeReceiveListener getAuthCodeReceiveListener() {
        return authCodeReceiveListener;
    }

    public static interface AuthCodeReceiveListener {
        // the auth code received are different for oauth1 an oauth2
        // so this mehtod just has a list of possible codes
        void onAuthCodeReceived(String... codes);

        void onAuthCodeError(String error);
        void onCancelled();
    }

}
