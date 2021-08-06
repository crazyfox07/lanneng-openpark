package com.lann.openpark.charge.bizobj.config;

/**
 * 时间补足配置
 *
 * @Author songqiang
 * @Description
 * @Date 2020/6/24 14:36
 **/
public class BorrowTimeConfig {
    private boolean allDayTimeCanBeTaken = true;
    private boolean dayTimeCanBeTaken = false;
    private boolean nightTimeCanBeTaken = false;
    private int timeLimit = 30;


    public int getTimeLimit() {
        return this.timeLimit;
    }


    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }


    public boolean isAllDayTimeCanBeTaken() {
        return this.allDayTimeCanBeTaken;
    }


    public void setAllDayTimeCanBeTaken(boolean allDayTimeCanBeTaken) {
        this.allDayTimeCanBeTaken = allDayTimeCanBeTaken;
    }


    public boolean isDayTimeCanBeTaken() {
        return this.dayTimeCanBeTaken;
    }


    public void setDayTimeCanBeTaken(boolean dayTimeCanBeTaken) {
        this.dayTimeCanBeTaken = dayTimeCanBeTaken;
    }


    public boolean isNightTimeCanBeTaken() {
        return this.nightTimeCanBeTaken;
    }


    public void setNightTimeCanBeTaken(boolean nightTimeCanBeTaken) {
        this.nightTimeCanBeTaken = nightTimeCanBeTaken;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("**********借时间配置************\n");
        sb.append("[时间限制]=" + this.timeLimit + "\n");
        sb.append("[全天可借]=" + this.allDayTimeCanBeTaken + "\n");
        sb.append("[白天可借]=" + this.dayTimeCanBeTaken + "\n");
        sb.append("[夜间可借]=" + this.nightTimeCanBeTaken + "\n");
        return sb.toString();
    }
}
