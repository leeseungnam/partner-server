package kr.wrightbrothers.framework.util.excel.util.write;

import kr.wrightbrothers.framework.util.excel.annotations.ExcelSheet;
import kr.wrightbrothers.framework.util.excel.annotations.Title;
import kr.wrightbrothers.framework.util.excel.util.write.share.WriteShare;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MakeTitle {

	public static void make() {
		
		if(!isUseTitle())  {
			setTitle(WriteShare.writeOption.getTitles());
		}
		else {
			setTitle();
		}
		
	}
	
	private static boolean isUseTitle() {
		Object object = GetHeaderContent.getFirstContent();
		return object.getClass().getDeclaredAnnotation(ExcelSheet.class).useTitle();
	}

	private static void setTitle(List<String> values) {

		Row row = null;
		Cell cell = null;

		int cellIndex = 0;

		if (values != null && values.size() > 0) {
			row = MakeRow.create();
			for (String value : values) {
				cell = row.createCell(cellIndex++);
				cell.setCellValue(value);
			}
		}

	}
	
	private static void setTitle() {
		
		Row row = MakeRow.create();
		Cell cell = null;
		
		int cellIndex = 0;
		
		List<String> titleList = new ArrayList<>();
		
		Field[] fields = GetHeaderContent.getFirstContent().getClass().getDeclaredFields();
		
		for (Field field : fields) {
			Title title = field.getAnnotation(Title.class);
			if ( title == null ) {
				continue;
			}
			
			cellIndex = MakeParentTitle.make(title, row, cell, cellIndex, fields);
			cellIndex = MakeNormalTitle.make(title, row, cell, cellIndex);
			
			titleList.add(title.value());
		}
		
		WriteShare.writeOption.setTitles(titleList);
		
	}
	
}
