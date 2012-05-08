package com.gurudigitalsolutions.xdroid;

import android.os.Message;
import android.util.Log;

public class GotoDesktopRunner implements Runnable
{
	public int DesktopID = 0;
	
	public GotoDesktopRunner(int desktopid)
	{
		DesktopID = desktopid;
	}
	
	@Override
	public void run()
	{
		Log.w("xdrun", "Launching GotoDesktopRunner");
		
		SSHHandler.XdroidCommand("gotodesktop " + Integer.toString(DesktopID));
		SSHHandler.SaveRemoteFileLocally(SSHHandler.XdroidCommand("desktopthumbnail"));
		
		try
		{
			Message msg = XdroidActivity.dvHandler.obtainMessage();
			msg.what = Runners.GotoDesktopWhat;
			XdroidActivity.dvHandler.sendMessage(msg);
			Log.w("xdrun", "Done with GotoDesktopRunner");
			XdroidActivity.dvHandler.sendMessage(msg);
		}
		catch(Exception ex)
		{
			Log.w("dbg", "Exception during GotoDesktopRunner");
			Log.w("dbg", ":: " + ex.getMessage());
		}
	}
}
