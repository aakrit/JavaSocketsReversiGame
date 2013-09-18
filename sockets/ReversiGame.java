package sockets;

import java.util.Scanner;

// pesudo Code
// Create game in Main

public class ReversiGame {

	//game fields
	private int[][] board;
	public int[][] getBoard() {
		return board;
	}

	private static final int totalrow = 8; //num of rows on the game board
	private static final int totalcol = 8; //num of cols on the game board
	public static final int B = 0; //black color team
	public static final int W = 1; //white color team
	public static final int EMPTY = -1;

	public static final int valid_move = 0;
	public static final int invalid_move = -1;
//	public static final int winning_move = 1;

	public static final String white = "WHITE";
	public static final String black = "BLACK";
	public static final String tie = "TIE";

	public ReversiGame()
	{
		board = new int[totalrow][totalcol]; //create board
		//initial board with empty values
		for(int i = 0; i < totalrow; ++i)
			for(int j = 0; j < totalcol; ++j)
				board[i][j] = EMPTY;
		initialBoardSet();
	}
	private void initialBoardSet()
	{
		board[3][3] = B;
		board[3][4] = W;
		board[4][3] = W;
		board[4][4] = B;
	}
	//method to reset the board
	public void resetBoard()
	{
		for(int i = 0; i < totalrow; ++i)
			for(int j = 0; j < totalcol; ++j)
				board[i][j] = EMPTY;
		initialBoardSet();
	}
	public int countPlayerPoints(int player)
	{
		int points = 0;
		for (int[] row : board)
		{
			for (int value : row)
			{
				if(value == W && player == W)
				{
					points++;
				}
				else if(value == B && player == B)
				{
					points++;
				}
			}
		}
		return points;
	}
	//return true if game is over, false otherwise
	public boolean checkIfGameIsNotDone()
	{
		for(int i = 0; i < totalrow; ++i)
		{
			for(int j = 0; j < totalcol; ++j)
			{
				if(board[i][j] == EMPTY)
				{
					return true;
				}
			}
		}
		return false;//only return false if game is over
	}
	//method to show board current layout
	public String showBorad() //trying to make it look decent
	{
		StringBuffer s = new StringBuffer();
		int j = 0;
		String white = "\u25CB", black = "\u25CF", empty = "\u2212";
		s.append("\r\n");
		s.append("   |     |     |     |     |     |     |     |     |   \r\n");
		for(int i = -1; i < 8; i++)
		{
			if(i == -1)
			{
				s.append("   |  ");
			}
			else
			{
				s.append(i+"  |  ");
			}
		}
		s.append("\r\n");
		s.append("___|_____|_____|_____|_____|_____|_____|_____|_____|___\r\n");
		s.append("   |     |     |     |     |     |     |     |     |   \r\n");
		for (int[] row : board)
		{
			s.append(j+"  |  ");
			for (int value : row)
			{
				if(value == B)
				{
					s.append(black+"  |  ");
				}
				else if (value == W)
				{
					s.append(white+"  |  ");
				}
				else
					s.append("   |  ");
			}
			s.append("\r\n");
			s.append("___|_____|_____|_____|_____|_____|_____|_____|_____|___\r\n");
			s.append("   |     |     |     |     |     |     |     |     |   \r\n");
			j++;
		}

		return s.toString();
	}
	//method for player to place a marker on the board
	//if returns -1, move is invalid, else move is valid
	public int markBoard(int row, int col, int markercolor)
	{
		if(markercolor != B && markercolor != W)
		{
			System.out.println("Incorrect Marker Color, Server Error!");
			return -1;
		}
		int input = isPlacementOk(row, col, markercolor);
		if (input == invalid_move)
		{
			return invalid_move; //return -1
		}
		else
		{
//			board[row][col] = markercolor;
			return valid_move; //return 1
		}
	}

	//method to check whether marker placement is allowed
	private int isPlacementOk(int row, int col, int markercolor)
	{
		//check if location is within board
		if ((row >= totalrow || row < 0) || (col >= totalcol || col < 0))
			return invalid_move;
		//check if location is on an empty square
		else if (board[row][col] != EMPTY)
			return invalid_move;
		//check if location is next to an actual value and points
		else if (check(row, col, markercolor) == false)
		{
			return invalid_move;
		}
		else
			return valid_move;
	}
	//method to check whether the same color connects two rows, cols,
	//or diagnols are along same line
	private boolean check(int row, int col, int color)
	{
		board[row][col] = color;
		int points = 0;
		boolean somepoints = false;
		//check each direction
		for (int i = -1; i < 2; ++i)//for i=row from -1 to 0 to 1
		{
			for (int j = -1; j < 2; ++j)//for j=col from -1 to 0 to 1
			{
				points = getPoints(row + i, col + j, color, i, j);
				if (points > 0)//can get points in that direction
				{
					somepoints = true;
					setPoints(row + i, col + j, color, i, j);
					points = 0;
				}
			}
		}
		if(somepoints)//user can get points by placing the marker at this location
			return true;
		else
		{
			board[row][col] = EMPTY;
			return false;
		}
	}
	private int getPoints(int row, int col, int color, int rowchange, int colchange)
	{
		   int i = row, j = col, points = 0;
		   while (true)
		   {
		      if (i < 0 || j < 0 || i > 7 || j > 7 || board[i][j] == EMPTY)
		         return 0;
		      else if (board[i][j] == color)
		         return points;
		      else
		         points++;

		      i += rowchange;
		      j += colchange;
		   }
	}
		private void setPoints(int row, int col, int color, int rowchange, int colchange)
		{
		   int i = row, j = col;
		   while (true)
		   {
		      if (i < 0 || j < 0 || i > 7 || j > 7 || board[i][j] == EMPTY)
		      {
		         return;
		      }
		      else if (board[i][j] == color)
		         return;
		      else
		      {
//		    	  System.out.println(i + " " + j );
		         board[i][j] = color;//change the color of that piece
		      }
		      i += rowchange;
		      j += colchange;
		   }
		}
	//method to check who the winner is
	//count up all the Blacks and Whites
	public String winner()
	{
		int blackcount = 0;
		int whitecount = 0;

		for (int[] row : board)
		{
			for (int value : row)
			{
				if(value == B)
					blackcount++;
				else if(value == W)
					whitecount++;
			}
		}
		if (blackcount > whitecount)
			return black;
		else if(blackcount < whitecount)
			return white;
		else //(blackcount == whitecount)
			return tie;
	}

	//the main method in this game can be used if the uses wants to play with themselves
	public static void main(String[] args) throws Exception
	{
		ReversiGame rg = new ReversiGame();
//		boolean marker = true;
		int markercolor = 0;
		System.out.println("Welcome to the Reversi Game\r\n");
		Scanner in = new Scanner(System.in);
		System.out.println("The size of this board is " + rg.getBoard().length + " X " + rg.getBoard().length);
		while(rg.checkIfGameIsNotDone())
		{
			System.out.println(rg.showBorad());
			String currentPlayer = (markercolor == B) ? "Black" : "White";
//			System.out.println("current player: " + currentPlayer);

			System.out.println("Player: " + currentPlayer + "! Please enter the row (of 'E' to exit): ");
			String row = in.next();//get and store user input
			if (row.equals("E"))
			{
				System.out.println("Thanks for playing");
				break;
			}
			if (row.equals("pass"))
			{
				System.out.println("passing your turn!");
				markercolor = (markercolor == 1) ? 0 : 1;
				continue;
			}
			System.out.println("Player: " + currentPlayer + "! Please enter the Col (of 'E' to exit): ");
			String col = in.next();
			if (col.equals("E"))
			{
				System.out.println("Thanks for playing");
				break;
			}
			if(rg.markBoard(Integer.parseInt(row), Integer.parseInt(col), markercolor) == -1)
			{
				System.out.println(currentPlayer + " : Incorrect Values Supplied! Please try again\r");
			}
			else
			{
				System.out.println();
				System.out.println("Player " + currentPlayer + " Your move was: " + row + ", " + col+"\r\n");
				System.out.println("Your current points are: " + rg.countPlayerPoints(markercolor)+"\r\n");
				markercolor = (markercolor == 1) ? 0 : 1;
			}

		}
		String winner = rg.winner();
		System.out.println("The Winner is: " + winner+"\r\n");

	}

}

//orginally the marker placement check was done using brute force for loop directional checking

//private boolean checkConnection(int row, int col, int markercolor)
//{
//
//	//check if placemet is valid
//	int totalPoints = 0;//start with 0 points
//	int points = 0;
//	//place the piece on the board to do the check
//	board[row][col] = markercolor;
//	//visit all 8 directions check for three conditions:
//	//empty, samecolor, differtcolor
//	//check north
//	int i = 0;
//	int j = 0;
//
//	i = row-1;
//	j = 0;
//	while(true)//start the value to check
//	{
//
//		if((i < 0) || board[i][col] == EMPTY)
//		{
//			points = 0;
//			break;
//		}
//		else if(board[i][col] == markercolor)
//			break;
//		else if(board[i][col] != markercolor)
//		{
//			points++;
//			continue;
//		}
//		i--;
//	}
//	totalPoints += points;
//	points = 0;
//
//	//check northeast
//	i = row-1;
//	j = col+1;
//	while(true)//start the value to check
//	{
//			if((i < 0) || (j > 7) || board[i][j] == EMPTY)
//			{
//				points = 0;
//				break;
//			}
//			else if(board[i][j] == markercolor)
//				break;
//			else
//			{
//				points++;
//			}
//			i--;
//			j++;
//	}
//	totalPoints += points;
//	points = 0;
//
//	//check east
//	i = 0;
//	j = col+1;
//	while(true)//start the value to check
//	{
//		if((j > 7) || board[row][j] == EMPTY)
//		{
//			points = 0;
//			break;
//		}
//		else if(board[row][j] == markercolor)
//			break;
//		else //if(board[row][i] != markercolor)
//		{
//			points++;
//		}
//		j++;
//	}
//	totalPoints += points;
//	points = 0;
//	//check southeast
//	i = row+1;
//	j = col+1;
//	while(true)//start the value to check
//	{
//		if((i > 7) || (j > 7) || board[i][j] == EMPTY)
//		{
//			points = 0;
//			break;
//		}
//		else if(board[i][j] == markercolor)
//			break;
//		else
//		{
//			points++;
//		}
//		i++;
//		j++;
//	}
//	totalPoints += points;
//	points = 0;
//
//	//check south
//	i = row+1;
//	j = 0;
//	while(true)//start the value to check
//	{
//		if((i > 7) || board[i][col] == EMPTY)
//		{
//			points = 0;
//			break;
//		}
//		else if(board[i][col] == markercolor)
//			break;
//		else //if(board[i][col] != markercolor)
//		{
//			points++;
//		}
//		i++;
//	}
//	totalPoints += points;
//	points = 0;
//
//	//check southwest
//	i = row+1;
//	j = col-1;
//	while(true)//start the value to check
//	{
//		if((i > 7) || (j < 0) || board[i][j] == EMPTY)
//		{
//			points = 0;
//			break;
//		}
//		else if(board[i][j] == markercolor)
//			break;
//		else
//		{
//			points++;
//		}
//		i--;
//		j++;
//	}
//	totalPoints += points;
//	points = 0;
//
//	//check west
//	i = 0;
//	j = col-1;
//	while(true)//start the value to check
//	{
//		if((j < 0) || board[row][j] == EMPTY)
//		{
//			points = 0;
//			break;
//		}
//		else if(board[row][j] == markercolor)
//			break;
//		else //if(board[row][i] != markercolor)
//		{
//			points++;
//		}
//		j--;
//	}
//	totalPoints += points;
//	points = 0;
//
//	//check northwest
//	i = row-1;
//	j = col-1;
//	while(true)//start the value to check
//	{
//		if((i < 0) || (j < 0) || board[i][j] == EMPTY)
//		{
//			points = 0;
//			break;
//		}
//		else if(board[i][j] == markercolor)
//			break;
//		else
//		{
//			points++;
//		}
//		i--;
//		j++;
//	}
//	totalPoints += points;
//
//	if (totalPoints > 0)
//	{
//		//update the board
//
//		return true;
//	}
//	else
//	{
//		//set board back to empty
//		board[row][col] = EMPTY;
//		return false;
//	}
//}
