package com.dianmi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianmi.mapper.SupplierTemplateMapper;
import com.dianmi.model.SupplierTemplate;
import com.dianmi.service.SupplierTemplateService;

@Service
public class SupplierTemplateServiceImpl implements SupplierTemplateService {

	@Autowired
	private SupplierTemplateMapper supplierTemplateMapper;

	public List<SupplierTemplate> findAll() {
		return supplierTemplateMapper.selectAll();
	}

}
