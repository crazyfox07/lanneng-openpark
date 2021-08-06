package com.lann.openpark.charge.bizobj.config;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * 收费规则基础算法配置
 *
 * @Author songqiang
 * @Description
 * @Date 2020/6/24 14:37
 **/
public class ChargeRuleUnit {
    private DurationChargeRule duractionRule = null;
    private PeriodChargeRule periodRule = null;

    public boolean isFreeSection() {
        if (this.duractionRule != null) {
            List<DurationUnit> dList = this.duractionRule.getDuractionList();
            for (DurationUnit vo : dList) {
                if (vo.getUnitCost() > 0.0D) {
                    return false;
                }
            }
        }
        if (this.periodRule != null) {
            List<PeriodUnit> pList = this.periodRule.getPeroidList();
            for (PeriodUnit vo : pList) {
                if (vo.getUnitCost() > 0.0D) {
                    return false;
                }
            }
        }
        return true;
    }


    public double getCost(Interval interv) {
        double total = 0.0D;

        if (interv == null) {
            return total;
        }

        if (getDuractionRule() != null) {
            int parkLength = (int) (interv.getEndMillis() - interv.getStartMillis()) / 60000;
            total = getDuractionRule().getCost(parkLength);
        } else {

            DateTime start = interv.getStart();
            DateTime end = interv.getEnd();
            total = getPeriodRule().getCost(start, end);
        }
        return total;
    }

    /**
     * 根据时间的交集计算当前配置需要借的时间 Description :
     *
     * @return
     */
    public long queryDistance2Entire(Interval interv) {
        long needBorrowTime = 0L;
        // 按时长计算
        if (getDuractionRule() != null) {
            int parkLength = (int) (interv.getEndMillis() - interv.getStartMillis()) / 60000;
            needBorrowTime = getDuractionRule().queryDistance2Entire(parkLength);
        } else {
            DateTime start = interv.getStart();
            DateTime end = interv.getEnd();
            needBorrowTime = getPeriodRule().queryDistance2Entire(start, end);
        }
        return needBorrowTime;
    }


    public DurationChargeRule getDuractionRule() {
        return this.duractionRule;
    }


    public void setDuractionRule(DurationChargeRule duractionRule) {
        this.duractionRule = duractionRule;
    }


    public PeriodChargeRule getPeriodRule() {
        return this.periodRule;
    }


    public void setPeriodRule(PeriodChargeRule periodRule) {
        this.periodRule = periodRule;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.duractionRule != null) {
            sb.append("[按时长计费]===\n");
            sb.append(this.duractionRule.toString());
        }
        if (this.periodRule != null) {
            sb.append("[按时段计费]===\n");
            sb.append(this.periodRule.toString());
        }
        return sb.toString();
    }
}


