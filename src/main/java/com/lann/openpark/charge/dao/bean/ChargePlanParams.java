package com.lann.openpark.charge.dao.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChargePlanParams {
    private String nid;
    private String planName;
    private String beginDate;
    private String endDate;
    private String planPriorityType;
    private String carTypes;
    private String chargePolicyId;
    private double policyPriority;
    private String regions;
}
