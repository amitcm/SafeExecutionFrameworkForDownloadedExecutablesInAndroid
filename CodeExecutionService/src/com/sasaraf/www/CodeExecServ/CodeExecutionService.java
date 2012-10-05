package com.sasaraf.www.CodeExecServ;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CodeExecutionService extends Service {

	String DOWNLOAD_URL_ID = "DOWNLOAD_URL_ID";
	String USER_OPTION = "USER_OPTION";
	int option_value;
	String download_url;
	String res;
	Bundle bundle;
	String RETURN_FILE_PATH = "ReturnFilePath";
	Toast tst;

	final int RB_WEB_ACCESS = 1;
	final int RB_WEB_ACCESS_MAL = 2;
	final int RB_SENSOR = 3;
	final int RB_SENSOR_MAL = 4;
	final int RB_FILE = 5;
	final int RB_FILE_MAL = 6;
	String PHONELET_OPTION = "PHONELET_OPTION";
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/** Called when the activity is first created. */
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		final int RB_WEB_ACCESS = 1;
		final int RB_WEB_ACCESS_MAL = 2;
		final int RB_SENSOR = 3;
		final int RB_SENSOR_MAL = 4;
		final int RB_FILE = 5;
		final int RB_FILE_MAL = 6;
		String PHONELET_OPTION = "PHONELET_OPTION";
		
		Intent ces_intent = new Intent();

		Log.d("1", "CodeExecutionService" + download_url);
		option_value = intent.getIntExtra(PHONELET_OPTION, 0);
		
		download_url = "http://mysbfiles.stonybrook.edu/~akhole/";
		switch (option_value)
		{
			
			case RB_WEB_ACCESS:
			case RB_WEB_ACCESS_MAL :
				download_url += "Attack_WebAccess.apk";
				break;
				
			case RB_FILE:
			case RB_FILE_MAL:
				download_url += "Attack_FileAccess.apk";
				
				
				break;	
				
			case RB_SENSOR:
			case RB_SENSOR_MAL:
				download_url += "Attack_sensor.apk";
				break;
				
			default :
				break;	
		}
		Log.i("CES",download_url);
		
		// TODO : TEST ..... REMOVE THE LINE BELOW
		//download_url = "http://mysbfiles.stonybrook.edu/~akhole/Attack_FileAccess.apk";
	
		ces_intent.setClassName("com.sasaraf.www.DownLoadService","com.sasaraf.www.DownLoadService.DownLoadService");
		ces_intent.putExtra(DOWNLOAD_URL_ID, download_url);
		//ces_intent.putExtra(USER_OPTION, option_value);
		tst = Toast.makeText(CodeExecutionService.this, "DOWNLOAD PATH " + download_url, Toast.LENGTH_LONG);
		tst.show();

		/*  Event  receiving code .... */
		
		final String SERVICE_BROADCAST_ACTION = "com.sasaraf.www";

		final IntentFilter serviceActiveFilter = new IntentFilter( SERVICE_BROADCAST_ACTION );

	//	serviceActiveFilter.addCategory( "com.your.android.CATEGORY" );

		this.registerReceiver( serviceReceiver, serviceActiveFilter );  

		startService(ces_intent);

		return 0;
	}

	
	BroadcastReceiver serviceReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive( final Context context, final Intent intent ) {

			
			//int b = 25;
			
			if( intent != null ) {
				String res = intent.getStringExtra(RETURN_FILE_PATH) ;
				int val = intent.getIntExtra("VAL",-1);
				
				// The service is active

				Log.i("1", "CES:  res = " + res);
				
				if (res == null)
				{
				//	Log.i("1", "CES:  res = " + res);
					return;
				}
				//			tst = Toast.makeText(CodeExecutionService.this, " CodeExecutionService :   " + res, Toast.LENGTH_LONG);
				//	tst.show();

				LaunchActivity(res, option_value);
			}
		}
	};
	
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
					
					tst = Toast.makeText(CodeExecutionService.this, "Running Phonelet" , Toast.LENGTH_SHORT);
					tst.show();
					
					if (functionname == "useGPSlocation")
					{
						filepath = (String)exposed.invoke(instance, lattitude,longitude);
						tst = Toast.makeText(CodeExecutionService.this, " Phonelet Returned data : "+filepath, Toast.LENGTH_LONG);
						tst.show();
					}
					else
					{
						filepath = (String) exposed.invoke(instance, lattitude,longitude);
						tst = Toast.makeText(CodeExecutionService.this, " Phonelet Returned data : "+filepath, Toast.LENGTH_LONG);
						tst.show();
					}
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					tst = Toast.makeText(CodeExecutionService.this, "Alert : Phonelet tried to do illegitimate access", Toast.LENGTH_LONG);
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

	@Override
	public void onCreate()
	{
		Log.d("2", "On?Create in  CES");
		super.onCreate();
	}


};

