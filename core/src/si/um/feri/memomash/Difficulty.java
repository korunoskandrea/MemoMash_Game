package si.um.feri.memomash;


import java.util.Arrays;
import java.util.List;

public enum Difficulty {
    EASY(1,"easy"),
    HARD(2, "hard");

    private final int value;
    private final String name;

    Difficulty(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public String[] getNames(){
        return new String[]{
                EASY.name, HARD.name
        };
    }
    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    static public Difficulty fromName(String difficulty){
        if(difficulty.equals("easy")){
            return EASY;
        } else {
            return HARD;
        }
    }


    @Override
    public String toString() {
        return name;
    }
}

