package com.dianmi.controller;


import com.dianmi.service.QingkuanService;
import com.dianmi.utils.json.ResultJson;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qingkuan/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QingkuanController extends BaseController {

    @NonNull
    private QingkuanService qingkuanService;

    /**
     * 查询垫付列表
     *
     * @param currPage  开始页码
     * @param pageSize  查询条数
     * @param yearMonth 查询月份
     * @return
     */
    @PostMapping("dianfuPageList")
    public ResultJson dianfuPageList(@RequestParam(value = "currPage", defaultValue = "1") Integer currPage,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                     @RequestParam(value = "yearMonth") String yearMonth,
                                     @RequestParam(value = "customerName", required = false) String customerName) {
        return qingkuanService.dianfuPageList(currPage, pageSize, yearMonth, customerName, user());
    }

    /**
     * 申请垫付(客户账单详情)
     *
     * @return
     */
    @PostMapping("applyDianfuList")
    public ResultJson applyDianfuList(@RequestParam("customerId") Integer customerId,
                                      @RequestParam("supplierId") Integer supplierId,
                                      @RequestParam("yearMonth") String yearMonth) {
        return qingkuanService.applyDianfuList(customerId, supplierId, yearMonth, user());
    }

    /**
     * 查询请款列表
     *
     * @param currPage
     * @param pageSize
     * @param yearMonth
     * @param supplierName
     * @return
     */
    @PostMapping("qingkuanPageList")
    public ResultJson qingkuanPageList(@RequestParam(value = "currPage", defaultValue = "1") Integer currPage,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                       @RequestParam(value = "yearMonth") String yearMonth,
                                       @RequestParam(value = "supplierName", required = false) String supplierName) {
        return qingkuanService.qingkuanPageList(currPage, pageSize, yearMonth, supplierName, user());
    }


    /**
     * 请款(供应商账单详情-账单明细)
     *
     * @param supplierId
     * @param yearMonth
     * @return
     */
    @PostMapping("supplierZhangdanMingxi")
    public ResultJson supplierZhangdanMingxi(@RequestParam(value = "currPage", defaultValue = "1") Integer currPage,
                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                             @RequestParam("supplierId") Integer supplierId,
                                             @RequestParam("yearMonth") String yearMonth) {
        return qingkuanService.supplierZhangdanMingxi(currPage, pageSize, supplierId, yearMonth, user());
    }

    /**
     * 请款(供应商账单详情-请款单)
     *
     * @param supplierId
     * @param yearMonth
     * @return
     */
    @PostMapping("zhangDanQingKuanDan")
    public ResultJson zhangDanQingKuanDan(@RequestParam(value = "currPage", defaultValue = "1") Integer currPage,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                          @RequestParam("supplierId") Integer supplierId,
                                          @RequestParam("yearMonth") String yearMonth) {
        return qingkuanService.zhangDanQingKuanDan(currPage, pageSize, supplierId, yearMonth, user());
    }


    /**
     * 请款(供应商账单详情-垫付申请单)
     *
     * @param supplierId
     * @param yearMonth
     * @return
     */
    @PostMapping("zhangDanDianfuDan")
    public ResultJson zhangDanDianfuDan(@RequestParam(value = "currPage", defaultValue = "1") Integer currPage,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                        @RequestParam("supplierId") Integer supplierId,
                                        @RequestParam("yearMonth") String yearMonth) {
        return qingkuanService.zhangDanDianfuDan(currPage, pageSize, supplierId, yearMonth, user());
    }


    /**
     * 导出请款信息
     *
     * @param supplierId
     * @param yearMonth
     * @return
     */
    @PostMapping("exportQingkuan")
    public ResultJson exportQingkuan(@RequestParam("supplierId") Integer supplierId,
                                     @RequestParam("yearMonth") String yearMonth) {
        return qingkuanService.exportQingkuan(supplierId, yearMonth, user());
    }
}
