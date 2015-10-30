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
import android.util.JsonReader;
import android.util.Log;

import org.de.jmg.learn.vok.Vokabel;
import org.de.jmg.learn.vok.typVok;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import br.com.thinkti.android.filechooserfrag.Data;
//import roboguice.util.Ln;

@TargetApi(11)
public class lib
{
    public final static  String QUIZLET_API_ENDPOINT = "https://api.quizlet.com/2.0";
    public final static String QUIZLET_CLIENT_ID = new String(Base64.decode(Data.QuizletClientID, Base64.DEFAULT));
    public static final String QUIZLET_CLIENT_SECRET = new String(Base64.decode(Data.SecretKey, Base64.DEFAULT));;

    /**
     * Make API call to Quizlet server with oauth
     *
     * @param url
     *            API call endpoint
     * @param authToken
     *            oauth auth token
     * @return Response of API call
     * @throws IOException
     *             If http response code is not 2xx
     */
    public static InputStream makeApiCall(URL url, String authToken) throws IOException {
        HttpsURLConnection conn = null;
        try {
            conn = (HttpsURLConnection) url.openConnection();
            if (authToken != null) {
                conn.addRequestProperty("Authorization", "Bearer " + authToken);
            }

            InputStream response = conn
                    .getInputStream();
            if (conn.getResponseCode() / 100 >= 3) {
                response = conn.getErrorStream();
            }
            return response;
        } finally {
            //conn.disconnect();
        }
    }

    /**
     * Fetch cardsets list from Quizlet     * @throws JSONException

     *
     * @param userId
     *            user name
     * @param authToken
     *            oauth token
     * @return cardsets list
     * @throws IOException
     *             IOException If http response code is not 2xx
     *             If the response is invalid JSON
     */
    public static InputStream getUserPrivateCardsets(String userId,
                                                         String authToken) throws IOException
    {
        URL url = new URL(QUIZLET_API_ENDPOINT + "/users/" + userId
                + "/sets");
        InputStream response = makeApiCall(url, authToken);
        return response;
    }

    public static boolean verifyAccessToken(final String[] accessTokens)
            throws Exception
    {
        final String TAG = "verfyAccessToken";
        String token = accessTokens[0];
        String userId = accessTokens[1];
        try {
            URL url1 = new URL(QUIZLET_API_ENDPOINT + "/users/" + userId);
            HttpsURLConnection conn = (HttpsURLConnection) url1
                    .openConnection();
            conn.addRequestProperty("Authorization",
                    "Bearer " + String.format(token));

            JsonReader s = new JsonReader(new InputStreamReader((conn.getInputStream()),"UTF-8"));
            s.beginObject();
            while (s.hasNext())
            {
                String name = s.nextName();
                if ("error".equals(name))
                {
                    String error = s.nextString();
                    Log.e(TAG, "Token validation error: " + error);
                    return false;
                }
                else
                {
                    s.skipValue();
                }
            }
            s.endObject();
            s.close();


        } catch (Exception e) {
            Log.i(TAG, "The saved access token is invalid", e);
            return false;
        }
        return true;
    }

    // The string array returns access token and user_id
    public static String[] getAccessTokens(final String[] requests)
            throws Exception {
        final String TAG = "getAccesTokens";
        String code = requests[0];
        String clientIdAndSecret = QUIZLET_CLIENT_ID + ":"
                + QUIZLET_CLIENT_SECRET;
        String encodedClientIdAndSecret = Base64.encodeToString(
                clientIdAndSecret.getBytes(), 0);
        URL url1 = new URL("https://api.quizlet.com/oauth/token");
        HttpsURLConnection conn = (HttpsURLConnection) url1.openConnection();
        conn.addRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8");

        // Add the Basic Authorization item
        conn.addRequestProperty("Authorization", "Basic " + encodedClientIdAndSecret);

        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        String payload = String.format("grant_type=%s&code=%s&redirect_uri=%s",
                URLEncoder.encode("authorization_code", "UTF-8"),
                URLEncoder.encode(code, "UTF-8"),
                URLEncoder.encode(Data.RedirectURI, "UTF-8"));
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(payload);
        out.close();

        if (conn.getResponseCode() / 100 >= 3) {
            Log.e(TAG, "Http response code: " + conn.getResponseCode() + " response message: " + conn.getResponseMessage());
            JsonReader r = new JsonReader(new InputStreamReader(conn.getErrorStream(),"UTF-8"));
            String error = "";
            r.beginObject();
            while (r.hasNext())
            {
                error += r.nextName() + r.nextString() + "\r\n";
            }
            r.endObject();
            r.close();
            Log.e(TAG,"Error response for: " + url1 + " is "
                    + error);
            throw new IOException("Response code: " + conn.getResponseCode());
        }

        JsonReader s = new JsonReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
        try {
            String accessToken = null;
            String userId = null;
            s.beginObject();
            while (s.hasNext())
            {
                String name = s.nextName();
                if (name.equals("access_token"))
                {
                    accessToken = s.nextString();
                }
                else if(name.equals("user_id"))
                {
                    userId = s.nextString();
                }
                else
                {
                    s.skipValue();
                }
            }
            s.endObject();
            s.close();
            return new String[] { accessToken, userId };
        }
        catch (Exception e) {
            // Throw out JSON exception. it is unlikely to happen
            throw new RuntimeException(e);
        }
        finally {
            conn.disconnect();
        }
    }



    public static String uploadToQuizlet(Vokabel vok, String authToken, String userId, String description) throws IOException {
        // First read card because if it failed we don't even bother uploading.

        // Following doing upload
        StringBuilder data = new StringBuilder();
        data.append(String.format("whitespace=%s",
                URLEncoder.encode("1", "UTF-8")));
        data.append(String.format("&title=%s",
                URLEncoder.encode(org.de.jmg.lib.lib.
                        getFilenameWithoutExtension(new File(vok.getFileName()).getName()), "UTF-8")));
        data.append(String.format("&description=%s",
                URLEncoder.encode(description, "UTF-8"), "UTF-8"));
        // Get cards from cardList
        for (int i = 1; i < vok.getVokabeln().size(); i++) {
            typVok c = vok.getVokabeln().get(i);
            data.append(String.format("&terms[]=%s",
                    URLEncoder.encode(c.Wort
                            + (org.de.jmg.lib.lib.libString.IsNullOrEmpty(c.Kom)?"":"\n" + c.Kom), "UTF-8")));
            String Meaning;
            if (org.de.jmg.lib.lib.libString.IsNullOrEmpty(c.Bed2))
            {
                Meaning = c.Bed1;
            }
            else
            {
                Meaning = "1. " + c.Bed1 + "\n2. " + c.Bed2;
            }
            if (!org.de.jmg.lib.lib.libString.IsNullOrEmpty(c.Bed3))
            {
                Meaning += "\n3. " + c.Bed3;
            }
            data.append(String.format("&definitions[]=%s",
                    URLEncoder.encode(Meaning, "UTF-8")));
        }

        data.append(String.format("&lang_terms=%s",
                URLEncoder.encode(vok.getLangWord().getLanguage(), "UTF-8")));
        data.append(String.format("&lang_definitions=%s",
                URLEncoder.encode(vok.getLangMeaning().getLanguage(), "UTF-8")));
        data.append(String.format("&allow_discussion=%s",
                URLEncoder.encode("true", "UTF-8")));

        URL url;
        if (!org.de.jmg.lib.lib.libString.IsNullOrEmpty(userId))
        {
            //url = new URL(QUIZLET_API_ENDPOINT + "/users/" + userId
                    //+ "/sets");
            url = new URL(QUIZLET_API_ENDPOINT + "/sets");
        }
        else
        {
            url = new URL(QUIZLET_API_ENDPOINT + "/sets");
        }

        return makePostApiCall(url, data.toString(), authToken);
    }

    private static String makePostApiCall(URL url, String content, String authToken)
            throws IOException {
        HttpsURLConnection conn = null;
        OutputStreamWriter writer = null;
        String res = "";
        try {
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Authorization", "Bearer " + authToken);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(content);
            writer.close();

            if (conn.getResponseCode() / 100 >= 3) {
                Log.v("makePostApiCall","Post content is: " + content);
                String error = "";
                try {
                    JsonReader r = new JsonReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                    r.beginObject();
                    while (r.hasNext()) {
                        error += r.nextName() + ": " + r.nextString() + "\r\n";
                    }
                    r.endObject();
                    r.close();
                }
                catch (Throwable eex)
                {

                }
                Log.v("makePostApiCall", "Error string is: "
                        + error );
                res = error;
                throw new IOException("Response code: "
                        + conn.getResponseCode() + " URL is: " + url + " \nError: " + error);
            }
            else
            {
                JsonReader r = new JsonReader(new InputStreamReader(conn.getInputStream()));
                r.beginObject();
                while (r.hasNext())
                {
                    try
                    {
                        res += r.nextName() + ": " + r.nextString() + "\n";
                    }
                    catch (Exception ex)
                    {
                        r.skipValue();
                    }
                }
                return res;
            }
        } finally {
            conn.disconnect();
            //return res;
        }
    }

}
