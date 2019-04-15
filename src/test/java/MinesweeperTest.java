import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import pages.MinesweeperPage;
import services.MinesweeperService;

public class MinesweeperTest {
    private RemoteWebDriver driver;

    @BeforeEach
    void setup() {
        System.setProperty("webdriver.chrome.driver", "C:/Users/Farin/IdeaProjects/minesweeper-ai/chromedriver.exe");
        driver = new ChromeDriver();

    }

    @Test
    void testMinesweeper() {
        for (int i =0; i < 3; i++) {
            MinesweeperService minesweeperService = new MinesweeperService(driver);
            minesweeperService.startGame(8,10);
            MinesweeperPage.sleepSecond();
        }
    }

    @AfterEach
    void cleanup() {
        driver.close();
    }
}
