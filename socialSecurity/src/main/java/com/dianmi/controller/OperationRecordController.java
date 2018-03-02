package com.dianmi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.model.OperationRecord;
import com.dianmi.service.OperationRecordService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * created by www 2017/10/23 11:23
 */
@RestController
@RequestMapping("/api/opt")
public class OperationRecordController extends BaseController {

	@Autowired
	private OperationRecordService operationRecordService;

	/**
	 * @param
	 * @return
	 * @description 用户当月所有操作记录
	 */
	@RequestMapping("/optRecords")
	public ResultJson userOperationRecord(String yearMonth, Integer currPage, Integer pageSize) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || currPage == null || pageSize == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			List<OperationRecord> oprationRecordList = operationRecordService.userOperationRecord(yearMonth,
					user().getUId());
			PageInfo<OperationRecord> pageInfo = new PageInfo<>(oprationRecordList);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, pageInfo);
		}
		return resultJson;
	}
}