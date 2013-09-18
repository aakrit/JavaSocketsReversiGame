all: ReversiServer.class

ReversiServer.class:
	javac sockets/ReversiServer.java sockets/ReversiGame.java sockets/Players.java sockets/GameBoards.java

start:
	java sockets/ReversiServer

clean:
	rm sockets/*.class
