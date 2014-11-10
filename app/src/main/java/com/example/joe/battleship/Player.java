package com.example.joe.battleship;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Joe on 10/26/2014.
 */
public class Player {
/*
    Model level encapsulation of a player
        This class should be used by the BattleShipGame class only
 */

    //number of holes on all ships for a player
    public static final int MAX_HOLES = 17;
    //a map of ships by name, ship
    private HashMap<String, Ship> ships;
    //multi dimensional array of the players grid
    private GameSquare[][] playerGrid;
    //players name (this is currently unused but may be implemented later)
    private String name;
    //total count of the times a player's ship has been hit
    private int hitCount;

    /*
        Constructor
     */
    public Player(String name) {
        this.name = name;
        hitCount = 0;
        ships = new HashMap<String, Ship>();
        initShips();
        playerGrid = new GameSquare[BattleShipGame.BOARD_WIDTH][BattleShipGame.BOARD_HEIGHT];
        initGrids();
    }

    /*
        Initialize all the ships
     */
    private void initShips() {
        ships.put("Carrier", new Ship("Carrier", 5));
        ships.put("Battleship", new Ship("Battleship", 4));
        ships.put("Cruiser", new Ship("Cruiser", 3));
        ships.put("Submarine", new Ship("Submarine", 3));
        ships.put("Destroyer", new Ship("Destroyer", 2));
    }

    /*
        Initialize the grid
     */
    private void initGrids() {
        for(int i = 0; i < BattleShipGame.BOARD_WIDTH; i++) {
            for(int j = 0; j < BattleShipGame.BOARD_HEIGHT; j++) {
                playerGrid[i][j] = new GameSquare(i, j);
            }
        }
    }

    /*
        return the players name
     */
    public String getName() {
        return name;
    }

    /*
        Set the players name
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
        Set the players grid to a different grid
     */
    public void setPlayerGrid(GameSquare[][] grid) {
        playerGrid = grid;
    }

    /*
        Place a ship onto the players grid
     */
    public boolean placeShip(int row, int col, Direction direction, String shipName) {
        Ship ship = ships.get(shipName);
        if(ship == null) {
            Log.i("Player.placeShip()", "Unable to place ship, invalid ship name");
            return false;
        }

        if(intersects(row, col, ship.getHoles(), direction)) {
            return false;
        }
        ship.setDirection(direction);

        boolean placed = ship.setPos(row, col);
        if(placed) {
            if(direction == Direction.VERTICAL) {
                for(int i = row; i < row + ship.getHoles(); i++) {
                    playerGrid[i][col].setType(SquareType.SHIP);
                }
            }
            else {
                for(int i = col; i < col + ship.getHoles(); i++) {
                    playerGrid[row][i].setType(SquareType.SHIP);
                }
            }
        }

        return placed;
    }

    /*
        Method used to check if a ship intersects another ship
     */
    private boolean intersects(int row, int col, int holes, Direction direction) {

        for(Ship ship : ships.values()) {
            if(!ship.isPlaced()) {
                continue;
            }
            if(ship.intersects(row, col, holes, direction)) {
                return true;
            }
        }
        return false;
    }

    /*
        returns an array of the players ships
     */
    public Ship[] getShips() {
        return ships.values().toArray(new Ship[ships.size()]);
    }

    public GameSquare[][] getPlayerGrid() {
        return playerGrid.clone();
    }

    /*
     * Return true if hit, false if miss
     */
    public boolean attack (int row, int col) throws Exception {
        if(playerGrid[row][col].getType() == SquareType.SHIP) {
            playerGrid[row][col].setType(SquareType.HIT);
            hitCount++;
            return true;
        }
        playerGrid[row][col].setType(SquareType.MISS);
        return false;

    }

    /*
        Return if the player has lost
     */
    public boolean hasLost() {
        return hitCount >= MAX_HOLES;
    }
}
