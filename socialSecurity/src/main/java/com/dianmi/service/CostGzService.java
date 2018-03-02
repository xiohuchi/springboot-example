package com.dianmi.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import com.dianmi.model.CostGz;
import com.dianmi.model.accumulation.GZAccumulation;
import com.dianmi.model.owndetailshow.GZDetailShow;
import com.dianmi.model.ownhome.SZLDDGShow;
import com.dianmi.utils.json.ResultJson;

//广州自有户
public interface CostGzService {

	/**
	 * @param file
	 * @param userId
	 * @param yearMonth
	 *            导入社保费用
	 */
	ResultJson importCost(MultipartFile file, Integer userId,Integer supplierId, String yearMonth);

	/**
	 * @param file
	 * @param userId
	 * @param yearMonth
	 *            导入公积金费用
	 */
	ResultJson importGongjijin(MultipartFile file, Integer userId,Integer supplierId, String yearMonth);

	/**
	 * 读取广州自有户扣费信息
	 */
	List<CostGz> readGZSocialInfo(String filePath, Integer userId, Integer customerId, String yearMonth);

	List<SZLDDGShow> selectGZhomeAll(Integer supplierId, String month, Integer currPage, Integer pageSize);

	List<GZDetailShow> selectGZInfoAll(Integer supplierId, String yearMonth, Integer currPage, Integer pageSize);

	List<GZAccumulation> readGZAccumulationFee(String filePath, Integer getUId, Integer supplierId, String yearMonth,
			String city) throws EncryptedDocumentException, InvalidFormatException, FileNotFoundException, IOException;

	String writeGZAccumulationFee(Integer supplierId, String yearMonth) throws IOException;

	String writeGZInfoAll(Integer supplierId, String yearMonth) throws IOException;

	String deleteGZInfoAll(Integer supplierId, String yearMonth);

}
