package com.dianmi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dianmi.mapper.SupplierMapper;
import com.dianmi.model.Supplier;
import com.dianmi.model.User;
import com.dianmi.service.SupplierService;
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

@Service
public class SupplierServiceImpl implements SupplierService {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private SupplierMapper supplierMapper;

    public List<Integer> selectSupplierByUserId(int userId) {
        return supplierMapper.selectSupplierByUserId(userId);
    }

    public List<Supplier> getSuppliers(String supplierName, int userId, int roleType) {
        return supplierMapper.selectSuppliers("%" + supplierName + "%", userId, roleType);
    }

    /**
     * 根据城市获取供应商信息
     *
     * @param city
     * @return
     */
    public List<Map<String, Object>> selectByCity(String city) {
        return supplierMapper.selectByCity("%" + city + "%");
    }


    /**
     * 获取供应商信息
     *
     * @param user
     * @param currPage
     * @param pageSize @return
     */
    @Override
    public ResultJson pageList(User user, Integer currPage, Integer pageSize) {
        logger.info("登录用户:{} " + user.getName() + "用户权限:{}" + user.getRoleType());
        PageHelper.startPage(currPage, pageSize);
        PageInfo<Supplier> supplierPageInfo = new PageInfo<>();
        if (user.getRoleType() == 2) { //专员（只查询自己的）
            supplierPageInfo.setList(supplierMapper.pageList(user.getUId()));
        }

        if (user.getRoleType() == 3) {//主管查所有的
            supplierPageInfo.setList(supplierMapper.pageList(null));
        }
        return ResultUtil.success(RestEnum.SUCCESS, supplierPageInfo);
    }

    @Override
    public ResultJson saveEntity(String SupplierString) {
        if (SupplierString == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        SupplierString = SupplierString.substring(SupplierString.indexOf("{"), SupplierString.lastIndexOf("}") + 1);
        int size = 0;
        try {
            // JSON 字符串转 Java对象
            Supplier Supplier = JSONObject.parseObject(SupplierString, Supplier.class);
            size = supplierMapper.insertSelective(Supplier);
        } catch (Exception e) {
            logger.error("SupplierServiceImpl @ saveEntity", e);
            throw new RuntimeException(e);
        }
        return size == 1 ? ResultUtil.success(RestEnum.SUCCESS) : ResultUtil.error(RestEnum.SAVE_FALL);
    }


    @Override
    public ResultJson getByIdEntity(Integer id) {
        if (id == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        Supplier supplier = supplierMapper.selectByPrimaryKey(id);
        if (supplier == null) {
            return ResultUtil.error(RestEnum.ENTITY_IS_EMPTY);
        }
        return ResultUtil.success(RestEnum.SUCCESS, supplier);
    }

    @Override
    public ResultJson updateEntity(String SupplierString) {
        if (SupplierString == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        SupplierString = SupplierString.substring(SupplierString.indexOf("{"), SupplierString.lastIndexOf("}") + 1);
        int size = 0;
        try {
            // JSON 字符串转 Java对象
            Supplier Supplier = JSONObject.parseObject(SupplierString, Supplier.class);
            size = supplierMapper.updateByPrimaryKey(Supplier);
        } catch (Exception e) {
            logger.error("SupplierServiceImpl @ updateEntity", e);
            throw new RuntimeException(e);
        }
        return size == 1 ? ResultUtil.success(RestEnum.SUCCESS) : ResultUtil.error(RestEnum.SAVE_FALL);
    }

    @Override
    public ResultJson deleteEntity(Integer id) {
        if (id == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        int size = supplierMapper.deleteByPrimaryKey(id);
        return size == 1 ? ResultUtil.success(RestEnum.SUCCESS) : ResultUtil.error(RestEnum.DELETE_FAIL);
    }


}