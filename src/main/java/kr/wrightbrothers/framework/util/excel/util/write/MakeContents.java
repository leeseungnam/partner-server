package kr.wrightbrothers.framework.util.excel.util.write;

import kr.wrightbrothers.framework.util.excel.annotations.Field;
import kr.wrightbrothers.framework.util.excel.annotations.Title;
import kr.wrightbrothers.framework.util.excel.util.write.share.WriteShare;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class MakeContents {

	public static void make() {
		Row row = null;
		List<?> values = WriteShare.writeOption.getContents();
		if (values != null && values.size() > 0) {
			MakeCell makeCell = null;
			while ( true ) {
				Object obj = getValue(values);
				if ( obj == null )
					break;
				
				row = MakeRow.create();
				if(WriteShare.rowCellStyle != null)
					row.setRowStyle(WriteShare.rowCellStyle);
				
				makeCellAndFillValue(obj, makeCell, row);
				values.remove(0);
				flush();
			}
		}
	}
	
	private static Object getValue(List<?> values) {
		try {
			return values.get(0);
		}
		catch ( IndexOutOfBoundsException e ) {
			return null;
		}
	}
	
	private static void makeCellAndFillValue(Object obj, MakeCell makeCell, Row row) {
		java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
		for (java.lang.reflect.Field field : fields) {
			int cellIndex = -1;
			field.setAccessible(true);
			
			if (field.isAnnotationPresent(Title.class)) {
				Title anno = field.getAnnotation(Title.class);
				String title = anno.value();
				cellIndex = getColumnIndex(title);
			}
			else if(field.isAnnotationPresent(Field.class)) {
				Field anno = field.getAnnotation(Field.class);
				cellIndex = CellReference.convertColStringToIndex(anno.value());
			}
			
			if(cellIndex >= 0) {
				if ( makeCell == null ) {
					makeCell = new MakeCell(obj, null, row, cellIndex);
				}
				else {
					makeCell.changeCell(obj, null, row, cellIndex);
				}
				makeCell.fillValue(field, row);
			}
		}
	}
	
	private static void flush() {
		if ( WriteShare.wb instanceof SXSSFWorkbook ) {
			try {
				((SXSSFSheet)WriteShare.sheet).flushRows(10000);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}
	
	private static int getColumnIndex(String title) {
		return WriteShare.writeOption.getTitles().indexOf(title);
	}
	
}
