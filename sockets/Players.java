package sockets;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Players // implements Runnable
{
	private static int color = 0;
	private static boolean blackturn = false;
	private static boolean whiteturn = false;

	public String name = null;
	private Socket sock = null;
//	private OutputStream output = null;
//	private InputStream input = null;
	private Scanner in = null;
	private PrintWriter out = null;
	private ReversiGame rg = null;
	private int playertype = 0;//1 = player, 2 = observer


	public Players(String name, Socket sock, ReversiGame game, int type) throws Exception
	{
		this.name = name;
		this.playertype = type;
		this.sock = sock;
		this.rg = game;
		setup();
	}
	private void setup()
	{
		try
		{
			if(playertype == 1)
			{
		    		InputStream input = sock.getInputStream();
		            OutputStream output = sock.getOutputStream();

		            in = new Scanner(input);
		            out = new PrintWriter(output, true /* autoFlush */);
		            System.out.println("A Player has connected to the server!");
		            out.println("Welcome to the Reversi game " + name);
		            out.println("Please enter a name or 'none' if you don't want one\r\n");
		            String pname = in.nextLine();
		            if(pname.equalsIgnoreCase("none"))
		            	;//use defaut
		            else
		            	this.name = pname;
			}
			else if(playertype == 2)//observer
			{
				//no point to having this unless multithreaded since i don't want server to
				//black read waiting on an observer
//				InputStream input = sock.getInputStream();
	            OutputStream output = sock.getOutputStream();

//	            in = new Scanner(input);
	            out = new PrintWriter(output, true /* autoFlush */);
	            out.println("Welcome " + name + " you are observing this game! \r\n");
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public String readMove() throws Exception
	{
		String move = in.nextLine();
		return move;
	}

	public void sendOutput(String move) throws Exception
	{
		out.println(move);
	}
	public void closesocket() throws IOException
	{
		sock.close();
	}
}
