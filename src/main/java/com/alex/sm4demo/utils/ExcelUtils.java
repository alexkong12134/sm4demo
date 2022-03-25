package com.alex.sm4demo.utils;

import com.alex.sm4demo.pojo.ExcelPerson;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Alex on 2022/3/24 15:33
 */
public class ExcelUtils {

    public static List<ExcelPerson> excelToShopIdList(InputStream inputStream) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        inputStream.close();
        // 在工作簿获取目标工作表
        Sheet sheet = workbook.getSheetAt(0);
        // 获取到最后一行
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        // 该集合用来储存行对象
        ArrayList<ExcelPerson> forExcel = new ArrayList<>();
        // 遍历整张表，从第二行开始，第一行的表头不要，循环次数不大于最后一行的值
        for (int i = 1; i < physicalNumberOfRows; i++) {
            // 该对象用来储存行数据
            ExcelPerson example = new ExcelPerson();
            // 获取当前行数据
            Row row = sheet.getRow(i);
            // 获取目标单元格的值并存进对象中
            Cell cell = row.getCell(0);
            cell.setCellType(CellType.NUMERIC);
            int value = (int) cell.getNumericCellValue();
            example.setIdnum(value);
            example.setUname(row.getCell(1).getStringCellValue());
            example.setAddr(row.getCell(2).getStringCellValue());
            example.setAge(row.getCell(3).getStringCellValue());
            example.setSex(row.getCell(4).getStringCellValue());
            example.setIdcard(row.getCell(5).getStringCellValue());
            example.setPhone(row.getCell(6).getStringCellValue());
            // 把对象放到集合里
            forExcel.add(example);
        }
        return forExcel;
    }

}
