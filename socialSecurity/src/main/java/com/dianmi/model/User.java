package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private Integer uId;

    private String name;

    private String mobilePhone;

    private String password;

    private String department;

    private Byte roleType;

    private String email;

    public User(Integer uId, String name, String mobilePhone, String password, String department, Byte roleType, String email) {
        this.uId = uId;
        this.name = name;
        this.mobilePhone = mobilePhone;
        this.password = password;
        this.department = department;
        this.roleType = roleType;
        this.email = email;
    }


    @Override
    public String toString() {
        return "User{" +
                "uId=" + uId +
                ", name='" + name + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", password='" + password + '\'' +
                ", department='" + department + '\'' +
                ", roleType=" + roleType +
                ", email='" + email + '\'' +
                '}';
    }
}