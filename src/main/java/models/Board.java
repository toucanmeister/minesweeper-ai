package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pages.MinesweeperPage;

@Setter
@Getter
public class Board {
    private int numOfRows;
    private MinesweeperPage minesweeperPage;

    public Cell[] cells;
    public List<Cell> unclickedCells;
    public List<Cell> unclickedAndUnflaggedCells;
    public List<Cell> flaggedCells;

    public Board(MinesweeperPage minesweeperPage, int numOfRows) {
        this.numOfRows = numOfRows;
        cells = new Cell[getBoardSize()];

        for (int i = 0; i < getBoardSize(); i++) {
            cells[i] = new Cell(i, createCellCoordinates(i));
        }
        for (Cell cell: cells) {
            cell.addNeighbors(createNeighborList(cell));
        }
        this.minesweeperPage = minesweeperPage;
    }

    public int getBoardSize() {
        return numOfRows * numOfRows;
    }

    private int[] createCellCoordinates(int cellNum) throws IndexOutOfBoundsException {

        int counter = 0;
        for (int row = 0; row < getNumOfRows(); row++) {
            for (int column = 0; column < getNumOfRows(); column++) {
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

        if (row < 0 || row > getNumOfRows() - 1) {
            return false;
        } else {
            return column >= 0 && column <= getNumOfRows() - 1;
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
