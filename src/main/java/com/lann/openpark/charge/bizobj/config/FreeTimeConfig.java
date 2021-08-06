package com.lann.openpark.charge.bizobj.config;

/**
 * 免费时间配置项
 *
 * @Author songqiang
 * @Description
 * @Date 2020/6/24 14:38
 **/
public class FreeTimeConfig {
    private String freeMode = null;
    private int freeTime = 0;
    private int freeLimitTime = 0;

    private boolean allDayAvailable = true;
    private boolean dayAvailable = false;
    private boolean nightAvailable = false;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("**********免费时间配置规则************\n");
        sb.append("[免费时长]=")
                .append(this.freeTime + "\n")
                .append("[免费策略]=")
                .append(this.freeMode + "\n")
                .append("[超时时间]=")
                .append(this.freeLimitTime + "\n")
                .append("[全天可用]=")
                .append(this.allDayAvailable + "\n")
                .append("[白天可用]=")
                .append(this.dayAvailable + "\n")
                .append("[夜间可用]=")
                .append(this.nightAvailable + "\n");
        return sb.toString();
    }


    public String getFreeMode() {
        return this.freeMode;
    }


    public void setFreeMode(String freeMode) {
        this.freeMode = freeMode;
    }


    public int getFreeTime() {
        return this.freeTime;
    }


    public void setFreeTime(int freeTime) {
        this.freeTime = freeTime;
    }


    public int getFreeLimitTime() {
        return this.freeLimitTime;
    }


    public void setFreeLimitTime(int freeLimitTime) {
        this.freeLimitTime = freeLimitTime;
    }


    public boolean isAllDayAvailable() {
        return this.allDayAvailable;
    }


    public void setAllDayAvailable(boolean allDayAvailable) {
        this.allDayAvailable = allDayAvailable;
        if (allDayAvailable) {
            this.dayAvailable = false;
            this.nightAvailable = false;
        }
    }


    public boolean isDayAvailable() {
        return this.dayAvailable;
    }


    public void setDayAvailable(boolean dayAvailable) {
        this.dayAvailable = dayAvailable;
        if (dayAvailable) {
            this.allDayAvailable = false;
        }
    }


    public boolean isNightAvailable() {
        return this.nightAvailable;
    }


    public void setNightAvailable(boolean nightAvailable) {
        this.nightAvailable = nightAvailable;
        if (nightAvailable)
            this.allDayAvailable = false;
    }
}
