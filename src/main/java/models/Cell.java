package models;

import gameplay.Sweeper;
import lombok.Data;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Cell {
    private int cellNum;
    private int[] cellCoordinates;
    private List<Integer> neighbors;
    private WebElement webElement;

    public Cell(int cellNum, int[] cellCoordinates, WebElement webElement) {
        this.cellNum = cellNum;
        this.cellCoordinates = cellCoordinates;
        this.webElement = webElement;
    }

    void addNeighbors(List<Cell> neighbors) {
        this.neighbors = neighbors.stream()
                .map(Cell::getCellNum)
                .collect(Collectors.toList());
    }

    public void click() {
        this.webElement.click();
        Sweeper.sleepFor(100);
    }

    public void flag(ChromeDriver driver) {
        Actions actions = new Actions(driver);
        actions.contextClick(this.webElement).perform();
        Sweeper.sleepFor(100);
    }
}
