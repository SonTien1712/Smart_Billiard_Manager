package com.BillardManagement.Util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Simple Excel exporter utility using Apache POI.
 * Provides methods to export tabular data (Object[] rows) into XLSX bytes.
 */
public final class ExcelExporter {

    private ExcelExporter() {}

    /**
     * Export rows (Object[] per row) with header to XLSX bytes.
     * Numbers (Number) will be written as numeric cells, others as text.
     *
     * @param header array of header column names
     * @param rows list of Object[] for each row
     * @return byte[] content of xlsx file
     * @throws IOException when writing workbook fails
     */
    public static byte[] exportTable(String[] header, List<Object[]> rows) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CreationHelper createHelper = wb.getCreationHelper();
            Sheet sheet = wb.createSheet("Sheet1");

            // header style
            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            int r = 0;
            Row headerRow = sheet.createRow(r++);
            for (int c = 0; c < header.length; c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(header[c]);
                cell.setCellStyle(headerStyle);
            }

            // data rows
            for (Object[] row : rows) {
                Row rr = sheet.createRow(r++);
                for (int c = 0; c < header.length; c++) {
                    Cell cell = rr.createCell(c);
                    Object val = (c < row.length) ? row[c] : null;
                    writeCell(createHelper, cell, val);
                }
            }

            // autosize
            for (int i = 0; i < header.length; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        }
    }

    private static void writeCell(CreationHelper helper, Cell cell, Object val) {
        if (val == null) {
            cell.setCellValue("");
            return;
        }
        if (val instanceof Number) {
            cell.setCellValue(((Number) val).doubleValue());
            return;
        }
        if (val instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) val).doubleValue());
            return;
        }
        if (val instanceof Boolean) {
            cell.setCellValue((Boolean) val);
            return;
        }
        // fallback to string
        cell.setCellValue(val.toString());
    }
}