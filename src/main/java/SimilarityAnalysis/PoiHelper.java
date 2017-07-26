package SimilarityAnalysis;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;

/**
 * Created by Administrator on 2017/7/4.
 */
public class PoiHelper {
    public static void exportXlsToFile(String[] title, String[][] data, File file){
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("相似度分析");

        HSSFRow titleRow = sheet.createRow(0);
        for (int i = 0; i < title.length; i++) {
            HSSFCell titleCell = titleRow.createCell(i);
            titleCell.setCellValue(title[i]);
        }

        for (int i = 0; i < data.length; i++) {
            HSSFRow dataRow = sheet.createRow(i + 1);
            for (int j = 0; j < data[i].length; j++){
                HSSFCell dataCell = dataRow.createCell(j);
                dataCell.setCellValue(data[i][j]);
            }
        }

        try {
            workbook.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportTxtToFile(String[] title, String[][] data, File file){
        try (FileWriter fw = new FileWriter(file)) {
            for (int i = 0; i < title.length; i++) {
                fw.write(title[i] + "\t");
            }
            fw.write("\r\n");

            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++){
                    fw.write(data[i][j] + "\t");
                }
                fw.write("\r\n");
            }

            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
