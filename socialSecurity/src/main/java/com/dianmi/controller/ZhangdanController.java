package com.dianmi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.model.ZhangdanMingxi;
import com.dianmi.model.po.FenzuZhangdanPo;
import com.dianmi.model.po.ZhangDanMingxiPo;
import com.dianmi.model.po.ZongZhangdanPo;
import com.dianmi.service.ZhangdanService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * created by www 2017/10/25 17:34
 */
@RestController
@RequestMapping("/api/bill")
public class ZhangdanController extends BaseController {

	@Autowired
	private ZhangdanService zhangdanService;

	/**
	 * 供应商账单
	 * 
	 * @param yearMonth
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	@PostMapping("/supplierBill")
	public ResultJson supplierBill(String yearMonth, Integer currPage, Integer pageSize, String supplierName) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || currPage == null || pageSize == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			if (StringUtils.isEmpty(supplierName)) {
				supplierName = "";
			}
			List<ZongZhangdanPo> list = zhangdanService.supplierZhangdan(yearMonth, user().getUId(), supplierName,
					user().getRoleType());
			PageInfo<ZongZhangdanPo> pageInfo = new PageInfo<>(list);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, pageInfo);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	@PostMapping("/customerBill")
	public ResultJson customerBill(String yearMonth, Integer currPage, Integer pageSize, String customerName) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || currPage == null || pageSize == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			if (StringUtils.isEmpty(customerName)) {
				customerName = "";
			}
			PageHelper.startPage(currPage, pageSize);
			List<ZongZhangdanPo> list = zhangdanService.customerZhangdan(yearMonth, user().getUId(), customerName,
					user().getRoleType());
			PageInfo<ZongZhangdanPo> pageInfo = new PageInfo<>(list);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, pageInfo);
		}
		return resultJson;
	}

	/**
	 * @param reportingPeriod
	 * @param supplierId
	 * @return
	 */
	@PostMapping("/groupByCustomer")
	public ResultJson groupByCustomer(String yearMonth, Integer supplierId, Integer currPage, Integer pageSize) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || supplierId == null || currPage == null || pageSize == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			List<FenzuZhangdanPo> list = zhangdanService.groupByCustomer(yearMonth, supplierId);
			PageInfo<FenzuZhangdanPo> pageInfo = new PageInfo<>(list);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, pageInfo);
		}
		return resultJson;
	}

	/**
	 * @param reportingPeriod
	 * @param customerId
	 * @return
	 */
	@PostMapping("/groupBySupplier")
	public ResultJson groupBySupplier(String yearMonth, Integer customerId, Integer currPage, Integer pageSize) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || customerId == null || currPage == null || pageSize == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			List<FenzuZhangdanPo> list = zhangdanService.groupBySupplier(yearMonth, customerId);
			PageInfo<FenzuZhangdanPo> pageInfo = new PageInfo<>(list);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, pageInfo);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @param supplierId
	 * @param customerId
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	@PostMapping("/supplierMingxi")
	public ResultJson supplierMingxi(String yearMonth, Integer supplierId, Integer currPage, Integer pageSize) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || supplierId == null || currPage == null || pageSize == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			List<ZhangDanMingxiPo> list = zhangdanService.supplierZhangdanMingxi(yearMonth, supplierId);
			PageInfo<ZhangDanMingxiPo> pageInfo = new PageInfo<>(list);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, pageInfo);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @param customerId
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	@PostMapping("/customerMingxi")
	public ResultJson mincustomerMingxigxi(String yearMonth, Integer customerId, Integer currPage, Integer pageSize) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || customerId == null || currPage == null || pageSize == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			List<ZhangDanMingxiPo> list = zhangdanService.customerZhangdanMingxi(yearMonth, customerId);
			PageInfo<ZhangDanMingxiPo> pageInfo = new PageInfo<>(list);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, pageInfo);
		}
		return resultJson;
	}

	/**
	 * @param zdId
	 * @return
	 */
	@PostMapping("/toEdit")
	public ResultJson toEdit(Integer zdId) {
		ResultJson resultJson;
		if (zdId == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			ZhangdanMingxi zhangdanMingxi = zhangdanService.getById(zdId);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, zhangdanMingxi);
		}
		return resultJson;
	}

	/**
	 * @param zhangdanMingxiStr
	 * @return
	 */
	@PutMapping("/update")
	public ResultJson update(String zhangdanMingxiStr) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(zhangdanMingxiStr)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			zhangdanService.update(zhangdanMingxiStr);
			resultJson = ResultUtil.success(RestEnum.SUCCESS);
		}
		return resultJson;
	}

	/**
	 * @param zmId
	 * @return
	 */
	@DeleteMapping("/delete")
	public ResultJson delete(Integer zmId) {
		ResultJson resultJson;
		if (null == zmId) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			Map<String, Integer> map = new HashMap<String, Integer>();
			int deleteResult = zhangdanService.delete(zmId);
			map.put("result", deleteResult);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, map);
		}
		return resultJson;
	}

}