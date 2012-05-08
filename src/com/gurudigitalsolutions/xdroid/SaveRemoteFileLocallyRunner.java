package com.gurudigitalsolutions.xdroid;

import android.content.Context;
import android.os.Message;
import android.util.Log;

public class SaveRemoteFileLocallyRunner implements Runnable
{
	public String Filename = "";
	
	public SaveRemoteFileLocallyRunner(String filename)
	{
		Filename = filename;
	}
	
	@Override
	public void run() 
	{
		Log.w("xdrun", "Launching SaveRemoteFileLocallyRunner");
		SSHHandler.SaveRemoteFileLocally(Filename);
		
		Message msg = XdroidActivity.dvHandler.obtainMessage();
		msg.what = Runners.SaveRemoteFileWhat;
		if(!XdroidActivity.dvHandler.hasMessages(msg.what))
		{
			try
			{
				XdroidActivity.dvHandler.sendMessage(msg);
			}
			catch(Exception ex)
			{
				Log.w("dbg", "SaveRemoteFileLocally Exception");
				Log.w("dbg", ex.getMessage());
			}
		}
		Log.w("xdrun", "Done with SaveRemoteFileLocallyRunner");
	}
}
