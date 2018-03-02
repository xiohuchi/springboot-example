package com.dianmi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dianmi.model.SupplierTemplate;
import com.dianmi.service.SupplierTemplateService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;

@RestController
@RequestMapping("/api/template")
public class SupplierTemplateController extends BaseController {

	@Autowired
	private SupplierTemplateService supplierTemplateService;

	/**
	 * @return
	 */
	@PostMapping("/all")
	public ResultJson findAll() {
		List<SupplierTemplate> list = supplierTemplateService.findAll();
		ResultJson resultJson = ResultUtil.success(RestEnum.SUCCESS, list);
		return resultJson;
	}

}