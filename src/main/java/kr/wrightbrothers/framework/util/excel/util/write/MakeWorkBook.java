package kr.wrightbrothers.framework.util.excel.util.write;

import kr.wrightbrothers.framework.util.excel.util.write.share.WriteShare;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MakeWorkBook {

	/**
	 * 
	 */
	public static void makeWorkBookAndSheet() {
		WriteShare.wb = MakeWorkBook.getWorkbook(WriteShare.writeOption.getFileName());
		WriteShare.sheet = WriteShare.wb.getSheet(WriteShare.writeOption.getSheetName());
		WriteShare.rowIndex = WriteShare.writeOption.getStartRow();
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static Workbook getWorkbook(String fileName) {
		try {
			
			InputStream inputStream = new ClassPathResource(WriteShare.writeOption.getTemplateFile()).getInputStream();
			
			if (FileType.isXls(fileName)) {
				return new HSSFWorkbook(inputStream);
			}
			if (FileType.isXlsx(fileName)) {
				return new XSSFWorkbook(inputStream);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not find Excel file");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not find Excel file");
		}
		return null;
		
	}

}
