package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CreateProductFunctionalTest {

    @LocalServerPort
    private Integer port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        baseUrl = String.format("http://localhost:%d", port);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testCreateProduct_Success() {
        driver.get(baseUrl + "/product/create");

        WebElement nameInput = driver.findElement(By.id("nameInput"));
        WebElement quantityInput = driver.findElement(By.id("quantityInput"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        nameInput.sendKeys("Test Product");
        quantityInput.sendKeys("10");
        submitButton.click();

        assertTrue(driver.getCurrentUrl().endsWith("/product/list"));
        WebElement productName = driver.findElement(By.xpath("//td[contains(text(), 'Test Product')]"));
        assertNotNull(productName);
    }


    @Test
    void testCreateProduct_MissingNameValidation() {
        driver.get(baseUrl + "/product/create");

        WebElement quantityField = driver.findElement(By.id("quantityInput"));
        quantityField.sendKeys("10");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        assertTrue(driver.getCurrentUrl().contains("/product/create"),
                "The user should remain on the creation page upon validation error.");

        WebElement errorAlert = driver.findElement(By.cssSelector("div.text-danger"));
        assertTrue(errorAlert.isDisplayed(),
                "An error message should be visible if the product name is missing.");
    }

    @Test
    void testCreateProduct_NegativeQuantity() {
        driver.get(baseUrl + "/product/create");

        WebElement nameInput = driver.findElement(By.id("nameInput"));
        WebElement quantityInput = driver.findElement(By.id("quantityInput"));
        nameInput.sendKeys("Rusdi Ngawi");
        quantityInput.sendKeys("-5");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        assertTrue(driver.getCurrentUrl().contains("/product/create"),
                "Should remain on '/product/create' when quantity is negative.");

        WebElement errorMessage = driver.findElement(By.cssSelector("div.text-danger"));
        assertTrue(errorMessage.isDisplayed(),
                "Validation error message should be displayed for negative quantity.");
    }

    @Test
    void testCreateProduct_EmptyQuantity() {
        driver.get(baseUrl + "/product/create");

        WebElement nameInput = driver.findElement(By.id("nameInput"));
        nameInput.sendKeys("Empty Quantity Product");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        assertTrue(driver.getCurrentUrl().contains("/product/create"),
                "Should remain on '/product/create' if quantity is empty.");

        WebElement errorMessage = driver.findElement(By.cssSelector("div.text-danger"));
        assertTrue(errorMessage.isDisplayed(),
                "Error message for empty product quantity should be displayed.");
    }
}