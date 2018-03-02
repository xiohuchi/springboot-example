package com.dianmi.service;

import com.dianmi.model.User;
import com.dianmi.utils.json.ResultJson;

public interface QingkuanService {

    ResultJson dianfuPageList(Integer currPage, Integer pageSize, String yearMonth, String customerName, User user);

    ResultJson applyDianfuList(Integer customerId, Integer supplierId, String yearMonth, User user);

    ResultJson qingkuanPageList(Integer currPage, Integer pageSize, String yearMonth, String supplierName, User user);

    ResultJson supplierZhangdanMingxi(Integer currPage, Integer pageSize, Integer supplierId, String yearMonth, User user);

    ResultJson zhangDanQingKuanDan(Integer currPage, Integer pageSize, Integer supplierId, String yearMonth, User user);

    ResultJson zhangDanDianfuDan(Integer currPage, Integer pageSize, Integer supplierId, String yearMonth, User user);

    ResultJson exportQingkuan(Integer supplierId, String yearMonth, User user);
}
