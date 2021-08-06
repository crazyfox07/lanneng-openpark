package com.lann.openpark.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.FieldPosition;

import org.apache.commons.lang.StringUtils;


public class NumberUtils {
    public static String toFormatNumber(String format, String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        DecimalFormat df = new DecimalFormat(format);
        StringBuffer sb = new StringBuffer();
        df.format(new BigDecimal(value), sb, new FieldPosition(0));
        return sb.toString();
    }


    public static Float toFloat(String value) {
        if (StringUtils.isEmpty(value)) {
            return Float.valueOf(0.0F);
        }
        return Float.valueOf(Float.parseFloat(value));
    }


    public static Integer toInteger(String value) {
        if (StringUtils.isEmpty(value)) {
            return Integer.valueOf(0);
        }
        return Integer.valueOf(Integer.parseInt(value));
    }


    public static Long toLong(String value) {
        if (StringUtils.isEmpty(value)) {
            return Long.valueOf(0L);
        }
        return Long.valueOf(Long.parseLong(value));
    }


    public static Double toDouble(String value) {
        if (StringUtils.isEmpty(value)) {
            return Double.valueOf(0.0D);
        }
        return Double.valueOf(Double.parseDouble(value));
    }


    public static BigDecimal toBig(Object o) {
        if (o == null || o.toString().equals("") || o.toString().equals("NaN")) {
            return new BigDecimal(0);
        }
        return new BigDecimal(o.toString());
    }


    public static int averageNumber(Object divisor, Object dividend) {
        if (divisor == null || dividend == null) {
            return 0;
        }
        BigDecimal a = toBig(divisor);
        BigDecimal b = toBig(dividend);
        if (a.equals(toBig(Integer.valueOf(0))) || b.equals(toBig(Integer.valueOf(0)))) {
            return 0;
        }
        BigDecimal c = a.divide(b, 0, 4);
        return c.intValue();
    }


    public static double doubleRound(double doubleValue, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("小数点后保留位数必须大于零！");
        }
        BigDecimal b = new BigDecimal(doubleValue);
        b = b.setScale(scale, 4);
        return b.doubleValue();
    }


    public static double doubleRoundTwoBit(double doubleValue) {
        return doubleRound(doubleValue, 2);
    }


    public static int doubleCompare(Double a, Double b) {
        BigDecimal data1 = new BigDecimal(doubleRoundTwoBit(a.doubleValue()));
        BigDecimal data2 = new BigDecimal(doubleRoundTwoBit(b.doubleValue()));
        return data1.compareTo(data2);
    }
}



