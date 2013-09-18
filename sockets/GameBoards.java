package sockets;

import java.util.ArrayList;

public class GameBoards {

	private static int totalboards = 0;//set start to zero

	public static int getTotalboards() {
		return totalboards;
	}

	private int boardnumber = 0; //number for this current board

	private String whiteplayer;
	private String blackplayer;
	private ArrayList<String> observers;

	public GameBoards(int boardnumber)
	{
		this.boardnumber = boardnumber;
		totalboards++;
	}

	public int getBoardnumber() {
		return boardnumber;
	}

	private void setBoardnumber(int boardnumber) {
		this.boardnumber = boardnumber;
	}

	public String getWhiteplayer() {
		return whiteplayer;
	}

	public void setWhiteplayer(String whiteplayer) {
		this.whiteplayer = whiteplayer;
	}

	public String getBlackplayer() {
		return blackplayer;
	}

	public void setBlackplayer(String blackplayer) {
		this.blackplayer = blackplayer;
	}

	public ArrayList<String> getObservers() {
		return observers;
	}

	public void addObservers(String observer) {
		this.observers.add(observer);
	}

}
