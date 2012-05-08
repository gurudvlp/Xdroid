package com.gurudigitalsolutions.xdroid;

import android.content.Context;
import android.os.Message;
import android.util.Log;

public class WindowCloseRunner implements Runnable
{
	public Context context;
	
	public WindowCloseRunner(Context thecontext)
	{
		context = thecontext;
	}
	
	@Override
	public void run() 
	{
		Log.w("xdrun", "Launching WindowCloseRunner");
		XdroidActivity.XWindows[XWindow.SelectedWindow].CloseWindow(context);
		XWindow.RefreshWindowList(context);
		
		try
		{
			Message msg = XdroidActivity.dvHandler.obtainMessage();
			msg.what = Runners.WindowCloseWhat;
			XdroidActivity.dvHandler.sendMessage(msg);
			Log.w("xdrun", "Done with WindowCloseRunner");
		}
		catch(Exception ex)
		{
			Log.w("dbg", "Exception during WindowCloseRunner");
			Log.w("dbg", ex.getMessage());
		}
	}

}
