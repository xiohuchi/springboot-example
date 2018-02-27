package com.sample.domain;

import lombok.Data;

@Data
public class UUserRole {
    private Long id;
    private Long uid;//用户ID
    private Long rid;//角色ID
}
