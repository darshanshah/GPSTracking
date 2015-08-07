package com.android.gps_status;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class act2 extends Activity {

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        Button b1 = (Button) findViewById(R.id.abt_ok);
        b1.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) 
            {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        });

    }
}