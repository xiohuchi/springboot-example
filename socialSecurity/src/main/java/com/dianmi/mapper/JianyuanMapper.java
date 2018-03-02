package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.Jianyuan;
import com.dianmi.model.po.JianyuanPo;

public interface JianyuanMapper {
	int deleteByPrimaryKey(Integer jyId);

	int insert(Jianyuan record);

	int insertSelective(Jianyuan record);

	Jianyuan selectByPrimaryKey(Integer jyId);

	int updateByPrimaryKeySelective(Jianyuan record);

	int updateByPrimaryKey(Jianyuan record);

	List<Map<String, Object>> isExistsByCertificateNumber(@Param("city") String city,
			@Param("reportingPeriod") String reportingPeriod, @Param("certificateNumber") String certificateNumber);

	int updateStatusToConfirm(@Param("userId") int userId, @Param("operationRecordId") int operationRecordId);

	List<JianyuanPo> selectPaging(@Param("reportingPeriod") String reportingPeriod,
			@Param("paramName") String paramName, @Param("supplierId") int supplierId);

	int countJianyuanTotal(@Param("reportingPeriod") String reportingPeriod, @Param("supplierId") int supplierId);

	List<JianyuanPo> selectBySupplierMsg(@Param("reportingPeriod") String reportingPeriod,
			@Param("supplierId") int supplierId);

	int updateStatus(@Param("jyId") Integer jyId);

	int deleteById(@Param("jyId") Integer jyId);

	String selectMinusEmployeeName(@Param("city") String city, @Param("supplierId") Integer supplierId,
			@Param("reportingPeriod") String month, @Param("certificateNumber") String certificateNumber);

	void updateMinusStatus(@Param("certificateNumber") String certificateNumber, @Param("reportingPeriod") String month,
			@Param("city") String city);

	void updateMinusSocialStatus(@Param("certificateNumber") String certificateNumber, @Param("city") String city,
			@Param("reportingPeriod") String month);

	List<Jianyuan> selectJianyuanAll();

	int amount(@Param("yearMonth") String yearMonth, @Param("supplierId") int supplierId, @Param("type") int type);

	List<JianyuanPo> selectBySupplierId(@Param("reportingPeriod") String reportingPeriod,
			@Param("supplierId") int supplierId);

	int emailAmount(@Param("yearMonth") String yearMonth, @Param("type") int type, @Param("roleType") int roleType,
			@Param("userId") int userId);

	int jianyuanAmount(@Param("yearMonth") String yearMonth, @Param("userId") int userId,
			@Param("roleType") int roleType);

	int jianyuanFaild(@Param("yearMonth") String yearMonth, @Param("city") String city,
			@Param("certificateNumber") String certificateNumber);

	List<JianyuanPo> isSendEmailJianyuan(@Param("yearMonth") String yearMonth,
			@Param("isSendEmail") Integer isSendEmail, @Param("userId") Integer userId,
			@Param("roleType") Byte roleType);

	List<JianyuanPo> noSupplierJianyuan(String yearMonth);
	
	List<String> getAllCertificateNumber(@Param("yearMonth") String yearMonth, @Param("supplierId") Integer supplierId);
	
	int jianyuanSuccess(@Param("yearMonth") String yearMonth, @Param("city") String city,
			@Param("certificateNumber") String certificateNumber);
	
	List<Jianyuan> getAllByCity(@Param("yearMonth")String yearMonth,@Param("city")String city,@Param("type")Integer type);
	
	int updateExportStatus(int jyId);
}