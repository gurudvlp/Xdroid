package com.gurudigitalsolutions.xdroid;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class DrawView extends View
{
    Paint paint = new Paint();
    public int screenWidth = 1280;
    public int screenHeight = 800;
    
    public int canvasWidth = 1;
    public int canvasHeight = 1;
    
    public int UseColor = 0;
    
    public Context tcontext;
    
    public DockStation Dock;
    public DockStation[] Toolbars = new DockStation[8];
    
    public long LastToolbarTime = 0;
    public int ToolbarVisible = -1;
    public RenderTrackpad TrackpadRenderer;
    public boolean TrackpadEnabled = false;
    public boolean TrackpadStart = false;
    
    public int MainRenderer = 0;
    //	-1	= Not connected
    //	0	= Windows
    //	1	= Desktop grid
    
    

    public DrawView(Context context) {
        super(context);
        this.setOnTouchListener(handleOnTouch);
        tcontext = context;
        Dock = new DockStation();
        Dock.AddItem(R.drawable.input_touchpad, "Touchpad", 0, new WindowCloseRunner(context));
		Dock.AddItem(R.drawable.viewrefresh, "Refresh", 1, new WindowCloseRunner(context));
		Dock.AddItem(R.drawable.edit_find, "Zoom", 2, new WindowCloseRunner(context));
		Dock.AddItem(R.drawable.video_display, "Workspaces", 3, new WindowCloseRunner(context));
		Dock.AddItem(R.drawable.network_server, "Server", 4, new WindowCloseRunner(context));
		Dock.AddItem(R.drawable.go_jump, "Show on Computer", 5, new WindowCloseRunner(context));
		
		Toolbars[0] = new DockStation();
		
		Toolbars[0].AddItem(R.drawable.windowclose, "Close", 0, new WindowCloseRunner(context));
		Toolbars[0].AddItem(R.drawable.viewrestore, "Maximize", 1, new WindowMaximizeRunner(context));
		Toolbars[0].AddItem(R.drawable.viewfullscreen, "Fullscreen", 2, new WindowFullscreenRunner(context));
		Toolbars[0].AddItem(R.drawable.edit_find, "Focus", 3, new WindowFocusRunner(context));
		Toolbars[0].myIconSize = 64;
		
    }
    
    

    @Override
    public void onDraw(Canvas canvas) 
    {
    	this.canvasWidth= canvas.getWidth();
    	this.canvasHeight = canvas.getHeight();
    	
    	this.screenHeight = XWindow.ScreenHeight;
    	this.screenWidth = XWindow.ScreenWidth;
    	
    	//Log.w("dbg", "canvasWidth: " + Integer.toString(this.canvasWidth));
    	//Log.w("dbg", "canvasHeight: " + Integer.toString(this.canvasHeight));
    	if(SSHHandler.Initialized && this.MainRenderer < 0) { this.MainRenderer = 0; }
    	
    	
    	paint.setStrokeWidth(0);
    	if(this.TrackpadRenderer != null)
    	{
    		this.TrackpadRenderer.canvasHeight = canvasHeight;
    		this.TrackpadRenderer.canvasWidth = canvasWidth;
    	}
    	
    	//	MainRenderer: 	-1	=	Not connected
    	//					0	=	Workspace
    	//					1	=	Desktop Grid
    	/*if(this.TrackpadEnabled)
    	{
    		//	The user has trackpad-mode enabled
    	}
    	else */if(this.MainRenderer == -1) { RenderToolbox(canvas); }
    	else if(this.MainRenderer == 1)
    	{
    		//	Render a desktop grid
    		
    		canvas = RenderDesktopGrid(canvas);
    		
    		
    		if(!this.TrackpadEnabled)
			{
    			RenderMouse(canvas);
    			RenderToolbox(canvas);
			}
    		
    		
    		
    	}
    	else
    	{
	    	
    		if(XWindow.ViewOnlySelectedWindow)
	    	{
	    		Log.w("dbg", "ViewOnlySelectedWindow, so here it goes");
	    		RenderWindow(canvas, XWindow.SelectedWindow);
	    	}
	    	else
	    	{
	    		RenderWindows(canvas);
	    	}
	    	
	    	if(!this.TrackpadEnabled) { RenderMouse(canvas); }
	    	if(!this.TrackpadEnabled) { RenderToolbox(canvas); }
    	}
    	
    	if(!this.TrackpadEnabled)
    	{
	    	Log.w("dbg", "ToolbarVisible: " + Integer.toString(ToolbarVisible));
	    	if(ToolbarVisible > -1)
			{
	    		if(Toolbars[ToolbarVisible] != null)
	    		{
	    			if(Toolbars[ToolbarVisible].DimScreenOnFocus)
	    			{
	    				paint.setColor(Color.BLACK);
	    				paint.setStyle(Style.FILL);
	    				paint.setAlpha(192);
	    				canvas.drawRect(0, 0, canvasWidth, canvasHeight - DockStation.Reserved, paint);
	    				
	    				paint.setAlpha(255);
	    			}
	    			canvas = RenderToolbar(canvas, ToolbarVisible);
	    		}
				
			}
    	}
    	else
    	{
    		if(this.TrackpadRenderer == null) { this.TrackpadRenderer = new RenderTrackpad(canvasHeight, canvasWidth, tcontext); }
    		canvas = this.TrackpadRenderer.Render(canvas);
    	}

    }
    
  
    public int XtoX(int remotewindowx)
    {
    	//	Return the local x coordinate for this x..  scaled proportianately
    	
    	//float propx = (remotewindowx * canvasWidth) / this.screenWidth;
    	float multi = (float)this.canvasWidth / (float)this.screenWidth;
    	//return (int)propx;
    	return (int)(remotewindowx * multi);
    }
    
    public int YtoY(int remotewindowy)
    {
    	//float propy = (remotewindowy * canvasHeight) / this.screenHeight;
    	float multi = (float)this.canvasHeight / (float)this.screenHeight;
    	//return (int)propy;
    	//Log.w("dbg", "YtoY: " + Integer.toString(remotewindowy) + " :: " + Integer.toString((int)(remotewindowy * multi)));
    	//return (int)(remotewindowy * multi);
    	return (int)(((this.canvasHeight - DockStation.Reserved) * remotewindowy) / this.screenHeight);
    	
    }
    
    public int XtoRX(int localpointx)
    {
    	return (int)((this.screenWidth / this.canvasWidth) * localpointx);
    }
    
    public int YtoRY(int localpointy)
    {
    	return (int)((this.screenHeight / (this.canvasHeight - DockStation.Reserved)) * localpointy);
    }
    
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event)
    {
    	super.onKeyLongPress(keyCode, event);
    	Log.w("dbg", "DrawView: onKeyLongPress");
    	return false;
    	
    	
    }
    
    
    
    public OnTouchListener handleOnTouch = new OnTouchListener()
    {
    	

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// if(event.getAction() != MotionEvent.ACTION_DOWN)
	        // return super.onTouchEvent(event);
	        //Log.w("dbg", "onTouch :: DrawView");
			
			float eventx = event.getX();
			float eventy = event.getY();
			
			if(TrackpadEnabled)
			{
				//	The trackpad is enabled, so that is the mouser
				//	It needs to run
				
				if(TrackpadRenderer == null) { TrackpadRenderer = new RenderTrackpad(canvasWidth, canvasHeight, tcontext); }
				
				if(TrackpadStart)
				{
					TrackpadRenderer.onTouch(event);
				}
				
			}
			else if(eventy > canvasHeight - DockStation.Reserved
					//&& event.getAction() != MotionEvent.ACTION_DOWN
					&& event.getAction() == MotionEvent.ACTION_DOWN
					&& event.getEventTime() - LastToolbarTime > 1000)
			{
				//	The user pressed in the area reserved for the
				//	dock.
				LastToolbarTime = event.getEventTime();
				
				for(int eitem = 0; eitem < Dock.DockItems.length; eitem++)
				{
					if(Dock.DockItems[eitem] != null)
					{
						if(eventx > eitem * DockStation.IconSize)
						{
							if(eventx < (eitem * DockStation.IconSize) + DockStation.IconSize)
							{
								//	This is the button they hit
								Log.w("dbg", "Dock item " + Integer.toString(eitem) + " : " + Dock.DockItems[eitem].Name);
								XdroidActivity.instance.Vibrate(50);
								
								if(eitem == 0)
								{
									/*Log.w("dbg", "Attempting a left click event.");
									SSHHandler.XdroidCommand("clickmouse 1 1");	//clickmouse <button> <times>
									//SSHHandler.XdroidCommand("setmouseposition " + Integer.toString(XWindow.MouseX) + " " + Integer.toString(XWindow.MouseY));
									
									XWindow.RefreshWindowList(tcontext);
									invalidate();*/
									TrackpadEnabled = true;
									TrackpadStart = false;
									XdroidActivity.dvHandler.postDelayed(new TrackpadStarterRunner(), 500);
								}
								else if(eitem == 1)
								{
									Log.w("dbg", "Refreshing screen");
									
									XWindow.RefreshWindowList(tcontext);
									//invalidate();
								}
								else if(eitem == 2)
								{
									//	Zoom in to single app mode
									Log.w("dbg", "Toggling zoom on a window");
									if(XWindow.ViewOnlySelectedWindow) { XWindow.ViewOnlySelectedWindow = false; }
									else { XWindow.ViewOnlySelectedWindow = true; }
									invalidate();
								}
								else if(eitem == 3)
								{
									if(MainRenderer == 0) { MainRenderer = 1; }
									else { MainRenderer = 0; }
									invalidate();
								}
								else if(eitem == 4)
								{
									XdroidActivity.ChooseServerDialog(tcontext);
								}
								else if(eitem == 5)
								{
									//	Bring computer screen to this screen.
									Log.w("dbg", "Switching desktops from menu");
									XdroidActivity.dvHandler.post(new GotoDesktopRunner(XWindow.CurrentDesktop));
									//new Thread(new GotoDesktopRunner(XWindow.CurrentDesktop)).start();
								}
							}
						}
					}
				}
			}
			else
	        if(event.getEventTime() - event.getDownTime() > 1000)
	        {
	        	
        		Log.w("dbg", "Held down the screen for more than 1/4 second.");
        		int tx = (int)event.getX();
        		int ty = (int)event.getY();
        		Log.w("dbg", "X:Y :: " + Integer.toString(tx) + " " + Integer.toString(ty));
        		
        		if(TrackpadEnabled)
        		{
        			//	Need to disable the trackpad
        			TrackpadEnabled = false;
        			invalidate();
        		}
        		else
        		{
        			//	Do these coords intersect with a window?
	        		if(XWindow.ViewOnlySelectedWindow)
	        		{
	        			//	Only one window is shown right now, so yeah, it's
	        			// 	in it's borders.
	        			//WindowOptionsDialog(tcontext, XWindow.SelectedWindow);
	        		}
	        		else
	        		{
		        		for(int ewin = 0; ewin < XdroidActivity.XWindows.length; ewin++)
		        		{
		        			if(XdroidActivity.XWindows[ewin].DesktopID == XWindow.CurrentDesktop)
		        			{
		        				if(XdroidActivity.XWindows[ewin].CoordsInWindow(XtoRX(tx), YtoRY(ty)))
		        				{
		        					if(event.getEventTime() - LastToolbarTime > 500)
		        					{
			        					//	Found a window that was pushed down in!
			        					Log.w("dbg", "Longpress on desktop " + Integer.toString(XWindow.CurrentDesktop) + " window " + Integer.toString(ewin));
			        					//WindowOptionsDialog(tcontext, ewin);
			        					
			        					if(ToolbarVisible == 0) { ToolbarVisible = -1; }
			        					else { ToolbarVisible = 0; }
			        					LastToolbarTime = event.getEventTime();
			        					invalidate();
		        					}
		        				}
		        			}
		        		}
	        		}
        		}
	        	
	        }
	        else if(ToolbarVisible > -1)
	        {
	        	if(event.getEventTime() - LastToolbarTime > 500)
	        	{
	        		Log.w("dbg", "Toolbar active, and a press detected.");
	        		LastToolbarTime = event.getEventTime();
	        		float tx = event.getX();
	        		float ty = event.getY();
	        		
	        		int itemtouched = Toolbars[ToolbarVisible].ButtonAt((int)tx, (int)ty);
	        		
	        		if(itemtouched > -1)
	        		{
	        			Log.w("dbg", "Item touched: " + Integer.toString(itemtouched));
	        			
	        			//new Thread(Toolbars[ToolbarVisible].DockItems[itemtouched].onClick()).start();
		        		Toolbars[ToolbarVisible].DockItems[itemtouched].onClick();
		        		ToolbarVisible = -1;
		        		//invalidate();
	        		}
	        	}
	        }
	        else
	        {
	        	//super.onTouchEvent(event);
	        	//	Do the coordinates of where the touch happened match a
	        	//	window?  If so, we should 'select' that window, and have
	        	//	a border drawn around it.
	        	
	        	if((int)event.getY() < canvasHeight - DockStation.Reserved)
	        	{
		        	if(MainRenderer == 1)
		        	{
		        		//	We are looking for a desktop grid layout type press
		        		XdroidActivity.instance.Vibrate(50);
		        		
		        		int tx = (int)event.getX();
		        		int ty = (int)event.getY();
		        		
		        		int screen = 0;
		        		for(int y = 0; y < 3; y ++)
		        		{
		        			for(int x = 0; x < 3; x++)
		        			{
		        				if(tx > (x * ((float)canvasWidth)) / 3 && tx < (x * ((float)canvasWidth) / 3) + ((float)canvasWidth) / 3)
		        				{
		        					Log.w("dbg", "Found the x sector.");
		        					if(ty < canvasHeight - DockStation.Reserved && ty > (y * ((float)canvasHeight)) / 3 && ty < (y * ((float)canvasHeight) / 3) + ((float)canvasHeight) / 3)
		        					{
		        						Log.w("dbg", "Found desktop, x=" + Integer.toString(x) + " y=" + Integer.toString(y) + " screen=" + Integer.toString(screen));
		        						XWindow.CurrentDesktop = screen;
		        						MainRenderer = 0;
		        						XWindow.RefreshWindowList(tcontext);
		        						invalidate();
		        					}
		        				}
		        				
		        				screen++;
		        			}
		        		}
		        	}
		        	else if(MainRenderer == 0)
		        	{
			        	if(!XWindow.ViewOnlySelectedWindow)
			        	{
				        	int tx = (int)event.getX();
			        		int ty = (int)event.getY();
			        		
			        		Log.w("dbg", "tx, ty: " + Integer.toString(tx) + ", " + Integer.toString(ty));
			        		
				        	for(int ewin = 0; ewin < XdroidActivity.XWindows.length; ewin++)
			        		{
			        			if(XdroidActivity.XWindows[ewin] != null
			        			&& XdroidActivity.XWindows[ewin].DesktopID == XWindow.CurrentDesktop)
			        			{
			        				if(XdroidActivity.XWindows[ewin].CoordsInWindow(XtoRX(tx), YtoRY(ty)))
			        				{
			        					//	Found a window that was pushed down in!
			        					if(ty < canvasHeight - DockStation.Reserved)
			        					{
				        					Log.w("dbg", "Short press on desktop " + Integer.toString(XWindow.CurrentDesktop) + " window " + Integer.toString(ewin));
				        					XWindow.SelectedWindow = ewin;
				        					try
				        					{
				        						invalidate();
				        					}
				        					catch(Exception ex)
				        					{
				        						Log.w("dbg", "Short press on desktop invalidate exception");
				        					}
			        					}
			        				}
			        			}
			        		}
				        	
				        	XWindow.MouseX = XtoRX(tx);
				        	XWindow.MouseY = YtoRY(ty);
			        	}
			        	else
			        	{
			        		//	The user is viewing one Window in fullscreen on
			        		//	their phone or tablet.  So touches will move the
			        		//	mouse.
			        		int tx = (int)event.getX();
			        		int ty = (int)event.getY();
			        		
			        		int ewin = XWindow.SelectedWindow;
			        		
			        		
			        		
			        		float nmox = ((float)tx) * XdroidActivity.XWindows[ewin].Width;
			        		nmox = nmox / canvasWidth;
			        		
			        		float nmoy = ((float)ty) * XdroidActivity.XWindows[ewin].Height;
			        		nmoy = nmoy / (canvasHeight - DockStation.Reserved);
			        		
			        		XWindow.MouseX = (int)(nmox) + XdroidActivity.XWindows[ewin].X;
			        		XWindow.MouseY = (int)(nmoy) + XdroidActivity.XWindows[ewin].Y;
			        		
			        		//
			        		//	This should update the server as well!!!
			        		//Scraper wind = new Scraper(tcontext, "", "");
			        		/*if(event.getAction() == MotionEvent.ACTION_UP && event.getEventTime() - event.getDownTime() > 25)
			        		{
			        			//Scraper wind = new Scraper(tcontext, "", "");
			        			Log.w("dbg", "Moving the mouse and clicking.");
			        			wind.URL = XWindow.BaseUrl + "/setmouseposition.php?mousex=" +
			        					Integer.toString(XWindow.MouseX) + "&mousey=" +
			        					Integer.toString(XWindow.MouseY) + "&click=single";
			        		}
			        		else
			        		{*/
			        		/*	wind.URL = XWindow.BaseUrl + "/setmouseposition.php?mousex=" +
			        					Integer.toString(XWindow.MouseX) + "&mousey=" +
			        					Integer.toString(XWindow.MouseY);
			        		*/
			        		//}
			        		//wind.run();
			        		
			        		//new Thread(new SetMousePositionRunner(XWindow.MouseX, XWindow.MouseY)).start();
			        		XdroidActivity.dvHandler.post(new SetMousePositionRunner(XWindow.MouseX, XWindow.MouseY));
			        		//SSHHandler.XdroidCommand("setmouseposition " + Integer.toString(XWindow.MouseX) + " " + Integer.toString(XWindow.MouseY));
			        		
			        		//invalidate();
			        	}
		        	}
	        	}
	        	else
	        	{
	        		//	There was a mouse movement, and it happened in
	        		//	the space reserved for the dock
	        		Log.w("dbg", "Dock space touched");
	        	}
	        }
	        //MotionEvent.
	        return true;
			
		}
        
        
    };
    
  

    public Canvas RenderWindows(Canvas canvas)
    {
    	for(int ewin = 0; ewin < XdroidActivity.XWindows.length; ewin++)
    	{
    		if(XdroidActivity.XWindows[ewin] != null
    		&& XdroidActivity.XWindows[ewin].DesktopID == XWindow.CurrentDesktop)
    		{
    			canvas = RenderWindow(canvas, ewin);
    			
    		}
    	}
    	
    	return canvas;
    }
    
    public Canvas RenderWindow(Canvas canvas, int window)
    {
    	paint.setColor(Color.DKGRAY);
    	
    	if(XWindow.ViewOnlySelectedWindow)
    	{
    		if(XWindow.SelectedWindow == window)
    		{
    			paint.setStrokeWidth(3);
    			paint.setColor(Color.LTGRAY);
    			
    			canvas.drawRect(XtoX(XdroidActivity.XWindows[window].X), 
    					YtoY(XdroidActivity.XWindows[window].Y), 
    	    			XtoX(XdroidActivity.XWindows[window].Width) + XtoX(XdroidActivity.XWindows[window].X), 
    	    			YtoY(XdroidActivity.XWindows[window].Height) + YtoY(XdroidActivity.XWindows[window].Y) - YtoY(DockStation.Reserved), paint);
    			
    			if(XdroidActivity.XWindows[window].ThumbnailUrl != null
    					&& !XdroidActivity.XWindows[window].ThumbnailUrl.equals(""))
				{
    				if(new File(XdroidActivity.CacheDir + "/" + XdroidActivity.XWindows[window].ID + ".jpg").exists())
    				{
						Bitmap bitm;
							
						//SSHHandler.SaveRemoteFileLocally(XdroidActivity.XWindows[window].ThumbnailUrl);
			    		
			    		
						bitm = BitmapFactory.decodeFile(XdroidActivity.CacheDir + "/" + XdroidActivity.XWindows[window].ID + ".jpg");
						
		
						Rect src = new Rect();
						Rect dst = new Rect();
						
						src.top = 0;
						src.left = 0;
						src.right = bitm.getWidth();
						src.bottom = bitm.getHeight();
						
						dst.top = 0;
						dst.left = 0;
						dst.right = canvasWidth;
						dst.bottom = canvasHeight - DockStation.Reserved;
						
						canvas.drawBitmap(bitm, src, dst, paint);
					
    				}
    				else
    				{
    					//	This window has a thumbnail URL, but it isn't
    					//	cached locally.  So we need to attempt to
    					//	actually download the thumbnail
    					if(XdroidActivity.TimeStamp() - XdroidActivity.XWindows[window].LastUpdate > 30)
    					{
    						XdroidActivity.XWindows[window].LastUpdate = XdroidActivity.TimeStamp();
	    					Log.w("dbg", "Cached thumbnail for window not found, attempting to download");
	    					//new Thread(new SaveRemoteFileLocallyRunner(XdroidActivity.XWindows[window].ThumbnailUrl)).start();
	    					XdroidActivity.dvHandler.post(new SaveRemoteFileLocallyRunner(XdroidActivity.XWindows[window].ThumbnailUrl));
	    					//SSHHandler.SaveRemoteFileLocally(XdroidActivity.XWindows[window].ThumbnailUrl);
    					}
    				}
				}
    		}
    	}
    	else
    	{
    		//
    		//	The user is trying to see the whole workspace of windows
    	
	    	
			canvas.drawRect(XtoX(XdroidActivity.XWindows[window].X), 
					YtoY(XdroidActivity.XWindows[window].Y), 
	    			XtoX(XdroidActivity.XWindows[window].Width) + XtoX(XdroidActivity.XWindows[window].X), 
	    			YtoY(XdroidActivity.XWindows[window].Height) + YtoY(XdroidActivity.XWindows[window].Y), paint);
			
			
    	
	    	if(!XdroidActivity.XWindows[window].ThumbnailUrl.equals(""))
	    	{
	    		
	    		if(new File(XdroidActivity.CacheDir + "/" + XdroidActivity.XWindows[window].ID + ".jpg").exists())
	    		{
		    		//SSHHandler.SaveRemoteFileLocally(XdroidActivity.XWindows[window].ThumbnailUrl);
		    		
		    		
					Bitmap thumb = BitmapFactory.decodeFile(XdroidActivity.CacheDir + "/" + XdroidActivity.XWindows[window].ID + ".jpg");
					if(thumb != null)
					{
		    			Rect src = new Rect();
		    			Rect dst = new Rect();
		    			
		    			src.top = 0;
		    			src.left = 0;
		    			src.right = thumb.getWidth();
		    			src.bottom = thumb.getHeight();
		    			
		    			dst.top = YtoY(XdroidActivity.XWindows[window].Y);
		    			dst.left = XtoX(XdroidActivity.XWindows[window].X);
		    			dst.right = XtoX(XdroidActivity.XWindows[window].Width) + dst.left;
		    			dst.bottom = YtoY(XdroidActivity.XWindows[window].Height) + dst.top;
		    			
		    			canvas.drawBitmap(thumb, src, dst, paint);
					}
					else
					{
						Log.w("dbg", "Image was not converted to a bitmap for some reason.");
					}
	    		
	    		}
	    		else
	    		{
//	    			This window has a thumbnail URL, but it isn't
					//	cached locally.  So we need to attempt to
					//	actually download the thumbnail
					if(XdroidActivity.TimeStamp() - XdroidActivity.XWindows[window].LastUpdate > 30)
					{
						XdroidActivity.XWindows[window].LastUpdate = XdroidActivity.TimeStamp();
    					Log.w("dbg", "Cached thumbnail for window not found, attempting to download");
    					//new Thread(new SaveRemoteFileLocallyRunner(XdroidActivity.XWindows[window].ThumbnailUrl)).start();
    					XdroidActivity.dvHandler.post(new SaveRemoteFileLocallyRunner(XdroidActivity.XWindows[window].ThumbnailUrl));
    					//SSHHandler.SaveRemoteFileLocally(XdroidActivity.XWindows[window].ThumbnailUrl);
					}
	    		}
	    	}
		//	White line on left side of window
    	
	    	if(window == XWindow.SelectedWindow)
	    	{
				paint.setColor(Color.RED);
				paint.setStyle(Style.STROKE);
				paint.setStrokeWidth(2);
				
				canvas.drawRect(XtoX(XdroidActivity.XWindows[window].X),
						YtoY(XdroidActivity.XWindows[window].Y), 
						XtoX(XdroidActivity.XWindows[window].X + XdroidActivity.XWindows[window].Width),
						YtoY(XdroidActivity.XWindows[window].Height) + YtoY(XdroidActivity.XWindows[window].Y), 
						paint);
				
				
	    	}
    	}
    	
    	return canvas;
    }
    
    public Canvas RenderMouse(Canvas canvas)
    {
    	int ewin = XWindow.SelectedWindow;
		int mousex = XWindow.MouseX;
		int mousey = XWindow.MouseY;
		if(XdroidActivity.XWindows[XWindow.SelectedWindow].CoordsInWindow(mousex, mousey))
		{
			//	The mouse pointer is in the currently selected window
			//
			//		PCWIDTH		PHONEWIDTH
			//		PCHEIGHT	PHONEHEIGHT
			Log.w("dbg", "Mouse pointer is in the enlarged window.");
			Log.w("dbg", "Mouse x,y: " + Integer.toString(mousex) + "," + Integer.toString(mousey));
			
			int srnWidth;
			int srnHeight;
			if(XWindow.ViewOnlySelectedWindow)
			{
				srnWidth = this.screenWidth;
				srnHeight = this.screenHeight;
			}
			else
			{
				srnWidth = XdroidActivity.XWindows[XWindow.SelectedWindow].Width;
				srnHeight = XdroidActivity.XWindows[XWindow.SelectedWindow].Height;
			}
			int pmox = (int)(mousex - XdroidActivity.XWindows[XWindow.SelectedWindow].X);
			int pmoy = (int)(mousey - XdroidActivity.XWindows[XWindow.SelectedWindow].Y);
			
			Log.w("dbg", "gMouse x,y: " + Integer.toString(pmox) + "," + Integer.toString(pmoy));
			
			//pmox = (int)(((float)pmox) / srnWidth)) * ((float)canvasWidth)));
			//pmoy = (int)((this.canvasHeight / XdroidActivity.XWindows[XWindow.SelectedWindow].Height) * pmoy);
			//pmoy = (int)((float)(pmoy / srnHeight) * (float)canvasHeight);
			
			float tmox = ((float)pmox) / srnWidth;
			float tmoy = ((float)pmoy) / srnHeight;
			tmox = tmox * canvasWidth;
			tmoy = tmoy * canvasHeight;
			
			Log.w("dbg", "pMouse x,y: " + Integer.toString(pmox) + "," + Integer.toString(pmoy));
			Log.w("dbg", "Screen: " + Integer.toString(srnWidth) + " " + Integer.toString(srnHeight));
			Log.w("dbg", "canvas: " + Integer.toString(canvasWidth) + " " + Integer.toString(canvasHeight));
			Log.w("dbg", "tmo x, y: " + Float.toString(tmox) + " " + Float.toString(tmoy));
			paint.setColor(Color.GREEN);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(2);
			canvas.drawCircle((int)tmox, (int)tmoy, (float) 12.0, paint);
			
		}
		
    	return canvas;
    }
    
    public Canvas RenderToolbox(Canvas canvas)
    {
    	//
    	//	Render the dock for the Xdroid app.
    	//
    	
    	for(int eitem = 0; eitem < this.Dock.DockItems.length; eitem++)
    	{
    		if(Dock.DockItems[eitem] != null)
    		{
    			Bitmap thumb = BitmapFactory.decodeResource(getResources(), Dock.DockItems[eitem].IconResourceID);
    			
    			Rect src = new Rect();
    			Rect dst = new Rect();
    			
    			src.top = 0;
    			src.left = 0;
    			src.right = thumb.getWidth();
    			src.bottom = thumb.getHeight();
    			
    			dst.top = canvasHeight - DockStation.IconSize;;
    			dst.left = (eitem * DockStation.IconSize);
    			dst.right = (eitem * DockStation.IconSize) + DockStation.IconSize;
    			dst.bottom = this.canvasHeight;
    			
    			
    			
    			paint.setStyle(Paint.Style.FILL);
    			canvas.drawBitmap(thumb, src, dst, paint);
    		}
    	}
    	
    	return canvas;
    }
    
    public Canvas RenderToolbar(Canvas canvas, int toolbarid)
    {
    	Log.w("dbg", "Attempting to render toolbar " + Integer.toString(toolbarid));
    	
    	Rect bg = new Rect();
    	bg.top = Toolbars[toolbarid].CenteredY(this.canvasHeight - DockStation.Reserved);
    	bg.bottom = canvasHeight - DockStation.Reserved - bg.top;
    	
    	bg.left = Toolbars[toolbarid].CenteredX(canvasWidth);// + (DockStation.IconSize / 6);
    	bg.right = canvasWidth - bg.left;// - (DockStation.IconSize / 6);
    	Toolbars[toolbarid].UpdateScreenRect(bg);
    	
    	bg.left = bg.left + (DockStation.IconSize / 6);
    	bg.right = canvasWidth - bg.left;// - (DockStation.IconSize / 6);
    	paint.setStyle(Style.FILL);
    	paint.setColor(Color.BLUE);
    	paint.setAlpha(96);
    	canvas.drawRect(bg, paint);
    	
    	//	Update this toolbar's location on the screen
    	
    	//canvas.drawCircle(bg.left, ((bg.bottom - bg.top) / 2) + bg.top, (bg.bottom - bg.top) / 2, paint);
    	//canvas.drawCircle(bg.right, ((bg.bottom - bg.top) / 2) + bg.top, (bg.bottom - bg.top) / 2 , paint);
    	RectF toval = new RectF();
    	toval.top = bg.top;
    	toval.bottom = bg.bottom;
    	toval.left = bg.right - (DockStation.IconSize / 2);
    	toval.right = bg.right + (DockStation.IconSize / 2);
    	canvas.drawArc(toval, 270, 180, false, paint);
    	
    	toval.left = bg.left - (DockStation.IconSize / 2);
    	toval.right = bg.left + (DockStation.IconSize / 2);
    	canvas.drawArc(toval, -270, 180, false, paint);
    	
    	paint.setAlpha(255);
    	
    	
    	
    	for(int eitem = 0; eitem < this.Toolbars.length; eitem++)
    	{
    		if(Toolbars[toolbarid].DockItems[eitem] != null)
    		{
    			Log.w("dbg", "Drawing toolbarid " + Integer.toString(toolbarid) + " item " + Integer.toString(eitem));
    			Bitmap thumb = BitmapFactory.decodeResource(getResources(), Toolbars[toolbarid].DockItems[eitem].IconResourceID);
    			
    			Rect src = new Rect();
    			Rect dst = new Rect();
    			
    			src.top = 0;
    			src.left = 0;
    			src.right = thumb.getWidth();
    			src.bottom = thumb.getHeight();
    			
    			dst.top = Toolbars[toolbarid].CenteredY(canvasHeight - DockStation.Reserved);
    			dst.left = Toolbars[toolbarid].CenteredX(this.canvasWidth) + (eitem * Toolbars[toolbarid].myIconSize);
    			dst.right = dst.left + Toolbars[toolbarid].myIconSize;
    			dst.bottom = canvasHeight - DockStation.Reserved - dst.top;
    			
    			
    			
    			paint.setStyle(Paint.Style.FILL);
    			//canvas.drawRect(dst, paint);
    			canvas.drawBitmap(thumb, src, dst, paint);
    			
    			if(Toolbars[toolbarid].DrawGridAroundButtons)
    			{
    				paint.setStyle(Style.STROKE);
    				paint.setColor(Color.GRAY);
    				canvas.drawRect(dst, paint);
    			}
    			//Toolbars[toolbarid].DockItems[eitem].SetScreenPos(dst);
    		}
    	}
    	return canvas;
    }
    
    public Canvas RenderDesktopGrid(Canvas canvas)
    {
    	//
    	//	Render the grid of workspaces available on the computer
    	//	that we are connected to.
    	//
    	
    	paint.setStrokeWidth(3);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		
		int screen = 0;
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 3; x++)
			{
				//canvas.drawRect(x * (canvasWidth / 3), y * (canvasHeight / 3), x * (canvasWidth / 3) + (canvasWidth / 3), y * (canvasHeight / 3) + (canvasHeight / 3), paint);
				
				for(int ewin = 0; ewin < XdroidActivity.XWindows.length; ewin++)
				{
					if(XdroidActivity.XWindows[ewin] != null)
					{
						if(XdroidActivity.XWindows[ewin].DesktopID == screen)
						{
							int maxheight = canvasHeight / 3;
							int maxwidth = canvasWidth / 3;
							int xoffs = x * (canvasWidth / 3);
							int yoffs = y * (canvasHeight / 3);
							
							int winnewx = XdroidActivity.XWindows[ewin].X;
							int winnewy = XdroidActivity.XWindows[ewin].Y;
							int winneww = XdroidActivity.XWindows[ewin].Width;
							int winnewh = XdroidActivity.XWindows[ewin].Height;
							
							winnewx = (int)(((float)winnewx) * (((float)canvasWidth) / 3));
							winnewy = (int)(((float)winnewy) * (((float)canvasHeight) / 3));
							winneww = (int)(((float)winneww) * (((float)canvasWidth) / 3));
							winnewh = (int)(((float)winnewh) * (((float)canvasHeight) / 3));
							
							winnewx = winnewx / screenWidth;
							winnewy = winnewy / screenHeight;
							winneww = winneww / screenWidth;
							winnewh = winnewh / screenHeight;
							
							Log.w("dbg", Integer.toString(winnewx) + " " + Integer.toString(winnewy));
							paint.setStyle(Style.FILL);
							paint.setColor(Color.LTGRAY);
							canvas.drawRect(winnewx + xoffs, winnewy + yoffs, winnewx + winneww + xoffs, winnewy + winnewh + yoffs, paint);
							
							paint.setStyle(Style.STROKE);
							paint.setStrokeWidth(3);
							paint.setColor(Color.WHITE);
							canvas.drawRect(winnewx + xoffs, winnewy + yoffs, winnewx + winneww + xoffs, winnewy + winnewh + yoffs, paint);
						}
					}
				}
				
				Bitmap bitm;
				
				if(new File(XdroidActivity.CacheDir + "/" + Integer.toString(screen) + "-thumb.jpg").exists())
				{
					
					
					bitm = BitmapFactory.decodeFile(XdroidActivity.CacheDir + "/" + Integer.toString(screen) + "-thumb.jpg");
					
					if(bitm != null)
					{

						Rect src = new Rect();
						Rect dst = new Rect();
						
						src.top = 0;
						src.left = 0;
						src.right = bitm.getWidth();
						src.bottom = bitm.getHeight();
						
						//dst.top = YtoY(XdroidActivity.XWindows[window].Y);
						//dst.left = XtoX(XdroidActivity.XWindows[window].X);
						//dst.right = XtoX(XdroidActivity.XWindows[window].Width) + dst.left;
						//dst.bottom = YtoY(XdroidActivity.XWindows[window].Height) + dst.top;
						dst.top = y * (canvasHeight / 3);
						dst.left = x * (canvasWidth / 3);
						dst.right = x * (canvasWidth / 3) + (canvasWidth / 3);
						dst.bottom = y * (canvasHeight / 3) + (canvasHeight / 3);// - DockStation.Reserved;
						
						canvas.drawBitmap(bitm, src, dst, paint);
					}
					else
					{
						Log.w("dbg", "RenderDesktopGrid: Cached thumbnail could not be loaded");
					}
						
				}
				else
				{
					//	This desktop thumbnail is not currently cached.
					//	So a new thread needs to be spawned to download it.
					String dtpath = SSHHandler.XdroidCommand("desktopthumbnail " + Integer.toString(screen));
					//new Thread(new SaveRemoteFileLocallyRunner(dtpath)).start();
					XdroidActivity.dvHandler.post(new SaveRemoteFileLocallyRunner(dtpath));
					//SSHHandler.SaveRemoteFileLocally(dtpath);
					
				}
				screen++;
			}
		}
    	return canvas;
    }
    
    public Canvas RenderMousePad(Canvas canvas)
    {
    	//	Background
    	Rect bg = new Rect();
    	
    	bg.top = 0;
    	bg.bottom = canvasHeight;
    	bg.left = 0;
    	bg.right = canvasWidth;
    	
    	paint.setStyle(Style.FILL);
    	paint.setColor(Color.BLUE);
    	paint.setAlpha(96);
    	canvas.drawRect(bg, paint);
    	
    	
    	//	Scroll bar area
    	bg.top = 0;
    	bg.bottom = canvasHeight;
    	bg.left = canvasWidth - 50;
    	bg.right = canvasWidth;
    	paint.setColor(Color.rgb(0, 0, 64));
    	paint.setAlpha(128);
    	canvas.drawRect(bg, paint);
    	
    	
    	//	Mouse buttons
    	return canvas;
    }
}

