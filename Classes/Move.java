package Classes;

import java.io.Serializable;

public class Move implements Serializable {
    private int row;
    private int column;
    private int ronda;
    public Move(int row, int column) {
        this.row = row;
        this.column = column;
        this.ronda = 0;
    }

    public Move(int row, int column, int ronda) {
        this.row = row;
        this.column = column;
        this.ronda = ronda;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }

    public int getRonda() { return ronda; }
}
