package com.dianmi.controller;


import com.dianmi.service.FuLiTaoService;
import com.dianmi.utils.json.ResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 福利套信息 Controller
 */
@RestController
@RequestMapping("/api/fulitao/")
public class FuLiTaoController extends BaseController {

    @Autowired
    private FuLiTaoService fuLiTaoService;


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
        return fuLiTaoService.pageList(pageSize, currPage);
    }

    /**
     * 保存福利套信息
     *
     * @param fuLiTaoString
     * @return
     */
    @PostMapping("saveEntity")
    public ResultJson saveEntity(String fuLiTaoString) {
        return fuLiTaoService.saveEntity(fuLiTaoString);
    }

    /**
     * 根据ID获取福利套信息
     *
     * @param id
     * @return
     */
    @PostMapping("getByIdEntity")
    public ResultJson getByIdEntity(Integer id) {
        return fuLiTaoService.getByIdEntity(id);
    }


    /**
     * 更新福利套信息
     *
     * @param fuLiTaoString
     * @return
     */
    @PutMapping("updateEntity")
    public ResultJson updateEntity(String fuLiTaoString) {
        return fuLiTaoService.updateEntity(fuLiTaoString);
    }


    /**
     * 删除福利套信息
     *
     * @param id
     * @return
     */
    @DeleteMapping("deleteEntity")
    public ResultJson deleteEntity(@RequestParam(value = "id") Integer id) {
        return fuLiTaoService.deleteEntity(id);
    }


    /**
     * 导出福利套数据
     *
     * @return
     */
    @RequestMapping("exportEntity")
    public ResultJson exportEntity() {
        return fuLiTaoService.exportEntity();
    }


    /**
     * 导入福利套信息
     *
     * @return
     */
    @RequestMapping("importEntity")
    public ResultJson importEntity(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return fuLiTaoService.importEntity(file, request);
    }

}
