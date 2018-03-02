package com.dianmi.controller;

import com.dianmi.service.CustomerService;
import com.dianmi.utils.json.ResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * created by www 2017/10/20 19:20
 */
@RestController
@RequestMapping("/api/customer/")
public class CustomerController extends BaseController {

    @Autowired
    private CustomerService customerService;

    /**
     * @return
     */
    @PostMapping("/all")
    public ResultJson all() {
        return customerService.getAllCustomer();
    }


    @PostMapping("pageList")
    public ResultJson pageList(@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "currPage", defaultValue = "1") Integer currPage) {
        return customerService.pageList(pageSize, currPage);
    }


    /**
     * 保存客户信息
     *
     * @param customerStr
     * @return
     */
    @PostMapping("saveEntity")
    public ResultJson saveEntity(String customerStr) {
        return customerService.saveEntity(customerStr);
    }


    /**
     * 根据ID获取客户信息
     *
     * @param id
     * @return
     */
    @PostMapping("getByIdEntity")
    public ResultJson getByIdEntity(Integer id) {
        return customerService.getByIdEntity(id);
    }


    /**
     * 更新客户信息
     *
     * @param customerStr
     * @return
     */
    @PutMapping("updateEntity")
    public ResultJson updateEntity(String customerStr){
        return customerService.updateEntity(customerStr);
    }


    /**
     * 删除客户信息
     *
     * @param id
     * @return
     */
    @DeleteMapping("deleteEntity")
    public ResultJson deleteEntity(Integer id) {
        return customerService.deleteEntity(id);
    }

}