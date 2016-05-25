package br.com.ieptbto.cra.util;

import java.io.Serializable;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class XlsUtil implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	public static String getCellToString(Row row, int index) {
		Cell cell = row.getCell(index);
		String value = "";
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				value = cell.getStringCellValue();
			}
		}
		return value.trim();
	}

	public static boolean isEmptyCell(Row row, int index) {
		Cell cell = row.getCell(index);
		String value = "";
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				value = cell.getStringCellValue();
			}
		}
		if (value.trim().isEmpty()) {
			return true;
		}
		return false;
	}
}