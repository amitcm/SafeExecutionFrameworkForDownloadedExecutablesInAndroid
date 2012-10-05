package com.sasaraf.www.ControlActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class ControlActivityActivity extends Activity {
	/** Called when the activity is first created. */

	String CONTROL_ACT_SHARED_PREF = "ControlActSharedPref";
	String RADIO_OPTION_STATE = "RadioOptionState";
	final int DEFAULT_RADIO_VALUE = 0;
	final int ON_RADIO_VALUE = 1;
	final int OFF_RADIO_VALUE = 2;
	RadioButton rButton_Off;
	RadioButton rButton_On;
	int current_radioOptionState;
	byte[] buffer;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//SharedPreferences spref = getSharedPreferences(CONTROL_ACT_SHARED_PREF, MODE_PRIVATE);

		/*
		SharedPreferences spref = getSharedPreferences(CONTROL_ACT_SHARED_PREF, MODE_WORLD_READABLE);
		int spref_radioOptionState = spref.getInt(RADIO_OPTION_STATE, DEFAULT_RADIO_VALUE);

		if (spref_radioOptionState == DEFAULT_RADIO_VALUE)
		{
			// First run since installing the app. Set default value to ON_RADIO_VALUE.
			SharedPreferences.Editor spref_edit = spref.edit();
			spref_edit.putInt(RADIO_OPTION_STATE, ON_RADIO_VALUE);
			spref_radioOptionState = ON_RADIO_VALUE;
			spref_edit.commit();        	
		}

		 */

		readFile();

		rButton_On = (RadioButton)findViewById(R.id.ctrl_act_rdo_On);
		rButton_Off = (RadioButton)findViewById(R.id.ctrl_act_rdo_Off);

		switch (buffer[0])
		{

		case  ON_RADIO_VALUE:
		case DEFAULT_RADIO_VALUE:	
			rButton_On.setChecked(true);
			rButton_Off.setChecked(false);
			// spref_radioOptionState = ON_RADIO_VALUE;
			break;

		case OFF_RADIO_VALUE:
			rButton_Off.setChecked(true);
			rButton_On.setChecked(false);
			//spref_radioOptionState = OFF_RADIO_VALUE;
			break;
		}


		rButton_On.setOnClickListener(onClickListener);		
		rButton_Off.setOnClickListener(onClickListener);
	}


	public void writeFile() 
	{

		int bufferLength = buffer.length; 

		File SDCardRoot = Environment.getExternalStorageDirectory();
		//create a new file, specifying the path, and the filename
		//which we want to save the file as.
		File file = new File(SDCardRoot,"somefile.txt");

		//this will be used to write the downloaded data into the file we created
		FileOutputStream fileOutput = null;
		try {
			fileOutput = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return;
		}


		try {
			fileOutput.write(buffer, 0, bufferLength);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//	e.printStackTrace();
			return;
		}

	}


	public void readFile() 
	{
		buffer = new byte[1];
		int bufferLength = buffer.length; 

		File SDCardRoot = Environment.getExternalStorageDirectory();


		File file = new File(SDCardRoot,"somefile.txt");

		if (!file.exists())
		{
			buffer[0] = ON_RADIO_VALUE;
			writeFile();
			return;
		}

		//this will be used to write the downloaded data into the file we created
		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

			// File read Error. By default switch on the Service.

			buffer[0] = ON_RADIO_VALUE;
			writeFile();

			return;
		}
		try {
			fileInput.read(buffer, 0, bufferLength);
		} catch (IOException e) {
			// TODO Auto-generated catch block


			// File read Error. By default switch on the Service.

			buffer[0] = ON_RADIO_VALUE;
			writeFile();

			return;
		}

	}



	OnClickListener onClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View arg0) {

			//	SharedPreferences spref = getSharedPreferences(CONTROL_ACT_SHARED_PREF, MODE_WORLD_READABLE);
			//	SharedPreferences.Editor spref_editor = spref.edit();

			// TODO Auto-generated method stub
			switch (arg0.getId())
			{

			case R.id.ctrl_act_rdo_On :
				//	current_radioOptionState = ON_RADIO_VALUE;
				rButton_On.setChecked(true);
				rButton_Off.setChecked(false);

				buffer[0] = ON_RADIO_VALUE;
				//	spref_editor.putInt(RADIO_OPTION_STATE,current_radioOptionState);
				//	spref_editor.commit(); 
				writeFile();
				break;

			case R.id.ctrl_act_rdo_Off :
				//	current_radioOptionState = OFF_RADIO_VALUE;
				rButton_On.setChecked(false);
				rButton_Off.setChecked(true);
				buffer[0] = OFF_RADIO_VALUE;
				//	spref_editor.putInt(RADIO_OPTION_STATE,current_radioOptionState);
				//spref_editor.commit();
				writeFile();
				break;	
			}


		}
	};
}