package models;

import enums.CellStatus;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Cell {
    private int cellNum;
    private int[] cellCoordinates;
    private List<Integer> neighbors;
    private CellStatus status;
    private String cellText = "";

    public Cell(int cellNum, int[] cellCoordinates) {
        setCellNum(cellNum);
        setCellCoordinates(cellCoordinates);
        setStatus(CellStatus.UNCLICKED);
    }

    void addNeighbors(List<Cell> neighbors) {
        this.neighbors = neighbors.stream()
                .map(Cell::getCellNum)
                .collect(Collectors.toList());
    }
}
