package com.dianmi.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.dianmi.model.User;
import com.dianmi.utils.json.ResultJson;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import com.dianmi.model.owndetailshow.SupplierParticularsShow;
import org.springframework.web.multipart.MultipartFile;

public interface SupplierCostService {

	public ResultJson readSupplierSocialInfo(MultipartFile file, Integer getUId, Integer supplierId, String yearMonth) throws Exception;

	public  Map<String, Object> selectSupplierCostAll(Integer supplierId,String month,Integer currPage,Integer pageSize);

	public String selectCityBySupplierId(String yearMonth, Integer supplierId);

	public List<SupplierParticularsShow> selectSupplierCostDetails(Integer supplierId, String yearMonth, Integer pageSize,Integer currPage);

	public String writeSupplierCostDetails(Integer supplierId, String yearMonth) throws IOException ;

	ResultJson deleteSupplierCostDetails(Integer supplierId, String yearMonth);

    ResultJson supplierCostPageList(Integer currPage, Integer pageSize, String yearMonth, String paramName);

	ResultJson supplierCostStatistics(String yearMonth, User user);
}

