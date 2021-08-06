package com.lann.openpark.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;

public class SignatureUtil {
    public static String sign(String key, String[] params) {
        String res = null;

        Arrays.sort((Object[]) params);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i]);
        }
        sb.append(key);
        res = DigestUtils.md5Hex(sb.toString());
        return res;
    }

    public static void main(String[] args) {
        String str = "wugangwei";
        String res1 = DigestUtils.shaHex(str);
        String res2 = DigestUtils.md5Hex(str);
        // String res3 = DigestUtils.md5DigestAsHex(str.getBytes());


        System.out.println(res1);
        System.out.println(res2);
        // System.out.println(res3);
    }
}



