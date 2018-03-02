package com.dianmi.service;

import java.io.IOException;

import com.dianmi.utils.json.ResultJson;

/**
 * @Author:create by lzw
 * @Date:2017年12月5日 上午10:51:05
 * @Description:
 */
public interface CostService {

	ResultJson cost(String yearMonth);

	ResultJson exportCost(String yearMonth, Integer type, Integer supplierId)throws IOException ;
}