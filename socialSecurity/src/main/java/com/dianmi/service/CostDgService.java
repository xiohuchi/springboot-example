package com.dianmi.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.dianmi.utils.json.ResultJson;

public interface CostDgService {

	ResultJson importCost(MultipartFile file, Integer userId, Integer supplierId, String yearMonth);

	ResultJson importGongjijin(MultipartFile file, Integer userId, Integer supplierId, String yearMonth);

	public String writeDGInfoAll(Integer supplierId, String yearMonth) throws IOException;

	public String deleteDGInfoAll(Integer supplierId, String yearMonth);

}
