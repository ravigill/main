package com.zoxis.qa.automation.seleniumframework.core.utilities;

/**
 * Utility class to read and access data from Excel files.
 * TO-DO: Add write functionality
 * @author Ravidev Gill (ravi.gill@spindriftgroup.com)
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class SeleniumExcelDataset implements SeleniumDataset {
	private String path;
	private int rowCnt;
	private int columnCnt;
	private String sheetName;
	private Sheet sheet;
	private int currentRowNr = 1;
	private ArrayList<String> columnNames;

	/**
	 * Constructor
	 * 
	 * @param path
	 * @param sheetName
	 */
	public SeleniumExcelDataset(String path, String sheetName) {
		this.sheetName = sheetName;
		this.path = path;
		if (isExists()) {
			try {
				readData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get path of the Excel file
	 * 
	 * @return {@link String}
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Get the count for total number of rows
	 * 
	 * @return Integer
	 */
	public int getRowCnt() {
		return rowCnt;
	}

	/**
	 * Get the count for total number of Columns
	 * 
	 * @return Integer
	 */
	public int getColumnCnt() {
		return columnCnt;
	}

	/**
	 * Get the WorkSheet name
	 * 
	 * @return String
	 */
	public String getSheetName() {
		return sheetName;
	}

	/**
	 * Get the WorkSheet reference
	 * 
	 * @return {@link Sheet}
	 */
	public Sheet getSheet() {
		return sheet;
	}

	/**
	 * Get current row number
	 * 
	 * @return Integer
	 */
	public int getCurrentRowNr() {
		return currentRowNr;
	}

	/**
	 * Set the current row number
	 * 
	 * @param currentRowNr
	 * @return Boolean
	 */
	public boolean setCurrentRowNr(int currentRowNr) {
		boolean ret = false;
		if (currentRowNr < rowCnt && currentRowNr > 0) {
			this.currentRowNr = currentRowNr;
			ret = true;
		}
		return ret;
	}

	/**
	 * Check if the given File exists on the filesystem
	 * 
	 * @return Boolean
	 */
	private boolean isExists() {
		boolean ret = false;
		try{
			InputStream is = SeleniumExcelDataset.class.getResourceAsStream(path);
			ret = true;
		}catch(Exception e){
			ret = false;
		}
		return ret;
	}

	/**
	 * Check if the WorkSheet has more Rows
	 * 
	 * @return Boolean
	 */
	public boolean hasMoreRows() {
		if (sheet == null) {
			try {
				readData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		boolean ret = false;
		if (currentRowNr < rowCnt) {
			ret = true;
		}
		return ret;
	}

	/**
	 * Get Value from the Given Column number
	 * 
	 * @param ColumnNumber
	 * @return String
	 */
	public String getColumn(int column) {
		String ret = null;
		if (hasMoreRows()) {
			ret = this.getDataFromLocation(currentRowNr, column);
		}
		return ret;
	}

	/**
	 * Gat Value from the Given Columen name
	 * 
	 * @param columnName
	 * @return String
	 */
	public String getColumn(String columnName) {
		String ret = null;
		ret = this.getDataFromLocation(currentRowNr, getColumnNr(columnName));
		return ret;
	}

	/**
	 * Get the column index for the given name (the first row of each sheet
	 * should contain column names)
	 * 
	 * @param columnName
	 * @return Integer
	 */
	private int getColumnNr(String columnName) {
		int ret = -1;
		ArrayList<String> cNames = getColumnNames();
		if (cNames.contains(columnName)) {
			ret = cNames.indexOf(columnName) + 1;
		}
		return ret;
	}

	/**
	 * Move to next Row
	 * 
	 * @return
	 */
	public int nextRow() {
		if (currentRowNr <= rowCnt) {
			currentRowNr++;
		}else{
			currentRowNr = 1;
		}
		return currentRowNr;
	}

	/**
	 * Reset the Current row number to 0
	 * 
	 */
	public void reset() {
		currentRowNr = 0;
	}

	/**
	 * Get Value from the specified row and coulmn number.
	 * 
	 * @param row
	 * @param column
	 * @return String
	 */
	public String getDataFromLocation(int row, int column) {
		String ret = "";
		if (sheet == null) {
			try {
				readData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((row <= rowCnt && row >= 0) && (column <= columnCnt && column >= 0)) {
			ret = sheet.getCell(column - 1, row).getContents();
		}
		return ret;
	}

	/**
	 * Get Value from the specified Row number and ColumnName
	 * 
	 * @param rowNr
	 * @param columnName
	 * @return
	 */
	public String getDataFromLocation(int rowNr, String columnName) {
		String ret = null;
		ret = getDataFromLocation(rowNr, getColumnNr(columnName));
		return ret;
	}

	/**
	 * Get a List of ColumnNames
	 * 
	 * @return
	 */
	public ArrayList<String> getColumnNames() {

		if (columnNames == null) {
			int rows = getRowCnt();
			if (rows > 0) {
				int cnt = 1;
				int columnCnt = getColumnCnt();
				columnNames = new ArrayList<String>(columnCnt);
				while (cnt <= columnCnt) {
					columnNames.add(getDataFromLocation(0, cnt));
					cnt++;
				}
			}
		}

		return columnNames;
	}

	/**
	 * Read data from the WorkSheet
	 * 
	 * @throws BiffException
	 * @throws IOException
	 */
	private void readData() throws BiffException, IOException {
		Workbook wb;
		try{
			wb = Workbook.getWorkbook(SeleniumExcelDataset.class.getResourceAsStream(path));
		}catch(Exception e){
			wb = Workbook.getWorkbook(new File(path));
		}
		this.sheet = wb.getSheet(this.sheetName);
		this.columnCnt = this.sheet.getColumns();
		this.rowCnt = this.sheet.getRows();
	}

	/**
	 * Get the Data (2D Array) from the WorkSheet
	 */
	public String[][] getData() {
		String[][] ret = null;
		try {
			ret = SeleniumExcelDataset.getData(this.path, this.sheetName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * Get the Data (2D Array) from the WorkSheet with the given Row limitation
	 * 
	 * @param maximumRows
	 * @return String[][]
	 */
	public String[][] getData(int maximumRows) {
		String[][] ret = null;
		try {
			ret = SeleniumExcelDataset.getData(this.path, this.sheetName, maximumRows);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Get data (2D Array) from the given Path and SheetName
	 * 
	 * @param path
	 * @param sheetName
	 * @return String[][]
	 * @throws BiffException
	 * @throws IOException
	 */
	public static String[][] getData(String path, String sheetName)
	throws BiffException, IOException {
		Workbook wb;
		try{
			wb = Workbook.getWorkbook(SeleniumExcelDataset.class.getResourceAsStream(path));
		}catch(Exception e){
			wb = Workbook.getWorkbook(new File(path));
		}
		Sheet sheet = wb.getSheet(sheetName);
		int columns = sheet.getColumns();
		int rows = sheet.getRows();
		String[][] data = new String[rows - 1][columns];
		for (int y = 1; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				data[y - 1][x] = sheet.getCell(x, y).getContents();
			}
		}
		return data;
	}
	
	/**
	 * Get data (2D Array) from the given Path and SheetName with the given Row limitation
	 * 
	 * @param path
	 * @param sheetName
	 * @param maximumRows
	 * @return
	 * @throws BiffException
	 * @throws IOException
	 */
	public static String[][] getData(String path, String sheetName, int maximumRows)
	throws BiffException, IOException {
		Workbook wb;
		try{
			wb = Workbook.getWorkbook(SeleniumExcelDataset.class.getResourceAsStream(path));
		}catch(Exception e){
			wb = Workbook.getWorkbook(new File(path));
		}
		Sheet sheet = wb.getSheet(sheetName);
		int columns = sheet.getColumns();
		int rows = sheet.getRows();
		if(rows > maximumRows && maximumRows > 0) rows = maximumRows+1;
		String[][] data = new String[rows - 1][columns];
		for (int y = 1; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				data[y - 1][x] = sheet.getCell(x, y).getContents();
			}
		}
		return data;
	}
}
