package com.saucelabs.example;

import com.saucelabs.example.util.ResultReporter;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.openqa.selenium.OutputType;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by grago on 28/02/2017.
 */
public class TestSetup {

    private ResultReporter reporter;
    private ThreadLocal<AndroidDriver> driver = new ThreadLocal<AndroidDriver>();
  
  /**
   * DataProvider that explicitly sets the browser combinations to be used.
   *
   * @param testMethod
   * @return
   */
  @DataProvider(name = "devices", parallel = true)
  public static Object[][] sauceBrowserDataProvider(Method testMethod) {
      return new Object[][]{
    		  //Verify that your account has access to the devices below
              new Object[]{"Android", "Samsung Galaxy S6", "7"},
              new Object[]{"Android", "LG Nexus 5X", "8"}
      };
  }
  
  private AndroidDriver createDriver(String platformName, String platformVersion, String deviceName, String methodName) throws MalformedURLException {
  	
      DesiredCapabilities capabilities = new DesiredCapabilities();
      capabilities.setCapability("testobject_api_key", System.getenv("TESTOBJECT_API_KEY"));
	  capabilities.setCapability("deviceName", deviceName);
      capabilities.setCapability("platformVersion", platformVersion);
      capabilities.setCapability("platformName", platformName);
      capabilities.setCapability("name",  methodName);

      driver.set(new AndroidDriver<WebElement>(
              new URL(System.getenv("APPIUM_URL")), capabilities));
      return driver.get();
  }

    /* A simple addition, it expects the correct result to appear in the result field. */
    @Test(dataProvider = "devices")
    public void twoPlusTwoOperation(String platformName, String deviceName, String platformVersion, Method method) throws MalformedURLException {

    	AndroidDriver driver = createDriver(platformName, platformVersion, deviceName, method.getName());

        /* Get the elements. */
        MobileElement buttonTwo = (MobileElement)(driver.findElement(By.id("net.ludeke.calculator:id/digit2")));
        MobileElement buttonPlus = (MobileElement)(driver.findElement(By.id("net.ludeke.calculator:id/plus")));
        MobileElement buttonEquals = (MobileElement)(driver.findElement(By.id("net.ludeke.calculator:id/equal")));
        MobileElement resultField = (MobileElement)(driver.findElement(By.xpath("//android.widget.EditText[1]")));

        /* Add two and two. */
        buttonTwo.click();
        buttonPlus.click();
        buttonTwo.click();
        driver.getScreenshotAs(OutputType.FILE);
        buttonEquals.click();
        driver.getScreenshotAs(OutputType.FILE);

        /* Check if within given time the correct result appears in the designated field. */
        (new WebDriverWait(driver, 30)).until(ExpectedConditions.textToBePresentInElement(resultField, "4"));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
    	AndroidDriver driver = getWebDriver();
    	reporter = new ResultReporter();
        boolean success = result.isSuccess();
        String sessionId = driver.getSessionId().toString();

        reporter.saveTestStatus(sessionId, success);
        driver.quit();
    }
    
    /**
     * @return the {@link WebDriver} for the current thread
     */
    public AndroidDriver getWebDriver() {
        return driver.get();
    }
}
