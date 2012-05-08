package com.gurudigitalsolutions.xdroid;

import android.content.Context;
import android.os.Message;
import android.util.Log;

public class WindowMaximizeRunner implements Runnable
{
	public Context context;
	
	public WindowMaximizeRunner(Context thecontext)
	{
		context = thecontext;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		XdroidActivity.XWindows[XWindow.SelectedWindow].Maximize(context);
		XWindow.RefreshWindowList(context);
		
		try
		{
			Message msg = XdroidActivity.dvHandler.obtainMessage();
			msg.what = Runners.WindowMaximizeWhat;
			XdroidActivity.dvHandler.sendMessage(msg);
		}
		catch(Exception ex)
		{
			Log.w("dbg", "Exception during WindowMaximizeRunner");
			Log.w("dbg", ":: " + ex.getMessage());
		}
	}
}
