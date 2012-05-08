package com.gurudigitalsolutions.xdroid;

import android.os.Message;
import android.util.Log;

public class SaveLocalFileRemotelyRunner implements Runnable
{
	public String Filename = "";
	public String RemoteDir = "";
	public String Mode = "0755";
	
	public SaveLocalFileRemotelyRunner(String filename, String remotedir, String mode)
	{
		Filename = filename;
		RemoteDir = remotedir;
		Mode = mode;
	}
	
	@Override
	public void run() 
	{
		Log.w("xdrun", "Launching SaveLocalFileRemotelyRunner");
		SSHHandler.SaveLocalFileRemotely(Filename, RemoteDir, Mode);
		
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

