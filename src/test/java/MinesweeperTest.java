import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
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
        MinesweeperService minesweeperService = new MinesweeperService(driver);
        minesweeperService.startGame(10,2);
    }

    @AfterEach
    void cleanup() {
        driver.close();
    }
}
