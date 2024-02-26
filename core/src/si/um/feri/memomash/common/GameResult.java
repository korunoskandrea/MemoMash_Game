package si.um.feri.memomash.common;

import si.um.feri.memomash.Difficulty;

public class GameResult {
    private String playerName;
    private int playerNumber;
    private float time;
    private Difficulty difficulty;
    private BoardDimmesnison dimmesnison;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public float getTime() {
        return time;
    }

    public Difficulty getDifficulty(){
        return difficulty;
    }

    public si.um.feri.memomash.common.GameResult.BoardDimmesnison getDimmesnison() {
        return dimmesnison;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public GameResult(int playerNumber,String playerName, float time,Difficulty difficulty, BoardDimmesnison dimmesnison) {
        this.playerNumber = playerNumber;
        this.playerName = playerName;
        this.time = time;
        this.difficulty = difficulty;
        this.dimmesnison = dimmesnison;
    }

    public GameResult(int playerNumber, String playerName, float time,Difficulty difficulty, int width, int height) {
        this(playerNumber,playerName, time, difficulty, new BoardDimmesnison(width, height));
    }

    static public class BoardDimmesnison {
        private int width;
        private int height;

        public BoardDimmesnison(int widht, int heiht) {
            this.width = widht;
            this.height = heiht;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }
    }
}