package si.um.feri.memomash.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.List;

import si.um.feri.memomash.Difficulty;
import si.um.feri.memomash.screen.SettingsScreen;


public class GameManager {

    public static final GameManager INSTANCE = new GameManager();

    private static final String SOUND_KEY = "sound";
    private static final String BACKGROUND_MUSIC_KEY = "music";
    private static final String GRID_SIZE_KEY = "gridSize";
    private static final String RESULTS_FILE_NAME = "results.json";
    private static final String DIFFICULTY_KEY = "level";

    private final Preferences PREFS;
    private boolean soundDisabled = true;
    private boolean bgMusicDisabled = true;
    private String selectedGridSize = "4x5";
    private List<GameResult> results = new ArrayList<>();
    private Difficulty difficulty = Difficulty.EASY;
    private FileHandle resultsFile;


    private GameManager() {
        PREFS = Gdx.app.getPreferences(SettingsScreen.class.getSimpleName());
        soundDisabled = PREFS.getBoolean(SOUND_KEY, true);
        bgMusicDisabled = PREFS.getBoolean(BACKGROUND_MUSIC_KEY, true);
        selectedGridSize = PREFS.getString(GRID_SIZE_KEY, "4x5");
        difficulty = Difficulty.fromName(PREFS.getString(DIFFICULTY_KEY, Difficulty.EASY.toString()));

        resultsFile = Gdx.files.local(RESULTS_FILE_NAME);
        loadResults();
    }

    public boolean isSoundEnabled() {
        return !soundDisabled;
    }
    public boolean isBackgroundMusicEnabled() {
        return !bgMusicDisabled;
    }
    public String getSelectedGridSize() {
        return selectedGridSize;
    }
    public Difficulty getSelectedDifficulty() {
        return difficulty;
    }


    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        PREFS.putString(DIFFICULTY_KEY, difficulty.toString());
        PREFS.flush();
    }

    public int getBoardRows() {
        return Integer.parseInt(selectedGridSize.split("x")[0]);
    }

    public int getBoardColumns() {
        return Integer.parseInt(selectedGridSize.split("x")[1]);
    }

    public void setSoundDisabled(boolean soundDisabled) {
        this.soundDisabled = soundDisabled;
        PREFS.putBoolean(SOUND_KEY, soundDisabled);
        PREFS.flush();
    }

    public void setMusicDisabled(boolean musicDisabled) {
        this.bgMusicDisabled = musicDisabled;
        PREFS.putBoolean(BACKGROUND_MUSIC_KEY, musicDisabled);
        PREFS.flush();
    }

    public void setSelectedGridSize(int columns, int rows) {
        this.selectedGridSize = rows + "x" + columns;
        PREFS.putString(GRID_SIZE_KEY, selectedGridSize);
        PREFS.flush();
    }

    public void changePlayerName(String newName, int playerNumber) {
        if (results != null && !results.isEmpty()) {
            for (GameResult result : results) {
                if (result.getPlayerNumber() == playerNumber) {
                    result.setPlayerName(newName);
                    break; // Assuming playerNumber is unique, exit loop once found
                }
            }
            saveResults();
        }
    }


    public void addResult(int playerNumber, String playerName, float elapsedTime){
        results.add(new GameResult(playerNumber, playerName, elapsedTime,getSelectedDifficulty(), getBoardColumns(), getBoardRows()));
        saveResults();
    }
    public List<GameResult> getResults() {
        return results;
    }

    private void saveResults() {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        String resultJson = json.prettyPrint(results); // Apply prettyPrint to the JSON string
        resultsFile.writeString(resultJson, false);
    }

    public void clearAllPlayerData() {
        results.clear();
        saveResults();
    }

    public void loadResults(){
        if (resultsFile.exists()) {
            String resultJson = resultsFile.readString();
            Json json = new Json();
            results = json.fromJson(ArrayList.class, resultJson);
        }
    }
}