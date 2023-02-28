package Classes;

import java.io.Serializable;

public class Player implements Serializable {
    private String username;
    private boolean playing;
    private int score;
    public Player(String username) {
        this.username = username;
        this.playing = false;
        this.score = 0;
    }
//    public Player(String username, boolean playing, int score) {
//        this.username = username;
//        this.playing = playing;
//        this.score = 0;
//    }

    public String getUsername() {
        return this.username;
    }

    public int getScore() {
        return this.score;
    }
}
