package com.lann.openpark.util;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.List;

public class ExportUtil {

    public static final Integer maxExportNum = 65536;

    /**
     * 导出excel设置标题栏
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/2 9:24
     **/
    public static HSSFRow creatTitleRow(HSSFSheet sheet, List<String> titleList) {

        HSSFRow row = null;
        // 设置小标题
        row = sheet.createRow(0);
        row.setHeight((short) (16.50 * 30));//设置行高

        for (int i = 0; i < titleList.size(); i++) {
            row.createCell(i).setCellValue(titleList.get(i));
        }

        return row;

    }

    /**
     * 错误导出
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/2 10:27
     **/
    public static HSSFRow creatErrorExport(HSSFSheet sheet, String errMsg) {

        HSSFRow row = null;
        // 设置小标题
        row = sheet.createRow(0);
        row.setHeight((short) (16.50 * 30));//设置行高

        row.createCell(0).setCellValue(errMsg);

        return row;

    }

}
