package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "client_role")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ClientRole {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    private String roleName;
    private String roleContent;
    private String roleMenu;
}
