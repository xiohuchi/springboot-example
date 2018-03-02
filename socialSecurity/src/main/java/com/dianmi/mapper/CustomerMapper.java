package com.dianmi.mapper;

import com.dianmi.model.Customer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CustomerMapper {
    int deleteByPrimaryKey(Integer cId);

    int insert(Customer record);

    int insertSelective(Customer record);

    Customer selectByPrimaryKey(@Param("cId") Integer cId);

    int updateByPrimaryKeySelective(Customer record);

    int updateByPrimaryKey(Customer customer);

    List<Customer> getCustomerByUserId();

    Map findCustomerInfo(@Param("cId") Integer customerId);

    List<Integer> getIdByCustomerName(@Param("customerName") String customerName);

    List<Integer> getIdByDeptIdAndCustomerName(@Param("deptId") int deptId, @Param("customerName") String customerName);

    String findCustomerName(@Param("cId") Integer customerId);

    List<Map<String, Object>> getAllCustomer();

    List<Customer> pageList();


    List<Map<String, String>> customerSpinner(@Param("paramName") String paramName, @Param("userId") Integer userId, @Param("roleType") Byte roleType);

}