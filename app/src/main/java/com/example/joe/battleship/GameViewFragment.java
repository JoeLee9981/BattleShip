package com.example.joe.battleship;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Joe on 11/1/2014.
 */
public class GameViewFragment extends Fragment {
    public interface OnTileUpdateListener {
        public void onTileUpdate(int row, int col, boolean hit);
    }

    private OnTileUpdateListener onTileUpdateListener;

    private LinearLayout playerLayout = null;
    private BattleShipBoardLayout playerBoard;
    private BattleShipBoardLayout opponentBoard;
    private BattleshipController gameController = null;
    private boolean hasPressed = false;

    public void setOnTileUpdateListener(OnTileUpdateListener listener) {
        onTileUpdateListener = listener;
    }

    public BattleShipBoardLayout getPlayerBoard() {
        return playerBoard;
    }

    public BattleShipBoardLayout getOpponentBoard() {
        return opponentBoard;
    }

    public void setPressed(boolean hasPressed) {
        this.hasPressed = hasPressed;
    }

    public void forceInitialize(Context context) {
        if(playerBoard == null) {
            playerBoard = new BattleShipBoardLayout(context, BattleshipController.getInstance());
        }
        if(opponentBoard == null) {
            opponentBoard = new BattleShipBoardLayout(context, BattleshipController.getInstance());
        }
        gameController = BattleshipController.getInstance();
    }

    public OnTileUpdateListener getOnTileUpdateListener() {
        return onTileUpdateListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(gameController == null) {
            gameController = BattleshipController.getInstance();
        }
        if(playerLayout != null) {
            playerLayout.removeAllViews();
        }

        /*
            Create the players board and opponent board
         */
        playerLayout = new LinearLayout(getActivity());

        if(getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            playerLayout.setOrientation(LinearLayout.VERTICAL);
        else
            playerLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams boardViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        if(playerBoard == null) {
            playerBoard = new BattleShipBoardLayout(getActivity(), gameController);
        }
        playerBoard.setBackgroundColor(Color.BLACK);
        playerBoard.setPadding(5, 5, 5, 5);
        playerLayout.addView(playerBoard, boardViewParams);

        if(opponentBoard == null) {
            opponentBoard = new BattleShipBoardLayout(getActivity(), gameController);
        }
        opponentBoard.setOnTileClickListener(new BattleShipBoardLayout.OnTileClickListener() {
            @Override
            public void onTileClick(View v) {
                BattleShipTileView view = (BattleShipTileView) v;
                if (hasPressed) {
                    Log.i("BattleShipTile.onPressed", "Player already selected option, transition using next");
                    return;
                }
                if (view.isPressed()) {
                    Log.i("BattleShipTile.onPressed", String.format("Button %d, %d is already pressed", view.getRow(), view.getCol()));
                    return;
                }
                if (gameController.isGameOver()) {
                    Log.i("BattleShipTile.onPressed", "Game is over");
                    return;
                }

                Log.i("BattleShipTileView.onClick", String.format("row: %d, col %d", view.getRow(), view.getCol()));
                try {
                    if (gameController.attack(view.getRow(), view.getCol())) {
                        view.setImageResource(R.drawable.hit);
                        //playerBoardB.setTile(view.getRow(), view.getCol(), true);
                        if (onTileUpdateListener != null) {
                            onTileUpdateListener.onTileUpdate(view.getRow(), view.getCol(), true);
                        }

                    } else {
                        view.setImageResource(R.drawable.miss);
                        //playerBoardB.setTile(view.getRow(), view.getCol(), false);
                        if (onTileUpdateListener != null) {
                            onTileUpdateListener.onTileUpdate(view.getRow(), view.getCol(), false);
                        }

                    }
                    GameState state = gameController.getGameState(gameController.getFileName());
                    GameListFragment.updateSelectedView(state);

                    view.press();
                    hasPressed = true;
                    BattleShipActivity.getNextButton().setEnabled(true);
                } catch (Exception e) {
                    Log.i("BattleShipTileView.onClick", "Unable to perform attack: " + e);
                    e.printStackTrace();
                }
            }
        });
        opponentBoard.setBackgroundColor(Color.BLACK);
        opponentBoard.setPadding(5, 5, 5, 5);
        playerLayout.addView(opponentBoard, boardViewParams);

        return playerLayout;
    }

    //reset the boards
    public void resetBoards() {
        hasPressed = false;
        if(playerBoard != null)
            playerBoard.resetBoard();
        if(opponentBoard != null)
            opponentBoard.resetBoard();
    }
}
