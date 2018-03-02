package com.dianmi.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.dianmi.model.User;
import com.dianmi.model.Zengyuan;
import com.dianmi.model.po.ZengyuanPo;
import com.dianmi.utils.json.ResultJson;

/**
 * created by www 2017/10/20 15:58
 */
public interface ZengyuanService {

	public String importDate(MultipartFile file, int userId, int deptId, String yearMonth);

	public List<ZengyuanPo> zengyuanPaging(String reportingPeriod, String paramName, int supplierId);

	public List<Map<String, Object>> selectSupplier(String reportingPeriod, int userId, int roleType);

	public Map<String, Object> currMonthTotalZengjianyuan(String reportingPeriod, int supplierId, int userId,
			byte type);

	public String sendEmailToSupplier(String reportingPeriod, int supplierId, User user);

	public int deleteById(int zyId);

	public List<Zengyuan> selectZengyuanAll();

	public void updateAddStatus(String certificateNumber, String city, String month);

	public void updateAddSocialStatus(String certificateNumber, String month, String city);

	public Zengyuan getByZyId(int zyId);

	public int update(String zengyuanStr);

	public String selectAddEmployeeName(String city, Integer supplierId, String month, String certificateNumber);

	public int[] amount(String yearMonth, int supplierId);

	public void updateSupplier(int fwId, String employeeInfo);

	public String exportZengjianyuan(String yearMonth, int supplierId);

	public int[] emailAmount(String yearMonth, int userId, int roleType);

	public int[] zengjianYuanAmount(String yearMonth, int userId, int roleType);

	public int notMatchSupplierAmount(String yearMonth);

	public List<Map<String, String>> allCity(String yearMonth, int userId, int roleType);

	public List<ZengyuanPo> selectByCity(String yearMonth, String city, int userId, int roleType);

	public List<Map<String, String>> allDept(String yearMonth, int userId, int roleType);

	public List<ZengyuanPo> selectByDept(String yearMonth, int userId, int roleType, String deptName);

	public ResultJson isSendEmailZengyuan(String yearMonth, Integer isSendEmail, User user, Integer currPage,
			Integer pageSize);

	public ResultJson noSupplierZengyuan(String yearMonth, Integer currPage, Integer pageSize);

	public int zengyuanFaild(String yearMonth, String city, String reportingPeriod);

	/**
	 * @param yearMonth
	 * @param supplierId
	 * @return
	 */
	public List<String> getAllCertificateNumber(String yearMonth, Integer supplierId);
	
	public ResultJson export(String yearMonth,Integer cityType,Integer exportType)throws IOException;

}