package in.vtech.selenum.testNG.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;



public class DreamPortalTest {


	 private WebDriver driver; 
	    private WebDriverWait wait; 
	    private final String BASE_URL = "https://arjitnigam.github.io/myDreams/"; 

	    
	    @BeforeClass
	    public void setup() {
	        
	        WebDriverManager.chromedriver().setup();
	        driver = new ChromeDriver();
	        driver.manage().window().maximize(); 
	        
	        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	    }

	   
	    @Test(priority = 1, description = "Verifies the functionality of the Home page (index.html)")
	    public void testIndexPage() {
	        driver.get(BASE_URL); 

	        
	        WebElement loadingAnimation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loading-animation")));
	        Assert.assertTrue(loadingAnimation.isDisplayed(), "Loading animation should be visible initially.");

	        
	        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-animation")));
	        Assert.assertFalse(loadingAnimation.isDisplayed(), "Loading animation should disappear.");


	        
	        WebElement mainContent = driver.findElement(By.id("main-content"));
	        WebElement myDreamsButton = driver.findElement(By.id("myDreamsButton"));
	        Assert.assertTrue(mainContent.isDisplayed(), "Main content should be visible.");
	        Assert.assertTrue(myDreamsButton.isDisplayed(), "My Dreams button should be visible.");

	        
	        String originalWindowHandle = driver.getWindowHandle();

	       
	        myDreamsButton.click();

	        
	        wait.until(ExpectedConditions.numberOfWindowsToBe(3));

	        
	        Set<String> allWindowHandles = driver.getWindowHandles();
	        List<String> newWindowHandles = new ArrayList<>(allWindowHandles);
	        newWindowHandles.remove(originalWindowHandle);

	        
	        Assert.assertEquals(newWindowHandles.size(), 2, "Expected exactly 2 new tabs/windows to open.");

	        boolean diaryPageFound = false;
	        boolean totalPageFound = false;

	        
	        for (String handle : newWindowHandles) {
	            driver.switchTo().window(handle); 
	            String currentUrl = driver.getCurrentUrl();
	            if (currentUrl.contains("dreams-diary.html")) {
	                diaryPageFound = true;
	            } else if (currentUrl.contains("dreams-total.html")) {
	                totalPageFound = true;
	            }
	        }
	        
	        Assert.assertTrue(diaryPageFound, "dreams-diary.html should open in a new tab.");
	        Assert.assertTrue(totalPageFound, "dreams-total.html should open in a new tab.");

	        
	        driver.switchTo().window(originalWindowHandle);
	    }

	    
	    @Test(priority = 2, description = "Verifies the data and structure of the Dream Log Table (dreams-diary.html)")
	    public void testDreamsDiaryPage() {
	        
	        driver.get(BASE_URL + "dreams-diary.html");
	        
	        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dreamTableBody")));

	        
	        List<WebElement> dreamEntries = driver.findElements(By.cssSelector("#dreamTableBody tr"));
	        
	        Assert.assertEquals(dreamEntries.size(), 10, "Expected exactly 10 dream entries in the log table.");

	        
	        for (WebElement entry : dreamEntries) {
	           
	            List<WebElement> columns = entry.findElements(By.tagName("td"));

	            
	            Assert.assertEquals(columns.size(), 3, "Each dream entry row should have 3 columns (Dream Name, Days Ago, Dream Type).");

	            
	            for (WebElement column : columns) {
	                Assert.assertFalse(column.getText().trim().isEmpty(), "All columns in a dream entry should be filled and not empty.");
	            }

	            
	            String dreamType = columns.get(2).getText();
	            Assert.assertTrue(dreamType.equals("Good") || dreamType.equals("Bad"),
	                    "Dream type should be either 'Good' or 'Bad', but found: '" + dreamType + "'.");
	        }
	    }

	    
	    @Test(priority = 3, description = "Verifies the statistics on the Summary Page (dreams-total.html)")
	    public void testDreamsTotalPage() {
	       
	        driver.get(BASE_URL + "dreams-total.html");
	        
	        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("goodDreamsCount")));

	        
	        Assert.assertEquals(driver.findElement(By.id("goodDreamsCount")).getText(), "6", "Good Dreams count is incorrect.");
	        Assert.assertEquals(driver.findElement(By.id("badDreamsCount")).getText(), "4", "Bad Dreams count is incorrect.");
	        Assert.assertEquals(driver.findElement(By.id("totalDreamsCount")).getText(), "10", "Total Dreams count is incorrect.");
	        Assert.assertEquals(driver.findElement(By.id("recurringDreamsCount")).getText(), "2", "Recurring Dreams count is incorrect.");

	        
	        WebElement recurringDreamsList = driver.findElement(By.id("recurringDreamsList"));
	        String recurringDreamsText = recurringDreamsList.getText();

	        Assert.assertTrue(recurringDreamsText.contains("Flying over mountains"),
	                "\"Flying over mountains\" should be listed as a recurring dream.");
	        Assert.assertTrue(recurringDreamsText.contains("Lost in maze"),
	                "\"Lost in maze\" should be listed as a recurring dream.");
	    }

	    
	    @AfterClass
	    public void teardown() {
	        if (driver != null) {
	            driver.quit(); 
	        }
	    }
}

	

