package com.example.joe.battleship;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Joe on 10/27/2014.
 */
public class BattleShipTileView extends ImageView {

    private int row;
    private int col;
    private boolean pressed;

    //constructor, sets the row and col for tracking purposes
    public BattleShipTileView(Context context, int row, int col) {
        super(context);
        this.row = row;
        this.col = col;
        pressed = false;
    }

    /*
        press the  button, this is used to prevent the same tile
            from being pressed multiple times
     */
    public void press() {
        pressed = true;
    }

    //check if the tile is pressed
    public boolean isPressed() {
        return pressed;
    }

    //reset the tile to it's initialized state
    public void reset() {
        this.pressed = false;
        setImageResource(R.drawable.unknown);
    }

    //get row and column
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

}
