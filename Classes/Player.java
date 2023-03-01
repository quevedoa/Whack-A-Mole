package Classes;

import java.io.Serializable;

public class Player implements Serializable {
    private String username;
    private int score;
    private int juegosGanados;
    private boolean currentGameWinner;
    public Player(String username) {
        this.username = username;
        this.score = 0;
        this.juegosGanados = 0;
        this.currentGameWinner = false;
    }

    public String getUsername() { return this.username; }
    public int getScore() {
        return this.score;
    }
    public int getJuegosGanados() { return this.juegosGanados; }
    public boolean isCurrentGameWinner() { return this.currentGameWinner; }

    public void givePoint() { this.score = this.score + 1; }
    public void setCurrentGameWinner(boolean isWinner) {
        this.currentGameWinner = isWinner;
    }
}
