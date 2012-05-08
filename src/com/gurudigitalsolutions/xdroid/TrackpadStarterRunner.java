package com.gurudigitalsolutions.xdroid;

import android.os.Message;
import android.util.Log;

public class TrackpadStarterRunner implements Runnable
{
	public TrackpadStarterRunner()
	{
		
	}
	
	@Override
	public void run()
	{
		XdroidActivity.drawView.TrackpadStart = true;
		//Message msg = XdroidActivity.dvHandler.obtainMessage();
		
		try
		{
			Message msg = Message.obtain();
			msg.what = Runners.TrackPadStartRunner;
			XdroidActivity.dvHandler.sendMessage(msg);
		}
		catch(Exception ex)
		{
			Log.w("dbg", "Exception during TrackpadStarterRunner");
			Log.w("dbg", ex.getMessage());
		}
		Log.w("xdrun", "Done with TrackpadStartRunner");
	}
}
