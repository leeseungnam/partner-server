package kr.wrightbrothers.apps.common.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;

public class ExcelUtil {

    public XSSFWorkbook workbook;       // 엑셀 워크 북
    public XSSFFont font;               // 기본 폰트
    public XSSFCellStyle head;          // 헤드 서식
    public XSSFCellStyle body;          // 내용 서식
    public XSSFCellStyle bodyNumber;    // 내용 숫자 형식의 서식
    public XSSFCellStyle bodyText;      // 내용 텍스트 형식의 서식
    public XSSFSheet sheet;             // 시트
    public XSSFRow row;                 // 로우
    public XSSFCell cell;               // 셀
    public int rowNumber;               // 시트 행 번호
    public int mergeCount;              // 병합 처리 건수
    public int subMergeCount;           // 소분류 병합 처리 건수

    public ExcelUtil() {
        new ExcelUtil(null, 0);
    }

    public ExcelUtil(InputStream stream, int startRowNumber) {
        // Excel 템플릿 사용 여부에 따른 초기화 설정.
        try {
            this.workbook = stream == null ? new XSSFWorkbook() : new XSSFWorkbook(stream);
        } catch (IOException ioException){
            // 템플릿 파일 실패일 경우 기본 생성자 처리
            this.workbook = new XSSFWorkbook();
        }

        // 로우 설정
        rowNumber = startRowNumber;

        // 헤드 셀 서식 설정
        font = workbook.createFont();
        initFont(font, 10, false);

        head = workbook.createCellStyle();
        initCellStyle(head, font, HorizontalAlignment.CENTER, convertShort("11111".toCharArray()));

        // 본문 셀 서식 설정
        body = workbook.createCellStyle();
        initCellStyle(body, font, HorizontalAlignment.CENTER, convertShort("11110".toCharArray()));

        bodyText = workbook.createCellStyle();
        initCellStyle(bodyText, font, HorizontalAlignment.LEFT, convertShort("11110".toCharArray()));

        bodyNumber = workbook.createCellStyle();
        XSSFDataFormat format = workbook.createDataFormat();
        bodyNumber.setDataFormat(format.getFormat("#,##0"));
        initCellStyle(bodyNumber, font, HorizontalAlignment.RIGHT, convertShort("11110".toCharArray()));
    }

    // POI 폰트 초기화
    public void initFont(XSSFFont font,
                         int fontSize,
                         boolean isBold) {
        font.setFontHeightInPoints((short) fontSize);
        font.setFontName("맑은고딕");
        font.setBold(isBold);
    }

    // POI 셀 초기화
    public void initCellStyle(XSSFCellStyle cs,
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

        // 셀 배경색 유무
        setCellBackgroundColor(cs, options[4]);
    }

    public void setCellBoldStyle(XSSFCellStyle cs,
                                 short[] options) {
        // 셀 테두리 설정
        // XSSFCellStyle.BORDER_THIN 활성화 1, 비활성화 0
        cs.setBorderLeft(BorderStyle.valueOf(options[0]));
        cs.setBorderRight(BorderStyle.valueOf(options[1]));
        cs.setBorderTop(BorderStyle.valueOf(options[2]));
        cs.setBorderBottom(BorderStyle.valueOf(options[3]));
    }

    public void setCellBackgroundColor(XSSFCellStyle cs,
                                       short isBackgroundColor) {
        // 셀 배경샛 유무
        if (isBackgroundColor == 0x1) {
            cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
    }

    public short[] convertShort(char[] options) {
        int[] before = new int[5];
        for (int i = 0; i < 5; i++)
            before[i] = Character.getNumericValue(options[i]);

        short[] convert = new short[5];
        for (int i = 0; i < 5; i++)
            convert[i] = (short) before[i];

        return convert;
    }

    public void setCellValue(int index,
                             Object value) {
        setCellValue(index, value, false);
    }

    public void setCellValue(int index,
                             Object value,
                             boolean isTextAlign) {
        cell = row.createCell(index);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
            cell.setCellStyle(bodyNumber);
        }
        else if (value instanceof Long) {
            cell.setCellValue((Long) value);
            cell.setCellStyle(bodyNumber);
        }
        else if (value instanceof String) {
            cell.setCellValue(String.valueOf(value));
            cell.setCellStyle(isTextAlign ? bodyText : body);
        }
        else {
            cell.setCellValue(value + "");
            cell.setCellStyle(body);
        }
    }
}
