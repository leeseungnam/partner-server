package kr.wrightbrothers.framework.util.excel.util.read;

import kr.wrightbrothers.framework.util.excel.util.read.share.ReadShare;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellReference;



public class CellReferenceUtil {

	public static String getName(int cellIndex) {
		return CellReference.convertNumToColString( ReadShare.cell != null ? ReadShare.cell.getColumnIndex() : cellIndex);
	}
	
	public static String getValue() {
		if(ReadShare.cell == null) {
			return "";
		}
		if( ReadShare.cell.getCellType() == CellType.FORMULA) {
			return ReadShare.cell.getCellFormula();
		}
		if( ReadShare.cell.getCellType() == CellType.NUMERIC ) {
			return String.valueOf((long) ReadShare.cell.getNumericCellValue());
		}
		if( ReadShare.cell.getCellType() == CellType.STRING ) {
			return ReadShare.cell.getStringCellValue();
		}
		if( ReadShare.cell.getCellType() == CellType.BOOLEAN ) {
			return ReadShare.cell.getBooleanCellValue() + "";
		}
		if( ReadShare.cell.getCellType() == CellType.ERROR ) {
			return ReadShare.cell.getErrorCellValue() + "";
		}
		if( ReadShare.cell.getCellType() == CellType.BLANK ) {
			return "";
		}
		
		return ReadShare.cell.getStringCellValue();
	}
	
}
