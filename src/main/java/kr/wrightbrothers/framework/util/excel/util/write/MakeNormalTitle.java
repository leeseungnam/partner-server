package kr.wrightbrothers.framework.util.excel.util.write;

import kr.wrightbrothers.framework.util.excel.annotations.Title;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class MakeNormalTitle {

	public static int make(Title title, Row row, Cell cell, int cellIndex) {
		if ( title.parentTitle().equals("") ) {
			cell = row.createCell(cellIndex);
			
			cell.setCellValue(title.value());
			CellMerger.merge(row.getRowNum(), title.rowMerge(), cellIndex, title.cellMerge());
			cellIndex += title.cellMerge();
		}
		
		return cellIndex;
	}
	
}
