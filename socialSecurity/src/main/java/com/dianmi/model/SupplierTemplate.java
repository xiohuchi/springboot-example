package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SupplierTemplate {
    private Integer id;

    private Integer userId;

    private Integer supplierId;

    private String templateName;

    private String filePath;

    public SupplierTemplate(Integer id, Integer userId, Integer supplierId, String templateName, String filePath) {
        this.id = id;
        this.userId = userId;
        this.supplierId = supplierId;
        this.templateName = templateName;
        this.filePath = filePath;
    }

}