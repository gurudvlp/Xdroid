package com.gurudigitalsolutions.xdroid;

import android.content.Context;
import android.os.Message;
import android.util.Log;

public class WindowFullscreenRunner implements Runnable
{
	public Context context;
	
	public WindowFullscreenRunner(Context thecontext)
	{
		context = thecontext;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		XdroidActivity.XWindows[XWindow.SelectedWindow].FullScreen(context);
		XWindow.RefreshWindowList(context);
		
		try
		{
			Message msg = XdroidActivity.dvHandler.obtainMessage();
			msg.what = Runners.WindowFullscreenWhat;
			XdroidActivity.dvHandler.sendMessage(msg);
		}
		catch(Exception ex)
		{
			Log.w("dbg", "Exception during WindowFullscreenRuner");
			Log.w("dbg", ":: " + ex.getMessage());
		}
	}
}
