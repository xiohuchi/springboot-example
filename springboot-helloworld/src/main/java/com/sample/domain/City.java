package com.sample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 城市实体类
 * <p>
 * Created by bysocket on 07/02/2017.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class City {
    private Long id;
    private Long provinceId;
    private String cityName;
    private String description;
}
