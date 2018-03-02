package com.dianmi.controller;

import com.dianmi.service.CityService;
import com.dianmi.utils.json.ResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/city/")
public class CityController {
    
    @Autowired
    private CityService cityService;

    /**
     * 查询所有福利套信息(分页查询)
     *
     * @param pageSize
     * @param currPage
     * @return
     */
    @PostMapping("pageList")
    public ResultJson pageList(@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "currPage", defaultValue = "1") Integer currPage) {
        return cityService.pageList(pageSize, currPage);
    }

    /**
     * 保存福利套信息
     *
     * @param cityStr
     * @return
     */
    @PostMapping("saveEntity")
    public ResultJson saveEntity(String cityStr) {
        return cityService.saveEntity(cityStr);
    }

    /**
     * 根据ID获取福利套信息
     *
     * @param id
     * @return
     */
    @PostMapping("getByIdEntity")
    public ResultJson getByIdEntity(Integer id) {
        return cityService.getByIdEntity(id);
    }


    /**
     * 删除福利套信息
     *
     * @param id
     * @return
     */
    @DeleteMapping("deleteEntity")
    public ResultJson deleteEntity(@RequestParam(value = "id") Integer id) {
        return cityService.deleteEntity(id);
    }
}
