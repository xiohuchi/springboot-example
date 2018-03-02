package com.dianmi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.model.po.FuwufanganPo;
import com.dianmi.service.FuwufanganService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @Author:create by lzw
 * @Date:2017年12月4日 下午4:14:08
 * @Description:
 */
@RestController
@RequestMapping("/app")
public class AppController {

	@Autowired
	private FuwufanganService fuwufanganService;

	@PostMapping("/fuwufangan")
	public ResultJson fuwufangan(String city, Integer pageSize, Integer currPage) {
		ResultJson resultJson;
		if (pageSize == null || currPage == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			if (StringUtils.isEmpty(city))
				city = "";
			List<FuwufanganPo> list = fuwufanganService.getAll(city);
			PageInfo<FuwufanganPo> pageInfo = new PageInfo<>(list);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, pageInfo);
		}
		return resultJson;
	}
}