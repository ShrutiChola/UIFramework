package com.maximus.tx.MaxCRMDemo.testUtils;




import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class testUtils {
	
	public static WebDriver driver;

	public static WebDriver selectBrowser(String browser)
	{
		if(browser.equalsIgnoreCase("chrome"))
		{
			String DriverPath = System.getProperty("user.dir")+"\\src\\com\\FFQHome\\browserDrivers\\chromedriver.exe";
			System.setProperty("webdriver.chrome.driver", DriverPath);
			driver = new ChromeDriver();
			driver.manage().window().maximize();
			return driver;
		}
		else if(browser.equalsIgnoreCase("firefox"))
		{
			String DriverPath = System.getProperty("user.dir")+"\\src\\com\\FFQHome\\browserDrivers\\geckodriver.exe";
			System.setProperty("webdriver.chrome.driver", DriverPath);
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
			return driver;
		}
		else if(browser.equalsIgnoreCase("IE"))
		{
			driver = new InternetExplorerDriver();
			driver.manage().window().maximize();
			return driver;
		}
		return null;
	}
	
	public void driverWait(int timeinsec) throws InterruptedException
	{
		//Reporter.log("Waiting for "+timeinsec+ "seconds");
		Thread.sleep(timeinsec*1000);
	}
	
	public void impWait(int timeinsec)
	{
		try{
			driver.manage().timeouts().implicitlyWait(timeinsec, TimeUnit.SECONDS);
		}
		catch(Throwable error){
			Assert.assertTrue("page timedout", false);
		}
	}
}

