package Classes;

import java.io.Serializable;

public class Move implements Serializable {
    private int row;
    private int column;
    private int numJuego;
    public Move(int row, int column) {
        this.row = row;
        this.column = column;
//        this.numJuego = numJuego;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }
}
