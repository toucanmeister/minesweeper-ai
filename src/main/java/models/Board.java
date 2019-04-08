package models;

import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    public final static int SIZE = 64;
    public final static int ROWSIZE = 8;

    public Cell[] cells = new Cell[SIZE];

    public Board(ChromeDriver driver) {

        for (int i = 0; i < SIZE; i++) {
            cells[i] = new Cell(i, createCellCoordinates(i), driver.findElementById(Integer.toString(i)));
        }
        for (Cell cell: cells) {
            cell.addNeighbors(createNeighborList(cell));
        }
    }

    private int[] createCellCoordinates(int cellNum) throws IndexOutOfBoundsException {

        int counter = 0;
        for (int row = 0; row < Board.ROWSIZE; row++) {
            for (int column = 0; column < Board.ROWSIZE; column++) {
                if (counter == cellNum) {
                    return new int[]{row, column};
                }
                counter++;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    private List<Cell> createNeighborList(Cell cell) {

        int[] originCoordinates = cell.getCellCoordinates();
        int originRow = originCoordinates[0];
        int originColumn = originCoordinates[1];
        List<Cell> neighbors = new ArrayList<>();

        // go through all adjacent cells (includes cells that aren't even on the board)
        for (int row = originRow - 1; row <= originRow + 1; row++) {
            for (int column = originColumn - 1; column <= originColumn + 1; column++) {

                // ignore self
                if (row == originRow && column == originColumn) {
                    continue;
                }

                if (coordinatesAreOnBoard(row, column)) {
                    neighbors.add(getCellByCoordinates(row, column));
                }
            }
        }
        return neighbors;
    }

    private boolean coordinatesAreOnBoard(int row, int column) {

        if (row < 0 || row > Board.ROWSIZE - 1) {
            return false;
        } else {
            return column >= 0 && column <= Board.ROWSIZE - 1;
        }
    }

    private Cell getCellByCoordinates(int row, int column) throws IndexOutOfBoundsException {

        for (Cell cell : cells) {
            if (Arrays.equals(cell.getCellCoordinates(), new int[]{row, column})) {
                return cell;
            }
        }
        throw new IndexOutOfBoundsException();
    }
}
