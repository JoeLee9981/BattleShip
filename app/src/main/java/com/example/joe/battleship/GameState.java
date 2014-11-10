package com.example.joe.battleship;

/**
 * Created by Joe on 11/1/2014.
 */
public class GameState {

    /*
        View layer state used by the list view
            to encapsulate the data of a game state
     */
    private String gameName;
    private GameMode gameTurn;
    private int missilesA;
    private int missilesB;
    private int winner;

    public GameState(String gameName) {
        this.gameName = gameName;
        missilesA = 0;
        missilesB = 0;
    }

    public String getGameName() {
        return gameName;
    }

    public int getWinner() {

        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getMissilesA() {
        return missilesA;
    }

    public void setMissilesA(int missilesA) {
        this.missilesA = missilesA;
    }

    public int getMissilesB() {
        return missilesB;
    }

    public void setMissilesB(int missilesB) {
        this.missilesB = missilesB;
    }

    public GameMode getGameTurn() {
        return gameTurn;
    }

    public void setGameTurn(GameMode gameTurn) {
        this.gameTurn = gameTurn;
    }
}
