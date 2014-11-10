package com.example.joe.battleship;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Joe on 10/26/2014.
 */

/*
    Model layer enum used to track the state of the game
 */
enum GameMode {
        SETUP,
        PLAYER_A_TURN,
        PLAYER_B_TURN,
        PLAYER_A_WON,
        PLAYER_B_WON
}

/*
    Model Layer encapsulation of the battleship game
        Controls the players and all moves and ships
        Only the controller should access this class
 */
public class BattleShipGame {
    //board settings
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 10;
    //static identifier for player A and player B
    public static final int PLAYER_A = 0;
    public static final int PLAYER_B = 1;

    //Player classes, contains board and ship info
    private Player playerA;
    private Player playerB;

    //The mode of the game (turn)
    private GameMode gameMode;


    //constructor
    public BattleShipGame(String playerAName, String PlayerBName) {
        playerA = new Player(playerAName);
        playerB = new Player(PlayerBName);
        gameMode = GameMode.SETUP;
    }

    /*
        Getters and setters
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    //public trigger setting ships randomly for both players
    public void setShipsRandomly() {
        setShipsRandomly(playerA);
        setShipsRandomly(playerB);
    }

    //This is called to start the game
    public void startGame() {
        if(gameMode != GameMode.SETUP) {
            Log.i("BattleShipGame.startGame()", "Unable to start game, is not in SETUP mode");
            return;
        }
        gameMode = GameMode.PLAYER_A_TURN;
    }

    /*
        Helper method to set ships randomly for a player
     */
    public void setShipsRandomly(Player player) {
        Random ran = new Random();
        for(Ship ship : player.getShips()) {
            boolean success;
            do {
                int row = ran.nextInt(9);
                int col = ran.nextInt(9);
                Direction direction = ran.nextBoolean() ? Direction.HORIZONTAL : Direction.VERTICAL;
                success = player.placeShip(row, col, direction, ship.getName());
            } while(!success);
        }
        int a = 1;
    }

    //check if the game is over
    public boolean gameOver() {
        if(playerA.hasLost()) {
            gameMode = GameMode.PLAYER_B_WON;
            return true;
        }
        else if(playerB.hasLost()) {
            gameMode = GameMode.PLAYER_A_WON;
            return true;
        }
        return false;
    }

    /*
     * Attack a player square
     *      row is the row to attack
     *      col is the column to attack
     *      player is the player to attack
     */
    public boolean attackPlayer(int row, int col) throws Exception {

        if(gameMode == GameMode.PLAYER_A_TURN) {
            boolean hit = playerB.attack(row, col);
            gameMode = GameMode.PLAYER_B_TURN;
            return hit;
        }
        else if(gameMode == GameMode.PLAYER_B_TURN) {
            boolean hit = playerA.attack(row, col);
            gameMode = GameMode.PLAYER_A_TURN;
            return hit;
        }
        else {
            throw new Exception("Invalid game mode for operation (Attack)");
        }
    }
}
