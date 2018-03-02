package com.dianmi.service;

import java.util.List;
import java.util.Map;

import com.dianmi.model.Supplier;
import com.dianmi.model.User;
import com.dianmi.utils.json.ResultJson;

public interface SupplierService {

	public List<Integer> selectSupplierByUserId(int userId);

	public List<Supplier> getSuppliers(String supplierName, int userId, int roleType);

	public List<Map<String, Object>> selectByCity(String city);

	ResultJson pageList(User user, Integer currPage, Integer pageSize);

    ResultJson saveEntity(String supplierStr);

	ResultJson updateEntity(String supplierStr);

	ResultJson deleteEntity(Integer id);

	ResultJson getByIdEntity(Integer id);
}
