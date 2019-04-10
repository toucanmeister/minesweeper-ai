package services;

import enums.CellStatus;
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
        setDriver(driver);
    }

    private void createBoard(int numOfRows) {
        setBoard(new Board(getMinesweeperPage(),numOfRows));
        minesweeperPage.setBoard(board);
    }

    public void startGame(int numOfRows, int numOfMines) {
        createBoard(numOfRows);

        getMinesweeperPage().setNumOfRows(numOfRows);

        getMinesweeperPage().setNumOfMines(numOfMines);
        getMinesweeperPage().clickStart();

        playMinesweeper();
    }

    private void playMinesweeper() {
        clickRandomCell();
        if (getMinesweeperPage().isAlive()) {
            playLoop();
        }
    }

    private void playLoop() {
        while(true) {
            System.out.println("FLAG");
            findAndFlagMines();
            if (getMinesweeperPage().isWon()) {
                break;
            }
            System.out.println("CLICK");
            boolean didSomething = findAndClickSafeCells();

            if (!didSomething){
                System.out.println("RANDOM");
                clickRandomCell();
            }

            if (getMinesweeperPage().isWon() || !getMinesweeperPage().isAlive()) {
                break;
            }
        }
    }

    private void clickRandomCell() {
        Cell[] unclickedCells = Arrays.stream(board.cells)
                .filter(minesweeperPage.cellIsNotClickedAndNotFlagged())
                .toArray(Cell[]::new);

        getMinesweeperPage().clickCell(unclickedCells[ThreadLocalRandom.current().nextInt(0, unclickedCells.length)]);
        updateClickedCells();
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
                .filter(minesweeperPage.cellHasOpenNeighbors())
                .collect(Collectors.toList());
    }


    private List<Cell> findSafeCells() {
        List<Cell> cellsWithNeighborMines = findCellsWithNeighbormines();
        List<Cell> safeCells = new ArrayList<>();

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
        updateClickedCells();
    }

    private void flag(List<Cell> foundMines) {
        for(Cell cell: foundMines) {
            getMinesweeperPage().flagCell(cell);
            cell.setStatus(CellStatus.FLAGGED);
        }
    }

    private void updateClickedCells() {
        Arrays.stream(board.cells)
            .filter(minesweeperPage.cellIsNotClicked())
            .filter(minesweeperPage.webElementIsClicked())
            .forEach(cell -> {
                cell.setStatus(CellStatus.CLICKED);
                cell.setCellText(getMinesweeperPage().getWebElementText(cell));
            });
    }
}
