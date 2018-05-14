package Remove_ZPL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class readingZPL_File 
{
	private static final String file_paths = new File(System.getProperty("user.dir")).getParent() + "\\TC_Details.txt";
	private static final String error_file = new File(System.getProperty("user.dir")).getParent() +"\\Exe\\RemoveZPLError.txt";
	private static final String success_file = new File(System.getProperty("user.dir")).getParent() +"\\Exe\\RemoveZPLSuccess.txt";
	
	public static String fInput = null;
	public static List<String> inputs;
	public static WebDriverWait wait;
	
	WebElement element;
	
	public List<String> readTCDetailsFile(WebDriver driver,Logger logger) 
	{
		inputs = new ArrayList<String>();
		String tcDetails = null;
		try 
		{
			try 
			{
				tcDetails = FileUtils.readFileToString(new File(file_paths), "UTF-8");
			} 
			catch (IOException e1) 
			{
				logger.error("An Exception has occured while reading the contents from TC_details.txt file --> \n"+e1.getMessage());
				System.exit(0);
			}
			String input_data[] = tcDetails.split("\n");
			
			for(int i=0;i<input_data.length;i++)
			{
				inputs.add(input_data[i].trim());
			}
		}
		catch (Exception e) 
		{
			createErrorFile(driver, logger);
			logger.error("An Exception has occured in readTCDetailsFile Method --> "+e.toString());
			driver.quit();
			System.exit(0);
		}
		return inputs;
	}
	
	
	public void createErrorFile(WebDriver driver,Logger logger) 
	{
		try
		{
			File file = new File(error_file);
			FileWriter writer = new FileWriter(file);
			file.createNewFile();
			writer.flush();
			writer.close();
		}
		catch(Exception e)
		{
			createErrorFile(driver, logger);
			logger.error("An Exception has occured while creating RemoveZPLError.txt text file --> \n"+e.toString());
			driver.quit();
			System.exit(0);
		}
	}
	
	
	public void createSuccessFile(WebDriver driver, Logger logger) 
	{
		try
		{
			File file = new File(success_file);
			FileWriter writer = new FileWriter(file);
			file.createNewFile();

			writer.write("Removed ZPL Succesfully Finished");
			writer.flush();
			writer.close();
		}
		catch(Exception e)
		{
			createErrorFile(driver, logger);
			logger.error("An Exception has occured while creating RemoveZPLSuccess.txt text file --> \n"+e.toString());
			driver.quit();
			System.exit(0);
		}
	}
}
