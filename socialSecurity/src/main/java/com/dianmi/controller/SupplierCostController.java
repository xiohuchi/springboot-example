package com.dianmi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.druid.util.StringUtils;
import com.dianmi.common.OwnHomeUtils;
import com.dianmi.service.SupplierCostService;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.DownloadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * created by www
 * 2017/10/25 20:48
 * 供应商费用导入
 */
@RestController
@RequestMapping("/api")
public class SupplierCostController extends BaseController {

    @Autowired
    private SupplierCostService supplierCostService;

    @PostMapping("/supplier/importData")
    public ResultJson readDGHomeInfo(@RequestParam("file") MultipartFile file, Integer supplierId, String yearMonth) throws Exception {
        return supplierCostService.readSupplierSocialInfo(file, user().getUId(), supplierId, yearMonth);

    }


    @PostMapping("/supplierCost/showFee")
    public ResultJson findParticularsInfos(String yearMonth, Integer supplierId, Integer pageSize, Integer currPage) {
        try {
            Map<String, Object> pageCostList = null;
            if (StringUtils.isEmpty(yearMonth) || currPage == null || pageSize == null || supplierId == null) {
                return ResultUtil.success(RestEnum.PARAMETER_ERROR);
            }
            yearMonth = yearMonth.trim().replaceAll("—", "-").replaceAll("/", "-");
            //String city = supplierCostService.selectCityBySupplierId(yearMonth,supplierId);
            //if(city!=null && city!=""){
            pageCostList = supplierCostService.selectSupplierCostAll(supplierId, yearMonth, currPage, pageSize);
            //}else {
            //return ResultUtil.success(RestEnum.FAILD);
            //}
            if (pageCostList != null) {
                //String OwnJson = MAPPER.writeValueAsString(pageCostList);
                return ResultUtil.success(RestEnum.SUCCESS, pageCostList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ResultUtil.success(RestEnum.FAILD, "哎呀！出错了！");
    }

    @PostMapping("/supplierCost/feeDetail")
    public ResultJson findSupplierCostInfos(String yearMonth, Integer supplierId, Integer pageSize, Integer currPage) {
        //Integer supplierId = zaiceService.selectSupplierId();
        try {
            List pageOwnList = null;
            if (supplierId == null || StringUtils.isEmpty(yearMonth) || currPage == null || pageSize == null) {
                return ResultUtil.success(RestEnum.PARAMETER_ERROR);
            } else {
                yearMonth = yearMonth.trim().replaceAll("—", "-").replaceAll("/", "-");
                //if(city!=null && city!=""){
                pageOwnList = supplierCostService.selectSupplierCostDetails(supplierId, yearMonth, pageSize, currPage);
                //}else {
                //return ResultUtil.success(RestEnum.FAILD);
                //}
            }
            if (pageOwnList != null) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("pageList", pageOwnList);
                map.put("totalCount", pageOwnList.size());
                return ResultUtil.success(RestEnum.SUCCESS, map);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ResultUtil.success(RestEnum.FAILD, "哎呀！出错了！");
    }


    @PostMapping("supplier/costDetail/exportData")
    public ResultJson writeSupplierCostInfos(String yearMonth, Integer supplierId) {
        String fileName = "编号" + supplierId + "的供应商" + yearMonth + "月社保费用明细.xls";
        String filePath = null;
        try {
            String pageOwnList = null;
            if (supplierId == null || StringUtils.isEmpty(yearMonth)) {
                return ResultUtil.success(RestEnum.PARAMETER_ERROR);
            } else {
                yearMonth = yearMonth.trim().replaceAll("—", "-").replaceAll("/", "-");
                filePath = supplierCostService.writeSupplierCostDetails(supplierId, yearMonth);
            }
            if (filePath != null) {
                DownloadFile.downloadFile(filePath, fileName);
                DeleteFile.DeleteFolder(filePath);
                return ResultUtil.success(RestEnum.SUCCESS);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            DeleteFile.DeleteFolder(fileName);
            return ResultUtil.success(RestEnum.FAILD, "哎呀！出错了！");
        }
        return ResultUtil.success(RestEnum.FAILD, "哎呀！出错了！");
    }


    /**
     * 根据供应商ID删除供应商明细
     *
     * @param supplierId
     * @param yearMonth
     * @return
     */
    @PostMapping("/supplierFee/deleteInfos")
    public ResultJson deleteSupplierCostInfos(Integer supplierId, String yearMonth) {
        return supplierCostService.deleteSupplierCostDetails(supplierId, yearMonth);
    }


    /**
     * 供应商费用单分页
     *
     * @param yearMonth
     * @param paramName
     * @return
     */
    @PostMapping("/supplier/supplierCostPageList")
    public ResultJson supplierCostPageList(@RequestParam(value = "currPage", defaultValue = "1") Integer currPage,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam(value = "yearMonth") String yearMonth,
                                           @RequestParam(value = "paramName", required = false) String paramName) {
        return supplierCostService.supplierCostPageList(currPage, pageSize, yearMonth, paramName);
    }

    /**
     * 供应商费用单统计
     *
     * @param yearMonth
     * @return
     */
    @PostMapping("/supplier/supplierCostStatistics")
    public ResultJson supplierCostStatistics(String yearMonth) {
        return supplierCostService.supplierCostStatistics(yearMonth, user());
    }


}
