package pages;

import lombok.Getter;
import lombok.Setter;
import models.Board;
import models.Cell;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.function.Predicate;

@Getter
@Setter
public class MinesweeperPage {
    private RemoteWebDriver driver;
    private Board board;

    final private By startButton = By.id("startButton");
    final private By numberOfRows = By.id("rowInput");
    final private By numberOfMines = By.id("minesInput");
    final private By lost = By.id("die");
    final private By won = By.id("win");

    final private String url = "localhost:4200";

    public Predicate<Cell> cellHasNeighbormines() {
        return cell -> !(getCellText(cell).equals(""));
    }

    public Predicate<Cell> cellIsNotClicked() {
        return cell -> !getCellBackgroundColor(cell).equals("rgba(169, 169, 169, 1)");
    }

    public Predicate<Cell> cellIsFlagged() {
        return cell -> getCellBackgroundColor(cell).equals("rgba(255, 99, 71, 1)");
    }

    public Predicate<Cell> cellIsNotClickedAndNotFlagged() {
        return cell -> !getCellBackgroundColor(cell).equals("rgba(169, 169, 169, 1)") &&
            !getCellBackgroundColor(cell).equals("rgba(255, 99, 71, 1)");
    }

    public Predicate<Cell> cellHasOpenNeighbors() {
        return cell -> cell.getNeighbors().stream()
                .map(neighborNum -> board.cells[neighborNum])
                .anyMatch(cellIsNotClickedAndNotFlagged());
    }

    public MinesweeperPage(RemoteWebDriver driver) {
        setDriver(driver);
        goToPage();
    }

    private void goToPage() {
        getDriver().get(getUrl());
    }

    public boolean isWon() {
        try {
            getDriver().findElement(won);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isAlive() {
        try {
            getDriver().findElement(lost);
            return false;
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    private static void sleepFor() {
        try {
            Thread.sleep((long) 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clickStart() {
        getDriver().findElement(startButton).click();
        sleepFor();
    }

    public String getCellText(Cell cell) {
        return getCellWebElement(cell).getText();
    }

    private String getCellBackgroundColor(Cell cell) {
        return getCellWebElement(cell).getCssValue("background-color");
    }

    public void clickCell(Cell cell) {
        getCellWebElement(cell).click();
    }

    public void flagCell(Cell cell) {
        Actions actions = new Actions(driver);
        actions.contextClick(getCellWebElement(cell)).perform();
    }

    private WebElement getCellWebElement(Cell cell) {
        return getDriver().findElementById(String.valueOf(cell.getCellNum()));
    }

    public void setNumOfRows(int numOfRows) {
        getDriver().findElement(numberOfRows).clear();
        getDriver().findElement(numberOfRows).sendKeys(String.valueOf(numOfRows));
    }

    public void setNumOfMines(int numOfMines) {
        getDriver().findElement(numberOfMines).clear();
        getDriver().findElement(numberOfMines).sendKeys(String.valueOf(numOfMines));
    }
}
