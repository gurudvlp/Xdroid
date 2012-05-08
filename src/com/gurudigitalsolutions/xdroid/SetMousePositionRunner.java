package com.gurudigitalsolutions.xdroid;

import java.io.IOException;

import android.os.Message;
import android.util.Log;

public class SetMousePositionRunner implements Runnable
{
	public static int TotalSpawned = 0;
	
	public int X = 0;
	public int Y = 0;
	public boolean Relative = false;
	
	public SetMousePositionRunner(int x, int y)
	{
		SetMousePositionRunner.TotalSpawned++;
		X = x;
		Y = y;
	}
	
	public SetMousePositionRunner(int x, int y, boolean relative)
	{
		SetMousePositionRunner.TotalSpawned++;
		X = x;
		Y = y;
		Relative = relative;
		
	}
	
	@Override
	public void run()
	{
		Log.w("xdrun", "Launching SetMousePositionRunner");
		
		if(!Relative)
		{
			//SSHHandler.XdroidCommand("setmouseposition " + Integer.toString(X) + " " + Integer.toString(Y));
			try {
				SSHHandler.ExecCommand("xte 'mousemove " + Integer.toString(X) + " " + Integer.toString(Y) + "'");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.w("dbg", "Exception while executing move mouse");
			}
		}
		else
		{
			//SSHHandler.XdroidCommand("setmousepositionr " + Integer.toString(X) + " " + Integer.toString(Y));
			try {
				SSHHandler.ExecCommand("xte 'mousermove " + Integer.toString(X) + " " + Integer.toString(Y) + "'");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.w("dbg", "Exception while moving mouse relatively");
			}
		}
		//	Nothing needs to be returned to the handler in the UI thread
		//	because we just set the position.
		
		/*Message msg = XdroidActivity.dvHandler.obtainMessage();
		//XdroidActivity.dvHandler.obtainMessage(what, arg1, arg2)
		msg.what = Runners.SetMousePositionWhat;
		XdroidActivity.dvHandler.sendMessage(msg);*/
		Log.w("xdrun", "Done with SetMousePositionRunner");
	}
}
