package com.gurudigitalsolutions.xdroid;

import android.content.Context;
import android.os.Message;
import android.util.Log;

public class WindowFocusRunner implements Runnable
{
	public Context context;
	
	public WindowFocusRunner(Context thecontext)
	{
		context = thecontext;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		XdroidActivity.XWindows[XWindow.SelectedWindow].GiveFocus(context);
		XWindow.RefreshWindowList(context);
		
		try
		{
			Message msg = XdroidActivity.dvHandler.obtainMessage();
			msg.what = Runners.WindowFocusWhat;
			XdroidActivity.dvHandler.sendMessage(msg);
		}
		catch(Exception ex)
		{
			Log.w("dbg", "Exception during WindowFocusRunner");
			Log.w("dbg", ":: " + ex.getMessage());
		}
	}
}
