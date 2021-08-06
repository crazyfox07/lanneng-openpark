package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "role_limit")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class RoleLimit {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;

    private String roleId;
    private String menuId;

}
