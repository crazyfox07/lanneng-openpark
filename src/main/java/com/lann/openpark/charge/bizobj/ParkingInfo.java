package com.lann.openpark.charge.bizobj;


import java.util.Date;

import com.lann.openpark.util.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;


public class ParkingInfo {
    private DateTime driveIn;
    private DateTime driveOut;
    private Interval parkInterval;
    private int parkLength;
    private int validParkLength;
    private DateTime calcDriveIn;
    private DateTime caclDriveOut;
    private double cost = 0.0D;


    private Interval[] loopArray;


    public ParkingInfo(Date timeIn, Date timeOut) {
        DateTime t1 = new DateTime(timeIn);
        DateTime t2 = new DateTime(timeOut);

        int timeInSecondField = t1.getSecondOfMinute();
        int timeOutSecondField = t2.getSecondOfMinute();
        if (timeInSecondField != 0) {
            t1 = t1.minusSeconds(timeInSecondField).plusMinutes(1);
        }
        if (timeOutSecondField != 0) {
            t2 = t2.minusSeconds(timeOutSecondField);
        }

        if (t1.compareTo((ReadableInstant) t2) > 0) {
            this.parkLength = 0;
        } else {
            this.parkLength = (int) (t2.getMillis() - t1.getMillis()) / 60000;
        }
        if (t1.isAfter((ReadableInstant) t2)) {
            DateTime temp = t1;
            t1 = t2;
            t2 = temp;
        }
        this.driveIn = t1;
        this.driveOut = t2;
        this.calcDriveIn = this.driveIn;
        this.caclDriveOut = this.driveOut;
        this.parkInterval = new Interval((ReadableInstant) t1, (ReadableInstant) t2);
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[DriveIn]=" + this.driveIn + "\n")
                .append("[DriveOut]=" + this.driveOut + "\n")
                .append("[parkLength]=" + this.parkLength + "\n")
                .append("[validParkLength]=" + this.validParkLength + "\n")
                .append("[calcDriveIn]=" + this.calcDriveIn + "\n")
                .append("[caclDriveOut]=" + this.caclDriveOut + "\n")
                .append("[cost]=" + this.cost + "\n")
                .append("[LoopArray]\n");
        for (int i = 0; i < this.loopArray.length; i++) {
            Interval interval = this.loopArray[i];
            sb.append(interval.getStart() + "~" + interval.getEnd() + "\n");
        }
        return sb.toString();
    }


    public int getParkLength() {
        return this.parkLength;
    }


    public void setParkLength(int parkLength) {
        this.parkLength = parkLength;
    }


    public DateTime getDriveIn() {
        return this.driveIn;
    }


    public void setDriveIn(DateTime driveIn) {
        this.driveIn = driveIn;
    }


    public DateTime getDriveOut() {
        return this.driveOut;
    }


    public void setDriveOut(DateTime driveOut) {
        this.driveOut = driveOut;
    }


    public DateTime getCalcDriveIn() {
        return this.calcDriveIn;
    }


    public void setCalcDriveIn(DateTime calcDriveIn) {
        this.calcDriveIn = calcDriveIn;
    }


    public DateTime getCaclDriveOut() {
        return this.caclDriveOut;
    }


    public void setCaclDriveOut(DateTime caclDriveOut) {
        this.caclDriveOut = caclDriveOut;
    }


    public double getCost() {
        double doubleRoundTwoBit = NumberUtils.doubleRoundTwoBit(this.cost);
        if (doubleRoundTwoBit < 0.0D) {
            return 0.0D;
        }
        return doubleRoundTwoBit;
    }


    public void setCost(double cost) {
        this.cost = cost;
    }


    public Interval getParkInterval() {
        return this.parkInterval;
    }


    public void setParkInterval(Interval parkInterval) {
        this.parkInterval = parkInterval;
    }


    public int getValidParkLength() {
        return this.validParkLength;
    }


    public void setValidParkLength(int validParkLength) {
        this.validParkLength = validParkLength;
    }


    public Interval[] getLoopArray() {
        return this.loopArray;
    }


    public void setLoopArray(Interval[] loopArray) {
        this.loopArray = loopArray;
    }

    public void printLoopString() {
        for (int i = 0; i < this.loopArray.length; i++) {
            Interval interval = this.loopArray[i];
            System.out.println(interval.getStart() + "~" + interval.getEnd());
        }
    }
}

