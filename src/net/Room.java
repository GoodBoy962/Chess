package net;

/**
 * Created by aleksandrpliskin on 07.12.15.
 */
public class Room {

    private String name;

    private ServerThread player1;

    private ServerThread player2;

    public Room(String name, ServerThread player1, ServerThread player2) {
        this.name = name;
        this.player1 = player1;
        this.player2 = player2;
    }

    public Room() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServerThread getPlayer1() {
        return player1;
    }

    public void setPlayer1(ServerThread player1) {
        this.player1 = player1;
    }

    public ServerThread getPlayer2() {
        return player2;
    }

    public void setPlayer2(ServerThread player2) {
        this.player2 = player2;
    }
}
