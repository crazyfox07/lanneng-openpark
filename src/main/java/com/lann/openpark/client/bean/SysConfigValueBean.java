package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SysConfigValueBean {
    public String domain_id;
    public String domain_key;
    public String open_epark_url;
    public String is_etc;
    public String etc_url;
    public String cars_limit;
    public String cars_limit_num;
    public String park_data_exp_days;
    public String file_upload_root;
    public String is_desk;
    public String serv_url;
    public String inner_pay;
    public String inner_pay_free_time;
    public String first_free;
    // public String first_free_time;

    public String cars_limit_white;// 车位数满之后，白名单车辆可以进入
    public String is_only_white_in;// 设置只有白名单车辆进入
    public String is_leaguer_fee;// 是否开启会员扣费
    public String is_park_card;// 是否开启平台优惠券、停车卡支付
    public String playform_failure;// 平台故障期间，免费放行
    public String abnormal_out;// 是否开启异常驶离控制
    public String abnormal_num;// 异常驶离次数阈值
    public String noin_noout;// 无驶入订单，出场不抬杆控制
    public String white_list_type;// 白名单方式，1普通白名单2客户白名单
    public String white_list_size;// 免费车辆数，0所有都免费，1一辆免费...
}
