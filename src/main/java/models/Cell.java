package models;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Cell {
    private int cellNum;
    private int[] cellCoordinates;
    private List<Integer> neighbors;

    public Cell(int cellNum, int[] cellCoordinates) {
        this.cellNum = cellNum;
        this.cellCoordinates = cellCoordinates;
    }

    void addNeighbors(List<Cell> neighbors) {
        this.neighbors = neighbors.stream()
                .map(Cell::getCellNum)
                .collect(Collectors.toList());
    }
}
