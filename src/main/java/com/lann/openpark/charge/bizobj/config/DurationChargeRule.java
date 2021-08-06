package com.lann.openpark.charge.bizobj.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 停车时长收费规则，所有区间均是[ a,b )<br>
 *
 * @Author songqiang
 * @Description
 * @Date 2020/6/24 14:37
 **/
public class DurationChargeRule {
    private List<DurationUnit> duractionList = new ArrayList<>();


    public void addDuractionUnit(int startMinite, int endMinite, int timeUnit, double unitCost) {
        if (startMinite < 0 || endMinite < 0 || timeUnit < 0 || unitCost < 0.0D) {
            throw new RuntimeException();
        }
        DurationUnit unit = new DurationUnit(startMinite, endMinite, timeUnit, unitCost);
        this.duractionList.add(unit);
    }


    public double getCost(long parkLength) {
        double total = 0.0D;
        for (DurationUnit d : this.duractionList) {
            // 时区开始分钟数
            int startMin = d.getStart();
            // 时区结束分钟数
            int endMin = d.getEnd();
            int timeUnit = d.getTimeUnit();// 单位分钟
            // 停车时长大于本时长跨度，用时长计算
            if (endMin < parkLength) {
                total += ((endMin - startMin) / timeUnit) * d.getUnitCost();
                continue;
            }
            if ((parkLength - startMin) % timeUnit != 0L) {
                total += ((parkLength - startMin) / timeUnit + 1L) * d.getUnitCost();
                break;
            }
            total += ((parkLength - startMin) / timeUnit) * d.getUnitCost();
        }


        return total;
    }

    /**
     * Description : 查询距离完整的计费单位，还差几分钟
     *
     * @param parkLength
     * @return
     */
    public long queryDistance2Entire(long parkLength) {
        long result = 0L;
        for (DurationUnit d : this.duractionList) {
            int endMin = d.getEnd();
            if (endMin < parkLength) {
                continue;
            }

            int startMin = d.getStart();
            int timeUnit = d.getTimeUnit();
            if ((parkLength - startMin) % timeUnit == 0L) {
                result = 0L;
                break;
            }
            result = timeUnit - (parkLength - startMin) % timeUnit;
        }


        return result;
    }

    public static void main(String[] args) {
        DurationChargeRule durationRule = new DurationChargeRule();
        durationRule.addDuractionUnit(0, 60, 60, 4.0D);
        durationRule.addDuractionUnit(60, 840, 60, 1.0D);
        // System.out.println(durationRule.queryDistance2Entire(220));
        System.out.println(durationRule.getCost(100));
    }

    public List<DurationUnit> getDuractionList() {
        return this.duractionList;
    }


    public void setDuractionList(List<DurationUnit> duractionList) {
        this.duractionList = duractionList;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.duractionList.size(); i++) {
            sb.append("时长配置").append(i + 1).append(": ").append(((DurationUnit) this.duractionList.get(i)).toString()).append("\n");
        }

        return sb.toString();
    }
}

