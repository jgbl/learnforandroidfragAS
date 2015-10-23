package org.liberty.android.fantastischmemo.downloader.quizlet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;

import org.de.jmg.lib.lib;
import org.liberty.android.fantastischmemo.downloader.oauth.OauthAccessCodeRetrievalFragment;

import roboguice.RoboGuice;
import roboguice.activity.RoboActionBarActivity;

public class LoginQuizletActivity extends RoboActionBarActivity {
    static {
        RoboGuice.setUseAnnotationDatabases(false);
    }
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QuizletOAuth2AccessCodeRetrievalFragment dlg = new QuizletOAuth2AccessCodeRetrievalFragment();
        dlg.setAuthCodeReceiveListener(new OauthAccessCodeRetrievalFragment.AuthCodeReceiveListener() {
            @Override
            public void onAuthCodeReceived(String... codes) {
                lib.ShowMessage(LoginQuizletActivity.this, codes[0], "AuthCode");
            }

            @Override
            public void onAuthCodeError(String error) {

            }

            @Override
            public void onCancelled() {

            }
        });

        dlg.show(this.getSupportFragmentManager(), "OauthAccessCodeRetrievalFragment");
    }
}