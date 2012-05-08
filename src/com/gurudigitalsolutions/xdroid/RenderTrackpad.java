package com.gurudigitalsolutions.xdroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

public class RenderTrackpad
{
	public Context context;
	public int canvasHeight = 1;
	public int canvasWidth = 1;
	public Paint paint;
	
	public int BackgroundAlpha = 128;
	public int BackgroundColor = Color.BLACK;
	
	public int ScrollbarWidth = 60;
	public int ScrollbarAlpha = 192;
	public int ScrollbarColor = Color.DKGRAY;
	
	public int ButtonHeight = 60;
	public int ButtonLeftAlpha = 192;
	public int ButtonRightAlpha = 192;
	public int ButtonLeftColor = Color.rgb(192, 192, 192);
	public int ButtonRightColor = Color.rgb(192, 192, 192);
	
	public int PrevX = -1;
	public int PrevY = -1;
	
	public int DownInZone = 0;
	
	public static int ZonePad = 0;
	public static int ZoneButtons = 1;
	public static int ZoneScrollbar = 2;
	public static int ZoneClose = 3;
	
	public RenderTrackpad(int CanvasHeight, int CanvasWidth, Context tcontext)
	{
		canvasHeight = CanvasHeight;
		canvasWidth = CanvasWidth;
		paint = new Paint();
		context = tcontext;
	}
	
	public Canvas Render(Canvas canvas)
	{
		canvas = RenderBackground(canvas);
		canvas = RenderScrollBar(canvas);
		canvas = RenderButtons(canvas);
    	
		return canvas;
	}
	
	public Canvas RenderBackground(Canvas canvas)
	{
		Rect bg = new Rect();
    	
    	bg.top = 0;
    	bg.bottom = canvasHeight;
    	bg.left = 0;
    	bg.right = canvasWidth;
    	
    	paint.setStyle(Style.FILL);
    	paint.setColor(this.BackgroundColor);
    	paint.setAlpha(this.BackgroundAlpha);
    	canvas.drawRect(bg, paint);
    	
    	return canvas;
	}
	
	public Canvas RenderScrollBar(Canvas canvas)
	{
		Rect src = new Rect();
		Rect dst = new Rect();
		
		//Bitmap goup = BitmapFactory.decodeFile(XdroidActivity.CacheDir + "/go_up.png");
		//Bitmap godown = BitmapFactory.decodeFile(XdroidActivity.CacheDir + "/go_down.png");
		
		
		Rect bg = new Rect();
		
		
		bg.top = 0;
    	bg.bottom = canvasHeight - this.ScrollbarWidth;
    	bg.left = canvasWidth - this.ScrollbarWidth;
    	bg.right = canvasWidth;
    	paint.setColor(this.ScrollbarColor);
    	paint.setAlpha(this.ScrollbarAlpha);
    	canvas.drawRect(bg, paint);
    	
    	
    	/*bg.top = 0;
    	bg.bottom = this.ScrollbarWidth;
    	bg.left = canvasWidth - this.ScrollbarWidth;
    	bg.right = canvasWidth;
    	paint.setColor(Color.BLACK);
    	paint.setAlpha(this.ScrollbarAlpha);
    	paint.setStrokeWidth(4);
    	paint.setStyle(Style.FILL);
    	paint.setColor(Color.DKGRAY);
    	canvas.drawRect(bg, paint);
    	paint.setStyle(Style.STROKE);
    	canvas.drawRect(bg, paint);*/
    	
    	/*paint.setColor(Color.BLACK);
    	bg.top = canvasHeight - (this.ScrollbarWidth * 2);
    	bg.bottom = canvasHeight - this.ScrollbarWidth;
    	bg.left = canvasWidth - this.ScrollbarWidth;
    	bg.right = canvasWidth;
    	paint.setColor(Color.BLACK);
    	paint.setAlpha(this.ScrollbarAlpha);
    	paint.setStrokeWidth(4);
    	paint.setStyle(Style.FILL);
    	paint.setColor(Color.DKGRAY);
    	canvas.drawRect(bg, paint);
    	paint.setStyle(Style.STROKE);
    	paint.setStyle(Style.STROKE);
    	canvas.drawRect(bg, paint);*/
    	
    	
    	//	Arrows
    	Bitmap goup = BitmapFactory.decodeResource(context.getResources(), R.drawable.go_up);
		Bitmap godown = BitmapFactory.decodeResource(context.getResources(), R.drawable.go_down);
		Bitmap gobottom = BitmapFactory.decodeResource(context.getResources(), R.drawable.go_bottom);
		Bitmap gotop = BitmapFactory.decodeResource(context.getResources(), R.drawable.go_top);
		
		paint.setAlpha(255);
		
		src.left = 0;
		src.top = 0;
		src.right = goup.getWidth();
		src.bottom = goup.getHeight();
		
		dst.top = 0;
		dst.left = canvasWidth - this.ScrollbarWidth;
		dst.right = canvasWidth;
		dst.bottom = this.ScrollbarWidth;
		
		canvas.drawBitmap(gotop, src, dst, paint);
		
		dst.top = canvasHeight - (this.ScrollbarWidth * 2);
		dst.bottom = canvasHeight - this.ScrollbarWidth;
		dst.left = canvasWidth - this.ScrollbarWidth;
		dst.right = canvasWidth;
		
		canvas.drawBitmap(gobottom, src, dst, paint);
		
		dst.top = canvasHeight - (this.ScrollbarWidth * 3);
		dst.bottom = canvasHeight - (this.ScrollbarWidth * 2);
		canvas.drawBitmap(godown, src, dst, paint);
		
		dst.top = this.ScrollbarWidth;
		dst.bottom = this.ScrollbarWidth * 2;
		canvas.drawBitmap(goup, src, dst, paint);
		return canvas;
	}
	
	public Canvas RenderButtons(Canvas canvas)
	{
		Rect bg = new Rect();
		
		bg.top = canvasHeight - this.ButtonHeight;
		bg.bottom = canvasHeight;
		bg.left = 0;
		bg.right = (canvasWidth - this.ScrollbarWidth) / 2;
		
		paint.setStyle(Style.FILL);
		paint.setColor(this.ButtonLeftColor);
		paint.setAlpha(this.ButtonLeftAlpha);
		canvas.drawRect(bg, paint);
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.BLACK);
		canvas.drawRect(bg, paint);
		
		bg.left = bg.right + 1;
		bg.right = canvasWidth - this.ScrollbarWidth - 1;
		
		paint.setStyle(Style.FILL);
		paint.setColor(this.ButtonRightColor);
		paint.setAlpha(this.ButtonRightAlpha);
		canvas.drawRect(bg, paint);
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.BLACK);
		canvas.drawRect(bg, paint);
		
		////
		//	Draw a close button over top of the trackpad logo
		
		paint.setStyle(Style.FILL);
		Bitmap ctp = BitmapFactory.decodeResource(context.getResources(), R.drawable.input_touchpad);
		Rect src = new Rect();
		Rect dst = new Rect();
		src.left = 0;
		src.top = 0;
		src.bottom = ctp.getHeight();
		src.right = ctp.getWidth();
		
		dst.left = canvasWidth - this.ScrollbarWidth;
		dst.right = canvasWidth;
		dst.top = canvasHeight - this.ScrollbarWidth;
		dst.bottom = canvasHeight;
		
		canvas.drawBitmap(ctp, src, dst, paint);
		
		paint.setColor(Color.RED);
		paint.setAlpha(255);
		paint.setStrokeWidth(5);
		
		/*canvas.drawLine(canvasWidth - this.ScrollbarWidth + 3, 
				canvasHeight - this.ScrollbarWidth + 3, 
				canvasWidth - 3,
				canvasHeight - 3,
				paint);*/
		
		canvas.drawLine(canvasWidth - this.ScrollbarWidth + 7,
				canvasHeight - 7, 
				canvasWidth - 7, 
				canvasHeight - this.ScrollbarWidth + 7,
				paint);
		
		RectF circ = new RectF();
		
		circ.left = dst.left;
		circ.right = dst.right;
		circ.bottom = dst.bottom;
		circ.top = dst.top;
		paint.setColor(Color.RED);
		paint.setStrokeWidth(5);
		paint.setStyle(Style.STROKE);
		canvas.drawArc(circ, 0, 360, true, paint);
		
		
		return canvas;
	}
	
	public void onTouch(MotionEvent event)
	{
		Log.w("tp", "Trackpad onTouch event");
		int eventx = (int)event.getX();
		int eventy = (int)event.getY();
		
		if(event.getAction() == event.ACTION_DOWN)
		{
			//	Determine what zone the screen was pressed
			if(eventx > canvasWidth - this.ScrollbarWidth && eventy < canvasHeight - this.ScrollbarWidth)
			{
				this.DownInZone = RenderTrackpad.ZoneScrollbar;
			}
			else if(eventy > canvasHeight - this.ButtonHeight && eventx < canvasWidth - this.ScrollbarWidth)
			{
				this.DownInZone = RenderTrackpad.ZoneButtons;
				
				if(eventx < (canvasWidth - this.ScrollbarWidth) / 2)
				{
					//	Left button was pressed
					Log.w("tp", "onTouch: Left mouse button pressed");
					new Thread(new MouseClickRunner(1, 1)).start();
				}
				else
				{
					//	Right button was pressed
					Log.w("tp", "onTouch: Right mouse button pressed.");
					new Thread(new MouseClickRunner(3, 1)).start();
				}
			}
			else if(eventx > canvasWidth - this.ScrollbarWidth && eventy > canvasHeight - this.ScrollbarWidth)
			{
				//	Right bottom corner of the screen (close icon)
				this.DownInZone = RenderTrackpad.ZoneClose;
				
				XdroidActivity.drawView.TrackpadEnabled = false;
				XdroidActivity.drawView.TrackpadStart = false;
				XdroidActivity.drawView.invalidate();
			}
			else
			{
				this.DownInZone = RenderTrackpad.ZonePad;
			}
		}
		else if(this.PrevX < 0 || this.PrevY < 0) { this.PrevX = eventx; this.PrevY = eventy; }
		else
		if(event.getAction() == event.ACTION_MOVE)
		{
			int xdist = eventx - this.PrevX;
			int ydist = eventy - this.PrevY;
			
			
			xdist = (int)((float)(XWindow.ScreenWidth * xdist) / ((float)canvasWidth));
			ydist = (int)((float)(XWindow.ScreenHeight * ydist) / ((float)canvasHeight));
			Log.w("tp", "onTouch: xdist,ydist: " + Integer.toString(xdist) + " " + Integer.toString(ydist));
			
			if(this.DownInZone == RenderTrackpad.ZonePad)
			{
				new Thread(new SetMousePositionRunner(xdist, ydist, true)).start();
			}
			else if(this.DownInZone == RenderTrackpad.ZoneScrollbar)
			{
				//	Scrolling logic
				if(ydist < 0)
				{
					//	Finger swiped upward
					ydist = ydist * -1;
					int pct = (int)(((float)ydist) / canvasHeight);
					
					if(pct > 50)
					{
						new Thread(new MouseClickRunner(4, 5, false)).start();
					}
					else
					{
						new Thread(new MouseClickRunner(4, 2, false)).start();
					}
				}
				else
				{
					//	Finger swiped downward
					int pct = (int)(((float)ydist) / canvasHeight);
					
					if(pct > 50)
					{
						new Thread(new MouseClickRunner(5, 5, false)).start();
					}
					else
					{
						new Thread(new MouseClickRunner(5, 2, false)).start();
					}
				}
			}
			this.PrevX = eventx;
			this.PrevY = eventy;
		}
		else
		if(event.getAction() == event.ACTION_UP)
		{
			this.PrevX = -1;
			this.PrevY = -1;
			this.DownInZone = -1;
			//new Thread(new SetMousePositionRunner(distx, disty, true)).start();
		}
	
		
		
		
		
	}
}
