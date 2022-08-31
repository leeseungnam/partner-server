package kr.wrightbrothers.framework.util.excel.write;

import kr.wrightbrothers.framework.util.excel.option.WriteOption;
import kr.wrightbrothers.framework.util.excel.util.write.AutoSizingColumns;
import kr.wrightbrothers.framework.util.excel.util.write.MakeContents;
import kr.wrightbrothers.framework.util.excel.util.write.MakeWorkBook;
import kr.wrightbrothers.framework.util.excel.util.write.WriteFileSystem;
import kr.wrightbrothers.framework.util.excel.util.write.share.WriteShare;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author ISJUNG
 *
 */
public class ExcelWrite {

	/**
	 * 엑셀 파일을 작성한다.
	 * 
	 * @param WriteOption
	 * @return Excel 파일의 File 객체
	 * @throws IOException 
	 */
	public static File write(WriteOption<?> writeOption) throws IOException {
		WriteShare.writeOption = writeOption;
		MakeWorkBook.makeWorkBookAndSheet();
		MakeContents.make();
		AutoSizingColumns.resize();
		String downloadPath = WriteFileSystem.write();
		WriteShare.resetRowIndex();
		return new File(downloadPath);
	}

}
