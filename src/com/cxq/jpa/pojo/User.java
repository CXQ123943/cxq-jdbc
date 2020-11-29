package com.cxq.jpa.pojo;

import com.cxq.annotation.Column;
import com.cxq.annotation.Id;
import com.cxq.annotation.Table;

import java.io.Serializable;

/**
 * @author CXQ
 * @version 1.0
 * */

@Table("userInfo")
public class User implements Serializable {
    @Id
    private Integer id;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "user_age", type = "integer", length = 10)
    private String userAge;
}