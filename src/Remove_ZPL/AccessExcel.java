package Remove_ZPL;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class AccessExcel 
{
	static WebDriver driver;
	static Logger logger;
	static readingZPL_File read = new readingZPL_File();
	List<String> testcaseid = read.readTCDetailsFile(driver, logger);
	String tcID = testcaseid.get(0)+"_Results";
	
	private static final String screenshotpath = new File(System.getProperty("user.dir")).getParent() + "\\Results\\";
	
	WebElement element = null;
	String req = null;
	public static WebDriverWait wait;

	int checkcount =1,total = 0;
	int removevalue,removelogicvalue, countfordp, logicdp =0;
	String prdctName,status;
	static int  exitqueue,firstTime= 0;
	ArrayList<String> queueCheck = new ArrayList<String>();

	public WebElement getElement(WebDriver driver,Map<String, String> objects,Logger logger) throws InterruptedException
	{
		String selectorType = objects.get("Selectortype");
		String selector = objects.get("Selector");
		String conditions = objects.get("Conditions");
		String keyboard = objects.get("Keyboard");
		String functions = objects.get("Functions");
		
		String date = date();
		int time=0;
		if(objects.get("Time")!="")
		{
			time = Integer.parseInt(objects.get("Time"));
		}
		String wait = objects.get("Wait");
		try
		{
			if(selectorType.equals("xpath")&& conditions.isEmpty() && keyboard.isEmpty() && functions.isEmpty())
			{
				element = driver.findElement(By.xpath(selector));
			}
			else if(selectorType.equals("xpath")&& wait.contains("wait") && functions.isEmpty())
			{
				eWait(driver, conditions, selector, time);
			}
			else if(selectorType.equals("xpath")&& keyboard.contains("k_enter"))
			{
				Thread.sleep(3000);
				driver.findElement(By.xpath(selector)).sendKeys(Keys.ENTER);
			}
			else if(selectorType.equals("xpath")&& keyboard.contains("k_down"))
			{
				Thread.sleep(3000);
				driver.findElement(By.xpath(selector)).sendKeys(Keys.DOWN);
			}
			else if(selectorType.equals("xpath")&& keyboard.contains("k_backspace"))
			{
				Thread.sleep(3000);
				driver.findElement(By.xpath(selector)).sendKeys(Keys.BACK_SPACE);
			}
			else if(selectorType.equals("xpath")&& keyboard.contains("k_cntrl_A"))
			{
				Thread.sleep(3000);
				driver.findElement(By.xpath(selector)).sendKeys(Keys.chord(Keys.CONTROL,"a"));
			}
			else if(selectorType.equals("id"))
			{
				element = driver.findElement(By.id(selector));
			}
			else if(selectorType.equals("xpath") && functions.equals("WaitForQueue"))
			{
				System.out.println("Sleeping for 3 minutes.");
				Thread.sleep(20000); 
				System.out.println("Sleep Time Over.");
			}
			else if(selectorType.equals("Screenshot"))
			{
				captureScreenshot(driver,objects,date,logger);
			}
			else if (selectorType.equals("") || !functions.isEmpty())
			{
			}
			else
			{
				System.out.println("invalid selector");
				logger.info("invalid selector Provided");
			}
		}
		catch(Exception e)
		{
			read.createErrorFile(driver, logger);
			logger.error("An Exception has occured in getElement method --> "+e.toString());
			driver.quit();
			System.exit(0);
		}
		return element;
	}


	public void doAction(WebDriver driver,WebElement element,Map<String, String> objects,String productname2,String getGroupname, Logger logger) throws InterruptedException, IOException
	{
		String action = objects.get("Action");
		String input = objects.get("Input");
		String selector = objects.get("Selector");
		String key_function = objects.get("Functions");

		List<String> tcdetails = new ArrayList<String>();
		tcdetails = read.readTCDetailsFile(driver, logger);
		String zplno = tcdetails.get(1).toUpperCase();
		String grpname = getGroupname;
		try
		{
			if(action.equals("click"))
			{
				try 
				{
					element.click();
					Thread.sleep(2000);
					infoLog(objects, logger);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
					logger.info("Exception occur in do_action method, in  Click action");
					driver.quit();
					System.exit(0);
				}
			}
			else if(action.equals("sendkeys"))
			{
				element.sendKeys(input);
				infoLog(objects, logger);
			}
			else if(action.equals("sendkeysfordrpdown"))
			{
				Thread.sleep(3000);
				element.sendKeys(input);
			}
			else if(action.contentEquals("ZPLNO"))
			{
				element.sendKeys(zplno);
				infoLog(objects, logger);
			}
			else if(action.contentEquals("GroupName"))
			{
				element.sendKeys(grpname);
			}
			else if(action.contentEquals("ProductName"))
			{
				element.sendKeys(productname2);
			}
			else if(action.equals("compare"))
			{
				WebDriverWait wait1 = new WebDriverWait(driver, 30);
				wait1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//*[@id='aw-console']/div[3]/div[2]")));
				boolean TxtBoxContent = IsRequiredTextPresent(driver,selector);

				if(TxtBoxContent==false)
				{
					logger.info("ZPL number Already Exists !!!!!");
					logger.info("Terminating the script!!!!");
					driver.quit();
					System.exit(0);
				}
			}
			else if(action.equals("Alert"))
			{
				boolean popupdlg = IsPopupDialogPresent(driver,selector);
				if(popupdlg==true)
				{
					element.click();
				}
				infoLog(objects, logger);
				logger.info(" Popup dialog Appearence in the Airwatch Console ==> "+popupdlg);
			}
			else if(action.equals("hover"))
			{
				Thread.sleep(2000);
				//action1.moveToElement(element).build().perform();
			}
			else if(action.equals("getTextBoxValue"))
			{
				String getTextBoxvalue = element.getText();
				getTextBoxvalue.trim();
				logger.info("Added ZPL is find in the Group =>"+getTextBoxvalue);
				if(getTextBoxvalue.contains(zplno))
				{
					logger.info("Checking whether Zpl is added or not in Group,ZPL is matching in the console");
				}
				else
				{
					read.createErrorFile(driver, logger);
					logger.info("Checking whether Zpl is added or not in Group,ZPL is not matching in the console");
					logger.info("Terminating the Program..");
					driver.quit();
					System.exit(0);
				}
			}
			else if(key_function.equals("removedropdown"))
			{
				infoLog(objects, logger);
				dynamicDropdown(driver, selector, action, input, zplno, logger);
			}
			else if(action.isEmpty())
			{
			}
			else
			{
				System.out.println("invalid action");
			}
		}
		catch(Exception e)
		{
			read.createErrorFile(driver, logger);
			logger.error("An Exception has occured in doAction method --> "+e.toString());
			driver.quit();
			System.exit(0);
		}
	}

	public boolean IsRequiredTextPresent(WebDriver driver,String selector)
	{
		try
		{
			driver.findElement(By.xpath(selector));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public boolean IsPopupDialogPresent(WebDriver driver, String selector)
	{
		try
		{
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.findElement(By.xpath("selector"));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}


	public void dynamicDropdown(WebDriver driver,String selector,String action,String input,String zplno,Logger logger)
	{
		try
		{
			List<WebElement> xpath = driver.findElements(By.xpath(selector));
			countfordp =xpath.size();
			int n = countfordp + (countfordp - 1);
			
			for(int i=0;i<n;i +=2)
			{
				element= driver.findElement(By.xpath(".//*[@id='Rule_Steps["+i+"]_Value']"));
				
				Select sel = new Select(driver.findElement(By.xpath(".//*[@id='Rule_Steps["+i+"]_Value']")));
				WebElement option = sel.getFirstSelectedOption();

				String rmvZPL = option.getText().trim();
				
				if(rmvZPL.contentEquals(zplno))
				{
					removevalue = i + 1;
					driver.findElement(By.xpath(".//*[@id='RuleSteps']/tbody/tr["+removevalue+"]/td[7]/a")).click();
					logger.info("AssetNumber and ZPL Value textbox is Removed");
					removelogicvalue = i;
					driver.findElement(By.xpath(".//*[@id='RuleSteps']/tbody/tr["+removelogicvalue+"]/td[7]/a")).click();
					logger.info("Logical operator is Removed.");
					break;
				}
				else if(checkcount == countfordp)
				{
					logger.error("AssetNumber and ZPL Value is not matching with any of the Dropdown. Plz Check it");
					logger.error("Removing ZPL is Failed.");
					read.createErrorFile(driver, logger);
					driver.quit();
					System.exit(0);
				}
				checkcount++;
			}
		}
		catch (Exception e) 
		{
			read.createErrorFile(driver, logger);
			logger.info("Problem ocuurs in dynamicDropdown function");
			logger.info("program is terminating...");
			driver.quit();
			System.exit(0);
		}
	}


	public void eWait(WebDriver driver,String conditions, String selector, int time) 
	{
		wait = new WebDriverWait(driver, time);

		if(conditions.equals("vElement"))
		{
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selector)));
		}
		else if(conditions.equals("ivElement"))
		{
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(selector)));
		}
		else
		{
			System.out.println("Invalid wait condition");
		}
	}


	public void captureScreenshot(WebDriver driver, Map<String, String> objects, String time,Logger logger)
	{
		try 
		{   
			List<String> testcaseid = read.readTCDetailsFile(driver, logger);
			String tcID = testcaseid.get(0)+"_Results";
			
			File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE); 
			FileUtils.copyFile(src, new File(screenshotpath+"\\"+tcID+"/"+"Screen_"+time+".png")); 
			infoLog(objects, logger);
		} 
		catch (IOException e)
		{
			logger.error("There is some problem in Screen Capturing Method--> \n"+e.toString());
			driver.quit();
			System.exit(0);
		}
	}


	public void infoLog(Map<String, String> objects,Logger logger)
	{
		String infoLog = objects.get("LoggerInfo");
		if(!infoLog.isEmpty())
		{
			logger.info(infoLog);
		}
	}

	public static String date()
	{
		DateFormat dateFormat = new SimpleDateFormat("HH_mm_ss");
		Date now = new Date();
		String nms_date = dateFormat.format(now);
		return nms_date;
	}

	
	@Override
	public String toString() 
	{
		return "accessExcel []";
	}

}

