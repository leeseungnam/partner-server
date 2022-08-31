package kr.wrightbrothers.framework.util.excel.read;

import kr.wrightbrothers.framework.util.excel.option.ReadOption;
import kr.wrightbrothers.framework.util.excel.util.read.AddData;
import kr.wrightbrothers.framework.util.excel.util.read.GetCell;
import kr.wrightbrothers.framework.util.excel.util.read.GetRow;
import kr.wrightbrothers.framework.util.excel.util.read.PreparedExcelRead;
import kr.wrightbrothers.framework.util.excel.util.read.share.ReadShare;
import org.apache.poi.ss.util.CellReference;

import java.util.List;

public class ExcelRead<T> extends PreparedExcelRead<T> {

	
	/**
	 * 엑셀 파일을 읽어옴
	 * @param readOption
	 * @return
	 */
	public List<T> readToList(String excelFilePath, Class<?> clazz) {
		ReadOption readOption = new ReadOption();
		readOption.setFilePath(excelFilePath);
		
		return readToList(readOption, clazz);
	}
	
	/**
	 * 엑셀 파일을 읽어옴
	 * @param readOption
	 * @return
	 */
	public List<T> readToList(ReadOption readOption, Class<?> clazz) {
		ReadShare.clazz = clazz;
		ReadShare.readOption = readOption;
		
		setup();
		createResultInstance();
		
		makeData(new AddData() {
			@Override
			public boolean pushData(int rowIndex) {
				return addData(rowIndex + 1, GetCell.getValue());
			}
		}, true);
		
		return result;
		
	}
	
	public String getValue(String filePath, String cellName) {
		return getValue(filePath, null, cellName);
	}
	
	public String getValue(String filePath, String sheetName, String cellName) {
		setup(filePath, sheetName);
		
		CellReference cr = new CellReference(cellName);
		GetRow.setRow(cr.getRow());
		GetCell.setCell(cr.getCol());
		
		return GetCell.getValue();
	}
	
}
