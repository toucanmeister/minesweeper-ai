package services;

import lombok.Getter;
import lombok.Setter;
import models.Board;
import models.Cell;
import org.openqa.selenium.remote.RemoteWebDriver;
import pages.MinesweeperPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
@Setter
public class MinesweeperService {
    private Board board;
    private MinesweeperPage minesweeperPage;
    RemoteWebDriver driver;

    public MinesweeperService(RemoteWebDriver driver) {
        setMinesweeperPage(new MinesweeperPage(driver));
        createBoard();
        setDriver(driver);
    }

    private void createBoard() {
        setBoard(new Board());
    }

    public void startGame() {
        getMinesweeperPage().clickStart();
        playMinesweeper();
    }

    boolean playMinesweeper() {
        clickRandomCell();

        if (getMinesweeperPage().isAlive()) {
            return playLoop();
        } else {
            return false;
        }
    }

    private boolean playLoop() {
        while(true) {
            findAndFlagMines();
            if (getMinesweeperPage().isWon()) {
                return true;
            }
            boolean didSomething = findAndClickSafeCells();

            if (!didSomething){
                clickRandomCell();
            }

            if (getMinesweeperPage().isWon()) {
                return true;
            } else if (!getMinesweeperPage().isAlive()) {
                return false;
            }
        }
    }

    public void clickRandomCell() {
        Cell[] unclickedCells = Arrays.stream(board.cells)
                .filter(minesweeperPage.cellIsNotClickedAndNotFlagged())
                .toArray(Cell[]::new);

        getMinesweeperPage().clickCell(unclickedCells[ThreadLocalRandom.current().nextInt(0, unclickedCells.length)]);
    }

    private void findAndFlagMines() {
        List<Cell> foundMines = findMines();
        flag(foundMines);
    }

    private boolean findAndClickSafeCells() {
        List<Cell> safeCells = findSafeCells();
        click(safeCells);

        return !safeCells.isEmpty();
    }

    private List<Cell> findMines() {
        List<Cell> cellsWithNeighbormines = findCellsWithNeighbormines();
        List<Cell> mines = new ArrayList<>();

        //SLOW
        for (Cell cell: cellsWithNeighbormines) {
            if (neighborsHaveToBeMines(cell)) {
                mines.addAll(cell.getNeighbors().stream()
                        .map(neighborNum -> board.cells[neighborNum])
                        .filter(minesweeperPage.cellIsNotClickedAndNotFlagged())
                        .collect(Collectors.toList()));
            }
        }
        mines = mines.stream()
                .distinct()
                .collect(Collectors.toList());
        return mines;
    }

    private boolean neighborsHaveToBeMines(Cell cell) {
        List<Integer> neighbors = cell.getNeighbors();

        int numOfNeighbors = (int) neighbors.stream()
                .map(neighborNum -> board.cells[neighborNum])
                .filter(minesweeperPage.cellIsNotClicked())
                .count();
        return numOfNeighbors == Integer.parseInt(getMinesweeperPage().getCellText(cell));
    }

    private List<Cell> findCellsWithNeighbormines() {
        return Arrays.stream(board.cells)
                .filter(minesweeperPage.cellHasNeighbormines())
                .collect(Collectors.toList());
    }


    private List<Cell> findSafeCells() {
        List<Cell> cellsWithNeighborMines = findCellsWithNeighbormines();
        List<Cell> safeCells = new ArrayList<>();

        //SLOW
        for (Cell cell: cellsWithNeighborMines) {
            if (allMinesFoundAround(cell)) {
                safeCells.addAll(cell.getNeighbors().stream()
                        .map(neighborNum -> board.cells[neighborNum])
                        .filter(minesweeperPage.cellIsNotClickedAndNotFlagged())
                        .collect(Collectors.toList()));
            }
        }
        return safeCells.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean allMinesFoundAround(Cell cell) {
        int numOfFlags = (int) cell.getNeighbors().stream()
            .map(neighborNum -> board.cells[neighborNum])
            .filter(minesweeperPage.cellIsFlagged())
            .count();
        return numOfFlags == Integer.parseInt(getMinesweeperPage().getCellText(cell));
    }

    private void click(List<Cell> safeCells) {
        for(Cell cell: safeCells) {
            getMinesweeperPage().clickCell(cell);
        }
    }

    private void flag(List<Cell> foundMines) {
        for(Cell cell: foundMines) {
            getMinesweeperPage().flagCell(cell);
        }
    }
}
