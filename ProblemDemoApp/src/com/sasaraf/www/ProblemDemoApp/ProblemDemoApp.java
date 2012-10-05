package com.sasaraf.www.ProblemDemoApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

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

public class ProblemDemoApp extends Activity {

	public String DOWNLOAD_URL_ID = "DOWNLOAD_URL_ID";
	public String TEMPDIRNAME = "/temp2";
	Bundle bundle = new Bundle();
	String RETURN_FILE_PATH = "ReturnFilePath";

	// Dialog Box ID
	final int DIALOG_OK_MESSAGE = 1;
	byte[] buffer;
	AlertDialog.Builder builder;


	String CONTROL_ACT_SHARED_PREF = "ControlActSharedPref";
	String RADIO_OPTION_STATE = "RadioOptionState";
	final int DEFAULT_RADIO_VALUE = 0;
	final int ON_RADIO_VALUE = 1;
	final int OFF_RADIO_VALUE = 2;
	RadioButton rButton_Off;
	RadioButton rButton_On;
	int current_radioOptionState;

	public static final String TPARTY_PREF = "TPARTY_PREF";

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
	int rb_chosen = RB_WEB_ACCESS;
	AlertDialog alert; 
	

	File pth;
	String tempDir;
	Toast tst;

	// The url to be downloaded from .... passed  by  the phonelet.		

	//public String download_url = "http://www.cs.stonybrook.edu/~jwong/CSE594/1.jpg";;
	public String download_url;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		pth = android.os.Environment.getExternalStorageDirectory();
		tempDir = pth.getAbsolutePath().toString() + TEMPDIRNAME;

		showOptions();
		
		File fp = new File(tempDir);
		if ( !fp.exists() )
		{
			if (!fp.mkdir())
			{
				tst = Toast.makeText(ProblemDemoApp.this, "Unable to create temporary directory    "+tempDir , Toast.LENGTH_SHORT);
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

		download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_WebAccess.apk";

	}




	public void runFunctionFromClass(String packageName,String className,String pathforApk,String functionname,Double lattitude, Double longitude)
	{

		//PathName
		Log.i("CLASSLOADER","IN runFunctionFromClass");
		//String jarFile = Environment.getExternalStorageDirectory() + pathforApk;
		//String jarFile = "/mnt/sdcard/Attack_FileAccess.apk";
		String jarFile = pathforApk;
		Log.i("CLASSLOADER", jarFile);
		Class<?> handler = null;
		DexClassLoader classLoader = new DexClassLoader(
				jarFile, getFilesDir().getAbsolutePath(), null, getClass().getClassLoader());
		try 
		{

			//	tst = Toast.makeText(CodeExecutionService.this, " runFunctionFromClass :   " + res, Toast.LENGTH_LONG);
			//tst.show();
			handler = classLoader.loadClass(className);

		} 
		catch (ClassNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}


		//Class<?> handler = Class.forName(className, true, myClassLoader);
		Method[] all = null;
		try {
			all = handler.getMethods();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		double a = 0 ;
		String filepath = null;
		for (Method item : all)
		{
			if (item.getName().equalsIgnoreCase(functionname))
			{
				Log.i("CLASSLOADER","Method Found");
				try {
					//a = (Integer)item.invoke(void);
					Constructor<?> ctor = null;
					try {
						ctor = handler.getConstructor();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Object instance = null;
					try {
						instance = ctor.newInstance();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Method exposed = null;
					try {
						exposed = handler.getMethod(functionname, Double.TYPE,Double.TYPE);
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i("CLASSLOADER","Calling Function");
					if (functionname == "useGPSlocation")
					{
						filepath = (String)exposed.invoke(instance, lattitude,longitude);
						tst = Toast.makeText(ProblemDemoApp.this, " Phonelet Returned data : "+filepath, Toast.LENGTH_LONG);
						tst.show();
					}
					else
					{
						filepath = (String) exposed.invoke(instance, lattitude,longitude);
						tst = Toast.makeText(ProblemDemoApp.this, " Phonelet Returned data : "+filepath, Toast.LENGTH_LONG);
						tst.show();
					}
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					tst = Toast.makeText(ProblemDemoApp.this, "Alert : Phonelet tried to do illegitimate access", Toast.LENGTH_LONG);
					tst.show();
					e.printStackTrace();
				}

			}
		}

		Log.i("CLASSLOADER", "\na =  " + a);
		//// End of Classloader code

	}


	private void LaunchActivity(String res,int option_value)
	{

		// Copy Amit's code here.


		// ClassLoader Code
		//PathName
		Log.i("CLASSLOADER","IN LaunchActivity");
		String goodFunction = "useGPSlocation";
		String maliciousFunction = "useGPSlocation_malicious";
		String packageName = null;
		String className = null;
		//String pathforApk = Environment.getExternalStorageState() + "/Attack_FileAccess.apk";
		String pathforApk = res;
		String functionname = null ;
		Double lattitude = (double) 10;
		Double longitude = (double) 15;

		switch(option_value)
		{

		case RB_WEB_ACCESS:
			packageName = "com.attack_webaccess.example";
			className = "com.attack_webaccess.example.Attack_WebAccessActivity";
			functionname = goodFunction;
			break;
		case RB_WEB_ACCESS_MAL :
			packageName = "com.attack_webaccess.example";
			className = "com.attack_webaccess.example.Attack_WebAccessActivity";
			functionname = maliciousFunction;			
			break;

		case RB_FILE:
			packageName = "com.attack_fileaccess.example";
			className = "com.attack_fileaccess.example.Attack_FileAccessActivity";
			functionname = goodFunction;
			break;
		case RB_FILE_MAL:
			packageName = "com.attack_fileaccess.example";
			className = "com.attack_fileaccess.example.Attack_FileAccessActivity";
			functionname = maliciousFunction;
			break;	

		case RB_SENSOR:
			packageName = "com.attack_sensor.example";
			className = "com.attack_sensor.example.Attack_sensorActivity";
			functionname = goodFunction;
			break;

		case RB_SENSOR_MAL:
			packageName = "com.attack_sensor.example";
			className = "com.attack_sensor.example.Attack_sensorActivity";
			functionname = maliciousFunction;
			break;

		default :
			break;	
		}

		runFunctionFromClass(packageName, className, pathforApk, functionname,lattitude, longitude);

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
			
			download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_WebAccess.apk";

			break;

		case RB_WEB_ACCESS_MAL:
			rb_web_access.setChecked(false);
			rb_web_access_mal.setChecked(true);
			rb_sensor.setChecked(false);
			rb_sensor_mal.setChecked(false);
			rb_file.setChecked(false);
			rb_file_mal.setChecked(false);


			download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_WebAccess.apk";

			break;	

		case  RB_SENSOR:
			rb_web_access.setChecked(false);
			rb_web_access_mal.setChecked(false);
			rb_sensor.setChecked(true);
			rb_sensor_mal.setChecked(false);
			rb_file.setChecked(false);
			rb_file_mal.setChecked(false);


			download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_sensor.apk";

			break;

		case RB_SENSOR_MAL:
			rb_web_access.setChecked(false);
			rb_web_access_mal.setChecked(false);
			rb_sensor.setChecked(false);
			rb_sensor_mal.setChecked(true);
			rb_file.setChecked(false);
			rb_file_mal.setChecked(false);
;

			download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_sensor.apk";

			break;

		case RB_FILE:
			rb_web_access.setChecked(false);
			rb_web_access_mal.setChecked(false);
			rb_sensor.setChecked(false);
			rb_sensor_mal.setChecked(false);
			rb_file.setChecked(true);
			rb_file_mal.setChecked(false);

			download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_FileAccess.apk";

			break;

		case RB_FILE_MAL:
			rb_web_access.setChecked(false);
			rb_web_access_mal.setChecked(false);
			rb_sensor.setChecked(false);
			rb_sensor_mal.setChecked(false);
			rb_file.setChecked(false);
			rb_file_mal.setChecked(true);

			download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_FileAccess.apk";

			break;
		
		}
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
		
		setCheckBoxonResume(rb_chosen);
		super.onResume();
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

				download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_WebAccess.apk";

				break;

			case R.id.rb_webaccess_mal:
				rb_web_access.setChecked(false);
				rb_web_access_mal.setChecked(true);
				rb_sensor.setChecked(false);
				rb_sensor_mal.setChecked(false);
				rb_file.setChecked(false);
				rb_file_mal.setChecked(false);

				rb_chosen = RB_WEB_ACCESS_MAL;

				download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_WebAccess.apk";

				break;	

			case R.id.rb_sensor:
				rb_web_access.setChecked(false);
				rb_web_access_mal.setChecked(false);
				rb_sensor.setChecked(true);
				rb_sensor_mal.setChecked(false);
				rb_file.setChecked(false);
				rb_file_mal.setChecked(false);

				rb_chosen = RB_SENSOR;

				download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_sensor.apk";

				break;

			case R.id.rb_sensor_mal:
				rb_web_access.setChecked(false);
				rb_web_access_mal.setChecked(false);
				rb_sensor.setChecked(false);
				rb_sensor_mal.setChecked(true);
				rb_file.setChecked(false);
				rb_file_mal.setChecked(false);

				rb_chosen = RB_SENSOR_MAL;

				download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_sensor.apk";

				break;

			case R.id.rb_file:
				rb_web_access.setChecked(false);
				rb_web_access_mal.setChecked(false);
				rb_sensor.setChecked(false);
				rb_sensor_mal.setChecked(false);
				rb_file.setChecked(true);
				rb_file_mal.setChecked(false);

				rb_chosen = RB_FILE;

				download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_FileAccess.apk";

				break;

			case R.id.rb_file_mal:
				rb_web_access.setChecked(false);
				rb_web_access_mal.setChecked(false);
				rb_sensor.setChecked(false);
				rb_sensor_mal.setChecked(false);
				rb_file.setChecked(false);
				rb_file_mal.setChecked(true);

				rb_chosen = RB_FILE_MAL;

				download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_FileAccess.apk";

				break;
			}
			
			String downloadfilePath = downloadFile();
			LaunchActivity(downloadfilePath, rb_chosen);

		}

	};
	

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



	public String downloadFile()
	{

		String DOWNLOAD_URL_ID =  "DOWNLOAD_URL_ID";
		String  downloadfilePath;


		//Log.d("1", "MyService" + download_url);
		File downloadFileDir = new File(tempDir);

		if (!downloadFileDir.exists())
		{
			if (!downloadFileDir.mkdir())
			{
				tst = Toast.makeText(ProblemDemoApp.this, "----------", Toast.LENGTH_LONG);
				tst.show();	
			}
		}

		downloadfilePath = downloadFileDir.getAbsoluteFile() + "/"+ System.currentTimeMillis()+".apk";


		URL url = null;
		// Open the http connection.
		HttpURLConnection my_conn = null;

		try{
			url = new URL(download_url);

			Log.i("DWNLSERVICE", "1");
			my_conn = (HttpURLConnection) url.openConnection();
			// TRY CATCH.

			// Indicate the method we wish to invoke …. i.e. the GET method.

			my_conn.setRequestMethod( "GET");


			// Indicates that this connection accepts output. I.e we can upload to the URL using this 
			// connection

			my_conn.setDoOutput(true);

			// Connect to the server < URL >
			Log.i("DWNLSERVICE", "2");
			my_conn.connect();

			// Define an inputstream which will contain the data
			InputStream istr = null;
			Log.i("DWNLSERVICE", "3");
			istr = my_conn.getInputStream();


			byte [] buffer = new byte[1024];

			// Get the length of the content.
			int len = my_conn.getContentLength();


			FileOutputStream fos = null;
			//fos = new FileOutputStream("/mnt/sdcard/temp1/24.jpeg");
			fos = new FileOutputStream(downloadfilePath);


			while ( ( len = istr.read(buffer) ) !=  -1 )
			{
				// Write to file \ process the data.

				fos.write(buffer, 0 , len);
			}


			Log.i("DWNLSERVICE", "5");
			my_conn.disconnect();

			return downloadfilePath;

		}

		catch (Exception e)
		{  
			tst = Toast.makeText(ProblemDemoApp.this, "MyService : Download Failed" , Toast.LENGTH_SHORT);
			tst.show();
			return null;
		}

	}


}