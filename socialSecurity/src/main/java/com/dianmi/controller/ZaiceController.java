package com.dianmi.controller;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.model.po.ZaicePo;
import com.dianmi.service.ZaiceService;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.DownloadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by www 2017/10/25 17:34
 */
@RestController
@RequestMapping("/api/zaice/")
@Slf4j
public class ZaiceController extends BaseController {

    @Autowired
    private ZaiceService zaiceService;

    @PostMapping("zaicePaging")
    public ResultJson zaicePaging(@RequestParam("type") Integer type,
                                  @RequestParam("yearMonth") String yearMonth,
                                  @RequestParam("paramName") String paramName,
                                  @RequestParam("pageSize") Integer pageSize,
                                  @RequestParam("currPage") Integer currPage,
                                  @RequestParam("customerId") Integer customerId) {
        ResultJson resultJson;
        if (StringUtils.isEmpty(yearMonth) || currPage == null || type == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        if (type != 1 && type != 2 && type != 3 && type != 4) {
            logger.error("参数错误！type:{}", type);
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }

        PageHelper.startPage(currPage, pageSize);
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(paramName))
            paramName = "";
        List<ZaicePo> list = zaiceService.zaicePaging(type, yearMonth, user().getUId(), paramName, null, customerId, null);
        PageInfo<ZaicePo> pageInfo = new PageInfo<>(list);
        map.put("zaicePaging", pageInfo);

        return ResultUtil.success(RestEnum.SUCCESS, map);
    }

    /**
     * 删除实际在册
     *
     * @param zcId
     * @return
     */
    @PostMapping("delete")
    public ResultJson delete(Integer zcId) {
        ResultJson resultJson;
        if (zcId == null) {
            resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
        } else {
            zaiceService.updateDeleteFlag(zcId);
            resultJson = ResultUtil.success(RestEnum.SUCCESS);
        }
        return resultJson;
    }

    /**
     * 导出在侧详情数据
     *
     * @param yearMonth
     * @param type       1、当月目标在册2、当月实际在册3、当月增员、4当月减员
     * @param supplierId 供应商ID
     * @param customerId 客户ID
     * @param cityName   城市名称
     * @return
     * @throws IOException
     */
    @PostMapping("export")
    public ResultJson exportZaice(@RequestParam("yearMonth") String yearMonth,
                                  @RequestParam("type") Integer type,
                                  @RequestParam(value = "supplierId", required = false) Integer supplierId,
                                  @RequestParam(value = "customerId", required = false) Integer customerId,
                                  @RequestParam(value = "cityName", required = false) String cityName) throws IOException {

        log.info("查询月份:{}", yearMonth, "供应商：{}, 城市:{}, 客户ID{}", supplierId, cityName, customerId);
        if (StringUtils.isEmpty(yearMonth) || null == type || (supplierId == null && customerId == null && StringUtils.isEmpty(cityName))) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        String fileName = type == 1 ? "目标在册" : type == 2 ?
                "实际在册" : type == 3 ? "当月增员" : type == 4 ? "当月减员" : "在册数据";

        String filePath = zaiceService.exportZaice(type, yearMonth, user().getUId(), supplierId, customerId, cityName);

        DownloadFile.downloadFile(filePath, fileName + yearMonth + ".xlsx");
        DeleteFile.DeleteFolder(filePath);
        return ResultUtil.success(RestEnum.SUCCESS);
    }

    /**
     * 导出在册汇总
     *
     * @param yearMonth
     * @param type
     * @return
     */
    @PostMapping("exportZaiceSummary")
    public ResultJson exportZaiceSummary(@RequestParam("yearMonth") String yearMonth,
                                         @RequestParam("type") Integer type) {
        return zaiceService.exportZaiceSummary(yearMonth, type, user());
    }

    /**
     * 下拉列表数据
     *
     * @param paramName
     * @return
     */
    @PostMapping("findCustomerAndSupplierAndCity")
    public ResultJson findCustomerAndSupplierAndCity(@RequestParam String paramName) {
        return zaiceService.findCustomerAndSupplierAndCity(paramName, user());
    }

    /**
     * 在册数据查询（增减，目标在册，实际在册员工）
     *
     * @param currPage
     * @param pageSize
     * @param customerName
     * @param type         1: 增员 2：减员 3：目标在册 4：实际在册
     * @param yearMonth
     * @return
     */
    @PostMapping("baseZaicePageList")
    public ResultJson baseZaicePageList(@RequestParam(value = "currPage", defaultValue = "1") Integer currPage,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                        @RequestParam(value = "customerName", required = false) String customerName,
                                        @RequestParam(value = "yearMonth") String yearMonth,
                                        @RequestParam(value = "type") Integer type) {
        return zaiceService.baseZaicePageList(currPage, pageSize, customerName, yearMonth, type, user());
    }

    /**
     * 在册（统计）
     *
     * @param yearMonth
     * @return
     */
    @PostMapping("zaiceStatistics")
    public ResultJson zaiceStatistics(@RequestParam(value = "yearMonth") String yearMonth) {
        return zaiceService.zaiceStatistics(yearMonth, user());
    }


}