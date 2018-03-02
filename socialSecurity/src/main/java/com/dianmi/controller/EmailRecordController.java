package com.dianmi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.model.EmailRecord;
import com.dianmi.service.EmailRecordService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * created by www 2017/10/25 17:36
 */
@RestController
@RequestMapping("/api/email")
public class EmailRecordController extends BaseController {

	@Autowired
	private EmailRecordService emailRecordService;

	@PostMapping("records")
	public ResultJson emailRecords(String yearMonth, Integer pageSize, Integer currPage) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || currPage == null || pageSize == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			Map<String, Object> map = new HashMap<>();
			List<EmailRecord> list = emailRecordService.emailRecords(yearMonth, user().getUId());
			PageInfo<EmailRecord> pageInfo = new PageInfo<>(list);
			map.put("emailRecords", pageInfo);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, map);
		}
		return resultJson;
	}
}
