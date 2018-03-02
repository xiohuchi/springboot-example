package com.dianmi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianmi.mapper.KefuDeptMapper;
import com.dianmi.model.KefuDept;
import com.dianmi.service.KefuDeptService;

@Service
public class KefuDeptServiceImpl implements KefuDeptService {

	@Autowired
	private KefuDeptMapper kefuDeptMapper;

	/**
	 * 所有客服部门信息
	 * 
	 * @return
	 */
	public List<KefuDept> selectDepts() {
		return kefuDeptMapper.selectDepts();
	}

}
