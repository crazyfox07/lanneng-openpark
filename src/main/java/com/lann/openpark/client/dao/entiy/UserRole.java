package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "user_role")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class UserRole {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;

    private String userId;
    private String roleId;
}
