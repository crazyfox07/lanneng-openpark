package com.lann.openpark.charge.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;


public class DateTimeUtil {

    /**
     * Description : 根据停车总时长拆分循环计费单位
     */
    public static Interval[] createIntervalArrayByLength(DateTime start, DateTime end, int length) {
        List<Interval> list = new ArrayList<>();

        DateTime nextStart = start.plusHours(length);
        while (nextStart.isBefore((ReadableInstant) end)) {
            Interval interval = new Interval((ReadableInstant) start, (ReadableInstant) nextStart);
            list.add(interval);
            start = new DateTime(nextStart);
            nextStart = nextStart.plusHours(length);
        }
        Interval lastInterval = new Interval((ReadableInstant) start, (ReadableInstant) end);
        list.add(lastInterval);
        Interval[] result = new Interval[list.size()];

        return list.toArray(result);
    }


    public static Interval[] createIntervalArrayByPoint(DateTime start, DateTime end, int point) {
        List<Interval> list = new ArrayList<>();

        int firstDistance = 0;
        if (start.getMinuteOfDay() >= point * 60) {
            firstDistance = 1440 - start.getMinuteOfDay() + point * 60;
        } else {
            firstDistance = point * 60 - start.getMinuteOfDay();
        }

        DateTime newStart = start.plusMinutes(firstDistance);
        while (newStart.isBefore((ReadableInstant) end)) {
            Interval e = new Interval((ReadableInstant) start, (ReadableInstant) newStart);
            list.add(e);
            start = newStart;
            newStart = newStart.plusHours(24);
        }
        Interval lastInterval = new Interval((ReadableInstant) start, (ReadableInstant) end);
        list.add(lastInterval);
        Interval[] result = new Interval[list.size()];
        return list.toArray(result);
    }

    /**
     * 停车时段换算成分钟数
     *
     * @Author songqiang
     * @Description
     * @Date 2020/6/23 11:11
     **/
    public static long getMinutesBetweenInterval(Interval interval) {
        DateTime start = interval.getStart();
        DateTime end = interval.getEnd();
        long length = (end.getMillis() - start.getMillis()) / 60000L;
        return length;
    }


    public static Interval overlap(int startMinute, int endMinute, Interval interval) {
        Interval result = null;

        LocalDate localDate = interval.getStart().toLocalDate();


        DateTime periodStartDateTime = null;
        DateTime periodEndDateTime = null;

        int driveInMinute = interval.getStart().getMinuteOfDay();

        if (endMinute > startMinute) {
            periodStartDateTime = localDate.toDateTime(new LocalTime(startMinute / 60, startMinute % 60));
            periodEndDateTime = localDate.toDateTime(new LocalTime(endMinute / 60, endMinute % 60));

        } else if (startMinute > endMinute) {

            if (driveInMinute >= endMinute) {
                periodStartDateTime = localDate.toDateTime(new LocalTime(startMinute / 60, startMinute % 60));
                periodEndDateTime = localDate.toDateTime(new LocalTime(endMinute / 60, endMinute % 60)).plusDays(1);
            } else {

                periodStartDateTime = localDate.toDateTime(new LocalTime(startMinute / 60, startMinute % 60)).minusDays(1);
                periodEndDateTime = localDate.toDateTime(new LocalTime(endMinute / 60, endMinute % 60));
            }
        }

        if (periodStartDateTime != null && periodEndDateTime != null) {
            result = interval.overlap((ReadableInterval) new Interval((ReadableInstant) periodStartDateTime, (ReadableInstant) periodEndDateTime));
        }
        return result;
    }


    public static Boolean ifTimeBelongSection(int[] interval, DateTime time) {
        int beginMinite = interval[0];
        int endMinite = interval[1];
        int timeMinite = time.getMinuteOfDay();
        if (beginMinite < endMinite) {
            if (timeMinite >= beginMinite && timeMinite < endMinite) {
                return Boolean.valueOf(true);
            }
        } else if ((timeMinite >= beginMinite && timeMinite < 1440) || (timeMinite >= 0 && timeMinite < endMinite)) {
            return Boolean.valueOf(true);
        }

        return Boolean.valueOf(false);
    }

    /**
     * 计算时间段section和停车时间的交集时长 Description :
     *
     * @param section      分钟数，可能会跨天，需要判断
     * @param parkInterval
     * @return
     */
    public static int getOverLapDurationMinutes(int[] section, Interval parkInterval) {
        long sum = 0L;
        DateTime parkInDateTime = parkInterval.getStart();// 驶入时间
        DateTime parkOutDateTime = parkInterval.getEnd();// 驶离时间

        DateTime sectionStart = new DateTime(parkInDateTime);
        sectionStart = sectionStart.withMillisOfDay(section[0] * 60 * 1000);// 时段开始时间
        DateTime sectionEnd = sectionStart.withMillisOfDay(section[1] * 60 * 1000);// 时段结束时间

        // 时间段起大于时间段止，说明跨天，时间段止需要加上1天
        if (section[0] > section[1]) {
            sectionEnd = sectionEnd.plusDays(1);
        }

        do {
            Interval tmpSection = new Interval((ReadableInstant) sectionStart, (ReadableInstant) sectionEnd);
            Interval overlap = tmpSection.overlap((ReadableInterval) parkInterval);
            if (overlap != null) {
                sum += overlap.toDurationMillis();
            }

            sectionStart = sectionStart.plusDays(1);
            sectionEnd = sectionEnd.plusDays(1);
        } while (sectionStart.compareTo((ReadableInstant) parkOutDateTime) < 0);

        return (int) sum / 60000;
    }

    public static void main(String[] args) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTime parkInDateTime = new DateTime(df.parse("2020-06-23 10:00:00"));
        DateTime parkOutDateTime = new DateTime(df.parse("2020-06-25 18:00:00"));

//        int[] section = {720, 900};
//        int parkLength = getOverLapDurationMinutes(section, new Interval((ReadableInstant) parkInDateTime, (ReadableInstant) parkOutDateTime));
//        System.out.println(parkLength);


        System.out.println("===========测试拆分begin=============");
        Interval[] array = createIntervalArrayByLength(parkInDateTime, parkOutDateTime, 5);
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }
        System.out.println("===========测试拆分end=============");


//        System.out.println("===========测试自定义点拆分begin=============");
//        Interval[] array1 = createIntervalArrayByPoint(parkInDateTime, parkOutDateTime, 5);
//        for (int i = 0; i < array1.length; i++) {
//            System.out.println(array1[i]);
//        }
//        System.out.println("===========测试自定义点拆分end=============");
    }
}


