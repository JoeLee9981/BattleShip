package com.example.joe.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import android.text.Editable;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;


public class BattleShipActivity extends Activity {

    //Fragments containing players boards
    private GameViewFragment playerAFragment;
    private GameViewFragment playerBFragment;
    //list fragment for loading games
    private GameListFragment listFragment;
    //label used to display game info
    private TextView playerLabel;
    private static Button nextButton;

    //Used to determine clicks on transition
    private boolean transition = false;

    //reference to the game controller singleton
    private BattleshipController battleShipGame;

    public static Button getNextButton() {
        return nextButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* if the controller is null, get the instance */

        if(battleShipGame == null) {
            battleShipGame = BattleshipController.getInstance();
            battleShipGame.setFileDirectory(getFilesDir());
        }

        //register game over listener
        battleShipGame.setOnGameOverListener(new BattleshipController.OnGameOverListener() {
            @Override
            public void onGameOver(String winner) {
                String toastText = String.format("Game Over - %s Wins", winner);
                Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG);
                toast.show();
            }
        });

        //set root layout
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        if(playerLabel == null) {
            playerLabel = new TextView(this);
            playerLabel.setText("Select New Game");
        }

        /*   Create Top Menu Bar  */
        LinearLayout menuLayout = new LinearLayout(this);
        menuLayout.setOrientation(LinearLayout.HORIZONTAL);
        menuLayout.setBackgroundColor(Color.BLACK);

        rootLayout.addView(menuLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));

        TextView gameLabel = new TextView(this);
        gameLabel.setText("WELCOME TO BATTLESHIP");
        gameLabel.setTextColor(Color.BLUE);
        gameLabel.setPadding(20, 20, 20, 20);

        Button newGameButton = new Button(this);
        newGameButton.setText("New Game");
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the game after views are set up
                playerAFragment.resetBoards();
                playerBFragment.resetBoards();

                final AlertDialog.Builder inputWindow = new AlertDialog.Builder(v.getContext());

                inputWindow.setTitle("Start a new game");
                inputWindow.setMessage("Please enter a game name:");

                final EditText input = new EditText(v.getContext());
                inputWindow.setView(input);
                inputWindow.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editable value = input.getText();
                        String gameName = value.toString();
                        if(gameName != null && !gameName.equals("")) {
                            battleShipGame.startNew(gameName);

                        }
                    }
                });

                inputWindow.show();
            }
        });

        menuLayout.addView(gameLabel, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        menuLayout.addView(newGameButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));

        /* set the next button */
        nextButton = new Button(this);
        nextButton.setEnabled(false);
        transition = false;
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(transition == true) {
                    FragmentTransaction trans = getFragmentManager().beginTransaction();
                    if (battleShipGame.isPlayerATurn()) {
                        trans.replace(11, playerAFragment);
                        trans.commit();
                        playerBFragment.setPressed(false);

                    } else if (battleShipGame.isPlayerBTurn()) {
                        trans.replace(11, playerBFragment);
                        trans.commit();
                        playerAFragment.setPressed(false);

                    }
                    nextButton.setEnabled(false);
                    transition = false;
                }
                else {
                    FragmentTransaction trans = getFragmentManager().beginTransaction();
                    if (battleShipGame.isPlayerATurn()) {
                        trans.hide(playerBFragment);
                        trans.commit();
                    } else if (battleShipGame.isPlayerBTurn()) {
                        trans.hide(playerAFragment);
                        trans.commit();
                    }
                    transition = true;
                    String toastText = String.format("Pass to other player and then click Next");
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });
        nextButton.setText("Next");
        menuLayout.addView(nextButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));

        /* Game Layout to encompass entire game */

        LinearLayout gameLayout = new LinearLayout(this);
        gameLayout.setOrientation(LinearLayout.HORIZONTAL);

        rootLayout.addView(gameLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0));

        /* Game List View Layout */


        FrameLayout gameListLayout = new FrameLayout(this);
        gameListLayout.setId(10);
        gameLayout.addView(gameListLayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        /* Game Board Layouts */

        final FrameLayout playerALayout = new FrameLayout(this);
        playerALayout.setId(11);
        gameLayout.addView(playerALayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));

        final FrameLayout playerBLayout = new FrameLayout(this);
        playerBLayout.setId(12);

        //set up the player fragments
        if(playerAFragment == null) {
            playerAFragment = new GameViewFragment();
            /*TODO: Force initialize is a poor way to handle a problem
                On part 2, reorganize view to resolve issue */
            playerAFragment.forceInitialize(this);
        }
        if(playerBFragment == null) {
            playerBFragment = new GameViewFragment();
            playerBFragment.forceInitialize(this);
        }

        /* Register Player Fragment Events */

        playerAFragment.setOnTileUpdateListener(new GameViewFragment.OnTileUpdateListener() {
            @Override
            public void onTileUpdate(int row, int col, boolean hit) {
                if(playerBFragment.getPlayerBoard() != null)
                    playerBFragment.getPlayerBoard().setTile(row, col, hit);
            }
        });

        playerBFragment.setOnTileUpdateListener(new GameViewFragment.OnTileUpdateListener() {
            @Override
            public void onTileUpdate(int row, int col, boolean hit) {
                if(playerAFragment.getPlayerBoard() != null)
                    playerAFragment.getPlayerBoard().setTile(row, col, hit);
            }
        });

        listFragment = new GameListFragment();
        listFragment.setOnGameSelectedListener(new GameListFragment.OnGameSelectedListener() {
            @Override
            public void onGameSelected(String gameName) {
                playerAFragment.resetBoards();
                playerBFragment.resetBoards();
                battleShipGame.loadFromFile(gameName);
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                if(battleShipGame.isPlayerATurn()) {
                    trans.replace(11, playerAFragment);
                    trans.commit();

                } else if(battleShipGame.isPlayerBTurn()) {
                    trans.replace(11, playerBFragment);
                    trans.commit();

                }
            }
        });

        battleShipGame.setOnListChangedListener(new BattleshipController.OnListChangedListener() {
            @Override
            public void onListChange() {
                findViewById(10).invalidate();
            }
        });

        /* Add to transaction and commit */

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(10, listFragment);
        transaction.add(11, playerAFragment);
        //transaction.add(12, playerBFragment);

        transaction.commit();

        /* Adding listeners to controller */

        battleShipGame.setOnPlayerAShipPlaceListener(new BattleshipController.OnShipPlaceListener() {
            @Override
            public void onShipPlace(int row, int col, Direction dir, int holes) {
                if(playerAFragment.getPlayerBoard() != null)
                    playerAFragment.getPlayerBoard().drawShip(row, col, dir, holes);
            }
        });

        battleShipGame.setOnPlayerBShipPlaceListener(new BattleshipController.OnShipPlaceListener() {
            @Override
            public void onShipPlace(int row, int col, Direction dir, int holes) {
                if(playerBFragment.getPlayerBoard() != null)
                    playerBFragment.getPlayerBoard().drawShip(row, col, dir, holes);
            }
        });

        battleShipGame.setOnPlayerATileLoadListener(new BattleshipController.OnTileLoadListener() {
            @Override
            public void onTileLoad(int row, int col, boolean hit) {
                if(playerBFragment.getPlayerBoard() != null)
                    playerBFragment.getPlayerBoard().setTile(row, col, hit);
                if(playerAFragment.getOpponentBoard() != null)
                    playerAFragment.getOpponentBoard().setTile(row, col, hit);
            }
        });

        battleShipGame.setOnPlayerBTileLoadListener(new BattleshipController.OnTileLoadListener() {
            @Override
            public void onTileLoad(int row, int col, boolean hit) {
                if(playerAFragment.getPlayerBoard() != null)
                    playerAFragment.getPlayerBoard().setTile(row, col, hit);
                if(playerBFragment.getOpponentBoard() != null)
                    playerBFragment.getOpponentBoard().setTile(row, col, hit);
            }
        });

        setContentView(rootLayout);
    }

}
