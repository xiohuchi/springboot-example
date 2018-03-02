package com.dianmi.controller;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.model.Fuwufangan;
import com.dianmi.model.po.FuwufanganPo;
import com.dianmi.service.FuwufanganService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fuwufangan")
public class FuwuFanganController extends BaseController {

    @Autowired
    private FuwufanganService fuwufanganService;

    @PostMapping("/paging")
    public ResultJson paging(String city, Integer pageSize, Integer currPage) {
        ResultJson resultJson;
        if (pageSize == null || currPage == null) {
            resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
        } else {
            PageHelper.startPage(currPage, pageSize);
            if (StringUtils.isEmpty(city))
                city = "";
            List<FuwufanganPo> list = fuwufanganService.getAll(city);
            PageInfo<FuwufanganPo> pageInfo = new PageInfo<>(list);
            resultJson = ResultUtil.success(RestEnum.SUCCESS, pageInfo);
        }
        return resultJson;
    }

    /**
     * @return
     */
    @PostMapping("getSupplier")
    public ResultJson getSupplier() {
        List<Map<String, Object>> list = fuwufanganService.getSuppliers(user().getUId());
        return ResultUtil.success(RestEnum.SUCCESS, list);
    }

    /**
     * @param fuwufanganStr
     * @return
     */
    @PostMapping("/add")
    public ResultJson add(String fuwufanganStr) {
        ResultJson resultJson;
        if (StringUtils.isEmpty(fuwufanganStr)) {
            resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
        } else {
            fuwufanganService.add(fuwufanganStr);
            resultJson = ResultUtil.success(RestEnum.SUCCESS);
        }
        return resultJson;
    }

    /**
     * @param fwId
     * @return
     */
    @PostMapping("/toEdit")
    public ResultJson toEdit(Integer fwId) {
        ResultJson resultJson;
        if (null == fwId) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        Fuwufangan fuwufangan = fuwufanganService.getById(fwId);
        resultJson = ResultUtil.success(RestEnum.SUCCESS, fuwufangan);
        return resultJson;
    }

    /**
     * @param fuwufanganStr
     * @return
     */
    @PutMapping("/update")
    public ResultJson update(String fuwufanganStr) {
        ResultJson resultJson;
        if (StringUtils.isEmpty(fuwufanganStr)) {
            resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
        } else {
            fuwufanganService.update(fuwufanganStr);
            resultJson = ResultUtil.success(RestEnum.SUCCESS);
        }
        return resultJson;
    }

    /**
     * @param fwId
     * @return
     */
    @DeleteMapping("/delete")
    public ResultJson delete(Integer fwId) {
        ResultJson resultJson;
        if (null == fwId) {
            resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
        } else {
            fuwufanganService.delete(fwId);
            resultJson = ResultUtil.success(RestEnum.SUCCESS);
        }
        return resultJson;
    }

}