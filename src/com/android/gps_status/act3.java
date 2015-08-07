package com.android.gps_status;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class act3 extends Activity
{
	Button b1,b2;
	EditText t1;
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        
        b1 = (Button)findViewById(R.id.button1);
        b2 = (Button)findViewById(R.id.button2);
        t1 = (EditText)findViewById(R.id.editText1);
        b1.setEnabled(false);
        //Listener
        b2.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) 
            {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        });
        t1.addTextChangedListener(new TextWatcher() 
	    { 
            public void beforeTextChanged(CharSequence s, int start, int count, int after) 
            { 
            	if((after>0))
        		{
        			b1.setEnabled(true);
        		}
            	else
            	{
            		if(start == 0)
            		b1.setEnabled(false);
            	}
            } 
            public void onTextChanged(CharSequence s, int start, int before, int count) 
            { 
            }
			
			public void afterTextChanged(Editable s) 
			{
				// TODO Auto-generated method stub
				
			}
	    }); 

        b1.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) 
            {
	            	setspinner();
	                Intent intent = new Intent();
	                setResult(RESULT_OK, intent);
	                finish();
            }

        });
        
        

    }
    private void setspinner()
    {
    	final String MY_DATABASE_NAME = "GPSDB";
        SQLiteDatabase myDB = null;
        try 
        {
        	myDB =  this.openOrCreateDatabase(MY_DATABASE_NAME, 0, null);
            myDB.execSQL("INSERT INTO tblids (IDs,default_id) Values ('"+t1.getText().toString()+"','F')");
        }
        catch(Exception ex)
        {
        	MessageBox("This Tracking ID is already in Database, Enter new One.");
        }
    }
    private void MessageBox(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}