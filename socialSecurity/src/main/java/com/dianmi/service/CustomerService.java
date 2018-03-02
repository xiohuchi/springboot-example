package com.dianmi.service;

import com.dianmi.model.Customer;
import com.dianmi.utils.json.ResultJson;

import java.util.List;
import java.util.Map;

/**
 * created by www 2017/10/20 19:19
 */
public interface CustomerService {

    public List<Customer> getCustomerByUserId();

    public Map findCustomerInfo(Integer customerId);

    public List<Integer> getIdByCustomerName(String customerName);

    public Integer getIdByDeptIdAndCustomerName(int deptId, String customerName);

    public int insert(Customer record);

    public String findCustomerName(Integer customerId);

    ResultJson getAllCustomer();

    ResultJson pageList(Integer pageSize, Integer currPage);

    ResultJson saveEntity(String customerStr);

    ResultJson updateEntity(String customerStr);

    ResultJson deleteEntity(Integer id);

    ResultJson getByIdEntity(Integer id);
}