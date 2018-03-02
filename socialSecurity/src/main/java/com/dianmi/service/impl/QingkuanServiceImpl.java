package com.dianmi.service.impl;

import com.dianmi.mapper.QingkuanMapper;
import com.dianmi.mapper.SupplierCostMapper;
import com.dianmi.mapper.ZhangdanMingxiMapper;
import com.dianmi.model.Qingkuan;
import com.dianmi.model.SupplierCost;
import com.dianmi.model.User;
import com.dianmi.model.po.ZhangDanMingxiPo;
import com.dianmi.service.QingkuanService;
import com.dianmi.utils.DateTimeUtil;
import com.dianmi.utils.file.CopyFile;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.DownloadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.dianmi.utils.poi.CreateExcelRow;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("ALL")
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class QingkuanServiceImpl implements QingkuanService {

    @NonNull
    private QingkuanMapper qingkuanMapper;
    @NonNull
    private ZhangdanMingxiMapper zhangdanMingxiMapper;
    @NonNull
    private SupplierCostMapper supplierCostMapper;

    @Override
    public ResultJson dianfuPageList(Integer currPage, Integer pageSize, String yearMonth, String customerName, User user) {
        if (yearMonth == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        PageHelper.startPage(currPage, pageSize);
        List<Qingkuan> qingkuanList = qingkuanMapper.dianfuPageList(yearMonth, "%" + customerName + "%", user.getUId(), user.getRoleType());
        PageInfo<Qingkuan> pageInfo = new PageInfo<>(qingkuanList);
        return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
    }

    @Override
    public ResultJson applyDianfuList(Integer customerId, Integer supplierId, String yearMonth, User user) {
        if (customerId == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        // 更新状态为 1.已申请垫付
        int count = qingkuanMapper.updateDianfuState(customerId, supplierId);
        // 返回客户账单详情(员工详情)
        List<SupplierCost> supplierCostList = supplierCostMapper.customerIdBySupplierCost(customerId, supplierId, yearMonth, user.getUId(), user.getRoleType());
        return ResultUtil.success(RestEnum.SUCCESS, supplierCostList);
    }

    @Override
    public ResultJson qingkuanPageList(Integer currPage, Integer pageSize, String yearMonth, String supplierName, User user) {
        if (yearMonth == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        PageHelper.startPage(currPage, pageSize);
        List<Qingkuan> qingkuanList = qingkuanMapper.qingkuanPageList(yearMonth, supplierName, user.getUId(), user.getRoleType());
        PageInfo<Qingkuan> pageInfo = new PageInfo<>(qingkuanList);
        return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
    }

    @Override
    public ResultJson supplierZhangdanMingxi(Integer currPage, Integer pageSize, Integer supplierId, String yearMonth, User user) {
        if (supplierId == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        PageHelper.startPage(currPage, pageSize);
        List<ZhangDanMingxiPo> zhangDanMingxiPos = zhangdanMingxiMapper.supplierZhangdanMingxi(yearMonth, supplierId);
        PageInfo<ZhangDanMingxiPo> pageInfo = new PageInfo<ZhangDanMingxiPo>(zhangDanMingxiPos);
        return ResultUtil.success(RestEnum.SUCCESS, pageInfo); //账单明细);
    }


    @Override
    public ResultJson zhangDanQingKuanDan(Integer currPage, Integer pageSize, Integer supplierId, String yearMonth, User user) {
        if (supplierId == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        PageHelper.startPage(currPage, pageSize);
        List<Qingkuan> qingkuanList = qingkuanMapper.zhangDanQingKuanDan(supplierId, yearMonth);
        PageInfo<Qingkuan> pageInfo = new PageInfo<Qingkuan>(qingkuanList);
        return ResultUtil.success(RestEnum.SUCCESS, pageInfo);//请款单
    }


    @Override
    public ResultJson zhangDanDianfuDan(Integer currPage, Integer pageSize, Integer supplierId, String yearMonth, User user) {
        if (supplierId == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        PageHelper.startPage(currPage, pageSize);
        List<Qingkuan> dianfuList = qingkuanMapper.zhangDanDianfuDan(supplierId, yearMonth);
        PageInfo<Qingkuan> pageInfo = new PageInfo<Qingkuan>(dianfuList);
        return ResultUtil.success(RestEnum.SUCCESS, pageInfo); // 垫付申请单
    }


    @Override
    public ResultJson exportQingkuan(Integer supplierId, String yearMonth, User user) {
        List<SupplierCost> mingxiList = supplierCostMapper.zhangdanMingxi(supplierId, yearMonth, user.getUId(), user.getRoleType()); //缴费明细
        List<Qingkuan> qingkuanList = qingkuanMapper.zhangDanQingKuanDan(supplierId, yearMonth);//请款单
        List<Qingkuan> dianfuList = qingkuanMapper.zhangDanDianfuDan(supplierId, yearMonth);// 垫付申请单
        if (mingxiList.isEmpty() && qingkuanList.isEmpty() && dianfuList.isEmpty()) {
            return ResultUtil.error(RestEnum.ENTITY_IS_EMPTY);//不存在数据
        }
        String templatePath = "请款申请.xlsx";
        String fromFilePath = realPath() + "template" + File.separator + "qingkuan" + File.separator
                + templatePath;
        String toFilePath = realPath() + "download" + File.separator + "excel" + File.separator
                + UUID.randomUUID().toString() + templatePath;
        CopyFile.copyFile(fromFilePath, toFilePath);
        String currentDate = DateTimeUtil.currentDateStr();
        CreateExcelRow cer = new CreateExcelRow();
        if (!mingxiList.isEmpty()) {
            for (int m = 0; m < mingxiList.size(); m++) {//账单明细
                String[] supplierArr = new String[15];
                SupplierCost supplier = mingxiList.get(m);
                supplierArr[0] = String.valueOf(m + 1);
                supplierArr[1] = supplier.getEmployeeName();//员工姓名
                supplierArr[2] = supplier.getCertificateNumber();// 身份证
                supplierArr[3] = supplier.getSocialSecurityNumber();// 社保帐号
                supplierArr[4] = supplier.getAccumulationFundNumber();//公积金账号
                supplierArr[5] = supplier.getCity();//参保地
                supplierArr[6] = supplier.getResidentNature();//户口性质
                supplierArr[7] = supplier.getSocialSecurityBeginTime();// 社保缴纳月份
                supplierArr[8] = supplier.getPaymentMonth();//缴费月份
                supplierArr[9] = String.valueOf(supplier.getSocialSecurityCardinalRadix());//社保缴纳基数
                supplierArr[10] = String.valueOf(supplier.getPensionCompanyRadix());//养老缴纳基数
                supplierArr[11] = String.valueOf(supplier.getPensionCompanyPay());//养老公司缴费
                supplierArr[12] = String.valueOf(supplier.getPensionPersonRadix());//养老个人缴纳基数
                supplierArr[13] = String.valueOf(supplier.getPensionPersonPay());//养老个人缴费
                supplierArr[14] = String.valueOf(supplier.getUnemploymentCompanyRadix());//失业保险公司缴纳基数
                supplierArr[15] = String.valueOf(supplier.getUnemploymentCompanyPay());//失业保险公司缴费
                supplierArr[16] = String.valueOf(supplier.getUnemploymentPersonRadix());//失业个人缴费基数
                supplierArr[17] = String.valueOf(supplier.getUnemploymentPersonPay());//失业保险个人缴费
                supplierArr[18] = String.valueOf(supplier.getInjuryCompanyRadix());//工伤保险企业缴费基数
                supplierArr[19] = String.valueOf(supplier.getInjuryCompanyPay());//工伤企业缴费
                supplierArr[20] = String.valueOf(supplier.getProcreateCompanyRadix());//生育保险企业缴费基数
                supplierArr[21] = String.valueOf(supplier.getMedicalTreatmentCompanyRadix());//医疗企业缴费基数
                supplierArr[22] = String.valueOf(supplier.getMedicalTreatmentPersonRadix());//医疗个人缴费基数
                supplierArr[23] = String.valueOf(supplier.getMedicalTreatmentPersonPay());//医疗个人缴费
                supplierArr[24] = String.valueOf(supplier.getSeriousIllnessCompanyRedix());//大病公司缴费基数
                supplierArr[25] = String.valueOf(supplier.getSeriousIllnessCompanyPay());//大病企业缴费
                supplierArr[26] = String.valueOf(supplier.getSeriousIllnessPersonRedix());//大病个人缴费基数
                supplierArr[27] = String.valueOf(supplier.getSeriousIllnessPersonPay());//大病个人缴费
                supplierArr[28] = String.valueOf(supplier.getAccumulationFundCompanyRedix());//公积金公司缴纳基数
                supplierArr[29] = String.valueOf(supplier.getAccumulationFundCompanyRatio());//公积金公司缴比例
                supplierArr[30] = String.valueOf(supplier.getAccumulationFundCompanyPay());//公积金公司缴费
                supplierArr[31] = String.valueOf(supplier.getAccumulationFundPersonRedix());//公积金个人缴纳基数
                supplierArr[32] = String.valueOf(supplier.getAccumulationFundPersonRatio());//公积金个人缴比例
                supplierArr[33] = String.valueOf(supplier.getAccumulationFundCompanyPay());//公积金个人缴费
                supplierArr[34] = String.valueOf(supplier.getOtherChargesCompany());//公司其他费用
                supplierArr[35] = String.valueOf(supplier.getOtherChargesPerson());//个人其他费用
                supplierArr[36] = String.valueOf(supplier.getDisabilityBenefit());//伤残津贴
                supplierArr[37] = String.valueOf(supplier.getServiceCharge());//服务费
                supplierArr[38] = String.valueOf(supplier.getCompanyTotal());//公司部分合计
                supplierArr[39] = String.valueOf(supplier.getPersonTotal());//个人部分合计
                supplierArr[40] = String.valueOf(supplier.getCompanyRefund());//企业退费
                supplierArr[41] = String.valueOf(supplier.getTotal());//合计
                supplierArr[42] = supplier.getRemark();//备注
                supplierArr[43] = supplier.getCustomerName();//客户名称

                cer.insertRows(toFilePath, "缴费明细", 6, supplierArr);
            }
        }
        if (!qingkuanList.isEmpty()) {
            for (int q = 0; q < qingkuanList.size(); q++) {//请款单
                String[] qingkuanArr = new String[13];
                Qingkuan qingkuan = qingkuanList.get(q);
                qingkuanArr[0] = qingkuan.getReportingPeriod();
                qingkuanArr[1] = qingkuan.getCustomerName();
                qingkuanArr[2] = qingkuan.getDeptName();
                qingkuanArr[3] = qingkuan.getCountEmp();
                qingkuanArr[4] = String.valueOf(qingkuan.getGongsishebao());
                qingkuanArr[5] = String.valueOf(qingkuan.getGerenshebao());
                qingkuanArr[6] = String.valueOf(qingkuan.getShebaoTotal());
                qingkuanArr[7] = String.valueOf(qingkuan.getGongsicanbaojin());
                qingkuanArr[8] = String.valueOf(qingkuan.getGongsigongjijin());
                qingkuanArr[9] = String.valueOf(qingkuan.getGerengongjijin());
                qingkuanArr[10] = String.valueOf(qingkuan.getGongjijinTotal());
                qingkuanArr[11] = String.valueOf(qingkuan.getFuwufeiTotal());
                qingkuanArr[12] = String.valueOf(qingkuan.getMountTotal());
                cer.insertRows(toFilePath, "请款申请表", 8, qingkuanArr);
            }
        }
        if (!dianfuList.isEmpty()) {
            for (int d = 0; d < dianfuList.size(); d++) {//垫付单
                String[] dianfuArr = new String[13];
                Qingkuan dianfu = dianfuList.get(d);
                dianfuArr[0] = dianfu.getReportingPeriod();
                dianfuArr[1] = dianfu.getCustomerName();
                dianfuArr[2] = dianfu.getDeptName();
                dianfuArr[3] = dianfu.getCountEmp();
                dianfuArr[4] = String.valueOf(dianfu.getGongsishebao());
                dianfuArr[5] = String.valueOf(dianfu.getGerenshebao());
                dianfuArr[6] = String.valueOf(dianfu.getShebaoTotal());
                dianfuArr[7] = String.valueOf(dianfu.getGongsicanbaojin());
                dianfuArr[8] = String.valueOf(dianfu.getGongsigongjijin());
                dianfuArr[9] = String.valueOf(dianfu.getGerengongjijin());
                dianfuArr[10] = String.valueOf(dianfu.getGongjijinTotal());
                dianfuArr[11] = String.valueOf(dianfu.getFuwufeiTotal());
                dianfuArr[12] = String.valueOf(dianfu.getMountTotal());
                cer.insertRows(toFilePath, "垫付申请表", 8, dianfuArr);
            }
        }
        try {
            DownloadFile.downloadFile(toFilePath, "供应商账单明细.xlsx");
            DeleteFile.deleteDirectory(toFilePath);
        } catch (IOException e) {
            log.error("清款明细下载异常:{}", e);
            throw new RuntimeException(e);
        }
        return ResultUtil.success(RestEnum.SUCCESS);
    }

    /**
     * @return
     */
    public String realPath() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request.getSession().getServletContext().getRealPath("/");
    }
}
