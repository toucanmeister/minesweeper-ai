package gameplay;

import org.openqa.selenium.chrome.ChromeDriver;

public class Start {

    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver", "C:/Users/Farin/IdeaProjects/minesweeper-ai/chromedriver.exe");

        ChromeDriver driver = new ChromeDriver();
        driver.get("localhost:4200");

        for(int i=0; i < 3; i++) {
            Sweeper sweeper = new Sweeper(driver);
            System.out.println(sweeper.playMinesweeper());
        }
        driver.close();
    }
}
