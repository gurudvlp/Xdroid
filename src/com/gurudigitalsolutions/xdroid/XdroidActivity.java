package com.gurudigitalsolutions.xdroid;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class XdroidActivity extends Activity {
    /** Called when the activity is first created. */
	public static DrawView drawView;
	public static Handler dvHandler;
	public static XWindow[] XWindows = new XWindow[48];
	public static com.trilead.ssh2.Connection SSHConnection;
	public static String CacheDir = "";
	public static int XdroidCliVersion = 20120317;
	public static int XdroidPhpVersion = 20120323;
	public static XdroidActivity instance;
	private GestureLibrary gLib;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
        XdroidActivity.instance = this;
        
        XdroidActivity.CacheDir = this.getCacheDir().toString();
        Log.w("dbg", "Cache Dir: " + XdroidActivity.CacheDir);
        this.ClearCache();
        
        //	Copy the xdroid php scripts to the cache
        //InputStream inputStream = getResources().openRawResource(R.raw.hello
        WriteResourceFileToCache(R.raw.xdroid, "xdroid.php");
        WriteResourceFileToCache(R.raw.xdroid_cli, "xdroid-cli");
		
		WriteResourceFileBinCache(R.drawable.go_bottom, "go_bottom.png");
		WriteResourceFileBinCache(R.drawable.go_down, "go_down.png");
		WriteResourceFileBinCache(R.drawable.go_top, "go_top.png");
		WriteResourceFileBinCache(R.drawable.go_up, "go_up.png");
		
        XdroidActivity.dvHandler = new Handler(){
        	@Override
        	public void handleMessage(Message msg)
        	{
        		//	msg: 1 = update ui
        		//	msg: 2 = Saved remote file to cache
        		Log.w("dbg", "xdHandler: " + msg.toString());
        		
        		if(msg.what > 0)
        		{
        			try
        			{
        				XdroidActivity.drawView.invalidate();
        			}
        			catch(Exception ex)
        			{
        				Log.w("dbg", "Error in handleMessage invalidating drawView");
        				Log.w("dbg", ex.getMessage());
        			}
        		}
        		else
        		{
        			Log.w("dbg", "xdHandler: Couldn't determine 'what' to do.");
        		}
        		this.removeMessages(msg.what);
        	}
        };
        
        //	Commenting out the following line will force the user to manually
        //	enter the connection details each time the app starts.  Not the
        //	way I would prefer for this to work, but for beta releases, this
        //	will be fine.
        SSHHandler.Initialize(this);
        SSHHandler.Initialized = false;
        //if(SSHHandler.Initialized) { SSHHandler.Connect(); }
        
        
        
        drawView = new DrawView(this);
        drawView.setBackgroundColor(android.graphics.Color.BLACK);
        //registerForContextMenu(drawView);
        
        gLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!gLib.load()) {
			Log.w("dbg", "could not load gesture library");
			finish();
		}
 
		//GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
		//gestures.addOnGesturePerformedListener(handleGestureListener);
		
       
		
       /* XWindows[0] = new XWindow();
        XWindows[0].DesktopID = 0;
        XWindows[0].Height = 494;
        XWindows[0].Width = 1277;
        XWindows[0].X = 1;
        XWindows[0].Y = 301;
        XWindows[0].ID = "0x0A30303";
        XWindows[0].Title = "Test X Window";*/
        
        //if(SSHHandler.Initialized) { XWindow.RefreshWindowList(this); }
        //else { drawView.MainRenderer = -1; }
        drawView.MainRenderer = -1;
		
        //gestures.addView(drawView, android.view.ViewGroup.LayoutParams.FILL_PARENT);
        setContentView(drawView);
        drawView.requestFocus();
        drawView.setOnLongClickListener(handleDrawViewLongClick);
        //drawView.setOnTouchListener();
        //ArrayList<View> alv = new ArrayList<View>();
        
        //alv.add(drawView);
        
        //gestures.addTouchables(alv);
        
        //setContentView(gestures);
        
        /*if(!SSHHandler.Initialized)
        {
        	ChooseServerDialog(this);
        }*/
        ChooseServerDialog(this);
    }
    
    public boolean onCreateOptionsMenu(Menu menu)
	{
    	
		return true;

	} 
	
	public boolean onOptionsItemSelected (MenuItem item)
	{
		
		
		return true;

	}
	
	public static long TimeStamp()
	{
		return (long)(System.currentTimeMillis() / 1000L);
	}
	
	
	private OnLongClickListener handleDrawViewLongClick = new OnLongClickListener()
	{

		@Override
		public boolean onLongClick(View arg0) {
			// TODO Auto-generated method stub
			Log.w("dbg", "Long click detected.");
			
			return false;
		}
		
	};
	
	private OnGesturePerformedListener handleGestureListener = new OnGesturePerformedListener()
	{
		@Override
		public void onGesturePerformed(GestureOverlayView gestureView,
				Gesture gesture) {
 
			ArrayList<Prediction> predictions = gLib.recognize(gesture);
 
			// one prediction needed
			if (predictions.size() > 0) {
				Prediction prediction = predictions.get(0);
				// checking prediction
				if (prediction.score > 1.0) {
					// and action
					
					Toast.makeText(XdroidActivity.this, prediction.name,
							Toast.LENGTH_SHORT).show();
					
					if(prediction.name.toLowerCase().equals("rightswipe"))
					{
						//	The user has slid their finder from the left of
						//	the screen to the right
						//	The screen to the left should be exposed.
						if(XWindow.CurrentDesktop == 0) { XWindow.CurrentDesktop = 8; }
						else { XWindow.CurrentDesktop = XWindow.CurrentDesktop - 1; }
						XWindow.RefreshWindowList(XdroidActivity.this);
					}
					else if(prediction.name.toLowerCase().equals("leftswipe"))
					{
						if(XWindow.CurrentDesktop == 8) { XWindow.CurrentDesktop = 0; }
						else { XWindow.CurrentDesktop = XWindow.CurrentDesktop + 1; }
						XWindow.RefreshWindowList(XdroidActivity.this);
					}
				}
			}
 
		}

	};
	
	public static void ChooseServerDialog(final Context context)
	{
		
		FrameLayout outfrm = new FrameLayout(context);
		LinearLayout llserv = new LinearLayout(context);
		ScrollView mscroll = new ScrollView(context);
		
		final EditText servername = new EditText(context);
		final TextView tvname = new TextView(context);
		final EditText serverport = new EditText(context);
		final TextView tvport = new TextView(context);
		final EditText serveruser = new EditText(context);
		final TextView tvuser = new TextView(context);
		final EditText serverpass = new EditText(context);
		final TextView tvpass = new TextView(context);
		
		
		llserv.setOrientation(llserv.VERTICAL);
		
		servername.setText(SSHHandler.HostAddress);
		tvname.setText("Host:");
		
		serverport.setText(Integer.toString(SSHHandler.Port));
		tvport.setText("Port:");
		
		serveruser.setText(SSHHandler.Username);
		tvuser.setText("Username:");
		
		serverpass.setText(SSHHandler.Password);
		tvpass.setText("Password:");
		serverpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		final AlertDialog alert;// = new AlertDialog(context);
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		
		builder.setTitle("Server");
		
		llserv.addView(tvname);
		llserv.addView(servername);
		llserv.addView(tvport);
		llserv.addView(serverport);
		llserv.addView(tvuser);
		llserv.addView(serveruser);
		llserv.addView(tvpass);
		llserv.addView(serverpass);
		
		mscroll.addView(llserv);
		
		outfrm.addView(mscroll);
		builder.setView(outfrm);
		
		builder.setPositiveButton("Use", new DialogInterface.OnClickListener()
		{
		  
			//@Override
			public void onClick(DialogInterface dialog, int which)
			{
				//	What do I even do here?
				dialog.dismiss();
				//XWindow.BaseUrl = servername.getText().toString();
				SSHHandler.AddServer(servername.getText().toString(), Integer.parseInt(serverport.getText().toString()), serveruser.getText().toString(), serverpass.getText().toString());
				//SSHHandler.Initialize(context);
				SSHHandler.Connect();
				
				if(SSHHandler.Authenticated)
				{
					XdroidActivity.drawView.MainRenderer = 0;
					XWindow.RefreshWindowList(context);
				}
				//XdroidActivity.drawView.invalidate();
				
			}
		});
		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
		  //@Override
		  public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		  }
		});
		
		
		alert = builder.create();
		//alert.setCanceledOnTouchOutside(true);
		
		//alert.addContentView(AccountLayout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		//LayoutParams lp = new LayoutParams(this, ViewGroup.LayoutParams);
		//alert.addContentView(AccountLayout, lp);
		
		alert.show();
	}
	
	public void ClearCache()
	{
		//	Clear the apps cache.  This will force the app to get the
		//	newest screen shots of each window when it launches.
		
		File cacheDir = this.getCacheDir();
		File[] files = cacheDir.listFiles();

		if (files != null) {
		    for (File file : files)
		    {
		       file.delete();
		    }
		}

	}
	
	public void WriteResourceFileToCache(int resourceid, String CacheFilename)
	{
		InputStream inputStream = getResources().openRawResource(resourceid);
        Reader reader = new InputStreamReader(inputStream);
		
		
		StringBuilder buffer = new StringBuilder();
		
		try
		{
			char[] tmp = new char[1024];
			int l;
	
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
			reader.close();
	
		}
		catch(Exception ex)
		{
			Log.w("setup", "Exceptiong reading resource file " + CacheFilename);
		}
		try {
			FileWriter fw = new FileWriter(XdroidActivity.CacheDir + "/" + CacheFilename);
			fw.write(buffer.toString());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w("setup", "Exception while writing " + CacheFilename + " to cache");
		}
	}
	
	public void WriteResourceFileBinCache(int resourceid, String CacheFilename)
	{
		try {
	        Resources res = getResources();
	        InputStream in_s = res.openRawResource(resourceid);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        int l = 0;
	        byte[] b = new byte[1024];
	        while((l = in_s.read(b, 0, b.length)) != -1)
	        {
		        //byte[] b = new byte[in_s.available()];
		       
		        baos.write(b, 0, l);
		        
	        }
	        FileOutputStream fos = new FileOutputStream(CacheFilename);
	        fos.write(baos.toByteArray());
	        fos.close();
	        in_s.close();
	    } catch (Exception e) {
	        // e.printStackTrace();
	        Log.w("dbg", "Exception while Writeing resource bin file to cache.");
	    }

	}
	
	public void Vibrate(int millis)
	{
		// Get instance of Vibrator from current Context
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		 
		// Vibrate for 300 milliseconds
		v.vibrate(millis);
	}
}