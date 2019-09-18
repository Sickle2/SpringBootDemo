package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @program: demo
 * @description:
 * @author: sickle
 * @create: 2019-09-17 19:26
 **/
@Entity
@Getter
@Setter
@ToString
@SQLDelete(sql = "update i_tenant_user set deleted = 1 where id = ?")
@Where(clause = "deleted = 0")
@Table(name = "i_user")
public class DemoUser extends BaseModel {
    private String username;
    private String phone;
    private String password;
    private Integer userType;
}
