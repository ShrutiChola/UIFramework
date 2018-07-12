package com.Allnewhybrid.testBase;

	import java.io.File;
	import java.io.IOException;
	import java.lang.reflect.Method;
	import java.nio.file.Files;
	import java.text.SimpleDateFormat;
	import java.util.Calendar;
	import java.util.concurrent.TimeUnit;

	import org.apache.log4j.Logger;
	import org.openqa.selenium.OutputType;
	import org.openqa.selenium.TakesScreenshot;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.chrome.ChromeOptions;
	import org.openqa.selenium.firefox.FirefoxOptions;
	import org.openqa.selenium.ie.InternetExplorerOptions;
	import org.testng.ITestResult;
	import org.testng.Reporter;
	import org.testng.annotations.AfterMethod;
	import org.testng.annotations.AfterTest;
	import org.testng.annotations.BeforeClass;
	import org.testng.annotations.BeforeMethod;
	import org.testng.annotations.BeforeSuite;
	import org.testng.annotations.BeforeTest;

	import com.aventstack.extentreports.ExtentReports;
	import com.aventstack.extentreports.ExtentTest;
	import com.aventstack.extentreports.Status;
	import com.Allnewhybrid.helper.browserConfiguration.BrowserType;
	import com.Allnewhybrid.helper.browserConfiguration.ChromeBrowser;
	import com.Allnewhybrid.helper.browserConfiguration.FirefoxBrowser;
	import com.Allnewhybrid.helper.browserConfiguration.IExploreBrowser;
	import com.Allnewhybrid.helper.browserConfiguration.config.ObjectReader;
	import com.Allnewhybrid.helper.browserConfiguration.config.PropertyReader;
	import com.Allnewhybrid.helper.excel.ExcelHelper;
	import com.Allnewhybrid.helper.javaScript.JavaScriptHelper;
	import com.Allnewhybrid.helper.logger.LoggerHelper;
	import com.Allnewhybrid.helper.resource.ResourceHelper;
	import com.Allnewhybrid.helper.wait.WaitHelper;
	import com.Allnewhybrid.testUtils.ExtentManager;
	/*import com.Allnewhybrid.helper.pageObject.LoginPage;
	import com.Allnewhybrid.helper.pageObject.MyAccountPage;
	import com.Allnewhybrid.helper.pageObject.NavigationMenu;
	import com.Allnewhybrid.helper.pageObject.RegistrationPage;
	import com.Allnewhybrid.helper.utils.ExtentManager;*/

	
	public class TestBase {
		
		public static ExtentReports extent;
		public static ExtentTest test;
		public WebDriver driver;
		private Logger log = LoggerHelper.getLogger(TestBase.class);
		public static File reportDirectery;
		
		@BeforeSuite
		public void beforeSuite() throws Exception{
			extent = ExtentManager.getInstance();
		}
		
		@BeforeTest
		public void beforeTest() throws Exception{
			ObjectReader.reader = new PropertyReader();
			reportDirectery = new File(ResourceHelper.getResourcePath("src/main/resources/screenShots"));
			setUpDriver(ObjectReader.reader.getBrowserType());
			test = extent.createTest(getClass().getSimpleName());
		}
		
		
		@BeforeMethod
		public void beforeMethod(Method method){
			test.log(Status.INFO, method.getName()+"**************test started***************");
			log.info("**************"+method.getName()+"Started***************");
		}
		
		@AfterMethod
		public void afterMethod(ITestResult result) throws IOException{
			if(result.getStatus() == ITestResult.FAILURE){
				test.log(Status.FAIL, result.getThrowable());
				String imagePath = captureScreen(result.getName(),driver);
				test.addScreenCaptureFromPath(imagePath);
			}
			else if(result.getStatus() == ITestResult.SUCCESS){
				test.log(Status.PASS, result.getName()+" is pass");
				//String imagePath = captureScreen(result.getName(),driver);
				//test.addScreenCaptureFromPath(imagePath);
			}
			else if(result.getStatus() == ITestResult.SKIP){
				test.log(Status.SKIP, result.getThrowable());
			}
			log.info("**************"+result.getName()+"Finished***************");
			extent.flush();
		}
		
		@AfterTest
		public void afterTest() throws Exception{
			if(driver!=null){
				driver.quit();
			}
		}
		
		public WebDriver getBrowserObject(BrowserType btype) throws Exception{
			
			try{
				switch(btype){
				case Chrome:
					// get object of ChromeBrowser class
					ChromeBrowser chrome = ChromeBrowser.class.newInstance();
					ChromeOptions option = chrome.getChromeOptions();
					return chrome.getChromeDriver(option);
				case Firefox:
					FirefoxBrowser firefox = FirefoxBrowser.class.newInstance();
					FirefoxOptions options = firefox.getFirefoxOptions();
					return firefox.getFirefoxDriver(options);
					
				case Iexplorer:
					IExploreBrowser ie = IExploreBrowser.class.newInstance();
					InternetExplorerOptions cap = ie.getIExplorerCapabilities();
					return ie.getIExplorerDriver(cap);
				default:
					throw new Exception("Driver not Found: "+btype.name());
				}
			}
			catch(Exception e){
				log.info(e.getMessage());
				throw e;
			}
		}
		
		public void setUpDriver(BrowserType btype) throws Exception{
			driver = getBrowserObject(btype);
			log.info("Initialize Web driver: "+driver.hashCode());
			WaitHelper wait = new WaitHelper(driver);
			wait.setImplicitWait(ObjectReader.reader.getImpliciteWait(), TimeUnit.SECONDS);
			wait.pageLoadTime(ObjectReader.reader.getPageLoadTime(), TimeUnit.SECONDS);
			driver.manage().window().maximize();
		}
		
		public String captureScreen(String fileName, WebDriver driver){
			if(driver == null){
				log.info("driver is null..");
				return null;
			}
			if(fileName==""){
				fileName = "blank";
			}
			Reporter.log("captureScreen method called");
			File destFile = null;
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
			File screFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			try{
				destFile = new File(reportDirectery+"/"+fileName+"_"+formater.format(calendar.getTime())+".png");
				Files.copy(screFile.toPath(), destFile.toPath());
				Reporter.log("<a href='"+destFile.getAbsolutePath()+"'><img src='"+destFile.getAbsolutePath()+"'height='100' width='100'/></a>");
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return destFile.toString();
		}

		
		public void getNavigationScreen(WebDriver driver) {
			log.info("capturing ui navigation screen...");
			new JavaScriptHelper(driver).zoomInBy60Percentage();
			 String screen = captureScreen("", driver);
			 new JavaScriptHelper(driver).zoomInBy100Percentage();
			 try {
				test.addScreenCaptureFromPath(screen);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public static void logExtentReport(String s1){
			test.log(Status.INFO, s1);
		}
		
		public void getApplicationUrl(String url){
			driver.get(url);
			logExtentReport("navigating to ..."+url);
		}
		
		public Object[][] getExcelData(String excelName, String sheetName){
			String excelLocation = ResourceHelper.getResourcePath("src/main/resources/configfile/")+excelName;
			log.info("excel location "+excelLocation);
			ExcelHelper excelHelper = new ExcelHelper();
			Object[][] data = excelHelper.getExcelData(excelLocation, sheetName);
			return data;
		}
		
		public WebElement getWebElement(String locator) throws Exception
		{
			System.out.println(locator);
			String val = Repository.getProperty(locator);
			System.out.println("property value" + val);
			return findLocatorElement(Repository.getProperty(locator));
					
		}
		
		public static WebElement findLocatorElement(String locator) throws Exception
		{
			System.out.println("prop value in find locator element " + locator);
			String[] splitstrng = locator.split("_");
			String locatorType = splitstrng[0];
			String locatorValue = splitstrng[1];
			
			if (locatorType.toLowerCase().equals("id"))
				return driver.findElement(By.id(locatorValue));
			else if(locatorType.toLowerCase().equals("xpath"))
				return driver.findElement(By.xpath(locatorValue));
			else if(locatorType.toLowerCase().equals("name"))
				return driver.findElement(By.name(locatorValue));
			else if(locatorType.toLowerCase().equals("classname")
					|| (locatorType.toLowerCase().equals("class")))
				return driver.findElement(By.className(locatorValue));
			else if(locatorType.toLowerCase().equals("tagname")
					|| (locatorType.toLowerCase().equals("tag")))
				return driver.findElement(By.className(locatorValue));
	 		else if(locatorType.toLowerCase().equals("link")
					|| (locatorType.toLowerCase().equals("linktext")))
				return driver.findElement(By.linkText(locatorValue));
			else if(locatorType.toLowerCase().equals("css")
					|| (locatorType.toLowerCase().equals("cssSelector")))
				return driver.findElement(By.cssSelector(locatorValue));
			else if(locatorType.toLowerCase().equals("partiallinktext"))
				return driver.findElement(By.partialLinkText(locatorValue));		
			
			else throw new Exception("Unknown locator type '" + locatorType + "'");
		}
	}

	

