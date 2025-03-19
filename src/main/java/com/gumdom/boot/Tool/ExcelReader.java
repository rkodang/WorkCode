package com.gumdom.boot.Tool;

import cn.hutool.poi.excel.cell.CellUtil;
import com.gumdom.boot.infrastructure.ExcelFilter;
import com.gumdom.boot.infrastructure.annotation.Excel;
import com.gumdom.boot.infrastructure.annotation.ExcelField;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {


    public static <T> List<T> get(Workbook workbook, Class<T> type, ExcelFilter excelFilter) throws IOException {
        return get(workbook.getSheetAt(0), type, excelFilter);
    }

    public static <T> List<T> get(Sheet sheet, Class<T> type, ExcelFilter excelFilter) throws IOException {
        Excel excel = type == null ? null : type.getAnnotation(Excel.class);
        if (excel == null) {
            throw new IOException("实体类[" + (null == type ? "" : type.getSimpleName()) + "],未定义@Excel注释");
        }
        int rows = sheet.getLastRowNum();
        List<T> list = new ArrayList<>(rows < 1000 ? rows : 0);

        Map<Integer, Field> fieldMap = recordFieldIndex(sheet.getRow(excel.headRowAt()), excel, type);
        try {
            for (int i = excel.dataStartRow() + 1; i <= rows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                T t = type.newInstance();
                boolean nonEmpty = false;
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Field field = fieldMap.get(j);
                    if (field == null) {
                        continue;
                    }
                    String value = String.valueOf(getValue(row.getCell(j)));
                    if (field.getAnnotation(ExcelField.class).isMerged()) {
                        if (isMergedRegion(sheet, i, j)) {
                            value = String.valueOf(getMergedRegionValue(sheet, i, j));
                        }
                    }
                    nonEmpty = nonEmpty || isNotEmpty(value);
                    if (int.class == field.getType()) {
                        field.set(t, isEmpty(value) ? 0 : Integer.parseInt(value));
                    } else {
                        field.set(t, value);
                    }
                }

                if (excel.skipAllEmpty() && !nonEmpty) {
                    if (excel.breakAllEmpty()) {
                        break;
                    } else {
                        //
                    }
                } else if (excelFilter == null || excelFilter.accept(t, i)) {
                    list.add(t);
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }


    private static <T> Map<Integer, Field> recordFieldIndex(Row headRow, Excel excel, Class<T> type) throws IOException {
        if (null == headRow) {
            throw new IOException("实例化异常:class=" + type.getSimpleName());
        }
        Field[] fields = type.getDeclaredFields();
        fields = fields == null ? new Field[]{} : fields;
        String supClass = type.getSuperclass().getName();
        if (excel.scanFirstSuperFields() && !"java.lang.Object".equals(supClass)) {
            Field[] supFields = type.getSuperclass().getDeclaredFields();
            supFields = supFields == null ? new Field[]{} : supFields;
            Field[] newFields = new Field[fields.length + supFields.length];
            System.arraycopy(fields, 0, newFields, 0, fields.length);
            System.arraycopy(supFields, 0, newFields, fields.length, supFields.length);
        }
        if (fields.length == 0) {
            throw new IOException("未定义属性:class=" + type.getSimpleName());
        }
        Map<Integer, Field> fieldMap = new HashMap<>();
        Map<String, Integer> keyColumnMap = null;
        for (int i = 0; i < fields.length; i++) {
            ExcelField ef = fields[i].getAnnotation(ExcelField.class);
            if (ef == null || ((ef.value() == null || "".equals(ef.value())) && ef.column() < 0)) {
                continue;
            }
            //优先根据位置匹配字段所属列
            if (ef.column() >= 0) {
                if (headRow.getCell(ef.column()) == null) {
                    throw new IOException("表格中未找到单元格:class=" + type.getSimpleName() + ",row=" + excel.headRowAt() + ",column=" + ef.column());
                }
                fields[i].setAccessible(true);
                fieldMap.put(ef.column(), fields[i]);
            } else {
                //根据表头匹配字段所属列
                if (keyColumnMap == null) {
                    keyColumnMap = new HashMap<>();
                    for (int j = 0; j < headRow.getLastCellNum(); j++) {
                        String key = String.valueOf(getValue(headRow.getCell(j)));
                        keyColumnMap.put(key, j);
                    }
                }
                Integer column = keyColumnMap.get(ef.value());
                if (column == null) {
                    throw new IOException("表格中未找到单元格:class=" + type.getSimpleName() + ",row=" + excel.headRowAt() + ",column=" + ef.column());
                }
                fields[i].setAccessible(true);
                fieldMap.put(column.intValue(), fields[i]);
            }
        }

        return fieldMap;
    }

    private static Object getValue(Cell cell) {
        return CellUtil.getCellValue(cell, true);
    }

    private static boolean isMergedRegion(Sheet sheet, int row, int column) {
        if (sheet != null) {
            int sheetMergeCount = sheet.getNumMergedRegions();
            for (int i = 0; i < sheetMergeCount; i++) {
                CellRangeAddress ca = sheet.getMergedRegion(i);
                if (row >= ca.getFirstRow() && row <= ca.getLastRow() && column >= ca.getFirstColumn() && column <= ca.getLastColumn()) {
                    return true;
                }
            }
        }

        return false;
    }

    private static Object getMergedRegionValue(Sheet sheet, int row, int column) {
        if (sheet != null) {
            int sheetMergeCount = sheet.getNumMergedRegions();
            for (int i = 0; i < sheetMergeCount; i++) {
                CellRangeAddress ca = sheet.getMergedRegion(i);
                if (row >= ca.getFirstRow() && row <= ca.getLastRow() && column >= ca.getFirstColumn() && column <= ca.getLastColumn()) {
                    return getValue(sheet.getRow(ca.getFirstRow()).getCell(ca.getFirstColumn()));
                }
            }
        }

        return "";
    }

}
