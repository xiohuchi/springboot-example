package com.dianmi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dianmi.mapper.CustomerMapper;
import com.dianmi.model.Customer;
import com.dianmi.model.Fulitao;
import com.dianmi.service.CustomerService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * created by www 2017/10/20 19:17
 */
@SuppressWarnings("ALL")
@Service
public class CustomerServiceImpl implements CustomerService {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private CustomerMapper customerMapper;

    public List<Customer> getCustomerByUserId() {
        return customerMapper.getCustomerByUserId();
    }

    @Override
    public Map findCustomerInfo(Integer customerId) {
        return customerMapper.findCustomerInfo(customerId);
    }

    public List<Integer> getIdByCustomerName(String customerName) {
        return customerMapper.getIdByCustomerName("%" + customerName + "%");
    }

    public Integer getIdByDeptIdAndCustomerName(int deptId, String customerName) {
        List<Integer> list = customerMapper.getIdByDeptIdAndCustomerName(deptId, customerName);
        return list.size() == 0 ? null : list.get(0);
    }

    public int insert(Customer record) {
        return customerMapper.insertSelective(record);
    }

    @Override
    public String findCustomerName(Integer customerId) {
        return customerMapper.findCustomerName(customerId);
    }

    /**
     * @return
     */
    public ResultJson getAllCustomer() {
        return ResultUtil.success(RestEnum.SUCCESS, customerMapper.getAllCustomer());
    }


    @Override
    public ResultJson pageList(Integer pageSize, Integer currPage) {
        PageHelper.startPage(currPage, pageSize);
        PageInfo<Customer> pageInfo = new PageInfo<>(customerMapper.pageList());
        return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
    }

    @Override
    public ResultJson saveEntity(String customerStr) {
        if (customerStr == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        customerStr = customerStr.substring(customerStr.indexOf("{"), customerStr.lastIndexOf("}") + 1);
        int size = 0;
        try {
            // JSON 字符串转 Java对象
            Customer customer = JSONObject.parseObject(customerStr, Customer.class);
            size = customerMapper.insertSelective(customer);
        } catch (Exception e) {
            logger.error("CustomerServiceImpl @ saveEntity", e);
            throw new RuntimeException(e);
        }
        return size == 1 ? ResultUtil.success(RestEnum.SUCCESS) : ResultUtil.error(RestEnum.SAVE_FALL);
    }

    @Override
    public ResultJson getByIdEntity(Integer id) {
        if (id == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        Customer customer = customerMapper.selectByPrimaryKey(id);
        if (customer == null) {
            return ResultUtil.error(RestEnum.ENTITY_IS_EMPTY);
        }
        return ResultUtil.success(RestEnum.SUCCESS, customer);
    }

    @Override
    public ResultJson updateEntity(String customerStr) {
        if (customerStr == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        customerStr = customerStr.substring(customerStr.indexOf("{"), customerStr.lastIndexOf("}") + 1);
        int size = 0;
        try {
            // JSON 字符串转 Java对象
            Customer customer = JSONObject.parseObject(customerStr, Customer.class);
            size = customerMapper.updateByPrimaryKey(customer);
        } catch (Exception e) {
            logger.error("CustomerServiceImpl @ updateEntity", e);
            throw new RuntimeException(e);
        }
        return size == 1 ? ResultUtil.success(RestEnum.SUCCESS) : ResultUtil.error(RestEnum.SAVE_FALL);
    }

    @Override
    public ResultJson deleteEntity(Integer id) {
        if (id == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        int size = customerMapper.deleteByPrimaryKey(id);
        return size == 1 ? ResultUtil.success(RestEnum.SUCCESS) : ResultUtil.error(RestEnum.SAVE_FALL);
    }

}
