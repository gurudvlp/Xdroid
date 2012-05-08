package com.gurudigitalsolutions.xdroid;

import android.content.Context;
import android.os.Message;

public class WindowGetConfigRunner implements Runnable
{
	public Context context;
	
	public WindowGetConfigRunner(Context thecontext)
	{
		context = thecontext;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		XdroidActivity.XWindows[XWindow.SelectedWindow].Maximize(context);
		XWindow.RefreshWindowList(context);
		Message msg = XdroidActivity.dvHandler.obtainMessage();
		msg.what = Runners.WindowGetConfigWhat;
		XdroidActivity.dvHandler.sendMessage(msg);
	}
}
