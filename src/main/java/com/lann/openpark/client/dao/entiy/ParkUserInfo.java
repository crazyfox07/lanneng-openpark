package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 停车场用户信息
 *
 * @Author songqiang
 * @Description
 * @Date 2020/10/29 14:32
 **/
@Data
@ToString
@Entity
@Table(name = "park_user_info")
public class ParkUserInfo implements Serializable {
    @Id
    private String userid;
    private String password;
    private String username;
    private String phone;
    private String roles;
    private String description;
    private String userRole;
}
