import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class ExcelReader {
    public static void main(String[] args) {
        String filePath = "数据库课程--知识点.xlsx";
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // 打印表头
            if (rows.hasNext()) {
                Row headerRow = rows.next();
                System.out.println("表头信息：");
                for (int i = 0; i < 15; i++) {
                    Cell cell = headerRow.getCell(i);
                    String value = getCellStringValue(cell);
                    System.out.print("列 " + i + " (" + (char)('A' + i) + "): [" + value + "]\t");
                }
                System.out.println();
            }
            
            // 打印前10行数据
            System.out.println("\n前10行数据：");
            int rowCount = 0;
            while (rows.hasNext() && rowCount < 10) {
                Row currentRow = rows.next();
                rowCount++;
                System.out.print("行 " + rowCount + ": ");
                
                for (int i = 0; i < 7; i++) {
                    Cell cell = currentRow.getCell(i);
                    String value = getCellStringValue(cell);
                    System.out.print("列 " + i + ": [" + value + "]\t");
                }
                System.out.println();
            }
            
            System.out.println("\n总行数：" + sheet.getPhysicalNumberOfRows());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
}