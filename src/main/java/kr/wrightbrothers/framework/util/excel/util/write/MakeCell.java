package kr.wrightbrothers.framework.util.excel.util.write;

import kr.wrightbrothers.framework.util.excel.annotations.Title;
import kr.wrightbrothers.framework.util.excel.util.write.share.WriteShare;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.List;

public class MakeCell {

	private static List<Class<?>> numericTypes;

	static {
		numericTypes = new ArrayList<Class<?>>();
		numericTypes.add(Byte.class);
		numericTypes.add(Short.class);
		numericTypes.add(Integer.class);
		numericTypes.add(Long.class);
		numericTypes.add(Float.class);
		numericTypes.add(Double.class);
	}
	
	private Object obj;
	private Row row;
	private int cellIndex;
	private CellStyle style;
	
	/**
	 * 
	 * @param obj
	 * @param fieldAnnotation
	 * @param row
	 * @param cellIndex
	 */
	public MakeCell(Object obj, Title fieldAnnotation, Row row, int cellIndex) {
		this.obj = obj;
		this.row = row;
		this.cellIndex = cellIndex;
		this.style =  WriteShare.sheet.getRow(1).getCell(cellIndex).getCellStyle();
	}
	
	/**
	 * 
	 * @param obj
	 * @param fieldAnnotation
	 * @param row
	 * @param cellIndex
	 */
	public void changeCell(Object obj, Title fieldAnnotation, Row row, int cellIndex) {
		this.obj = obj;
		this.row = row;
		this.cellIndex = cellIndex;
		this.style =  WriteShare.sheet.getRow(1).getCell(cellIndex).getCellStyle();
	}
	
	/**
	 * 
	 * @param f
	 * @param row
	 */
	public void fillValue(java.lang.reflect.Field f, Row row) {
		Cell cell = null;
		try {
			obj = f.get(obj);
//			format = f.getAnnotation(Format.class);
			cell = makeCellAndFill();
			cell.setCellStyle(style);
			/** Style 적용 주석 대용량 일때 poi 오류발생*/
			
//			if(row.getRowNum() > 1) {
//				CellStyle cellStyle = makeCellStyle(WriteShare.wb, row.getRowNum());
//				if ( cell != null ) {
//					cell.setCellStyle(cellStyle);
//					cell.setCellStyle(getPreferredCellStyle(cell));
//				}
//			}
			
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public CellStyle getPreferredCellStyle(Cell cell) {
		  CellStyle cellStyle = cell.getCellStyle();
		  if (cellStyle.getIndex() == 0) cellStyle = cell.getRow().getRowStyle();
		  if (cellStyle == null) cellStyle = cell.getSheet().getColumnStyle(cell.getColumnIndex());
		  if (cellStyle == null) cellStyle = cell.getCellStyle();
		  return cellStyle;
		 }
	
	private Cell makeCellAndFill() {
		Cell cell = null;
		
		if ( obj == null ) {
			cell = row.createCell(cellIndex, CellType.STRING);
			cell.setCellValue("");
			return cell;
		}
		
		if (obj.getClass() == String.class) {
			
			String data = obj + "";
			if (data.trim().startsWith("=")) {
				data = data.trim().substring(1).trim();
				cell = row.createCell(cellIndex, CellType.FORMULA);
				cell.setCellFormula(data);
			} else {
				cell = row.createCell(cellIndex, CellType.STRING);
				cell.setCellValue(data);
			}
			
		} else if (numericTypes.contains(obj.getClass())) {
			cell = row.createCell(cellIndex, CellType.NUMERIC);
			cell.setCellValue(Double.parseDouble(String.valueOf(obj)));
		} else if (obj.getClass() == Boolean.class) {
			cell = row.createCell(cellIndex, CellType.BOOLEAN);
			cell.setCellValue(Boolean.parseBoolean(obj + ""));
		}
		
		return cell;
	}
	
	private CellStyle makeCellStyle(Workbook wb, int rowNum) {
		
//		if ( style == null ) {
//			style = wb.createCellStyle();
//		}
		
//		if(rowNum == 1) {
//			wb.ge
//			wb.getCellStyleAt(rowNum)
//		}
//		
//		if(format != null) {
//			String alignment = format.alignment();
//			if ( alignment.equals(Format.LEFT) ) {
//				style.setAlignment(HorizontalAlignment.LEFT);
//			}
//			else if ( alignment.equals(Format.CENTER) ) {
//				style.setAlignment(HorizontalAlignment.CENTER);
//			}
//			else if ( alignment.equals(Format.RIGHT) ) {
//				style.setAlignment(HorizontalAlignment.RIGHT);
//			} 
//			
//			String vAlignment = format.verticalAlignment();
//			if ( vAlignment.equals(Format.V_TOP) ) {
//				style.setVerticalAlignment(VerticalAlignment.TOP);
//			}
//			else if ( vAlignment.equals(Format.V_CENTER) ) {
//				style.setVerticalAlignment(VerticalAlignment.CENTER);
//			}
//			else if ( vAlignment.equals(Format.V_BOTTOM) ) {
//				style.setVerticalAlignment(VerticalAlignment.BOTTOM);
//			} 
//			
//			String formatString = format.dataFormat();
//			if (formatString != null && formatString.length() > 0 ) {
//				if ( dataFormat == null ) {
//					dataFormat = wb.createDataFormat();
//				}
//				style.setDataFormat(dataFormat.getFormat(formatString));
//			}
//			
//			if ( format.bold() ) {
//				if ( font == null ) {
//					font = wb.createFont();
//				}
//				style.setFont(font);
//				font.setBold(true);
//			}
//		}
		return style;
	}
	
}
