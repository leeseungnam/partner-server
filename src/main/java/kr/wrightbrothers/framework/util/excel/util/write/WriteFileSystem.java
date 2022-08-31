package kr.wrightbrothers.framework.util.excel.util.write;

import kr.wrightbrothers.framework.util.excel.util.write.share.WriteShare;
import org.apache.poi.openxml4j.util.ZipSecureFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteFileSystem {

	public static String write() {
		FileOutputStream fos = null;
		String downloadPath = null;
		try {
			try { new File(WriteShare.writeOption.getFilePath()).mkdirs(); }catch (Exception e) {}
			downloadPath = WriteShare.writeOption.getFilePath() + WriteShare.writeOption.getFileName();
			ZipSecureFile.setMinInflateRatio(0);
			fos = new FileOutputStream(downloadPath);
			WriteShare.wb.write(fos);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		finally {
			if(fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {}
			}
		}
		
		return downloadPath;
	}
	
}
