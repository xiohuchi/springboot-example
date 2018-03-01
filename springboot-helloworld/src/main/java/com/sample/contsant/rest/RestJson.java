package com.sample.contsant.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回json格式
 *
 * @param <T>
 * @author www
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestJson<T> {
    private Integer status;
    private String message;
    private T data;
}
