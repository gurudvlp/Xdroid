package com.gurudigitalsolutions.xdroid;

import android.graphics.Rect;
import android.util.Log;

public class DockItem 
{
	public int IconResourceID = 0;
	public String Name = "";
	public int Index = 0;
	public Runnable Runner;
	public Rect ScreenPosition;
	
	public DockItem(int IconResID, String itemname, int index, Runnable runner)
	{
		this.IconResourceID = IconResID;
		this.Name = itemname;
		this.Index = index;
		this.Runner = runner;
	}
	
	public void onClick()
	{
		//new Thread(Runner).start();
		XdroidActivity.instance.Vibrate(50);
		XdroidActivity.dvHandler.post(Runner);
		//Runner.run();
	}
	
	public void SetScreenPos(Rect screenpos)
	{
		Log.w("dbg", "Setting screen position for toolbar item");
		Log.w("dbg", "l,r,t,b: " + Integer.toString(screenpos.left) +
				" " + Integer.toString(screenpos.right) +
				" " + Integer.toString(screenpos.top) +
				" " + Integer.toString(screenpos.bottom));
		this.ScreenPosition = screenpos;
		
	}
	
}
