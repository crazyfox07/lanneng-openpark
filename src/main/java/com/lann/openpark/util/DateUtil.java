package com.lann.openpark.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DateUtil {
    public static final int YEAR = 1;
    static final String YEAR_STR = "yyyy";
    public static final int MONTH = 2;
    static final String MONTH_STR = "MM";
    public static final int DATE = 3;
    static final String DATE_STR = "dd";
    public static final int HOUR = 4;
    static final String HOUR_STR = "HH";
    public static final int MINUTE = 5;
    static final String MINUTE_STR = "mm";
    public static final int SECOND = 6;
    static final String SECOND_STR = "ss";
    public static final int YEAR_MONTH_DATE = 7;
    public static final int YEAR_MONTH_DATE_HOUR = 8;
    public static final int YEAR_MONTH_DATE_HOUR_MINUTE = 9;
    public static final int YEAR_MONTH_DATE_HOUR_MINUTE_SECOND = 12;
    public static final int HOUR_MINUTE = 10;
    public static final int DATE_HOUR = 11;

    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);


    public static String getTime(int obj) {
        switch (obj) {
            case 1:
                return toTime("yyyy");
            case 2:
                return toTime("MM");
            case 3:
                return toTime("dd");
            case 4:
                return toTime("HH");
            case 5:
                return toTime("mm");
            case 6:
                return toTime("ss");
            case 7:
                return toTime("yyyy-MM-dd");
            case 8:
                return toTime("yyyy-MM-dd HH");
            case 9:
                return toTime("yyyy-MM-dd HH:mm");
            case 12:
                return toTime("yyyy-MM-dd HH:mm:ss");

            case 10:
                return toTime("HH:mm");
            case 11:
                return toTime("dd HH");
        }
        return toTime();
    }


    public static String toDate(String format) {
        return (new SimpleDateFormat(format)).format(new Date());
    }


    public static String toDate(String format, Date date) {
        return (new SimpleDateFormat(format)).format(date);
    }


    public static Date toDateTime(Object value) {
        return parseDate(value, "yyyy-MM-dd HH:mm:ss");
    }


    public static Date parseDate(Object value, String fromat) {
        try {
            DateFormat df = new SimpleDateFormat(fromat);
            return df.parse(value.toString());
        } catch (Exception e) {
            log.error("抛出异常", e);

            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(addDay(new Date(), -5));
    }


    public static String toTime(String format) {
        if (StringUtils.isEmpty(format)) {
            return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        }
        return (new SimpleDateFormat(format)).format(new Date());
    }


    public static String toTime(Date date) {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
    }


    public static String toTime() {
        return toTime("");
    }


    public static int getDaysBetweenTwoTimes(Object dateFrom, Object dateEnd) {
        Date dtFrom = parseDate(dateFrom, "yyyy-MM-dd");
        Date dtEnd = parseDate(dateEnd, "yyyy-MM-dd");
        long begin = dtFrom.getTime();
        long end = dtEnd.getTime();
        long inter = end - begin;
        if (inter < 0L) {
            inter *= -1L;
        }
        long dateMillSec = 86400000L;
        Long dateCnt = Long.valueOf(inter / dateMillSec);
        long remainder = inter % dateMillSec;
        if (remainder != 0L) {
            Long long_1 = dateCnt, long_2 = dateCnt = Long.valueOf(dateCnt.longValue() + 1L);
        }
        return dateCnt.intValue();
    }


    public static int getMonths(Date startDate, Date endDate) {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        int diffYear = endCalendar.get(1) - startCalendar.get(1);
        int diffMonth = diffYear * 12 + endCalendar.get(2) - startCalendar.get(2);
        return diffMonth;
    }


    public static String longToDate(Long value, String format) {
        try {
            DateFormat df = null;


            df = new SimpleDateFormat(format);

            Date dt = new Date(value.longValue());
            return df.format(dt);
        } catch (Exception exception) {

            return null;
        }
    }


    public static long pastDays() {
        long t = (new Date()).getTime();
        return t / 86400000L;
    }


    public static Date addDay(Date date, int count) {
        return new Date(date.getTime() + 86400000L * count);
    }


    public static String getDateString(Date date) {
        DateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        String dateString = formatter.format(date);
        return dateString;
    }


    public static String formatTime(Long ms) {
        Integer ss = Integer.valueOf(1000);
        Integer mi = Integer.valueOf(ss.intValue() * 60);
        Integer hh = Integer.valueOf(mi.intValue() * 60);
        Integer dd = Integer.valueOf(hh.intValue() * 24);

        Long day = Long.valueOf(ms.longValue() / dd.intValue());
        Long hour = Long.valueOf((ms.longValue() - day.longValue() * dd.intValue()) / hh.intValue());
        Long minute = Long.valueOf((ms.longValue() - day.longValue() * dd.intValue() - hour.longValue() * hh.intValue()) / mi.intValue());
        Long second = Long.valueOf((ms.longValue() - day.longValue() * dd.intValue() - hour.longValue() * hh.intValue() - minute.longValue() * mi.intValue()) / ss.intValue());
        Long milliSecond = Long.valueOf(ms.longValue() - day.longValue() * dd.intValue() - hour.longValue() * hh.intValue() - minute.longValue() * mi.intValue() - second.longValue() * ss.intValue());

        StringBuffer sb = new StringBuffer();
        if (day.longValue() > 0L) {
            sb.append(day + "天");
        }
        if (hour.longValue() > 0L) {
            sb.append(hour + "小时");
        }
        if (minute.longValue() > 0L) {
            sb.append(minute + "分");
        }
        if (second.longValue() > 0L) {
            sb.append(second + "秒");
        }
        if (milliSecond.longValue() > 0L) {
            sb.append(milliSecond + "毫秒");
        }
        if (sb.length() == 0) {
            sb.append("0");
        }
        return sb.toString();
    }


    public static Date getStartTimeOfDay(Date date) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(11, 0);
        day.set(12, 0);
        day.set(13, 0);
        day.set(14, 0);
        return day.getTime();
    }


    public static String[] getLast12Months(String time) {
        if (time.length() == 7) {
            time = time + "-01 00:00:00";
        } else if (time.length() == 110) {
            time = time.substring(0, 7) + "-01 00:00:00";
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            return null;
        }

        String[] last12Months = new String[12];
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(2, cal.get(2) + 1);
        for (int i = 0; i < 12; i++) {
            cal.set(2, cal.get(2) - 1);
            last12Months[11 - i] = cal.get(1) + "-" + addZeroForNum(String.valueOf(cal.get(2) + 1), 2);
        }

        return last12Months;
    }

    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);

                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }


    public static Date getEndTimeOfDay(Date date) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(11, 23);
        day.set(12, 59);
        day.set(13, 59);
        day.set(14, 999);
        return day.getTime();
    }


    public static String getCurrentDateTimeFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(new Date());
        return dateStr;
    }


    public static String formatDateYMDHMSSSS(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        String dateStr = sdf.format(date);
        return dateStr;
    }


    public static String formatDateYMDHMS(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);
        return dateStr;
    }


    public static String formatDateYMD(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        return dateStr;
    }

    public static String formatDateYMD1(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dateStr = sdf.format(date);
        return dateStr;
    }


    public static String formatDateYM(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String dateStr = sdf.format(date);
        return dateStr;
    }


    public static String formatDateY(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String dateStr = sdf.format(date);
        return dateStr;
    }


    public static Date parseDateYMDHMS(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error("字符串转日期方法parseDateYMDHMS()发生错误：" + e.toString() + ",dateStr=" + dateStr);
        }
        return date;
    }


    public static Date parseDateYMDHMSNoSpacer(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error("字符串转日期方法parseDateYMDHMSNoSpacer()发生错误：" + e.toString() + ",dateStr=" + dateStr);
        }
        return date;
    }


    public static Date parseDateYMD(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error("字符串转日期方法parseDateYMD()发生错误：" + e.toString() + ",dateStr=" + dateStr);
        }
        return date;
    }


    public static List<Date> getDateFromRange(Date beginDate, Date endDate) {
        List<Date> list = new ArrayList<>();

        Calendar calBegin = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();

        calBegin.setTime(beginDate);
        calEnd.setTime(endDate);
        list.add(calBegin.getTime());

        while (endDate.after(calBegin.getTime())) {

            calBegin.add(5, 1);
            list.add(calBegin.getTime());
        }
        return list;
    }


    public static int getMaxDayByYearMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, year);
        calendar.set(2, month);
        return calendar.getActualMaximum(5);
    }


    public static List<Date> getMonthFromRange(Date beginDate, Date endDate) {
        List<Date> list = new ArrayList<>();


        int endYear = endDate.getYear();
        int endMonth = endDate.getMonth();
        int maxDate = getMaxDayByYearMonth(endYear, endMonth);
        String endDateYMD = formatDateYM(endDate) + "-" + maxDate;
        Date end = parseDateYMD(endDateYMD);

        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(beginDate);


        while (end.after(calBegin.getTime())) {

            list.add(calBegin.getTime());
            calBegin.add(2, 1);
        }
        return list;
    }


    public static List<String> getYearFromRange(Date beginDate, Date endDate) {
        List<String> list = new ArrayList<>();

        int maxYear = Integer.parseInt(formatDateY(beginDate));
        int endYear = Integer.parseInt(formatDateY(endDate));

        while (endYear >= maxYear) {
            list.add(maxYear + "");
            maxYear++;
        }

        return list;
    }


    public static List<String> getWeekFromRange(Date beginDate, Date endDate) {
        List<String> listStr = new ArrayList<>();

        Calendar calBegin = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();

        calBegin.setTime(beginDate);
        calEnd.setTime(endDate);


        while (endDate.after(calBegin.getTime())) {

            int weekNum = calBegin.get(3);
            if (weekNum < 10) {
                listStr.add(formatDateY(calBegin.getTime()) + "_0" + weekNum);
            } else {
                listStr.add(formatDateY(calBegin.getTime()) + "_" + weekNum);
            }
            calBegin.add(6, 7);
        }
        return listStr;
    }


    public static Boolean checkOperateTimePermit() {
        Boolean flag = Boolean.valueOf(false);
        Date sysTime = new Date();
        String sysDate = formatDateYMD(sysTime);

        Date beginTime = parseDateYMDHMS(sysDate + " 07:20:00");
        Date endTime = parseDateYMDHMS(sysDate + " 22:10:00");

        if (sysTime.getTime() >= beginTime.getTime() && sysTime.getTime() <= endTime.getTime()) {
            flag = Boolean.valueOf(true);
        }
        return flag;
    }


    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sdf.format(new Date());
        return dateStr;
    }


    public static int getTimeInterval(Date beginTime, Date endTime) {
        Double timeInv = Double.valueOf(0.0D);
        if ((int) (beginTime.getTime() % 60000L / 1000L) != 0) {
            beginTime = new Date(beginTime.getTime() + 60000L);
            beginTime = parseDateYMDHMS(toDate("yyyy-MM-dd HH:mm:00", beginTime));
        }

        endTime = parseDateYMDHMS(toDate("yyyy-MM-dd HH:mm:00", endTime));

        timeInv = Double.valueOf((endTime.getTime() - beginTime.getTime()) / 1000.0D / 60.0D);

        if (timeInv.doubleValue() < 0.0D) {
            timeInv = Double.valueOf(0.0D);
        }
        return timeInv.intValue();
    }


    public static Date addSecond(Date date, int secondNum) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(13, secondNum);
        return calendar.getTime();
    }
}


