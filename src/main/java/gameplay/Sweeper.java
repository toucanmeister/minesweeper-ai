package gameplay;

import models.Board;
import models.Cell;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Sweeper {

    private Board board;
    private ChromeDriver driver;
    private boolean alive;
    private boolean won;

    Sweeper(ChromeDriver driver) {
        this.driver = driver;
        createBoard();
    }

    private void createBoard() {
        this.board = new Board(driver);
        driver.findElementById("startButton").click();
        sleepFor(1000);
    }

    public static void sleepFor(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    boolean playMinesweeper() {
        board.cells[ThreadLocalRandom.current().nextInt(0, Board.SIZE)].click();
        updateAliveStatus();
        updateWonStatus();

        if (alive) {
            return playLoop();
        } else {
            return false;
        }
    }

    private void updateAliveStatus() {
        try {
            driver.findElementById("die");
            this.alive = false;
        } catch (Exception e){
            this.alive = true;
        }
    }

    private void updateWonStatus() {
        try {
            driver.findElementById("win");
            this.won = true;
        } catch (Exception e) {
            this.won = false;
        }
    }

    private boolean playLoop() {
        while(true) {
            System.out.println("FLAG");
            findAndFlagMines();
            if (won) {
                return true;
            }

            System.out.println("CLICK");
            boolean didSomething = findAndClickSafeCells();
            if (!didSomething){
                System.out.println("RANDOM");
                clickRandomCell();
            }

            if (won) {
                return true;
            } else if (!alive) {
                return false;
            }
        }
    }

    private void clickRandomCell() {
        Cell[] unclickedCells = Arrays.stream(board.cells)
                .filter(Predicates.cellIsNotClicked())
                .filter(Predicates.cellIsNotFlagged())
                .toArray(Cell[]::new);

        unclickedCells[ThreadLocalRandom.current().nextInt(0, unclickedCells.length)].click();
        updateAliveStatus();
        updateWonStatus();
    }

    private void findAndFlagMines() {
        List<Cell> foundMines = findMines();
        flag(foundMines);
        updateWonStatus();
    }

    private boolean findAndClickSafeCells() {
        List<Cell> safeCells = findSafeCells();
        click(safeCells);
        updateAliveStatus();
        updateWonStatus();

        return !safeCells.isEmpty();
    }

    private List<Cell> findMines() {
        List<Cell> cellsWithNeighbormines = findCellsWithNeighbormines();

        List<Cell> mines = new ArrayList<>();

        for (Cell cell: cellsWithNeighbormines) {
            if (neighborsHaveToBeMines(cell)) {
                mines.addAll(cell.getNeighbors().stream()
                        .map(neighborNum -> board.cells[neighborNum])
                        .filter(Predicates.cellIsNotFlagged())
                        .filter(Predicates.cellIsNotClicked())
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
                .filter(Predicates.cellIsNotClicked())
                .count();
        return numOfNeighbors == Integer.parseInt(cell.getWebElement().getText());
    }

    private List<Cell> findCellsWithNeighbormines() {
        return Arrays.stream(board.cells)
                .filter(Predicates.cellHasNeighbormines())
                .collect(Collectors.toList());
    }


    private List<Cell> findSafeCells() {
        List<Cell> cellsWithNeighborMines = findCellsWithNeighbormines();

        List<Cell> safeCells = new ArrayList<>();

        for (Cell cell: cellsWithNeighborMines) {
            if (allMinesFoundAround(cell)){
                safeCells.addAll(cell.getNeighbors().stream()
                        .map(neighborNum -> board.cells[neighborNum])
                        .filter(Predicates.cellIsNotClicked())
                        .filter(Predicates.cellIsNotFlagged())
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
            .filter(Predicates.cellIsFlagged())
            .count();

        return numOfFlags == Integer.parseInt(cell.getWebElement().getText());
    }

    private void click(List<Cell> safeCells) {
        for(Cell cell: safeCells) {
            cell.click();
        }
    }

    private void flag(List<Cell> foundMines) {
        for(Cell cell: foundMines) {
            cell.flag(driver);
        }
    }
}
