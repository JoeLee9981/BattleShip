package com.example.joe.battleship;

import android.graphics.Point;

/**
 * Created by Joe on 10/26/2014.
 */

/*
    Enum to track the direction a ship is pointing
 */
enum Direction { HORIZONTAL, VERTICAL }

/*
    Ship class is a model level encapsualtion of a player's ship
 */
public class Ship {
    //name of the ship
    private String name;
    //number of holes on a ship (hits it can take)
    private int holes;
    //col and row of the top or left most square of the ship
    private int col;
    private int row;
    //direction the ship faces
    private Direction direction;
    //is the ship placed
    private boolean placed;

    /*
        Constructor
     */
    public Ship(String name, int holes) {
        this.name = name;
        this.holes = holes;
        this.direction = Direction.HORIZONTAL;
        this.placed = false;
    }

    //check if ship is placed
    public boolean isPlaced() {
        return placed;
    }

    /*
        Set the position (row, col) of a ship
            A ship is always going left to right, or top to bottom.
            The ship tracks only the top or left most position
     */
    public boolean setPos(int row, int col) {
        if(isValidPosition(row, col)) {
            this.row = row;
            this.col = col;
            placed = true;

            return true;
        }
        return false;
    }

    /*
        Set the direction of the ship
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /*
        get the direction of the ship
     */
    public Direction getDirection() {
        return direction;
    }

    /*
        Getters and Setters for other fields
     */
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getHoles() {
        return holes;
    }

    public String getName() {
        return name;
    }

    /*
        Returns if a given position is a valid placement for the ship
            Determined by checking the bounds of the ship according to the board
     */
    private boolean isValidPosition(int x, int y) {
        if(direction == null)
            return false;

        if(x >= 0 && x <= BattleShipGame.BOARD_WIDTH &&
                y >= 0 && y <= BattleShipGame.BOARD_HEIGHT) {
            if(direction == Direction.HORIZONTAL) {
                return y + holes <= BattleShipGame.BOARD_HEIGHT;
            }
            else {
                return x + holes <= BattleShipGame.BOARD_WIDTH;
            }
        }
        return false;
    }

    /*
        Checks if a ship to be placed at given row, col, dir will intersect with this
            Ship. This function takes advantage of the fact that the ships will always
            be either parallel or orthogonal to eachother.
     */
    public boolean intersects(int row, int col, int holes, Direction direction) {

        if(!placed) {
            return false;
        }
        //line 1
        int x1 = col;
        int x2 = direction == Direction.HORIZONTAL ? col + holes - 1 : col;
        int y1 = row;
        int y2 = direction == Direction.VERTICAL ? row + holes - 1 : row;
        //line 2
        int x3 = this.col;
        int x4 = this.direction == Direction.HORIZONTAL ? x3 + this.holes - 1 : x3;
        int y3 = this.row;
        int y4 = this.direction == Direction.VERTICAL ? y3 + this.holes - 1 : y3;

        if(direction == this.direction) {
            //parallel
            if(direction == Direction.HORIZONTAL) {
                if(y1 != y3) {
                    //different y can't intersect
                    return false;
                }
                else {
                    //return if overlap
                    return (x3 >= x1 && x3 <= x2) || (x4 >= x1 && x4 <= x2) ||
                            (x1 >= x3 && x1 <= x4) || (x2 >= x3 && x2 <= x4);
                }
            }
            else {
                if(x1 != x3) {
                    //different x can't intersect
                    return false;
                }
                else {
                    // return if overlap
                    return (y3 >= y1 && y3 <= y2) || (y4 >= y1 && y4 <= y2) ||
                            (y1 >= y3 && y1 <= y4) || (y2 >= y3 && y2 <= y4);
                }
            }
        }
        else  {
            //orthogonal
            if(direction == Direction.VERTICAL) {
                return x1 >= x3 && x1 <= x4 && y3 >= y1 && y3 <= y2;
            }
            else {
                return x3 >= x1 && x3 <= x2 && y1 >= y3 && y1 <= y4;
            }
        }
    }
}
