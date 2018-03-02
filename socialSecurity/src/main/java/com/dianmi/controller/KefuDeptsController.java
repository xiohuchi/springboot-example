package com.dianmi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dianmi.model.KefuDept;
import com.dianmi.service.KefuDeptService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;

@RestController
@RequestMapping("/api/dept")
public class KefuDeptsController extends BaseController {
	@Autowired
	private KefuDeptService kefuDeptService;

	/**
	 * 所有部门信息
	 * 
	 * @return
	 */
	@PostMapping("/all")
	public ResultJson allDepts() {
		List<KefuDept> list = kefuDeptService.selectDepts();
		return ResultUtil.success(RestEnum.SUCCESS, list);
	}

}
