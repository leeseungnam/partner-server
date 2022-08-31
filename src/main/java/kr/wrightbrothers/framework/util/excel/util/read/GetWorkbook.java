package kr.wrightbrothers.framework.util.excel.util.read;

import kr.wrightbrothers.framework.util.excel.util.read.share.ReadShare;
import kr.wrightbrothers.framework.util.excel.util.write.FileType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class GetWorkbook {

	public static void get(String filePath) {
		ReadShare.wb = getWorkbook(filePath);
	}
	
	public static void get(InputStream inputStream, String fileName) {
		ReadShare.wb = getWorkbook(inputStream, fileName);
	}
	
	public static Workbook getWorkbook(InputStream fis, String fileName) {
		if (FileType.isXls(fileName)) {
			try {
				return new HSSFWorkbook(fis);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			} finally {
				if ( fis != null ) {
					try {
						fis.close();
					}
					catch(IOException e1) {}
				}
			}
		}  
		if (FileType.isXlsx(fileName)) {
			try {
				return new XSSFWorkbook(fis);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			} finally {
				if ( fis != null ) {
					try {
						fis.close();
					}
					catch(IOException e1) {}
				}
			}
		}
		
		if ( fis != null ) {
			try {
				fis.close();
			}
			catch(IOException e1) {}
		}
		
		throw new RuntimeException(fileName + " isn't excel file format");
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static Workbook getWorkbook(String filePath) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return getWorkbook(fis, filePath);
	}
}
