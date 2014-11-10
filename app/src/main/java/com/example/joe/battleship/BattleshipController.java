package com.example.joe.battleship;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Joe on 10/26/2014.
 */
public class BattleshipController {

    /*
        Internal class used to encapsulate a move made in a game
     */
    public class GameMove {
        private int row;
        private int col;
        private int player;
        private boolean isHit;

        //Constructor
        public GameMove(int row, int col, int player, boolean isHit) {
            this.row = row;
            this.col = col;
            this.player = player;
            this.isHit = isHit;
        }

        /*
            Getters and Setters
         */
        public boolean isHit() {
            return isHit;
        }

        public void setHit(boolean isHit) {
            this.isHit = isHit;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public int getPlayer() {
            return player;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public void setPlayer(int player) {
            this.player = player;
        }
    }

    /*
        Interfaces used for listeners
     */
    public interface OnShipPlaceListener {
        public void onShipPlace(int row, int col, Direction dir, int holes);
    }

    public interface OnListChangedListener {
        public void onListChange();
    }

    public interface OnGameOverListener {
        public void onGameOver(String winner);
    }

    public interface OnTileLoadListener {
        public void onTileLoad(int row, int col, boolean hit);
    }

    /**************************************************/
    //this is the file to save the list of games into
    private static final String gameListFile = "GameListFile.txt";
    //Singleton instance reference
    private static BattleshipController instance = null;

    //These are the listeners
    private OnShipPlaceListener onPlayerAShipPlaceListener;
    private OnShipPlaceListener onPlayerBShipPlaceListener;
    private OnTileLoadListener onPlayerATileLoadListener;
    private OnTileLoadListener onPlayerBTileLoadListener;
    private OnGameOverListener onGameOverListener;
    private OnListChangedListener onListChangedListener;

    //Battleship game, acts as the model layer of the game
    private BattleShipGame game;
    //A list of all moves made in a current game
    private ArrayList<GameMove> moves;
    //A hash map by name, game of the games that have been saved
    private HashMap<String, GameState> gameList = new HashMap<String, GameState>();
    //An array list of game names, used for preserving order, since the HashMap does not
    private ArrayList<String> gameNames = new ArrayList<String>();

    //Reference to the filename of a game
    private String fileName = null;
    //Reference to the file directory to save to
    private File fileDirectory = null;

    /*
        Singleton get instance reference
     */
    public static BattleshipController getInstance() {
        if(instance == null) {
            instance = new BattleshipController();
        }
        return instance;
    }

    /*
        Constructor
     */
    private BattleshipController() {
        moves = new ArrayList<GameMove>();
    }

    /*
        Start a new game
     */
    public void startNew(String gameName) {
        moves.clear();
        fileName = gameName;

        game = new BattleShipGame("PlayerA", "PlayerB");
        setUpBoard();

        addGame(gameName, new GameState(gameName));
        game.startGame();

        gameList.get(gameName).setGameTurn(game.getGameMode());
        if(onListChangedListener != null)
            onListChangedListener.onListChange();
        saveGame();
    }

    /*
        Load a saved game from a file
     */
    public void loadFromFile(String fileName) {
        moves.clear();
        this.fileName = fileName;
        game = new BattleShipGame("PlayerA", "PlayerB");
        loadGame();
    }

    /*
        Getters and Setters
     */
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileDirectory(File fileDir) {
        this.fileDirectory = fileDir;
    }

    public ArrayList<String> getGameNames() {
        if(gameList.isEmpty()) {
            loadGameList();
        }
        return gameNames;
    }

    public Collection<GameState> getGameStates() {
        return gameList.values();
    }

    public GameState getGameState(String gameName) {
        return gameList.get(gameName);
    }

    public void removeGame(String name) {
        gameList.remove(name);
        gameNames.remove(name);
        saveGameList();
    }

    public void addGame(String gameName, GameState state) {
        gameList.put(gameName, state);
        gameNames.add(gameName);
        saveGameList();
    }

    public String getPlayerAName() {
        return game.getPlayerA().getName();
    }

    public String getPlayerBName() {
        return game.getPlayerB().getName();
    }

    /*
        used to set up a game board, currently places ships randomly
     */
    public void setUpBoard() {
        placeShipsRandomly();
    }

    /*
        Check if the game is over
     */
    public boolean isGameOver() {
        if(game == null) {
            Log.i("BattleshipController.placeShipsRandomly", "Game is null, perhaps you forgot to call .start()");
            return false;
        }
        return game.gameOver();
    }

    /*
        Checks if it is player A's turn
     */
    public boolean isPlayerATurn() {
        if(game.getGameMode() == GameMode.PLAYER_A_TURN)
            return true;
        return false;
    }

    /*
        Checks if it is player B's turn
     */
    public boolean isPlayerBTurn() {
        if(game.getGameMode() == GameMode.PLAYER_B_TURN)
            return true;
        return false;
    }

    /*
        These are currently unused, but leaving them in just in case
     */
    public void refreshPlayerAGrid() {
        for(Ship ship : game.getPlayerA().getShips()) {
            onPlayerAShipPlaceListener.onShipPlace(ship.getRow(), ship.getCol(), ship.getDirection(), ship.getHoles());
        }
        for(GameMove move : moves) {
            if(move.getPlayer() == game.PLAYER_A) {
                onPlayerATileLoadListener.onTileLoad(move.getRow(), move.getCol(), move.isHit());
            }
        }
    }

    public void refreshPlayerBGrid() {
        for(Ship ship : game.getPlayerB().getShips()) {
            onPlayerBShipPlaceListener.onShipPlace(ship.getRow(), ship.getCol(), ship.getDirection(), ship.getHoles());
        }
        for(GameMove move : moves) {
            if(move.getPlayer() == game.PLAYER_B) {
                onPlayerBTileLoadListener.onTileLoad(move.getRow(), move.getCol(), move.isHit());
            }
        }
    }

    /*
        Setters for the event listeners
     */
    public void setOnPlayerAShipPlaceListener(OnShipPlaceListener listener) {
        onPlayerAShipPlaceListener = listener;
    }

    public void setOnPlayerBShipPlaceListener(OnShipPlaceListener listener) {
        onPlayerBShipPlaceListener = listener;
    }

    public void setOnGameOverListener(OnGameOverListener listener) {
        onGameOverListener = listener;
    }

    public void setOnPlayerATileLoadListener(OnTileLoadListener listener) {
        onPlayerATileLoadListener = listener;
    }

    public void setOnPlayerBTileLoadListener(OnTileLoadListener listener) {
        onPlayerBTileLoadListener = listener;
    }

    public void setOnListChangedListener(OnListChangedListener listener) {
        onListChangedListener = listener;
    }

    /*
        Tells the game to place the ships randomly, then calls the listener to update the view
     */
    public void placeShipsRandomly() {
        if(game == null) {
            Log.i("BattleshipController.placeShipsRandomly", "Game is null, perhaps you forgot to call .start()");
            return;
        }
        game.setShipsRandomly();
        for(Ship ship : game.getPlayerA().getShips()) {
            if(ship.isPlaced() && onPlayerAShipPlaceListener != null) {
                onPlayerAShipPlaceListener.onShipPlace(ship.getRow(), ship.getCol(),
                        ship.getDirection(), ship.getHoles());
            }
        }

        for(Ship ship : game.getPlayerB().getShips()) {
            if(ship.isPlaced() && onPlayerBShipPlaceListener != null) {
                onPlayerBShipPlaceListener.onShipPlace(ship.getRow(), ship.getCol(),
                        ship.getDirection(), ship.getHoles());
            }
        }
    }

    /*
     * attack the opposing player
     *  row the row to attack
     *  col the column to attack
     *  playerId the identifier of the player performing the attack
     */
    public boolean attack(int row, int col) throws Exception{
        if(game == null) {
            Log.i("BattleshipController.placeShipsRandomly", "Game is null, perhaps you forgot to call .start()");
        }
        int player = game.getGameMode() == GameMode.PLAYER_A_TURN ? game.PLAYER_A : game.PLAYER_B;

        boolean hit = game.attackPlayer(row, col);

        GameState state = gameList.get(fileName);

        state.setGameTurn(game.getGameMode());


        moves.add(new GameMove(row, col, player, hit));

        if(player == game.PLAYER_A) {
            state.setMissilesA(state.getMissilesA() + 1);
        }
        else {
            state.setMissilesB(state.getMissilesB() + 1);
        }

        if(game.gameOver()) {
            if(game.getGameMode() == GameMode.PLAYER_A_WON) {
                if(onGameOverListener != null) {
                    onGameOverListener.onGameOver(game.getPlayerA().getName());
                    state.setWinner(game.PLAYER_A);
                    state.setGameTurn(game.getGameMode());
                }
                else {
                    throw new Exception("GAME OVER -> Listener is not set");
                }
            }
            else if(game.getGameMode() == GameMode.PLAYER_B_WON)
            {
                if(onGameOverListener != null) {
                    onGameOverListener.onGameOver(game.getPlayerB().getName());
                    state.setWinner(game.PLAYER_B);
                    state.setGameTurn(game.getGameMode());
                }
                else {
                    throw new Exception("GAME OVER -> Listener is not set");
                }
            }
        }

        //save the states
        saveGame();
        saveGameList();
        return hit;
    }

    /*
        Use to restore the state of a grid from a saved file
            This is important so as not to trigger some of the events
            that the attack function does
     */
    private void restoreGridFromSave(GameMove[] gameMoves) {
        if(game == null) {
            Log.i("BattleshipController.placeShipsRandomly", "Game is null, perhaps you forgot to call .start()");
        }

        for(GameMove move : gameMoves) {
            try {
                int row = move.getRow();
                int col = move.getCol();
                int player = move.getPlayer();

                boolean hit = game.attackPlayer(row, col);
                moves.add(new GameMove(row, col, player, hit));

                //intentionally don't check for null, as to allow the exception and log to ocurr
                if(move.getPlayer() == game.PLAYER_A) {
                    onPlayerATileLoadListener.onTileLoad(move.getRow(), move.getCol(), hit);
                }
                else {
                    onPlayerBTileLoadListener.onTileLoad(move.getRow(), move.getCol(), hit);
                }
            }
            catch(Exception e) {
                Log.w("BattleshipController.loadGame", String.format("Problem loading form file: %s", e.getMessage()));
            }
        }

        if(game.gameOver()) {
            if(game.getGameMode() == GameMode.PLAYER_A_WON) {
                if(onGameOverListener != null) {
                    onGameOverListener.onGameOver(game.getPlayerA().getName());
                }
            }
            else if(game.getGameMode() == GameMode.PLAYER_B_WON)
            {
                if(onGameOverListener != null) {
                    onGameOverListener.onGameOver(game.getPlayerB().getName());
                }
            }
        }
    }

    /*
        Saves the current game to a file
            This is set up to be done automatically
     */
    private void saveGame() {

        if(fileDirectory == null || fileName == null) {
            Log.w("BattleshipController.saveGame()", "Unable to save game, fileDir or fileName are null");
            return;
        }

        Gson gson = new Gson();
        String jsonList = gson.toJson(game.getPlayerA().getPlayerGrid());

        try {
            FileWriter writer = new FileWriter(new File(fileDirectory, fileName));
            BufferedWriter buffWriter = new BufferedWriter(writer);

            buffWriter.write(jsonList + "\n");

            jsonList = gson.toJson(game.getPlayerB().getPlayerGrid());

            buffWriter.write(jsonList + "\n");

            jsonList = gson.toJson(game.getPlayerA().getShips());

            buffWriter.write(jsonList + "\n");

            jsonList = gson.toJson(game.getPlayerB().getShips());

            buffWriter.write(jsonList + "\n");

            jsonList = gson.toJson(moves.toArray(new GameMove[moves.size()]));

            buffWriter.write(jsonList + "\n");

            buffWriter.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /*
        Load ships from an array
            This is used to load the game from a saved state
     */
    private void loadShips(Ship[] ships, int playerId) {
        if(playerId == game.PLAYER_A) {
            for(Ship ship : ships) {
                game.getPlayerA().placeShip(ship.getRow(), ship.getCol(), ship.getDirection(), ship.getName());
                onPlayerAShipPlaceListener.onShipPlace(ship.getRow(), ship.getCol(), ship.getDirection(), ship.getHoles());
            }
        }
        else {
            for(Ship ship : ships) {
                game.getPlayerB().placeShip(ship.getRow(), ship.getCol(), ship.getDirection(), ship.getName());
                onPlayerBShipPlaceListener.onShipPlace(ship.getRow(), ship.getCol(), ship.getDirection(), ship.getHoles());
            }
        }

    }

    /*
        Loads a game from a file then sets it as the current game
            Uses the listeners to update the view to the loaded game state
     */
    private void loadGame() {

        if(fileDirectory == null || fileName == null) {
            Log.w("BattleshipController.loadGame()", "Unable to load game, fileDir or fileName are null");
            return;
        }

        moves.clear();
        try {
            FileReader reader = new FileReader(new File(fileDirectory, fileName));
            BufferedReader buffReader = new BufferedReader(reader);

            String jsonList = buffReader.readLine();

            Gson gson = new Gson();

            GameSquare[][] grid = gson.fromJson(jsonList, GameSquare[][].class);
            game.getPlayerA().setPlayerGrid(grid);

            jsonList = buffReader.readLine();

            grid = gson.fromJson(jsonList, GameSquare[][].class);
            game.getPlayerB().setPlayerGrid(grid);

            jsonList = buffReader.readLine();

            Ship[] ships = gson.fromJson(jsonList, Ship[].class);
            loadShips(ships, game.PLAYER_A);

            jsonList = buffReader.readLine();

            ships = gson.fromJson(jsonList, Ship[].class);
            loadShips(ships, game.PLAYER_B);

            jsonList = buffReader.readLine();

            //Game must be started for moves to be set
            game.startGame();

            GameMove[] gameMoves = gson.fromJson(jsonList, GameMove[].class);

            restoreGridFromSave(gameMoves);
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /*
        Save the list of games to the file
            This is done automatically
     */
    private void saveGameList() {
        if(fileDirectory == null) {
            Log.w("BattleshipController.saveGameList()", "Unable to save game list, fileDir is null");
            return;
        }

        Gson gson = new Gson();
        String jsonList = gson.toJson(gameList.values().toArray(new GameState[gameList.values().size()]));

        try {
            FileWriter writer = new FileWriter(new File(fileDirectory, gameListFile));
            BufferedWriter buffWriter = new BufferedWriter(writer);

            buffWriter.write(jsonList);

            buffWriter.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /*
        Load the list of saved games
     */
    private void loadGameList() {
        if(fileDirectory == null) {
            Log.w("BattleshipController.loadGame()", "Unable to load game, fileDir or fileName are null");
            return;
        }

        gameList.clear();
        try {
            FileReader reader = new FileReader(new File(fileDirectory, gameListFile));
            BufferedReader buffReader = new BufferedReader(reader);

            String jsonList = buffReader.readLine();

            Gson gson = new Gson();

            GameState[] list = gson.fromJson(jsonList, GameState[].class);
            for(GameState state : list) {
                gameList.put(state.getGameName(), state);
                gameNames.add(state.getGameName());
            }

        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
