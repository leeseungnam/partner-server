package kr.wrightbrothers.framework.util.excel.util.write;

import kr.wrightbrothers.framework.util.excel.util.write.share.WriteShare;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;

public class AutoSizingColumns {

	public static void resize() {
		if (WriteShare.sheet instanceof SXSSFSheet) {
			((SXSSFSheet) WriteShare.sheet).trackAllColumnsForAutoSizing();
		}

		Row row = WriteShare.sheet.getRow(0);
		if(row == null) return;
		for ( int j = 0; j < row.getLastCellNum(); j++ ) {
			WriteShare.sheet.autoSizeColumn(j);
		}
	}
	
}
