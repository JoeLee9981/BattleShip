package com.example.joe.battleship;

import android.app.Fragment;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Joe on 10/30/2014.
 */
public class GameListFragment extends Fragment implements ListAdapter{

    public interface OnGameSelectedListener {
        public void onGameSelected(String gameName);
    }

    private OnGameSelectedListener onGameSelectedListener;
    private static TextView selectedView;
    private int selectedPosition;

    public void setOnGameSelectedListener(OnGameSelectedListener listener) {
        onGameSelectedListener = listener;
    }

    public OnGameSelectedListener getOnGameSelectedListener() {
        return onGameSelectedListener;
    }

    public void setSelectedView(String gameName) {

    }

    private String[] gameIdentifiers = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView gameView = new ListView(getActivity());
        gameView.setAdapter(this);

        return gameView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getCount() > 0;
    }

    @Override
    public int getCount() {
        return BattleshipController.getInstance().getGameNames().size();
    }

    @Override
    public Object getItem(int position) {
        if(gameIdentifiers == null) {
            ArrayList<String> identifiers = BattleshipController.getInstance().getGameNames();
            gameIdentifiers = identifiers.toArray(new String[identifiers.size()]);

        }
        return BattleshipController.getInstance().getGameState(gameIdentifiers[position]);
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(gameIdentifiers == null || gameIdentifiers.length - 1 < position) {
            ArrayList<String> identifiers = BattleshipController.getInstance().getGameNames();
            gameIdentifiers = identifiers.toArray(new String[identifiers.size()]);
        }
        String identifier = gameIdentifiers[position];
        final GameState state = BattleshipController.getInstance().getGameState(identifier);

        String gameName = "";
        String turn = "";
        String winner = "Undecided";

        int hitsA, hitsB;
        gameName = state.getGameName();
        if(state.getGameTurn() == GameMode.PLAYER_A_TURN) {
            turn = "Player A";
        }
        else if(state.getGameTurn() == GameMode.PLAYER_B_TURN) {
            turn = "Player B";
        }
        else if(state.getGameTurn() == GameMode.SETUP) {
            turn = "Setup";
        }
        else if(state.getGameTurn() == GameMode.PLAYER_A_WON || state.getGameTurn() == GameMode.PLAYER_B_WON) {
            turn = "Game Over";
            winner = state.getWinner() == BattleShipGame.PLAYER_A ? "Player A" : "Player B";
        }
        hitsA = state.getMissilesA();
        hitsB = state.getMissilesB();

        String stateAsString = String.format("%s\n" +
                                            "\tTurn: %s\n" +
                                            "HITS:\n" +
                                            "\tPlayer A: %d Player B: %d\n" +
                                            "Winner: %s",
                                            gameName, turn, hitsA, hitsB, winner);

        TextView gameView = new TextView(getActivity());
        gameView.setText(stateAsString);

        gameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onGameSelectedListener != null) {

                    TextView view = (TextView)v;
                    String name = view.getText().toString().split("\n")[0];
                    onGameSelectedListener.onGameSelected(name);
                    if(selectedView != null)
                        selectedView.setBackgroundColor(Color.WHITE);
                    view.setBackgroundColor(Color.YELLOW);
                    selectedView = view;
                }
            }
        });
        return gameView;
    }

    public static void updateSelectedView(GameState state) {

        if(selectedView == null) {
            return;
        }
        String gameName = "";
        String turn = "";
        String winner = "Undecided";

        int hitsA, hitsB;
        gameName = state.getGameName();
        if(state.getGameTurn() == GameMode.PLAYER_A_TURN) {
            turn = "Player A";
        }
        else if(state.getGameTurn() == GameMode.PLAYER_B_TURN) {
            turn = "Player B";
        }
        else if(state.getGameTurn() == GameMode.SETUP) {
            turn = "Setup";
        }
        else if(state.getGameTurn() == GameMode.PLAYER_A_WON || state.getGameTurn() == GameMode.PLAYER_B_WON) {
            turn = "Game Over";
            winner = state.getWinner() == BattleShipGame.PLAYER_A ? "Player A" : "Player B";
        }
        hitsA = state.getMissilesA();
        hitsB = state.getMissilesB();

        String stateAsString = String.format("%s\n" +
                        "\tTurn: %s\n" +
                        "HITS:\n" +
                        "\tPlayer A: %d Player B: %d\n" +
                        "Winner: %s",
                gameName, turn, hitsA, hitsB, winner);

        selectedView.setText(stateAsString);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }




}
