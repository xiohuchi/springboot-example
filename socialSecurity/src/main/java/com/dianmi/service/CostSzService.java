package com.dianmi.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import com.dianmi.utils.json.ResultJson;

public interface CostSzService {

	/**
	 * @param file
	 * @param userId
	 * @param supplierType
	 * @param yearMonth
	 *            导入深圳社保费用数据
	 */
	ResultJson importCost(MultipartFile file, Integer userId,Integer supplierId, String yearMonth);

	/**
	 * @param file
	 * @param userId
	 * @param supplierType
	 * @param yearMonth
	 *            导入深圳公积金数据
	 */
	ResultJson importGongjijin(MultipartFile file, Integer userId,Integer supplierId, String yearMonth);

	/**
	 * 自有户深圳分页
	 */
	// List<SZLDDGShow> selectSZhomeAll(Integer supplierId, String yearMonth,
	// Integer currPage, Integer pageSize);

	/**
	 * 自有户详情深圳分页
	 */
	// List<SZDetailShow> selectSZInfoAll(Integer supplierId, String yearMonth,
	// Integer currPage, Integer pageSize);

	/**
	 * 导入公积金
	 */
	/*
	 * List<SZAccumulation> readSZAccumulationFee(String filePath, Integer
	 * getUId, Integer supplierId, String yearMonth, String city) throws
	 * EncryptedDocumentException, InvalidFormatException, IOException;
	 */

	// String writeSZInfoAll(Integer supplierId, String yearMonth)
	// throws IOException, EncryptedDocumentException, InvalidFormatException;

	String writeSZAccumulationInfo(Integer supplierId, String yearMonth)
			throws EncryptedDocumentException, InvalidFormatException, FileNotFoundException, IOException;

	String deleteSZInfoAll(Integer supplierId, String yearMonth);
	
	ResultJson costGroupBySupplier(String yearMonth);

}
