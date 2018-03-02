package com.dianmi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.model.Supplier;
import com.dianmi.service.SupplierService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * created by www 2017/10/25 20:48
 */
@RestController
@RequestMapping("/api/supplier/")
public class SupplierController extends BaseController {
    @Autowired
    private SupplierService supplierService;

    /**
     * @param supplierName
     * @param currPage
     * @param pageSize
     * @return
     */
    @PostMapping("/paging")
    public ResultJson getSupplierByUserId(String supplierName, Integer currPage, Integer pageSize) {
        if (currPage == null || pageSize == null) {
            return ResultUtil.success(RestEnum.PARAMETER_ERROR);
        } else {
            PageHelper.startPage(currPage, pageSize);
            if (StringUtils.isEmpty(supplierName))
                supplierName = "";
            List<Supplier> list = supplierService.getSuppliers(supplierName, user().getUId(), user().getRoleType());
            PageInfo<Supplier> pageInfo = new PageInfo<>(list);
            return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
        }
    }

    /**
     * @param city
     * @return
     */
    @PostMapping("/getByCity")
    public ResultJson getSupplierByCity(String city) {
        ResultJson resultJson;
        if (StringUtils.isEmpty(city)) {
            resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
        } else {
            List<Map<String, Object>> list = supplierService.selectByCity(city);
            resultJson = ResultUtil.success(RestEnum.SUCCESS, list);
        }
        return resultJson;
    }


    /**
     * 查询供应商信息
     *
     * @return
     */
    @PostMapping("pageList")
    public ResultJson pageList(@RequestParam(value = "currPage", defaultValue = "1") Integer currPage,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return supplierService.pageList(user(), currPage, pageSize);
    }

    /**
     * 保存供应商信息
     *
     * @param supplierStr
     * @return
     */
    @PostMapping("saveEntity")
    public ResultJson saveEntity(String supplierStr) {
        return supplierService.saveEntity(supplierStr);
    }


    /**
     * 根据ID获取供应商信息
     *
     * @param id
     * @return
     */
    @PostMapping("getByIdEntity")
    public ResultJson getByIdEntity(Integer id) {
        return supplierService.getByIdEntity(id);
    }


    /**
     * 更新供应商信息
     *
     * @param supplierStr
     * @return
     */
    @PutMapping("updateEntity")
    public ResultJson updateEntity(String supplierStr) {
        return supplierService.updateEntity(supplierStr);
    }


    /**
     * 删除供应商信息
     *
     * @param id
     * @return
     */
    @DeleteMapping("deleteEntity")
    public ResultJson deleteEntity(@RequestParam(value = "id") Integer id) {
        return supplierService.deleteEntity(id);
    }





}