package com.gurudigitalsolutions.xdroid;

import java.io.IOException;
import java.util.StringTokenizer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

public class XWindow 
{
	//	This class represents a remote X Window
	public String ID = "";
	public int DesktopID = 0;
	public String Title = "";
	public int X = 0;
	public int Y = 0;
	public int Width = 100;
	public int Height = 100;
	public static int CurrentDesktop = 0;
	public static int SelectedWindow = 0;
	public static Boolean ViewOnlySelectedWindow = false;
	public static Boolean TrackMousePointer = false;
	public static int MouseX = 0;
	public static int MouseY = 0;
	public String ThumbnailUrl = "";
	public long LastUpdate = 0;
	
	public static int ScreenWidth = 1280;
	public static int ScreenHeight = 800;
	
	
	
	public static String BaseUrl = "http://192.168.1.95:4777";
	//public static String BaseUrl = "http://192.168.1.80:4777";
	public static String CloseWindowUrl = "/closewindow.php?";
	public static String FullScreenUrl = "/fullscreen.php?";
	public static String GiveFocusUrl = "/givefocus.php?";
	public static String GoToDesktopUrl = "/gotodesktop.php?";
	public static String MaximizeWindowUrl = "/maximizewindow.php?";
	
	public byte[] Thumbnail;
	
	
	public XWindow()
	{
		
	}
	
	public static void RefreshWindowList(Context context)
	{
		//	Grab overall configuration
		
		String conf = SSHHandler.XdroidCommand("getconfig");
		String[] parts = conf.split("\n");
		for(int epart = 0; epart < parts.length; epart++)
		{
			Log.w("dbg", parts[epart]);
			String[] keyval = parts[epart].split(":");
			if(keyval[0].contains("ScreenWidth")) { XWindow.ScreenWidth = Integer.parseInt(keyval[1].trim()); }
			if(keyval[0].contains("ScreenHeight")) { XWindow.ScreenHeight = Integer.parseInt(keyval[1].trim()); }
			// nonfunctional if(keyval[0].contains("DesktopCount")) { XWindow.= Integer.parseInt(keyval[1].trim()); }
		}
		
		new Thread(new RefreshWindowListRunner(context)).start();
	}
	
	public boolean CoordsInWindow(int xx, int yy)
	{
		if(xx >= this.X && xx <= this.X + this.Width)
		{
			if(yy >= this.Y && yy <= this.Y + this.Height)
			{
				return true;
			}
		}
		return false;
	}
	
	public void CloseWindow(Context context)
	{
		SSHHandler.XdroidCommand("closewindow " + this.ID);
		
		
		XWindow.RefreshWindowList(context);
		
	}
	
	public void FullScreen(Context context)
	{
		SSHHandler.XdroidCommand("fullscreen" + this.ID);
		
	}
	
	public void GiveFocus(Context context)
	{
		SSHHandler.XdroidCommand("gotodesktop " + Integer.toString(this.DesktopID));
		SSHHandler.XdroidCommand("focus " + this.ID);
		
		XWindow.RefreshWindowList(context);
	}
	
	public void Maximize(Context context)
	{
		SSHHandler.XdroidCommand("maximize " + this.ID);
		
		XWindow.RefreshWindowList(context);
		
	}
}
