package org.liberty.android.fantastischmemo.downloader.quizlet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
                if(codes.length>1)
                {
                    Intent intent = new Intent();
                    intent.putExtra("AuthCode", codes[0]);
                    intent.putExtra("user", codes[1]);
                    setResult(Activity.RESULT_OK, intent);
                }
                else
                {
                    setResult(Activity.RESULT_CANCELED);
                }
                finish();
            }

            @Override
            public void onAuthCodeError(String error) {
                lib.ShowMessage(LoginQuizletActivity.this,error,"");
                setResult(Activity.RESULT_CANCELED);
                finish();
            }

            @Override
            public void onCancelled() {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        dlg.show(this.getSupportFragmentManager(), "OauthAccessCodeRetrievalFragment");
    }
}