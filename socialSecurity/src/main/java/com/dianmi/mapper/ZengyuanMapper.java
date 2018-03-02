package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.Zengyuan;
import com.dianmi.model.po.ZengyuanPo;

public interface ZengyuanMapper {
	int deleteByPrimaryKey(Integer zyId);

	int insert(Zengyuan record);

	int insertSelective(Zengyuan record);

	Zengyuan selectByPrimaryKey(Integer zyId);

	int updateByPrimaryKeySelective(Zengyuan record);

	int updateByPrimaryKey(Zengyuan record);

	List<Map<String, Object>> selectIsExistsByCertificateNumber(@Param("city") String city,
			@Param("reportingPeriod") String reportingPeriod, @Param("certificateNumber") String certificateNumber);

	int updateStatusToConfirm(@Param("userId") int userId, @Param("operationRecordId") int operationRecordId);

	List<Map<String, Object>> selectSocialMsg(@Param("userId") int userId,
			@Param("operationRecordId") int operationRecordId);

	int updateFuwufanganMsg(@Param("supplierId") int supplierId, @Param("fuwufanganId") int fuwufanganId,
			@Param("reportingPeriod") String reportingPeriod, @Param("city") String city,
			@Param("certificateNumber") String certificateNumber);

	List<ZengyuanPo> zengyuanPaging(@Param("reportingPeriod") String reportingPeriod,
			@Param("paramName") String paramName, @Param("supplierId") int supplierId);

	int countZengyuanTotal(@Param("reportingPeriod") String reportingPeriod, @Param("supplierId") int supplierId);

	List<ZengyuanPo> selectBySupplierMsg(@Param("reportingPeriod") String reportingPeriod,
			@Param("supplierId") int supplierId);

	int updateStatus(@Param("zyId") Integer zyId);

	int deleteById(@Param("zyId") int zyId);

	List<Zengyuan> selectZengyuanAll();

	void updateAddStatus(@Param("certificateNumber") String certificateNumber, @Param("city") String city,
			@Param("reportingPeriod") String month);

	String selectAddEmployeeName(@Param("city") String city, @Param("supplierId") Integer supplierId,
			@Param("reportingPeriod") String month, @Param("certificateNumber") String certificateNumber);

	void updateAddSocialStatus(@Param("certificateNumber") String certificateNumber,
			@Param("reportingPeriod") String month, @Param("city") String city);

	int amount(@Param("yearMonth") String yearMonth, @Param("supplierId") int supplierId, @Param("type") int type);

	List<ZengyuanPo> selectBySupplierId(@Param("reportingPeriod") String reportingPeriod,
			@Param("supplierId") int supplierId);

	int emailAmount(@Param("yearMonth") String yearMonth, @Param("type") int type, @Param("userId") int userId,
			@Param("roleType") int roleType);

	int zengyuanAmount(@Param("yearMonth") String yearMonth, @Param("userId") int userId,
			@Param("roleType") int roleType);

	int notMatchSupplierAmount(String yearMonth);

	List<Map<String, String>> allCity(@Param("yearMonth") String yearMonth, @Param("userId") int userId,
			@Param("roleType") int roleType);

	List<ZengyuanPo> selectByCity(@Param("yearMonth") String yearMonth, @Param("city") String city,
			@Param("userId") int userId, @Param("roleType") int roleType);

	List<Map<String, String>> allDept(@Param("yearMonth") String yearMonth, @Param("userId") int userId,
			@Param("roleType") int roleType);

	List<ZengyuanPo> selectByDept(@Param("yearMonth") String yearMonth, @Param("userId") int userId,
			@Param("roleType") int roleType, @Param("deptName") String deptName);

	int zengyuanFaild(@Param("yearMonth") String yearMonth, @Param("city") String city,
			@Param("certificateNumber") String certificateNumber);

	List<ZengyuanPo> isSendEmailZengyuan(@Param("yearMonth") String yearMonth,
			@Param("isSendEmail") Integer isSendEmail, @Param("userId") Integer userId,
			@Param("roleType") Byte roleType);

	List<ZengyuanPo> noSupplierZengyuan(String yearMonth);

	/**
	 * @param yearMonth
	 * @param supplierId
	 * @return
	 */
	List<String> getAllCertificateNumber(@Param("yearMonth") String yearMonth, @Param("supplierId") Integer supplierId);

	List<ZengyuanPo> ziyouhuZengyuan(Integer userId);

	/**
	 * @param yearMonth
	 * @param city
	 * @return
	 */
	List<Zengyuan> getAllByCity(@Param("yearMonth") String yearMonth, @Param("city") String city,@Param("type")Integer type);
	
	int updateExportStatus(int zyId);
}