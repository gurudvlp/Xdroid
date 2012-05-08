package com.gurudigitalsolutions.xdroid;

public class Server
{
	public String IP = "";
	public String Username = "";
	public String Password = "";
	public Boolean IsDefault = false;
	public int Port = 22;
	
	public Server(String ip, int port, String username, String password, Boolean isdefault)
	{
		this.IP = ip;
		this.Username = username;
		this.Password = password;
		this.IsDefault = isdefault;
		this.Port = port;
	}
	
	public Server()
	{
		
	}
}
