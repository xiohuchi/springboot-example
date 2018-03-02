package com.dianmi.service;

import java.util.List;
import java.util.Map;

import com.dianmi.model.Fuwufangan;
import com.dianmi.model.po.FuwufanganPo;

public interface FuwufanganService {
	public List<FuwufanganPo> getAll(String city);

	public List<Map<String, Object>> getSuppliers(int userId);

	public void add(String fuwufanganStr);

	public Fuwufangan getById(int fwId);

	public void update(String fuwufanganStr);

	public void delete(int fwId);

}
