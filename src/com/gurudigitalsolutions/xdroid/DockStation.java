package com.gurudigitalsolutions.xdroid;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

public class DockStation 
{
	public Bitmap Icon;
	public int X = 0;
	public int Y = 0;
	public int Width = 0;
	public int Height = 0;
	
	public int myIconSize = -1;
	
	public boolean DimScreenOnFocus = true;
	public boolean DrawGridAroundButtons = false;
	
	public static int Reserved = 48;
	public static int IconSize = 48;
	
	public DockItem[] DockItems = new DockItem[8];
	
	public DockStation()
	{
		myIconSize = DockStation.IconSize;
	}
	
	public void AddItem(int ResourceID, String title, int index, Runnable runner)
	{
		if(index < 0 || index > 7) { return; }
		DockItems[index] = new DockItem(ResourceID, title, index, runner);
	}
	
	public int CenteredX(int canvaswidth)
	{
		float toret = (((float)canvaswidth) / 2) - ((((float)ActiveCount()) * myIconSize) / 2);
		return (int)toret;
	}
	
	public int CenteredY(int canvasheight)
	{
		float toret = (((float)canvasheight) / 2) - (((float)myIconSize) / 2);
		return (int)toret;
	}
	
	public int ActiveCount()
	{
		if(DockItems == null) { return 0; }
		int active = 0;
		for(int eitem = 0; eitem < DockItems.length; eitem++)
		{
			if(DockItems[eitem] != null) { active++; }
		}
		
		return active;
	}
	
	public void UpdateScreenRect(Rect screenpos)
	{
		this.X = screenpos.left;
		this.Y = screenpos.top;
		this.Width = screenpos.right - screenpos.left;
		this.Height = screenpos.bottom - screenpos.top;
		
		int ttlitem = 0;
		for(int eitem = 0; eitem < DockItems.length; eitem++)
		{
			if(DockItems[eitem] != null)
			{
				Rect itempos = new Rect();
				itempos.top = screenpos.top;
				itempos.bottom = screenpos.bottom;
				
				itempos.left = screenpos.left + (ttlitem * this.myIconSize);
				itempos.right = itempos.left + this.myIconSize;
				
				DockItems[eitem].SetScreenPos(itempos);
				ttlitem++;
			}
		}
	}
	
	public int ButtonAt(int x, int y)
	{
		Log.w("dbg", "Toolbar Button At? " + Integer.toString(x) + " " + Integer.toString(y));
		if(x < X || x > X + this.Width) { return -1; }
		if(y < Y || y > Y + this.Height) { return -1; }
		
		for(int eitem = 0; eitem < DockItems.length; eitem++)
		{
			if(DockItems[eitem] != null)
			{
				if(DockItems[eitem].ScreenPosition.contains(x, y))
				{
					Log.w("dbg", "Toolbar button " + Integer.toString(eitem) + " pressed");
					return eitem;
				}
			}
		}
		
		return -1;
	}
}
