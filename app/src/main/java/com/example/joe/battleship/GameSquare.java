package com.example.joe.battleship;

/**
 * Created by Joe on 10/26/2014.
 */

/*
    Enum used to manage the type of square
 */
enum SquareType {
    WATER,
    HIT,
    SHIP,
    MISS
}

/*
    Small wrapper class to encapsulate a game square on the model layer
        This allows fast checking for hits and misses.
 */
public class GameSquare {
    //the row and column of the square
    private int xPos;
    private int yPos;
    private SquareType type;

    public GameSquare(int x, int y) {
        xPos = x;
        yPos = y;
        this.type = SquareType.WATER;
    }

    public SquareType getType() {
        return type;
    }

    public void setType(SquareType type) {
        this.type = type;
    }
}
