package com.lann.openpark;

import org.junit.Test;

import java.net.InetAddress;
import java.text.ParseException;

public class ChargeTest {
    // @Test
    public void timeTest() throws ParseException {
//        Date date = null;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        // 驶入时间只要不是分钟整数，上取整
//        if ((int) (new Date().getTime() % 60000L / 1000L) != 0) {
//            date = new Date(new Date().getTime() + 60000L);
//        }
//        System.out.println(DateUtil.formatDateYMDHMS(date));
//        date = sdf.parse(DateUtil.toDate("yyyy-MM-dd HH:mm:00", date));
//        System.out.println(DateUtil.formatDateYMDHMS(date));
//
//        // 驶离时间下取整
//        date = sdf.parse(DateUtil.toDate("yyyy-MM-dd HH:mm:00", new Date()));
//        System.out.println(DateUtil.formatDateYMDHMS(date));
//
//        // 计算停车分钟数
//        Date date1 = DateUtil.toDateTime("2020-06-11 08:12:00");
//        Date date2 = DateUtil.toDateTime("2020-06-11 09:14:00");
//        double parkInv = (date2.getTime() - date1.getTime()) / 1000.0D / 60.0D;
//        System.out.println((int) parkInv);
    }

    @Test
    public void testPing() throws Exception {
        InetAddress address = InetAddress.getByName("http://jtss.rzbus.cn/openEpark/");
        System.out.println(address.isReachable(3000));
    }
}
