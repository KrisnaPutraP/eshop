package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class OrderFunctionalTest {

    @LocalServerPort
    private Integer port;

    private WebDriver driver;
    private String baseUrl;
    private WebDriverWait wait;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        baseUrl = String.format("http://localhost:%d", port);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Create a product first (assuming product management is already implemented)
        createTestProduct();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    void createTestProduct() {
        driver.get(baseUrl + "/product/create");

        try {
            WebElement nameInput = driver.findElement(By.id("nameInput"));
            WebElement quantityInput = driver.findElement(By.id("quantityInput"));
            WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

            String productName = "Test Product " + UUID.randomUUID().toString().substring(0, 8);
            nameInput.sendKeys(productName);
            quantityInput.sendKeys("100");
            submitButton.click();

            // Wait for redirect to product list
            wait.until(ExpectedConditions.urlContains("/product/list"));
        } catch (Exception e) {
            // If any exception occurs (e.g., page elements are different), just continue
            // This is just setup for the actual tests
            System.out.println("Error in product creation setup: " + e.getMessage());
        }
    }

    @Test
    void testCreateOrderSuccess() {
        // Navigate to create order page
        driver.get(baseUrl + "/order/create");

        // Fill the form
        String orderId = "ORDER-" + UUID.randomUUID().toString().substring(0, 8);
        String customerName = "Test Customer";

        WebElement idInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("idInput")));
        WebElement authorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("authorInput")));
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));

        idInput.sendKeys(orderId);
        authorInput.sendKeys(customerName);
        submitButton.click();

        // Wait for redirect and verify we're on the history page
        wait.until(ExpectedConditions.urlContains("/order/history"));

        // Enter the customer name to see their orders
        WebElement authorNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("authorNameInput")));
        authorNameInput.clear();
        authorNameInput.sendKeys(customerName);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verify we can see the order in the results
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

        // Check if the order ID is present in the page
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(orderId), "Order ID should be displayed in the results");
        assertTrue(pageSource.contains(customerName), "Customer name should be displayed in the results");
    }

    @Test
    void testViewOrderHistory() {
        // Go to history page
        driver.get(baseUrl + "/order/history");

        // Generate a unique customer name that definitely won't have orders
        String uniqueCustomer = "NonExistent-" + UUID.randomUUID().toString();

        // Enter a customer name with no orders
        WebElement authorNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("authorNameInput")));
        authorNameInput.clear();
        authorNameInput.sendKeys(uniqueCustomer);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verify "No orders found" message is displayed
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-info")));
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("No orders found"), "Page should indicate no orders were found");
    }

    @Test
    void testOrderPaymentWithVoucher() {
        // First create an order
        String orderId = "ORDER-" + UUID.randomUUID().toString().substring(0, 8);
        String customerName = "Test Customer for Voucher";

        // Create order
        createTestOrder(orderId, customerName);

        // Navigate to order history and find the order
        driver.get(baseUrl + "/order/history");
        WebElement authorNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("authorNameInput")));
        authorNameInput.clear();
        authorNameInput.sendKeys(customerName);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Find and click the Pay Now button
        WebElement payButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/order/pay/')]")));
        payButton.click();

        // Verify we're on the payment page
        wait.until(ExpectedConditions.urlContains("/order/pay/"));

        // Select voucher payment method
        Select paymentMethodSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.id("paymentMethodSelect"))));
        paymentMethodSelect.selectByValue("VOUCHER");

        // Wait for voucher field to be visible and fill it
        WebElement voucherInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("voucherCodeInput")));
        voucherInput.sendKeys("ESHOP12345678");

        // Submit payment
        WebElement submitPaymentButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        submitPaymentButton.click();

        // Wait for either success message or payment confirmation page
        try {
            // Try to find either payment ID on confirmation page or success alert
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/payment-confirmation"),
                    ExpectedConditions.presenceOfElementLocated(By.className("alert-success"))
            ));
            assertTrue(true, "Payment process completed successfully");
        } catch (Exception e) {
            fail("Payment process did not complete: " + e.getMessage());
        }
    }

    @Test
    void testOrderPaymentWithBankTransfer() {
        // First create an order
        String orderId = "ORDER-" + UUID.randomUUID().toString().substring(0, 8);
        String customerName = "Test Customer for Bank Transfer";

        // Create order
        createTestOrder(orderId, customerName);

        // Navigate to order history and find the order
        driver.get(baseUrl + "/order/history");
        WebElement authorNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("authorNameInput")));
        authorNameInput.clear();
        authorNameInput.sendKeys(customerName);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Find and click the Pay Now button
        WebElement payButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/order/pay/')]")));
        payButton.click();

        // Verify we're on the payment page
        wait.until(ExpectedConditions.urlContains("/order/pay/"));

        // Select bank transfer payment method
        Select paymentMethodSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.id("paymentMethodSelect"))));
        paymentMethodSelect.selectByValue("BANK_TRANSFER");

        // Wait for bank transfer fields to be visible and fill them
        WebElement bankNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bankNameInput")));
        bankNameInput.sendKeys("Bank XYZ");

        WebElement referenceCodeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("referenceCodeInput")));
        referenceCodeInput.sendKeys("REF123456");

        // Submit payment
        WebElement submitPaymentButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        submitPaymentButton.click();

        // Wait for either success message or payment confirmation page
        try {
            // Try to find either payment ID on confirmation page or success alert
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/payment-confirmation"),
                    ExpectedConditions.presenceOfElementLocated(By.className("alert-success"))
            ));
            assertTrue(true, "Payment process completed successfully");
        } catch (Exception e) {
            fail("Payment process did not complete: " + e.getMessage());
        }
    }

    // Helper method to create a test order
    private void createTestOrder(String orderId, String customerName) {
        driver.get(baseUrl + "/order/create");

        WebElement idInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("idInput")));
        WebElement authorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("authorInput")));
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));

        idInput.sendKeys(orderId);
        authorInput.sendKeys(customerName);
        submitButton.click();

        // Wait for redirect to history page
        wait.until(ExpectedConditions.urlContains("/order/history"));
    }
}