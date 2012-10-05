package com.sasaraf.www.DownLoadService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class DownLoadService extends Service{


	Toast tst;
	File downloadFileDir;
	private final IBinder mBinder = new MyBinder();
	public String TEMPDIRNAME = "/temp1";

	File pth = android.os.Environment.getExternalStorageDirectory();
	String tempDir = pth.getAbsolutePath().toString() + TEMPDIRNAME;
    String RETURN_FILE_PATH = "ReturnFilePath";
	static int val = 5;
	
	public class MyBinder extends Binder {
		DownLoadService getService() {
			return DownLoadService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		return mBinder;
	}

	@Override
	public void onCreate() {

		super.onCreate();


	    //val++;	    
	    Log.i("DWNLSERVICE : onCreate", "val  " + val);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String DOWNLOAD_URL_ID =  "DOWNLOAD_URL_ID";
		String download_url = intent.getStringExtra(DOWNLOAD_URL_ID);
        String  downloadfilePath;

	    
		//Log.d("1", "MyService" + download_url);
		downloadFileDir = new File(tempDir);
		
		if (!downloadFileDir.exists())
		{
			if (!downloadFileDir.mkdir())
			{
				tst = Toast.makeText(DownLoadService.this, "----------", Toast.LENGTH_LONG);
				tst.show();	
			}
		}

		downloadfilePath = downloadFileDir.getAbsoluteFile() + "/"+ System.currentTimeMillis()+".apk";
		
		
		URL url = null;
		// Open the http connection.
		HttpURLConnection my_conn = null;

		try{
			url = new URL(download_url);

			tst = Toast.makeText(DownLoadService.this, "DownLoading Phonelet" , Toast.LENGTH_SHORT);
			tst.show();
			
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

		    
			Intent _intent = new Intent();
		    _intent.setAction( "com.sasaraf.www" );
		    //_intent.addCategory( "com.your.android.CATEGORY" );
		    _intent.putExtra(RETURN_FILE_PATH, downloadfilePath );
		    Log.i("DWNLOAD",downloadfilePath);
		    _intent.putExtra("VAL", val);
		    Log.i("DWNLSERVICE", "5");

		   
		    Log.i("DOWNLOAD  : COMPLETE", "val" + val);
			
		    sendBroadcast(_intent);
			my_conn.disconnect();
			return START_NOT_STICKY;
			
		}

		catch (Exception e)
		{  
			tst = Toast.makeText(DownLoadService.this, "MyService : Download Failed" , Toast.LENGTH_SHORT);
			tst.show();
			return -1;
		}
		
		
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}
	
	
}
