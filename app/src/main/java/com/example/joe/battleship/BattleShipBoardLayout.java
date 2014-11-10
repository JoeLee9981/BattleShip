package com.example.joe.battleship;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

/**
 * Created by Joe on 10/26/2014.
 */
public class BattleShipBoardLayout extends GridLayout {

    /* listener for a tile click */
    public interface OnTileClickListener {
        public void onTileClick(View v);
    }

    /* Listener for a change to the tile */
    public interface OnTileChangeListener {
        public void onTileChange(View v, boolean hit);
    }

    private OnTileClickListener onTileClickListener = null;
    private OnTileChangeListener onTileChangeListener = null;

    //TODO: this has to be defined in only one place
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 10;

    //a reference to the controller singleton
    private BattleshipController controller;

    /*
        Constructor
     */
    public BattleShipBoardLayout(Context context, BattleshipController controller) {
        super(context);
        this.controller = controller;
        initGrid();
    }

    /*
        Getters and Setters for listeners
     */
    public OnTileClickListener getOnTileClickListener() {
        return onTileClickListener;
    }

    public void setOnTileClickListener(OnTileClickListener listener) {
        onTileClickListener = listener;
    }

    public OnTileChangeListener getOnTileChangeListener() {
        return onTileChangeListener;
    }

    public void setOnTileChangeListener(OnTileChangeListener listener) {
        onTileChangeListener = listener;
    }

    /*
        Initialize the player grids
     */
    public void initGrid() {
        setRowCount(10);
        setColumnCount(10);

        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                GridLayout.LayoutParams tileParams = new GridLayout.LayoutParams();

                tileParams.width = 40;
                tileParams.height = 40;
                BattleShipTileView tile = new BattleShipTileView(getContext(), i, j);
                tile.setImageResource(R.drawable.unknown);

                tile.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onTileClickListener != null) {
                            onTileClickListener.onTileClick(v);
                        }
                    }
                });

                tile.setBackgroundColor(Color.BLUE);

                addView(tile, tileParams);
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        Path path = new Path();

        float squareWidth = (float)getWidth() / BOARD_WIDTH;
        float squareHeight = (float)getHeight() / BOARD_HEIGHT;

        for(int i = 1; i < BOARD_WIDTH; i++) {
            path.moveTo(i * squareWidth, 0);
            path.lineTo(i * squareWidth, getHeight());
            canvas.drawPath(path, paint);
        }

        for(int i = 1; i < BOARD_HEIGHT; i++) {
            path.moveTo(0, i * squareHeight);
            path.lineTo(getWidth(), i * squareHeight);
            canvas.drawPath(path, paint);
        }
    }

    /*
        Draw a ship in a provided space direction and size
     */
    public void drawShip(int row, int col, Direction dir, int holes) {

        int index = 10 * row + col;

        View v = getChildAt(index);
        if(v instanceof BattleShipTileView) {
            BattleShipTileView tile = (BattleShipTileView)v;

            if(dir == Direction.HORIZONTAL) {
                tile.setImageResource(R.drawable.ship_left);
                for (int i = col + 1; i < col + holes - 1; i++) {
                    index = 10 * row + i;

                    v = getChildAt(index);
                    if(v instanceof BattleShipTileView) {
                        tile = (BattleShipTileView)v;
                        tile.setImageResource(R.drawable.ship_mid);
                    }
                }
                index = 10 * row + col + holes - 1;
                v = getChildAt(index);
                if(v instanceof BattleShipTileView) {
                    tile = (BattleShipTileView)v;
                    tile.setImageResource(R.drawable.ship_left);
                    tile.setRotation(180);
                }
            }
            else {
                tile.setImageResource(R.drawable.ship_left);
                tile.setRotation(90);
                for (int i = row + 1; i < row + holes - 1; i++) {
                    index = 10 * i + col;

                    v = getChildAt(index);
                    if(v instanceof BattleShipTileView) {
                        tile = (BattleShipTileView)v;
                        tile.setImageResource(R.drawable.ship_mid);
                        tile.setRotation(90);
                    }
                }
                index = 10 * (row + holes - 1) + col;
                v = getChildAt(index);
                if(v instanceof BattleShipTileView) {
                    tile = (BattleShipTileView)v;
                    tile.setImageResource(R.drawable.ship_left);
                    tile.setRotation(-90);
                }
            }

            Log.i("BattleShipBoardLayout.drawShip()",
                    String.format("row %d, col %d", tile.getRow(), tile.getCol()));
        }
    }

    /*
        Set a tile to a hit or miss
     */
    public void setTile(int row, int col, boolean hit) {
        int index = 10 * row + col;

        View v = getChildAt(index);
        if(v == null) {
            Log.i("BattleShipBoardLayout.setTile", "Unable to set tile, not instantiated yet.");
            return;
        }

        if(v instanceof BattleShipTileView) {
            BattleShipTileView view = (BattleShipTileView)v;
            if(hit)
                view.setImageResource(R.drawable.hit);
            else
                view.setImageResource(R.drawable.miss);
            view.press();
        }
    }

    /*
        Reset the boards back to the initial state
     */
    public void resetBoard() {
        for(int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);

            if(v instanceof BattleShipTileView) {

                BattleShipTileView view = (BattleShipTileView)v;
                view.setImageResource(R.drawable.unknown);
                view.setRotation(0);
                view.reset();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }


}
