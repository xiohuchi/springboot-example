package com.sample.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class URolePermission {
    private Long id;
    private Long rid;//角色ID
    private Long pid;//权限ID
}
