package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "log_free_go")
public class LogFreeGo {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;
    private String carno;
    private String orderNo;
    private Date inTime;
    private Date outTime;
    private Date logTime;
    private String userId;
    private String userName;
}
