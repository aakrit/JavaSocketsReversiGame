package sockets;

import java.net.*;
import java.io.*;
import java.util.*;

//some of the code here is inspired by the class examples provided by Prof Seigel
public class ReversiServer
{

	private int port;
	private ServerSocket serversocket;
	private ReversiGame rg;
	public int playersCount = 0;
	public int observersCount = 0;
	public int players = 0;//default start with two players and one observer
	public Players[] player;// = new Players[players];
	private int currentPlayer = 0;//used to loop for the currentplayer output

	public ReversiServer(int port, ReversiGame game, int playersAndObservers)
	{
		this.port = port;
		this.rg = game;
		this.player = new Players[playersAndObservers];
		this.players = playersAndObservers;
	}
	public void startGame() throws Exception
	{
		setup();//block until two clients have connected
		String playerColor;
		boolean otherPlayer = false;
		while(true)
		{
			if(!rg.checkIfGameIsNotDone())//check if game is still running(until all 64 spots are filled)
			{
				//if the game is done
				gameOver();//let the players and observers know who won
				rg.resetBoard();//reset the board
				int playAgain = playGameAgain();
				if(playAgain == 1)//not play again
				{
					closePlayerSocket();
					break;
				}
			}
			if(currentPlayer == 0)
				playerColor = "Black";
			else
				playerColor = "White";

			if(currentPlayer == 0 && otherPlayer == false)
			{
				player[1].sendOutput("Waiting for Player 1: "+player[0].name +" to move"+"\r\n");
				otherPlayer = true;
			}
			else if(currentPlayer == 1 && otherPlayer == false)
			{
				player[0].sendOutput("Waiting for Player 2: "+player[1].name +" to move"+"\r\n");
				otherPlayer = true;
			}
			player[currentPlayer].sendOutput(player[currentPlayer].name + ", your total points are: "+rg.countPlayerPoints(currentPlayer));
			player[currentPlayer].sendOutput(player[currentPlayer].name + ", your color is "+ playerColor +
					" \r\n\r\nPlease enter a move or type 'pass' to skip your move or 'exit' to leave game\r\n");

			String move = player[currentPlayer].readMove();

			if(move.equalsIgnoreCase("exit"))
			{
				quitGame(currentPlayer);
				break;
			}
			if(move.equalsIgnoreCase("pass"))
			{
				player[currentPlayer].sendOutput("You have passed your move!"+"\r\n");
				for(int j = 2; j < players; j++)
				{
					player[j].sendOutput("Player: " + (currentPlayer+1) + " has passed their turn"+"\r\n");
				}
				otherPlayer = false;
				toggle();
				continue;
			}
			try
			{
				String[] words = move.trim().split(" ");
				int row = Integer.parseInt(words[0]);
				int col = Integer.parseInt(words[1]);
				if((rg.markBoard(row, col, currentPlayer)) == 0)
				{
					//move was valid

					System.out.println("Player " + playerColor + " placed a marker at row "
					+ words[0] + " col " + words[1]);
					player[currentPlayer].sendOutput("Valid Move Placed, you now have "+rg.countPlayerPoints(currentPlayer)+ " points!\r\n\r\n");
					for(int j = 2; j < players; j++)
					{
						player[j].sendOutput("Player " + (currentPlayer+1) + " moved to: " + row + " " + col + " \r\n" +
								"Score: \t\tPlayer 1: " + rg.countPlayerPoints(0) +"points \t\tPlayer 2: "+ rg.countPlayerPoints(1)+"points \r\n");
					}
					otherPlayer = false;
					printBoard();
					toggle();
					continue;
				}
				else
				{
					player[currentPlayer].sendOutput("\r\n\r\nCannot move there since its either out of bounds," +
							" an occupied spot, or a position where no points can be gained!\r\n" +
							"Please select another location\r\n");
					player[currentPlayer].sendOutput(rg.showBorad());
					continue;
				}
			} catch (Exception e)
			{
				player[currentPlayer].sendOutput("\n\nValues not provided in Correct format! " +
						"\r\nPlease enter a move as follows: row number [0-7] space col number [0-7] " +
					"(ie: 2 4 (if you want to move to the 2nd row 4th col position))\r\n");
				player[currentPlayer].sendOutput(rg.showBorad());
				continue;
			}
		}
		serversocket.close();
		//notify winning player and losing player
	}
	private void toggle()
	{
		if(currentPlayer == 0)
		{
			currentPlayer = 1;
		}
		else
			currentPlayer = 0;
	}
	private void quitGame(int exitingplayer)// throws Exception
	{
		try
		{
			exitingplayer += 1;
			int otherplayer = 0;
			String othercolor = null;
			if(exitingplayer == 1)
			{
				otherplayer = 2;
				othercolor = "WHITE";
			}
			else
			{
				otherplayer = 1;
				othercolor = "BLACK";
			}
			for(int i = 0; i <= player.length; i++)
			{
				player[i].sendOutput("The Game is over since player " + exitingplayer + ", "+ player[(exitingplayer-1)].name+" ,has exited the game");
				player[i].sendOutput("Thus the winner is Player: " + otherplayer + ", " + player[(otherplayer-1)].name + "! \r\n");
				player[i].closesocket();
			}
		}catch (Exception e)
		{
			System.out.println(e);;
		}
	}
	private void gameOver()// throws Exception
	{
		try{
		String winner = rg.winner();
		for(int i = 0; i <= player.length; i++)
		{
			player[i].sendOutput("The Game is now over, thanks for Playing!"+"\r\n");
			if(winner.equalsIgnoreCase("WHITE"))
			{
				player[i].sendOutput("The winner of this game is Player: 2 Color: " + winner+"\r\n");
			}
			else if(winner.equalsIgnoreCase("BLACK"))
			{
				player[i].sendOutput("The winner of this game is Player: 1 Color: " + winner+"\r\n");
			}
			else
			{
				player[i].sendOutput("No Winners Here, the Game was a TIE"+"\r\n");
			}
//			player[i].closesocket();
		}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	private void closePlayerSocket()
	{
		try
		{
			for(int i = 0; i <= player.length; i++)
			{
				player[i].closesocket();
			}
		}catch (Exception e)
			{
				e.printStackTrace();
			}
	}
	private void printBoard()// throws Exception
	{
		try{
		for(int i = 0; i <= player.length; i++)
		{
			player[i].sendOutput(rg.showBorad());
		}
		}catch (Exception e)
		{
			System.out.println(e);//print to server log
		}
	}
	public void setup() throws Exception
	{
		try
		{
			serversocket = new ServerSocket(port);//server will listen on this port
			System.out.println("This game is running on port: " + port + "... waiting for two players and "+(players-2)+ " observer(s) to join");
			for (int i = 0; i < players; ++i)//only two players can play
			{
				Socket sock = serversocket.accept(); //block waiting on signal
				if(i < 2)
				{
					System.out.println("Player " + (i+1) + " has joined the game");
					//let both users knows the game has started
					player[i] = new Players("Player "+ (i+1), sock, rg, 1);
					if(i == 0)
					{
						player[i].sendOutput("Waiting for a player and "+(players-2)+ " observer(s)" +
								" to join the game ...\r\n");
					}
					if(i==1)
					{
						player[0].sendOutput("\r\nPlayer 2 has joined ... Their name is: " + player[1].name);
						player[1].sendOutput("Waiting for "+(players-2)+
								" observer(s) to join ...");
					}
				}
				else
				{
					System.out.println("An Observer has joined the game");
					//let both users knows the game has started
					player[i] = new Players("Player "+ (i+1), sock, rg, 2);
				}
//				Runnable r = new Players("Player "+ (i+1), sock, rg);//pass the socket and game
//				player[i] = (Players) r;
//				Thread t = new Thread(r);
//				t.start();
				playersCount++;
				if(playersCount == players)
				{
					player[0].sendOutput("Two Players and all observer have joined, Starting game...\r\n");
					player[1].sendOutput("Two Players and all observer have joined, Starting game...\r\n");
					for(int j = 2; j < players; j++)
					{
						player[j].sendOutput("You are now watching the game between two players \r\n");
						player[j].sendOutput("Player 1 ("+player[0].name+") is Black and Player 2("+player[1].name+") is White \r\n");
					}
					printBoard();
				}

			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	private int playGameAgain() throws Exception
	{
		//ask each player if they want to play again
		String player1Ans, player2Ans;
		player[0].sendOutput("Would you like to play again? (yes/no)\r\n");
		player1Ans = player[0].readMove();
		player[1].sendOutput("Would you like to play again? (yes/no)\r\n");
		player2Ans = player[1].readMove();
		if(player1Ans.equalsIgnoreCase("yes") && player2Ans.equalsIgnoreCase("yes"))
		{
			for(int i = 0; i <= player.length ; i++)
			{
				player[i].sendOutput("Both Players want to play again! Starting New Game...\r\n");
				player[i].sendOutput(rg.showBorad());
			}
			return 0;
		}
		else
		{
			for(int i = 0; i <= player.length ; i++)
			{
				if(player1Ans.equalsIgnoreCase("no"))
				{
					player[i].sendOutput("Player 1 does not want to play again, GoodBye!\r\n");
				}
				if(player2Ans.equalsIgnoreCase("no"))
				{
					player[i].sendOutput("Player 2 does not want to play again, GoodBye!\r\n");
				}
			}
			return 1;//end game
		}
	}
	public static void main(String[] args)
	{
		ReversiGame game = new ReversiGame();
		int players = 2;//game always starts with two players
		int observers = 0;//default is 0 observers unlesss specified in args
		int port = 1111;//default port
		if(args.length > 0)//specific more than 1 observer since 1 is the default
		{
			if(((Integer.parseInt(args[0])) > 0) && ((Integer.parseInt(args[0])) < 48))
			{
				observers = Integer.parseInt(args[0]);		//set the number of observers specified
			}
//			if(((Integer.parseInt(args[1])) > 1111) && ((Integer.parseInt(args[1])) < 65000))
//			{
//				port = Integer.parseInt(args[1]);
//			}
		}
		int playersAndObservers = players + observers;

		ReversiServer s = new ReversiServer(port, game, playersAndObservers);
		try
		{
			s.startGame();
		} catch (Exception e)
		{
			System.out.println(e);
		}
	}

}
