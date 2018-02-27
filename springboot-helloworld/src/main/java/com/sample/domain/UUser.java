package com.sample.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class UUser {
    private Long id;//
    private String nickname;//用户昵称
    private String account;//邮箱|登录帐号
    private String pswd;//密码
    private Date createTime;//创建时间
    private Date lastLoginTime;//最后登录时间
    private Long status;//1:有效，0:禁止登录

    private List<String> roleStrlist;
    private List<String> perminsStrlist;
}
