package com.dianmi.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.Jianyuan;
import com.dianmi.model.User;
import com.dianmi.model.po.JianyuanPo;
import com.dianmi.utils.json.ResultJson;

/**
 * created by www 2017/10/25 18:05
 */
public interface JianyuanService {

	public List<JianyuanPo> jianyuanPaging(String reportingPeriod, String paramName, int supplierId);

	public int deleteById(int jyId);

	public Jianyuan findByZyId(int jyId);

	public int update(String jianyuanStr);

	public String selectMinusEmployeeName(String city, Integer supplierId, String month, String certificateNumber);

	public void updateMinusStatus(String certificateNumber, String month, String city);

	public void updateMinusSocialStatus(String certificateNumber, String city, String month);

	public List<Jianyuan> selectJianyuanAll();

	public int[] amount(String yearMonth, int supplierId);

	public int[] emailAmount(String yearMonth, int roleType, int userId);
	
	public ResultJson isSendEmailJianyuan(String yearMonth, Integer isSendEmail, User user, Integer currPage,
			Integer pageSize);
	
	public ResultJson noSupplierJianyuan(String yearMonth, Integer currPage,
			Integer pageSize);
	
	public List<String> getAllCertificateNumber(String yearMonth,Integer supplierId);
	
	public int jianyuanFaild(String yearMonth, String city,String certificateNumber);
	
	public int jianyuanSuccess(String yearMonth, String city, String certificateNumber);

}