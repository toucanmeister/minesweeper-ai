package gameplay;

import models.Cell;

import java.util.function.Predicate;

public class Predicates {

    public static Predicate<Cell> cellHasNeighbormines() {
        return cell -> !(cell.getWebElement().getText().equals(""));
    }

    public static Predicate<Cell> cellIsNotClicked() {
        return cell -> !cell.getWebElement().getCssValue("background-color").equals("rgba(169, 169, 169, 1)");
    }

    public static Predicate<Cell> cellIsFlagged() {
        return cell -> cell.getWebElement().getCssValue("background-color").equals("rgba(255, 99, 71, 1)");
    }

    public static Predicate<Cell> cellIsNotFlagged() {
        return cell -> !(cell.getWebElement().getCssValue("background-color").equals("rgba(255, 99, 71, 1)"));
    }
}
