package com.dianmi.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import com.dianmi.model.accumulation.ZHAccumulation;
import com.dianmi.model.owndetailshow.ZHDetailShow;
import com.dianmi.model.ownhome.ZHShow;
import com.dianmi.utils.json.ResultJson;

public interface CostZhService {

	/**
	 * @param file
	 * @param userId
	 * @param yearMonth
	 *            導入社保費用
	 */
	ResultJson importCost(MultipartFile file, Integer userId,Integer supplierId, String yearMonth);

	/**
	 * @param file
	 * @param userId
	 * @param yearMonth
	 *            導入公積金
	 */
	ResultJson importGongjijin(MultipartFile file, Integer userId,Integer supplierId, String yearMonth);

	List<ZHShow> selectZHhomeAll(Integer supplierId, String month, Integer currPage, Integer pageSize);

	List<ZHDetailShow> selectZHInfoAll(Integer supplierId, String yearMonth, Integer currPage, Integer pageSize);

	List<ZHAccumulation> readZHAccumulationFee(String filePath, Integer getUId, Integer supplierId, String yearMonth,
			String city) throws EncryptedDocumentException, InvalidFormatException, FileNotFoundException, IOException;

	String writeZHAccumulationFee(Integer supplierId, String yearMonth) throws ParseException, IOException;

	String writeZHInfoAll(Integer supplierId, String yearMonth) throws IOException;

	String deleteZHInfoAll(Integer supplierId, String yearMonth);

}
