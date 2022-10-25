package kr.wrightbrothers.apps.common.util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

public class ExcelUtil {


    public static XSSFFont initFont(XSSFFont font,
                                    int fontSize,
                                    boolean isBold) {
        font.setFontHeightInPoints((short) fontSize);
        font.setFontName("맑은고딕");
        font.setBold(isBold);

        return font;
    }

    public static void initCellStyle(XSSFCellStyle cs,
                                     XSSFFont font,
                                     HorizontalAlignment align,
                                     short[] options) {
        // 폰트 설정
        cs.setFont(font);

        // 텍스트 정렬 설정
        cs.setAlignment(align);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setWrapText(true);

        // 셀 테두리 설정
        setCellBoldStyle(cs, options);
    }

    public static void setCellBoldStyle(XSSFCellStyle cs,
                                        short[] options) {
        // 셀 테두리 설정
        // XSSFCellStyle.BORDER_THIN 활성화 1, 비활성화 0
        cs.setBorderLeft(BorderStyle.valueOf(options[0]));
        cs.setBorderRight(BorderStyle.valueOf(options[1]));
        cs.setBorderTop(BorderStyle.valueOf(options[2]));
        cs.setBorderBottom(BorderStyle.valueOf(options[3]));
    }



}
