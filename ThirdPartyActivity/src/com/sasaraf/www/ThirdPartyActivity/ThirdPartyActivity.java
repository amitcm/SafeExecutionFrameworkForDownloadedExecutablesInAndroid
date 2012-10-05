package com.sasaraf.www.ThirdPartyActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.Toast;

public class ThirdPartyActivity extends Activity {

	public String DOWNLOAD_URL_ID = "DOWNLOAD_URL_ID";
	public String TEMPDIRNAME = "/temp1";
	Bundle bundle = new Bundle();
	String RETURN_FILE_PATH = "ReturnFilePath";


	String CONTROL_ACT_SHARED_PREF = "ControlActSharedPref";
	String RADIO_OPTION_STATE = "RadioOptionState";
	final int DEFAULT_RADIO_VALUE = 0;
	final int ON_RADIO_VALUE = 1;
	final int OFF_RADIO_VALUE = 2;

	// Dialog Box ID
	final int DIALOG_OK_MESSAGE = 1;
	byte[] buffer;
	AlertDialog.Builder builder;

	File pth;
	String tempDir;
	Toast tst;


	// RADIO BUTTONS
	
	RadioButton rb_web_access;
	RadioButton rb_web_access_mal;
	RadioButton rb_sensor;
	RadioButton rb_sensor_mal;
	RadioButton rb_file;
	RadioButton rb_file_mal;

	final int RB_WEB_ACCESS = 1;
	final int RB_WEB_ACCESS_MAL = 2;
	final int RB_SENSOR = 3;
	final int RB_SENSOR_MAL = 4;
	final int RB_FILE = 5;
	final int RB_FILE_MAL = 6;
	String PHONELET_OPTION = "PHONELET_OPTION";
	AlertDialog alert;
	
	
	int rb_chosen = RB_WEB_ACCESS;
	// The url to be downloaded from .... passed  by  the phonelet.		

	public static final String TPARTY_PREF = "TPARTY_PREF";
	
	//public String download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_FileAccess.apk";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		pth = android.os.Environment.getExternalStorageDirectory();
		tempDir = pth.getAbsolutePath().toString() + TEMPDIRNAME;

		showOptions();
		
		/*  Initialize  the  Dialog */
		builder = new AlertDialog.Builder(this);
		builder.setMessage("Please switch On the CES using the Control Activity")
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				return;
				/* User clicked OK so do some stuff */
			}
		});

		alert = builder.create();

		
		readFile();
		if (buffer[0] == OFF_RADIO_VALUE ||  buffer[0] == DEFAULT_RADIO_VALUE)
		{
			// tst = Toast.makeText(getApplicationContext(), "Control Activity has switched off", Toast.LENGTH_LONG);
			// tst.show();
			alert.show();
			return;
		}	

		File fp = new File(tempDir);
		if ( !fp.exists() )
		{
			if (!fp.mkdir())
			{
				tst = Toast.makeText(ThirdPartyActivity.this, "Unable to create temporary directory    "+tempDir , Toast.LENGTH_SHORT);
				tst.show();
				return ;
			}
		}
	}

	public void showOptions()
	{

		rb_web_access = (RadioButton) findViewById(R.id.rb_webaccess);;
		rb_web_access_mal = (RadioButton) findViewById(R.id.rb_webaccess_mal);;
		rb_sensor = (RadioButton) findViewById(R.id.rb_sensor);
		rb_sensor_mal = (RadioButton) findViewById(R.id.rb_sensor_mal);
		rb_file = (RadioButton) findViewById(R.id.rb_file);;
		rb_file_mal = (RadioButton) findViewById(R.id.rb_file_mal);

		rb_web_access.setOnClickListener(rb_listener);
		rb_web_access_mal.setOnClickListener(rb_listener);
		rb_sensor.setOnClickListener(rb_listener);
		rb_sensor_mal.setOnClickListener(rb_listener);
		rb_file.setOnClickListener(rb_listener);
		rb_file_mal.setOnClickListener(rb_listener);

		
		rb_web_access.setChecked(true);
		rb_web_access_mal.setChecked(false);
		rb_sensor.setChecked(false);
		rb_sensor_mal.setChecked(false);
		rb_file.setChecked(false);
		rb_file_mal.setChecked(false);

		
	}

	@Override
	public void onPause()
	{
		SharedPreferences spref = getSharedPreferences( TPARTY_PREF, 0 );
		SharedPreferences.Editor edit = spref.edit(); 
		edit.putInt("RB_CHOSEN", rb_chosen);
		edit.commit();
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		
		SharedPreferences spref = getSharedPreferences( TPARTY_PREF, 0 ); 
		rb_chosen = spref.getInt("RB_CHOSEN", rb_chosen);
		showOptions();

		readFile();
		if (buffer[0] == OFF_RADIO_VALUE ||  buffer[0] == DEFAULT_RADIO_VALUE)
		{
			// tst = Toast.makeText(getApplicationContext(), "Control Activity has switched off", Toast.LENGTH_LONG);
			// tst.show();
			
			alert.show();
			super.onResume();
			return;
		}	

		
		setCheckBoxonResume(rb_chosen);
		super.onResume();
	}
	
	
	public void setCheckBoxonResume(int iid)
	{
		switch (iid)
		{
		case RB_WEB_ACCESS:
			rb_web_access.setChecked(true);
			rb_web_access_mal.setChecked(false);
			rb_sensor.setChecked(false);
			rb_sensor_mal.setChecked(false);
			rb_file.setChecked(false);
			rb_file_mal.setChecked(false);

			break;

		case RB_WEB_ACCESS_MAL:
			rb_web_access.setChecked(false);
			rb_web_access_mal.setChecked(true);
			rb_sensor.setChecked(false);
			rb_sensor_mal.setChecked(false);
			rb_file.setChecked(false);
			rb_file_mal.setChecked(false);

			break;	

		case  RB_SENSOR:
			rb_web_access.setChecked(false);
			rb_web_access_mal.setChecked(false);
			rb_sensor.setChecked(true);
			rb_sensor_mal.setChecked(false);
			rb_file.setChecked(false);
			rb_file_mal.setChecked(false);

			break;

		case RB_SENSOR_MAL:
			rb_web_access.setChecked(false);
			rb_web_access_mal.setChecked(false);
			rb_sensor.setChecked(false);
			rb_sensor_mal.setChecked(true);
			rb_file.setChecked(false);
			rb_file_mal.setChecked(false);

			break;

		case RB_FILE:
			rb_web_access.setChecked(false);
			rb_web_access_mal.setChecked(false);
			rb_sensor.setChecked(false);
			rb_sensor_mal.setChecked(false);
			rb_file.setChecked(true);
			rb_file_mal.setChecked(false);

			break;

		case RB_FILE_MAL:
			rb_web_access.setChecked(false);
			rb_web_access_mal.setChecked(false);
			rb_sensor.setChecked(false);
			rb_sensor_mal.setChecked(false);
			rb_file.setChecked(false);
			rb_file_mal.setChecked(true);

			break;
		
		}
	}

	OnClickListener rb_listener = new OnClickListener()
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			switch(arg0.getId())
			{
			case R.id.rb_webaccess:
				rb_web_access.setChecked(true);
				rb_web_access_mal.setChecked(false);
				rb_sensor.setChecked(false);
				rb_sensor_mal.setChecked(false);
				rb_file.setChecked(false);
				rb_file_mal.setChecked(false);
				rb_chosen = RB_WEB_ACCESS;

				break;

			case R.id.rb_webaccess_mal:
				rb_web_access.setChecked(false);
				rb_web_access_mal.setChecked(true);
				rb_sensor.setChecked(false);
				rb_sensor_mal.setChecked(false);
				rb_file.setChecked(false);
				rb_file_mal.setChecked(false);
				
				rb_chosen = RB_WEB_ACCESS_MAL;
				
				break;	

			case R.id.rb_sensor:
				rb_web_access.setChecked(false);
				rb_web_access_mal.setChecked(false);
				rb_sensor.setChecked(true);
				rb_sensor_mal.setChecked(false);
				rb_file.setChecked(false);
				rb_file_mal.setChecked(false);
				
				rb_chosen = RB_SENSOR;
				
				
				break;

			case R.id.rb_sensor_mal:
				rb_web_access.setChecked(false);
				rb_web_access_mal.setChecked(false);
				rb_sensor.setChecked(false);
				rb_sensor_mal.setChecked(true);
				rb_file.setChecked(false);
				rb_file_mal.setChecked(false);
				
				rb_chosen = RB_SENSOR_MAL;
				
				break;

			case R.id.rb_file:
				rb_web_access.setChecked(false);
				rb_web_access_mal.setChecked(false);
				rb_sensor.setChecked(false);
				rb_sensor_mal.setChecked(false);
				rb_file.setChecked(true);
				rb_file_mal.setChecked(false);
				
				rb_chosen = RB_FILE;
				
				break;

			case R.id.rb_file_mal:
				rb_web_access.setChecked(false);
				rb_web_access_mal.setChecked(false);
				rb_sensor.setChecked(false);
				rb_sensor_mal.setChecked(false);
				rb_file.setChecked(false);
				rb_file_mal.setChecked(true);
				
				rb_chosen = RB_FILE_MAL;
				
				break;
			}
			
			
			readFile();
			if (buffer[0] == OFF_RADIO_VALUE ||  buffer[0] == DEFAULT_RADIO_VALUE)
			{
				// tst = Toast.makeText(getApplicationContext(), "Control Activity has switched off", Toast.LENGTH_LONG);
				// tst.show();
				alert.show();
				return;
			}
			
			
			Intent myIntent = new Intent();
			
			myIntent.setClassName( "com.sasaraf.www.CodeExecServ", "com.sasaraf.www.CodeExecServ.CodeExecutionService");

			myIntent.putExtra(PHONELET_OPTION, rb_chosen);
			startService(myIntent);
			
		}

	};


	public void readFile() 
	{
		buffer = new byte[1];
		int bufferLength = buffer.length; 

		File SDCardRoot = Environment.getExternalStorageDirectory();


		File file = new File(SDCardRoot,"somefile.txt");

		if (!file.exists())
		{
			buffer[0] = ON_RADIO_VALUE;
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

			return;
		}
		try {
			fileInput.read(buffer, 0, bufferLength);
		} catch (IOException e) {
			// TODO Auto-generated catch block


			// File read Error. By default switch on the Service.

			buffer[0] = ON_RADIO_VALUE;

			return;
		}

	}


	@Override
	public void onDestroy()
	{
		// Delete  the  temp directory.
		File delFile = new File(tempDir);

		if (delFile.exists())
		{
			delFile.delete();
		}

		super.onDestroy();
	}



}