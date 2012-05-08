package com.gurudigitalsolutions.xdroid;

import java.util.StringTokenizer;

import android.content.Context;
import android.os.Message;
import android.util.Log;

public class RefreshWindowListRunner implements Runnable
{
public Context context;
	
	public RefreshWindowListRunner(Context thecontext)
	{
		context = thecontext;
	}
	@Override
	public void run() 
	{
		String[] eachdow = SSHHandler.XdroidCommand("windowlist").split("\n");
		//Log.w("dbg", "WindowList:");
		//Log.w("dbg", winlist.ResultHTML);
		
		XdroidActivity.XWindows = new XWindow[eachdow.length];
		for(int ewin = 0; ewin < eachdow.length; ewin++)
		{
			Log.w("dbg", eachdow[ewin]);
			String twinline = eachdow[ewin].trim();
			twinline = twinline.replace("\t\t", "\t");
			
			
			StringTokenizer st = new StringTokenizer(twinline);
			String[] winparts = new String[st.countTokens()];
			for(int ept = 0; ept < winparts.length; ept++)
			{
				winparts[ept] = st.nextToken();
			}
			XdroidActivity.XWindows[ewin] = new XWindow();
			
			//for(int ewp = 0; ewp < winparts.length; ewp++)
			//{
			//	Log.w("dbg", "WinParts:" + Integer.toString(ewp) + ":" + winparts[ewp]);
			//}
			
			int height = 0;
			int width = 0;
			
			
			
			XdroidActivity.XWindows[ewin].ID = winparts[0];
			XdroidActivity.XWindows[ewin].DesktopID = Integer.parseInt(winparts[1]);
			XdroidActivity.XWindows[ewin].X = Integer.parseInt(winparts[2]);
			XdroidActivity.XWindows[ewin].Y = Integer.parseInt(winparts[3]);
			XdroidActivity.XWindows[ewin].Width = Integer.parseInt(winparts[4]);
			XdroidActivity.XWindows[ewin].Height = Integer.parseInt(winparts[5]);
			XdroidActivity.XWindows[ewin].Title = winparts[7];
			
			/*if(XdroidActivity.XWindows[ewin].X > XdroidActivity.XWindows[ewin].Width)
			{
				width = XdroidActivity.XWindows[ewin].X - XdroidActivity.XWindows[ewin].Width;
				XdroidActivity.XWindows[ewin].X = XdroidActivity.XWindows[ewin].Width;
				XdroidActivity.XWindows[ewin].Width = width;
				
			}
			else
			{
				XdroidActivity.XWindows[ewin].Width = XdroidActivity.XWindows[ewin].Width - XdroidActivity.XWindows[ewin].X;
			}
			
			if(XdroidActivity.XWindows[ewin].Y > XdroidActivity.XWindows[ewin].Height)
			{
				height = XdroidActivity.XWindows[ewin].Y - XdroidActivity.XWindows[ewin].Height;
				XdroidActivity.XWindows[ewin].Y = XdroidActivity.XWindows[ewin].Height;
				XdroidActivity.XWindows[ewin].Height = height;
				
			}
			else
			{
				XdroidActivity.XWindows[ewin].Height= XdroidActivity.XWindows[ewin].Height - XdroidActivity.XWindows[ewin].Y;
			}*/
			//XdroidActivity.XWindows[ewin].
		}
		
		//		Snag a pic of the desktop so we can get window pics for it
		String deskthumbpath = SSHHandler.XdroidCommand("desktopthumbnail " + Integer.toString(XWindow.CurrentDesktop));
		
		//Scraper winlist = new Scraper(context, "xdroid", "xdroid");
		//winlist.URL = XWindow.BaseUrl + "/desktopthumbnail.php?desktopid=" + Integer.toString(XWindow.CurrentDesktop);
		//winlist.run();
		
		//if(winlist.PreviousStatusCode == 200)
		if(!deskthumbpath.equals("FAIL"))
		{
			for(int esw = 0; esw < XdroidActivity.XWindows.length; esw++)
			{
				if(XdroidActivity.XWindows[esw] != null
				&& XdroidActivity.XWindows[esw].DesktopID == XWindow.CurrentDesktop)
				{
					
					XdroidActivity.XWindows[esw].ThumbnailUrl = SSHHandler.XdroidCommand("windowthumbnail " +
							Integer.toString(XWindow.CurrentDesktop) + " " +
							XdroidActivity.XWindows[esw].ID + " " +
							Integer.toString(XdroidActivity.XWindows[esw].X) + " " +
							Integer.toString(XdroidActivity.XWindows[esw].Y) + " " +
							Integer.toString(XdroidActivity.XWindows[esw].Width) + " " +
							Integer.toString(XdroidActivity.XWindows[esw].Height));
				
					
				}
			}
		}
		
		//			Update the position of the mouse
		
		String mspoints = SSHHandler.XdroidCommand("getmouseposition");
		String[] mparts = mspoints.split("\\s+");
		
		
		
		try
		{
			if(mparts.length >= 3)
			{
				XWindow.MouseX = Integer.parseInt(mparts[0]);
				XWindow.MouseY = Integer.parseInt(mparts[1]);
			}
			
			Message msg = XdroidActivity.dvHandler.obtainMessage();
			msg.what = Runners.RefreshWindowListWhat;
			XdroidActivity.dvHandler.sendMessage(msg);
		}
		catch(Exception ex)
		{
			Log.w("dbg", "Excepting during RefreshWindowListRunner");
			Log.w("dbg", ":: " + ex.getMessage());
		}
	}
}
