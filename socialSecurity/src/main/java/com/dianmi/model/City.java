package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class City {

    private Integer cId;

    private String province;

    private String name;

    private String shebaoDateStr;

    private String shebaoRule;

    private String gongjijinDateStr;

    private String gongjijinRule;

    public City(Integer cId, String province, String name, String shebaoDateStr, String shebaoRule, String gongjijinDateStr, String gongjijinRule) {
        this.cId = cId;
        this.province = province;
        this.name = name;
        this.shebaoDateStr = shebaoDateStr;
        this.shebaoRule = shebaoRule;
        this.gongjijinDateStr = gongjijinDateStr;
        this.gongjijinRule = gongjijinRule;
    }
}