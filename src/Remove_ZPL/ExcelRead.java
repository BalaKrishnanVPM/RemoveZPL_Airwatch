package Remove_ZPL;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelRead 
{
	public ArrayList<Map<String,String>> linklist = new ArrayList<Map<String,String>>();
	public ArrayList<Map<String,String>> removeZPLlist = new ArrayList<Map<String,String>>();
	public ArrayList<Map<String,String>> removeMoreProductZPLlist = new ArrayList<Map<String,String>>();

	String TCFilePath = new File(System.getProperty("user.dir")).getParent() + "\\TC_Data.xls";
	String removeZPLExcel = new File(System.getProperty("user.dir")).getParent() + "\\ExcelData\\RemoveZPL.xls";
	
	public static List<String> id;
	List<String> testcaseid;
	String tcID = null;
	int drpcheck = 1;
	int drpchecklimit = 0;
	
	ArrayList<String> sheetname = new ArrayList<String>();
	LinkedHashMap<String, String> sheetmap = new LinkedHashMap<>();
	readingZPL_File read = new readingZPL_File();
	
	public LinkedHashMap<String,String> productGroupNameMap(WebDriver driver,Logger logger) throws IOException, BiffException
	{
		try 
		{
			sheetmap = new LinkedHashMap<>();
			readingZPL_File read = new readingZPL_File();
			testcaseid = read.readTCDetailsFile(driver, logger);
			tcID = testcaseid.get(0);
			
			FileInputStream fs = new FileInputStream(TCFilePath);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(0);
			
			for(int row=1;row<sh.getRows();row++)
			{
				if(sh.getCell(0, row).getContents().contentEquals(tcID))
				{
					for(int col=1;col<3;col++)
					{
						sheetmap.put(sh.getCell(col, 0).getContents(), sh.getCell(col, row).getContents());
					}
				}
			}
		} 
		catch (IOException e) 
		{
			read.createErrorFile(driver, logger);
			logger.info("An exception has occured in productNameMap method --> "+e.toString());
			driver.quit();
			System.exit(0);
		}
		return sheetmap;
	}
	
	
	public ArrayList<Map<String,String>> linkDataSheet() throws BiffException
	{
		Map<String,String> linkmap ;
		try 
		{
			FileInputStream fs = new FileInputStream(removeZPLExcel);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(0);
			
			for(int row=1;row<sh.getRows();row++)
			{
				linkmap = new HashMap<String, String>();
				for(int column=0;column<sh.getColumns();column++)
				{
					linkmap.put(sh.getCell(column, 0).getContents(), sh.getCell(column, row).getContents());
				}
				linklist.add(linkmap);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		return linklist;
	}
	
	
	
	public ArrayList<Map<String,String>> removeZPLDataSheet() throws BiffException
	{
		Map<String,String> datamap;
		try 
		{
			FileInputStream fs = new FileInputStream(removeZPLExcel);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(1);
			
			for(int row=1;row<sh.getRows();row++)
			{
				datamap = new HashMap<String, String>();
				for(int column=0;column<sh.getColumns();column++)
				{
					datamap.put(sh.getCell(column, 0).getContents(), sh.getCell(column, row).getContents());
				}
				removeZPLlist.add(datamap);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		return removeZPLlist;
	}
	
	
	public ArrayList<Map<String,String>> removeZPL_MoreProductDataSheet() throws BiffException
	{
		Map<String,String> datamap;
		try 
		{
			FileInputStream fs = new FileInputStream(removeZPLExcel);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(2);
			
			for(int row=1;row<sh.getRows();row++)
			{
				datamap = new HashMap<String, String>();
				for(int column=0;column<sh.getColumns();column++)
				{
					datamap.put(sh.getCell(column, 0).getContents(), sh.getCell(column, row).getContents());
				}
				removeMoreProductZPLlist.add(datamap);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return removeMoreProductZPLlist;
	}
	

	@Override
	public String toString() 
	{
		return "Excelread[]";
	}
	
}