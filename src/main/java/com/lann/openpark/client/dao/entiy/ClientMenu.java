package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "client_menu")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ClientMenu {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    private String mName;
    private String mIndex;
    private String mType;
    private String mClass;
    private String mParent;
    private String mOrder;
    private String mYxbz;
}
