package com.lann.openpark.charge.util;

import com.lann.openpark.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LanNengStringUtils {
    private static final Logger log = LoggerFactory.getLogger(FileOperateUtil.class);


    public static final String EMPTY_STRING = "";


    public static String arryToStr(String[] arry, String split) {
        String result = "";
        for (String string : arry) {
            result = result + string + split;
        }
        return result.substring(0, result.length() - 1);
    }


    public static String getRandomChar(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sbf.append(chars.charAt((int) (Math.random() * 26.0D)));
        }
        return sbf.toString();
    }


    public static Boolean checkMobilePhone(String phoneNum) {
        if (phoneNum == null || phoneNum.trim().length() <= 0) {
            return Boolean.valueOf(false);
        }
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$");
        m = p.matcher(phoneNum);
        b = m.matches();
        return Boolean.valueOf(b);
    }


    public static String getRandomNumber(int length) {
        String chars = "0123456789";
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sbf.append(chars.charAt((int) (Math.random() * 10.0D)));
        }
        return sbf.toString();
    }


    public static String toTrim(String str) {
        String s = "";
        if (null != str) {
            s = str.trim();
        }
        return s;
    }


    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return (str2 == null);
        }

        return str1.equals(str2);
    }


    public static String createVerCode() {
        String result = "";
        try {
            Random r = new Random();
            for (int i = 0; i < 6; i++) {
                result = result + r.nextInt(10);
            }
        } catch (Exception e) {
            log.error("抛出异常", e);
        }
        return result;
    }


    public static synchronized String createOrderId() {
        return DateUtil.toTime("yyyyMMddHHmmssSSS") + getRandomNumber(4);
    }


    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }


    public static String getOutRefundNo() {
        String outRefundNo = "";

        outRefundNo = DateUtil.formatDateYMDHMSSSS(new Date()).replace("-", "").replace(":", "").replace(" ", "") + getRandomNumber(1);

        outRefundNo = outRefundNo.substring(2, outRefundNo.length());
        return outRefundNo;
    }
}


