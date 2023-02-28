package Classes;

import java.io.Serializable;

public class Move implements Serializable {
    private int row;
    private int column;
    public Move(int row, int column) {
        this.row = row;
        this.column = column;
    }
}
