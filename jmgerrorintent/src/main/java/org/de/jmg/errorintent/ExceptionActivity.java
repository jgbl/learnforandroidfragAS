package org.de.jmg.errorintent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



/**
 * Created by hmnatalie on 25.10.15.
 */
public class ExceptionActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception);
        Bundle extras = getIntent().getExtras();
        String msg = extras.getString("message");
        TextView txtMessage = (TextView)findViewById(R.id.txtMessage);
        txtMessage.setText(msg);
        Button btnOK = (Button)findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}