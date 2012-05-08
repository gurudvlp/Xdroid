package com.gurudigitalsolutions.xdroid;

import java.io.IOException;

import android.os.Message;
import android.util.Log;

public class MouseClickRunner implements Runnable
{
	int Button = 0;
	int Times = 1;
	boolean AlertUI = true;
	
	public MouseClickRunner(int button, int times)
	{
		Button = button;
		Times = times;
	}
	
	public MouseClickRunner(int button, int times, boolean alertui)
	{
		Button = button;
		Times = times;
		AlertUI = alertui;
	}
	
	@Override
	public void run()
	{
		Log.w("tp", "Launching MouseClickRunner");
		//SSHHandler.XdroidCommand("clickmouse " + Integer.toString(Button) + " " + Integer.toString(Times));
		for(int clks = 0; clks < Times; clks++)
		{
			try {
				SSHHandler.ExecCommand("xte 'mousedown " + Integer.toString(Button) + "' 'mouseup " + Integer.toString(Button) + "'");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.w("dbg", "Exception doing mouse click");
			}
		}
		
		if(AlertUI)
		{
			try
			{
			Message msg = XdroidActivity.dvHandler.obtainMessage();
			msg.what = Runners.MouseClickWhat;
			XdroidActivity.dvHandler.sendMessage(msg);
			Log.w("tp", "Wrapping up MouseClickRunner");
			}
			catch(Exception ex)
			{
				Log.w("dbg", "Exception during MouseClickRunner");
				Log.w("dbg", ":: " + ex.getMessage());
			}
		}
	}
}
