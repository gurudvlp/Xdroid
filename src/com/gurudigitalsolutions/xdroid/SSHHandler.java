package com.gurudigitalsolutions.xdroid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import com.trilead.ssh2.Session;

public class SSHHandler
{
	public static Connection SSHConnection;
	public static String Username = "";
	public static String Password = "";
	public static String HostAddress = "";
	public static int Port = 22;
	public static ServerDataSource ServerData;
	public static boolean Initialized = false;
	public static String HomePath = "";
	public static boolean Authenticated = false;
	public static Context context;
	
	public static void Initialize(Context tcontext)
	{
		SSHHandler.context = tcontext;
		SSHHandler.ServerData = new ServerDataSource(tcontext);
		SSHHandler.ServerData.open();
		
		Server sinfo = SSHHandler.ServerData.DefaultServerInfo();
		if(sinfo == null)
		{
			//	ooh :(    no server is saved as the default yet.
		}
		else
		{
			//	Hell yeah, found a default server to load up.
			SSHHandler.HostAddress = sinfo.IP;
			SSHHandler.Port = sinfo.Port;
			SSHHandler.Username = sinfo.Username;
			SSHHandler.Password = sinfo.Password;
			SSHHandler.Initialized = true;
		}
		//SSHHandler.SSHConnection = new Connection(HostAddress, Port);
		
	}
	
	public static void Connect()
	{
		Log.w("ssh", "Attempting to connect to the ssh server.");
		SSHHandler.SSHConnection = new Connection(HostAddress, Port);
		try {
			SSHHandler.SSHConnection.connect();
			if(SSHHandler.SSHConnection.authenticateWithPassword(Username, Password))
			{
				Log.w("ssh", "Authentication successful");
				SSHHandler.Authenticated = true;
				SSHHandler.HomePath = SSHHandler.ExecCommand("echo \"$HOME\"").trim();
				String folders = SSHHandler.ExecCommand("ls " + SSHHandler.HomePath + " -Al | grep xdroid");
				folders = folders.trim();
				
				if(folders.length() < 10)
				{
					//	Xdroid appears to not be installed
					SSHHandler.ExecCommand("mkdir " + SSHHandler.HomePath + "/xdroid");
					SSHHandler.ExecCommand("mkdir " + SSHHandler.HomePath + "/xdroid/images");
					SSHHandler.ExecCommand("mkdir " + SSHHandler.HomePath + "/xdroid/images/desktops");
					SSHHandler.ExecCommand("mkdir " + SSHHandler.HomePath + "/xdroid/images/winthumbs");
					
					
				}
				
				boolean installscripts = false;
				boolean xdcliexists = false;
				boolean xdphpexists = false;
				String cliver = "";
				String phpver = "";
				int CLIver = 0;
				int PHPver = 0;
				
				//	Check if Xdroid files exist already
				String xdsc = SSHHandler.ExecCommand("ls " + SSHHandler.HomePath + "/xdroid | grep xdroid-cli").trim();
				if(xdsc.length() < 6)
				{
					//	xdroid-cli script does not exist!
					Log.w("setup", "The xdroid-cli script does not appear to be present.");
					Log.w("setup", ":: " + xdsc);
					installscripts = true;
				}
				else
				{
					xdcliexists = true;
					cliver = SSHHandler.ExecCommand("cat " + SSHHandler.HomePath + "/xdroid/xdroid-cli | grep \"Xdroid Version\"").trim();
					
					if(cliver.length() < 10) { CLIver = 0; }
					else
					{
						cliver = cliver.replace("//", "").trim();
						cliver = cliver.replace("Xdroid Version:", "").trim();
						
						CLIver = Integer.parseInt(cliver);
					}
					Log.w("setup", "xdroid-cli version: " + cliver);
				}
				
				xdsc = SSHHandler.ExecCommand("ls " + SSHHandler.HomePath + "/xdroid | grep xdroid.php").trim();
				if(xdsc.length() < 6)
				{
					//	xdroid.php file does not exist!
					Log.w("setup", "The xdroid.php file does not appear to be present.");
					Log.w("setup", ":: " + xdsc);
					installscripts = true;
				}
				else 
				{
					xdphpexists = true;
					phpver = SSHHandler.ExecCommand("cat " + SSHHandler.HomePath + "/xdroid/xdroid.php | grep \"Xdroid Version\"").trim();
					
					if(phpver.length() < 10) { PHPver = 0; }
					else
					{
						phpver = phpver.replace("//", "").trim();
						phpver = phpver.replace("Xdroid Version:", "").trim();
						
						PHPver = Integer.parseInt(phpver);
					}
					Log.w("setup", "xdroid.php version: " + phpver);
				}
				
				if(installscripts)
				{
					if(!xdcliexists)
					{
						//	The xdroid-cli script doesn't exist, so it needs
						//	to be installed
						
						SSHHandler.SaveLocalFileRemotely(XdroidActivity.CacheDir + "/xdroid-cli", SSHHandler.HomePath + "/xdroid", "0755");
					}
					
					if(!xdphpexists)
					{
						//	The xdroid.php script doesn't exist, so it needs
						//	to be installed
						SSHHandler.SaveLocalFileRemotely(XdroidActivity.CacheDir + "/xdroid.php", SSHHandler.HomePath + "/xdroid", "0755");
					}
				}
				else
				{
					//	So there are scripts installed already, but are they
					//	new?
					//
					//	Who cares for now, haha
					if(CLIver < XdroidActivity.XdroidCliVersion)
					{
						SSHHandler.SaveLocalFileRemotely(XdroidActivity.CacheDir + "/xdroid-cli", SSHHandler.HomePath + "/xdroid", "0755");
					}
					
					if(PHPver < XdroidActivity.XdroidPhpVersion)
					{
						SSHHandler.SaveLocalFileRemotely(XdroidActivity.CacheDir + "/xdroid.php", SSHHandler.HomePath + "/xdroid", "0755");
					}
				}
				
			}
			else
			{
				Log.w("ssh", "Authentication failed :(");
				SSHHandler.Initialized = false;
				XdroidActivity.ChooseServerDialog(context);
			}
			
			//SSHHandler.SSHConnection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w("ssh", "SSH Connection resulted in an exception.");
			Log.w("ssh", ":" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static String ExecCommand(String command) throws IOException
	{
		Log.w("ssh", "ExecCommand: " + command);
		Session sshSession = SSHHandler.SSHConnection.openSession();
		sshSession.requestDumbPTY();
		
		
		//sshSession.startShell();
		InputStream sshstream = sshSession.getStdout();
		Reader reader = new InputStreamReader(sshstream);
		sshSession.execCommand("export DISPLAY=:0; " + command);
		//Reader reader = new InputStreamReader(instream, charset);
		
		StringBuilder buffer = new StringBuilder();
		//ByteBuilder bytebuffer = new ByteBuilder();
		
		try
		{
			char[] tmp = new char[1024];
			int l;
	
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
			Log.w("ssh", "Done reading stdout");
	
		}
		catch(Exception ex)
		{
			Log.w("ssh", "Exceptiong reading stdout");
		}
		finally {
	
			reader.close();
	
		}
	
		
		Log.w("ssh", "ssh buffer: " + buffer.toString());
		sshSession.close();
		
		return buffer.toString();
	}
	
	public static byte[] ExecCommandBytes(String command) throws IOException
	{
		Log.w("ssh", "ExecCommand: " + command);
		Session sshSession = SSHHandler.SSHConnection.openSession();
		sshSession.requestDumbPTY();
		
		
		//sshSession.startShell();
		InputStream sshstream = sshSession.getStdout();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		int bytesread;
		
		
		sshSession.execCommand("export DISPLAY=:0; " + command);
		while ((bytesread = sshstream.read(buffer)) > 0) {
	        //baos.write(buffer, 0, bytesread);
	        baos.write(buffer);
	        buffer = new byte[8192];
	    }

		
		
		Log.w("ssh", "ssh buffer: " + buffer.toString());
		sshSession.close();
		sshstream.close();
		
		return baos.toByteArray();
	}
	
	public static String XdroidCommand(String command)
	{
		//	Input should be like "setmouseposition 20 20"
		try {
			return ExecCommand(SSHHandler.HomePath + "/xdroid/xdroid-cli " + command);
			//return ExecCommand("/home/guru/xdroid/xdroid-cli " + command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static byte[] XdroidCommandBytes(String command)
	{
		//	Input should be like, "windowthumbnail windowid"
		//	and the output will be a byte array
		try
		{
			return ExecCommandBytes(SSHHandler.HomePath + "/xdroid/xdroid-cli " + command);
			//return ExecCommandBytes("/home/guru/xdroid/xdroid-cli " + command);
		} catch(IOException e) {
			Log.w("ssh", "Exception in XdroidCommandBytes");
		}
		
		return null;
	}
	
	public static void AddServer(String ip, int port, String user, String pass)
	{
		ServerData.createServerEntry(ip, port, user, pass, true);
		SSHHandler.HostAddress = ip;
		SSHHandler.Port = port;
		SSHHandler.Username = user;
		SSHHandler.Password = pass;
	}
	
	public static Bitmap GetBitmap(String path)
	{
		Bitmap bitm;
		
		Log.w("ssh", "GetBitmap: " + path);
		Session sshSession;
		try {
			sshSession = SSHHandler.SSHConnection.openSession();
			sshSession.requestDumbPTY();
			
			
			//sshSession.startShell();
			InputStream sshstream = sshSession.getStdout();
			sshSession.execCommand("cat " + path);
			bitm = BitmapFactory.decodeStream(sshstream);
			
			sshstream.close();
			sshSession.close();
			return bitm;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public static void SaveRemoteFileLocally(String filename)
	{
		SCPClient scpclient = new SCPClient(SSHHandler.SSHConnection);
		try {
			scpclient.get(filename, XdroidActivity.CacheDir);
		} catch (IOException e) {
			Log.w("ssh", "Exception while copying remote file to local device.");
			Log.w("ssh", e.getMessage());
		}
	}
	
	public static void SaveLocalFileRemotely(String filename, String remotedir, String mode)
	{
		SCPClient scpclient = new SCPClient(SSHHandler.SSHConnection);
		try{
			scpclient.put(filename, remotedir, mode);
		}catch(IOException e){
			Log.w("ssh", "Exception while copying local file to remote device.");
			Log.w("ssh", e.getMessage());
		}
	}
}
