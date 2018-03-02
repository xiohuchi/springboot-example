package com.dianmi.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.dianmi.mapper.JianyuanMapper;
import com.dianmi.model.Jianyuan;
import com.dianmi.model.User;
import com.dianmi.model.po.JianyuanPo;
import com.dianmi.service.JianyuanService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * created by www 2017/10/19 15:22
 */
@Service
public class JianyuanServiceImpl implements JianyuanService {

	@Autowired
	private JianyuanMapper jianyuanMapper;

	public List<JianyuanPo> jianyuanPaging(String reportingPeriod, String paramName, int supplierId) {
		return jianyuanMapper.selectPaging(reportingPeriod, "%" + paramName + "%", supplierId);
	}

	public int deleteById(int jyId) {
		return jianyuanMapper.deleteById(jyId);
	}

	public Jianyuan findByZyId(int jyId) {
		return jianyuanMapper.selectByPrimaryKey(jyId);
	}

	public int update(String jianyuanStr) {
		jianyuanStr = jianyuanStr.substring(jianyuanStr.indexOf("{"), jianyuanStr.lastIndexOf("}") + 1);
		JSONObject jsonObject = JSONObject.parseObject(jianyuanStr);
		Jianyuan jianyuan = new Jianyuan(jsonObject.getInteger("jyId"), jsonObject.getInteger("userId"),
				jsonObject.getInteger("customerId"), jsonObject.getString("clientName"),
				jsonObject.getString("employeeName"), jsonObject.getString("certificateType"),
				jsonObject.getString("certificateNumber"), jsonObject.getString("city"),
				jsonObject.getString("dimissionReason"), jsonObject.getString("dimissionDate"),
				jsonObject.getString("socialSecurityStopDate"), jsonObject.getString("accumulationFundStopDate"),
				jsonObject.getString("supplier"), jsonObject.getString("remark"), jsonObject.getDate("importDate"),
				jsonObject.getString("reportingPeriod"), jsonObject.getByte("status"));
		return jianyuanMapper.updateByPrimaryKey(jianyuan);
	}

	@Override
	public String selectMinusEmployeeName(String city, Integer supplierId, String month, String certificateNumber) {
		return jianyuanMapper.selectMinusEmployeeName(city, supplierId, month, certificateNumber);
	}

	// 根据员工姓名修改状态
	public void updateMinusStatus(String certificateNumber, String month, String city) {
		jianyuanMapper.updateMinusStatus(certificateNumber, month, city);
	}

	@Override
	public void updateMinusSocialStatus(String certificateNumber, String city, String month) {
		jianyuanMapper.updateMinusSocialStatus(certificateNumber, city, month);
	}

	@Override
	public List<Jianyuan> selectJianyuanAll() {
		List<Jianyuan> list = jianyuanMapper.selectJianyuanAll();
		if (list == null) {
			return new ArrayList<>();
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dianmi.service.JianyuanService#amount(java.lang.String, int)
	 */
	public int[] amount(String yearMonth, int supplierId) {
		int[] amount = new int[2];
		amount[0] = jianyuanMapper.amount(yearMonth, supplierId, 1);
		amount[1] = jianyuanMapper.amount(yearMonth, supplierId, 2);
		return amount;
	}

	/**
	 * @param yearMonth
	 * @param roleType
	 * @param userId
	 * @return 用户所有减员数量
	 */
	public int[] emailAmount(String yearMonth, int roleType, int userId) {
		int[] amount = new int[2];
		amount[0] = jianyuanMapper.emailAmount(yearMonth, 1, roleType, userId);
		amount[1] = jianyuanMapper.emailAmount(yearMonth, 2, roleType, userId);
		return amount;
	}

	/**
	 * @param yearMonth
	 * @param isSendEmail
	 * @param user
	 * @param currPage
	 * @param pageSize
	 * @return 已处理未处理减员
	 */
	public ResultJson isSendEmailJianyuan(String yearMonth, Integer isSendEmail, User user, Integer currPage,
			Integer pageSize) {
		if (StringUtils.isEmpty(yearMonth) || null == isSendEmail || null == currPage || null == pageSize)
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		PageHelper.startPage(currPage, pageSize);
		List<JianyuanPo> list = jianyuanMapper.isSendEmailJianyuan(yearMonth, isSendEmail, user.getUId(),
				user.getRoleType());
		PageInfo<JianyuanPo> pageInfo = new PageInfo<>(list);
		return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
	}

	/**
	 * @param yearMonth
	 * @param isSendEmail
	 * @param user
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	public ResultJson noSupplierJianyuan(String yearMonth, Integer currPage, Integer pageSize) {
		if (StringUtils.isEmpty(yearMonth) || null == currPage || null == pageSize)
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		PageHelper.startPage(currPage, pageSize);
		List<JianyuanPo> list = jianyuanMapper.noSupplierJianyuan(yearMonth);
		PageInfo<JianyuanPo> pageInfo = new PageInfo<>(list);
		return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
	}

	/**
	 * @param yearMonth
	 * @param supplierId
	 * @return
	 */
	public List<String> getAllCertificateNumber(String yearMonth, Integer supplierId) {
		return jianyuanMapper.getAllCertificateNumber(yearMonth, supplierId);
	}

	public int jianyuanFaild(String yearMonth, String city, String certificateNumber) {
		return jianyuanMapper.jianyuanFaild(yearMonth, city, certificateNumber);
	}
	
	public int jianyuanSuccess(String yearMonth, String city, String certificateNumber) {
		return jianyuanMapper.jianyuanSuccess(yearMonth, city, certificateNumber);
	}

}