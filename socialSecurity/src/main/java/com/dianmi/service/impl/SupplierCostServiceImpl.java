package com.dianmi.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.common.OwnHomeUtils;
import com.dianmi.mapper.SupplierCostMapper;
import com.dianmi.model.*;
import com.dianmi.model.owndetailshow.SupplierParticularsShow;
import com.dianmi.model.ownhome.SupplierShow;
import com.dianmi.service.*;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.UploadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor.BLUE;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SupplierCostServiceImpl extends CommonService implements SupplierCostService {
    @Value("${file.maximum.upload.size}")
    private long maxFileSize;
    @NonNull
    private SupplierCostMapper supplierCostMapper;
    @NonNull
    ZengyuanService addSocial;
    @NonNull
    JianyuanService minusSocial;
    @NonNull
    private ZaiceService zaiceService;
    @NonNull
    private ZhangdanService zhangdanService;

    private static ObjectMapper MAPPER = new ObjectMapper();


    // 读取供应商数据
    @Transactional
    public ResultJson readSupplierSocialInfo(MultipartFile file, Integer userId, Integer supplierId, String yearMonth) throws Exception {
        if (file.isEmpty() || supplierId == null || StringUtils.isEmpty(yearMonth)) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        //上传文件超出文件大小限制
        if (file.getSize() > maxFileSize) {
            return ResultUtil.success(RestEnum.FILE_SIZE_EXCEEDS_LIMIT);
        }

        String filePath = UploadFile.uploadFile(file);
        List<SupplierCost> list = new ArrayList<>();
        Workbook book = WorkbookFactory.create((new FileInputStream(new File(filePath))));

        Sheet sheet = book.getSheetAt(2);
        int rowNum = sheet.getLastRowNum();
        Integer line = getRowNums(sheet);
        Boolean flag = myVerdictSheetHead(sheet, line);
//        if (flag == false || line == null) {
//            throw new RuntimeException("格式错误！");
//        }
        Integer lines = line + 3;
        Integer nowLine = 0;
        for (int i = lines; i < rowNum; i++) {
            Row rows = sheet.getRow(i);
            if (rows == null) {
                continue;
            }
            for (int j = 1; j < rows.getLastCellNum(); j++) {
                if (j == 7 || j == 8) {
                    continue;
                }
                if (rows.getCell(j) == null) {
                    rows.getCell(j).setCellValue("");
                    continue;
                }
                rows.getCell(j).setCellType(CellType.STRING);
            }

            rows.getCell(0).setCellType(CellType.STRING);
            if (rows.getCell(0).getStringCellValue().contains("合计")) {
                break;
            }
            String name = rows.getCell(1).getStringCellValue() == "" ? "" : rows.getCell(1).getStringCellValue().trim();
            String certificateNumber = rows.getCell(2).getStringCellValue() == "" ? "" : rows.getCell(2).getStringCellValue().trim(); // 身份证号码
            if (name == "" && certificateNumber == "") {
                break;
            }
            String socialNum = rows.getCell(3).getStringCellValue() == "" ? "" : rows.getCell(3).getStringCellValue().trim();
            String accumulationNum = rows.getCell(4).getStringCellValue() == "" ? "" : rows.getCell(4).getStringCellValue().trim();
            String city = rows.getCell(5).getStringCellValue() == "" ? "" : rows.getCell(5).getStringCellValue().trim();
            String residence = rows.getCell(6).getStringCellValue() == "" ? "" : rows.getCell(6).getStringCellValue().trim();
            String startMonth;
            String payMonth = null;
            if (rows.getCell(7).getCellTypeEnum().equals(CellType.STRING)) {
                String tartMonth = rows.getCell(7).getStringCellValue() == "" ? "" : rows.getCell(7).getStringCellValue().trim();
                startMonth = getCaseMonth(tartMonth);
            }
            Date tartMonth = rows.getCell(7).getDateCellValue();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            //
            startMonth = tartMonth != null ? sdf.format(tartMonth) : "";

            if (rows.getCell(8).getCellTypeEnum().equals(CellType.STRING)) {
                String Feemonth = rows.getCell(8).getStringCellValue() == "" ? "" : rows.getCell(8).getStringCellValue().trim();
                payMonth = getCaseMonth(Feemonth);
            }
            Date FeeMonth = rows.getCell(8).getDateCellValue();
            //
            payMonth = FeeMonth != null ? sdf.format(FeeMonth) : "";

            Double socialFeeRedix = null;
            if ((rows.getCell(9).getStringCellValue()).contains("/")) {
                String[] sss = rows.getCell(9).getStringCellValue().split("/");
                socialFeeRedix = Double.valueOf(sss[0].trim()); // 需做判断
            } else {
                socialFeeRedix = rows.getCell(9).getStringCellValue().trim() == "" ? 0.00
                        : Double.valueOf(rows.getCell(9).getStringCellValue().trim());
            }
            Double pensionCompanyRedix = rows.getCell(10).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(10).getStringCellValue().trim());
            Double pensionPersonRedix = rows.getCell(10).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(10).getStringCellValue().trim());
            Double pensionCompanyFee = rows.getCell(11).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(11).getStringCellValue().trim());
            Double pensionPersonFee = rows.getCell(12).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(12).getStringCellValue().trim());
            Double unemploymentCompanyRedix = rows.getCell(13).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(13).getStringCellValue().trim());
            Double unemploymentPersonRedix = rows.getCell(13).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(13).getStringCellValue().trim());
            Double unemploymentCompanyFee = rows.getCell(14).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(14).getStringCellValue().trim());
            Double unemploymentPersonFee = rows.getCell(15).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(15).getStringCellValue().trim());
            Double injuryCompanyRedix = rows.getCell(16).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(16).getStringCellValue().trim());
            Double injuryCompanyFee = rows.getCell(17).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(17).getStringCellValue().trim());
            Double procreateCompanyRedix = rows.getCell(18).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(18).getStringCellValue().trim());
            Double procreateCompanyFee = rows.getCell(19).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(19).getStringCellValue().trim());
            Double medicalTreatmentCompanyRedix = rows.getCell(20).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(20).getStringCellValue().trim());
            Double medicalTreatmentCompanyFee = rows.getCell(21).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(21).getStringCellValue().trim());
            Double medicalTreatmentPersonRedix = rows.getCell(22).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(22).getStringCellValue().trim());
            Double medicalTreatmentPersonFee = rows.getCell(23).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(23).getStringCellValue().trim());
            Double seriousIllnessCompanyFee = rows.getCell(24).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(24).getStringCellValue().trim());
            Double seriousIllnessPersonFee = rows.getCell(25).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(25).getStringCellValue().trim());
            Double companySimpleTotal = rows.getCell(26).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(26).getStringCellValue().trim());
            Double personSimpleTotal = rows.getCell(27).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(27).getStringCellValue().trim());
            Double accumulationCompanyRedix = rows.getCell(28).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(28).getStringCellValue().trim());
            Double accumulationPersonRedix = rows.getCell(28).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(28).getStringCellValue().trim());
            String accumulationCompanyRatio = rows.getCell(29).getStringCellValue().trim() == "" ? " "
                    : rows.getCell(29).getStringCellValue().trim();
            Double accumulationCompanyFee = rows.getCell(30).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(30).getStringCellValue().trim());
            String accumulationPersonRatio = rows.getCell(31).getStringCellValue().trim() == "" ? " "
                    : rows.getCell(31).getStringCellValue().trim();
            Double accumulationPersonFee = rows.getCell(32).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(32).getStringCellValue().trim());
            Double otherCompanyFee = rows.getCell(33).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(33).getStringCellValue().trim());
            Double otherPersonFee = rows.getCell(34).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(34).getStringCellValue().trim());
            Double disabilityFee = rows.getCell(35).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(35).getStringCellValue().trim());
            Double companyTotal = rows.getCell(36).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(36).getStringCellValue().trim());
            Double personTotal = rows.getCell(37).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(37).getStringCellValue().trim());
            Double serviceFee = rows.getCell(38).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(38).getStringCellValue().trim());
            Double totalFee = rows.getCell(39).getStringCellValue().trim() == "" ? 0.00
                    : Double.valueOf(rows.getCell(39).getStringCellValue().trim());
            String remark = rows.getCell(40).getStringCellValue().trim() == "" ? " "
                    : rows.getCell(40).getStringCellValue().trim();
            String customerName = rows.getCell(41).getStringCellValue() == "" ? " "
                    : rows.getCell(41).getStringCellValue().trim();

            SupplierCost supplierCost = new SupplierCost();
            supplierCost.setUserId(userId);
            supplierCost.setReportingPeriod(yearMonth);
            supplierCost.setSupplierId(supplierId);
            supplierCost.setCertificateNumber(certificateNumber);
            supplierCost.setSocialSecurityNumber(socialNum);
            supplierCost.setAccumulationFundNumber(accumulationNum);
            supplierCost.setCity(city);
            supplierCost.setResidentNature(residence);
            supplierCost.setSocialSecurityBeginTime(startMonth);
            supplierCost.setPaymentMonth(payMonth);
            supplierCost.setSocialSecurityCardinalRadix(socialFeeRedix);
            supplierCost.setPensionCompanyRadix(pensionCompanyRedix);
            supplierCost.setPensionCompanyPay(pensionCompanyFee);
            supplierCost.setPensionPersonRadix(pensionPersonRedix);
            supplierCost.setPensionPersonPay(pensionPersonFee);
            supplierCost.setUnemploymentCompanyRadix(unemploymentCompanyRedix);
            supplierCost.setUnemploymentCompanyPay(unemploymentCompanyFee);
            supplierCost.setUnemploymentPersonRadix(unemploymentPersonRedix);
            supplierCost.setUnemploymentPersonPay(unemploymentPersonFee);
            supplierCost.setInjuryCompanyRadix(injuryCompanyRedix);
            supplierCost.setInjuryCompanyPay(injuryCompanyFee);
            supplierCost.setProcreateCompanyRadix(procreateCompanyRedix);
            supplierCost.setProcreateCompanyPay(procreateCompanyFee);
            supplierCost.setMedicalTreatmentCompanyRadix(medicalTreatmentCompanyRedix);
            supplierCost.setMedicalTreatmentCompanyPay(medicalTreatmentCompanyFee);
            supplierCost.setMedicalTreatmentPersonRadix(medicalTreatmentPersonRedix);
            supplierCost.setMedicalTreatmentPersonPay(medicalTreatmentPersonFee);
            // supplierCost.setSeriousIllnessCompanyRedix(0.00);
            // supplierCost.setSeriousIllnessPersonRedix(0.00);
            supplierCost.setSeriousIllnessCompanyPay(seriousIllnessCompanyFee);
            supplierCost.setSeriousIllnessPersonPay(seriousIllnessPersonFee);
            supplierCost.setAccumulationFundCompanyRedix(accumulationCompanyRedix);
            supplierCost.setAccumulationFundCompanyRatio(accumulationCompanyRatio);
            supplierCost.setAccumulationFundCompanyPay(accumulationCompanyFee);
            supplierCost.setAccumulationFundPersonRedix(accumulationPersonRedix);
            supplierCost.setAccumulationFundPersonRatio(accumulationPersonRatio);
            supplierCost.setAccumulationFundPersonPay(accumulationPersonFee);
            supplierCost.setOtherChargesCompany(otherCompanyFee);
            supplierCost.setOtherChargesPerson(otherPersonFee);
            supplierCost.setDisabilityBenefit(disabilityFee);
            supplierCost.setCompanyTotal(companyTotal);
            supplierCost.setPersonTotal(personTotal);
            supplierCost.setServiceCharge(serviceFee);
            supplierCost.setCompanyRefund(0.00); // 企业退费================================
            supplierCost.setTotal(totalFee);
            supplierCost.setCustomerName(customerName);
            String accountingUnit = zaiceService.findAccountingUnit(city, supplierId, yearMonth, userId);
            if (accountingUnit == null) {
                accountingUnit = "??????";
            }
            supplierCost.setAccountingUnit(accountingUnit); // 核算单位
            String supplierName = zaiceService.selectSupplierBySupplierId(city, supplierId);
            if (supplierName == null) {
                supplierName = "??????数据异常！";
            }
            supplierCost.setSupplierName(supplierName);

            if (name != "" && certificateNumber != "") {
                list.add(supplierCost);
                supplierCostMapper.insert(supplierCost);

                ZhangdanMingxi supplierDetail = getSupplierDetail(supplierCost);
                Map<String, Object> zaiceMsg = zaiceService.selectZaiceMsg(supplierCost.getCity(),
                        supplierCost.getReportingPeriod(), supplierCost.getCertificateNumber());
                int zaiceId = (int) zaiceMsg.get("zc_id");
                supplierDetail.setZaiceId(zaiceId);
                zhangdanService.updateZhangdanByZaiceId(supplierDetail);
            }
        }
        DeleteFile.DeleteFolder(filePath); // 上传成功删除Excel模板
        Integer count = 0;
        List<Zengyuan> zengyuanList = addSocial.selectZengyuanAll();
        List<Jianyuan> jianyuanList = minusSocial.selectJianyuanAll();
        for (Zengyuan zengyuan : zengyuanList) {
            String certificateNumber = zengyuan.getCertificateNumber();
            String city = zengyuan.getCity();
            String month = zengyuan.getReportingPeriod();
            Integer supplierIds = zengyuan.getSupplierId();
            if (city == null || certificateNumber == null || month == null || supplierIds == null) {
                continue;
            }
            if (OwnHomeUtils.getUpdateAddResult(supplierCostMapper, city, supplierIds, month, certificateNumber)) {
                count++;
                addSocial.updateAddStatus(certificateNumber, city, month);
                zaiceService.updateAddSocialStatus(city, month, certificateNumber);
            } else {
                addSocial.updateAddSocialStatus(certificateNumber, month, city);
            }
        }

        for (Jianyuan jianyuan : jianyuanList) {
            String certificateNumber = jianyuan.getCertificateNumber();
            String city = jianyuan.getCity();
            String month = jianyuan.getReportingPeriod();
            Integer supplierIds = zaiceService.getSupplierIdBySupplier(city, jianyuan.getSupplier(), certificateNumber);
            if (city == null || certificateNumber == null || month == null || supplierIds == null) {
                continue;
            }
            if (OwnHomeUtils.getUpdateMinusResult(supplierCostMapper, city, supplierIds, month, certificateNumber)) {
                minusSocial.updateMinusSocialStatus(certificateNumber, city, month);
                zaiceService.updateAddSocialStatus(city, month, certificateNumber);
            } else {
                minusSocial.updateMinusStatus(certificateNumber, month, city);
            }
        }
        if (list != null) {
            String dgJson = MAPPER.writeValueAsString(list);
            return ResultUtil.success(RestEnum.SUCCESS, "上传成功！");
        }
        return ResultUtil.success(RestEnum.SUCCESS, list);
    }


    // 获取有效数据起始行
    private Integer getRowNums(Sheet sheet) {
        Integer line = null;
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            Row rows = sheet.getRow(i);
            if (rows == null) {
                continue;
            }
            for (int j = 0; j < 4; j++) {
                if (rows.getCell(j) == null || rows.getCell(j).getAddress() == null) {
                    rows.getCell(j).setCellValue("");
                    continue;
                }
                rows.getCell(j).setCellType(CellType.STRING);
            }

            String strs = rows.getCell(0).getStringCellValue() == "" ? "" : rows.getCell(0).getStringCellValue().trim();
            if (!strs.equals("序号")) {
                continue;
            }
            String str = rows.getCell(1).getStringCellValue() == "" ? "" : rows.getCell(1).getStringCellValue().trim();
            String strr = rows.getCell(2).getStringCellValue() == "" ? "" : rows.getCell(2).getStringCellValue().trim();
            if (strs.equals("序号") && str.equals("姓名") && strr.contains("身份证")) {
                line = rows.getRowNum();
                break;
            }
        }
        return line;
    }

    // 判断表头
    private Boolean myVerdictSheetHead(Sheet sheet, Integer line) {
        Boolean flag1 = true;
        Boolean flag2 = true;
        Boolean flag3 = true;
        if (sheet == null || line == null) {
            return false;
        }
        Row rows = sheet.getRow(line);
        for (int j = 0; j < rows.getLastCellNum(); j++) {
            if (rows.getCell(j) == null) {
                rows.getCell(j).setCellValue("");
                continue;
            }
            rows.getCell(j).setCellType(CellType.STRING);
        }
        String strId = rows.getCell(0).getStringCellValue() == "" ? "" : rows.getCell(0).getStringCellValue().trim();
        String strName = rows.getCell(1).getStringCellValue() == "" ? "" : rows.getCell(1).getStringCellValue().trim();
        String strNumber = rows.getCell(2).getStringCellValue() == "" ? "" : rows.getCell(2).getStringCellValue().trim();
        String strSocialNum = rows.getCell(3).getStringCellValue() == "" ? "" : rows.getCell(3).getStringCellValue().trim();
        String strAccumulationNum = rows.getCell(4).getStringCellValue() == "" ? "" : rows.getCell(4).getStringCellValue().trim();
        String strCity = rows.getCell(5).getStringCellValue() == "" ? "" : rows.getCell(5).getStringCellValue().trim();
        String strResidence = rows.getCell(6).getStringCellValue() == "" ? "" : rows.getCell(6).getStringCellValue().trim();
        String strFirstMonth = rows.getCell(7).getStringCellValue() == "" ? "" : rows.getCell(7).getStringCellValue().trim();
        String strPayMonth = rows.getCell(8).getStringCellValue() == "" ? "" : rows.getCell(8).getStringCellValue().trim();
        String strSocialRedix = rows.getCell(9).getStringCellValue() == "" ? "" : rows.getCell(9).getStringCellValue().trim();
        String strSocialStatus = rows.getCell(10).getStringCellValue() == "" ? "" : rows.getCell(10).getStringCellValue().trim();
        String strCompanySimpleTotal = rows.getCell(26).getStringCellValue() == "" ? "" : rows.getCell(26).getStringCellValue().trim();
        String strPersonSimpleTotal = rows.getCell(27).getStringCellValue() == "" ? "" : rows.getCell(27).getStringCellValue().trim();
        String strHomeAccumulation = rows.getCell(28).getStringCellValue() == "" ? "" : rows.getCell(28).getStringCellValue().trim();
        String strOtherFee = rows.getCell(33).getStringCellValue() == "" ? "" : rows.getCell(33).getStringCellValue().trim();
        String strDisabilityFee = rows.getCell(35).getStringCellValue() == "" ? "" : rows.getCell(35).getStringCellValue().trim();
        String strCompanyTotal = rows.getCell(36).getStringCellValue() == "" ? "" : rows.getCell(36).getStringCellValue().trim();
        String strPersonTotal = rows.getCell(37).getStringCellValue() == "" ? "" : rows.getCell(37).getStringCellValue().trim();
        String strServiceFee = rows.getCell(38).getStringCellValue() == "" ? "" : rows.getCell(38).getStringCellValue().trim();
        String strTotal = rows.getCell(39).getStringCellValue() == "" ? "" : rows.getCell(39).getStringCellValue().trim();
        String strRemark = rows.getCell(40).getStringCellValue() == "" ? "" : rows.getCell(40).getStringCellValue().trim();
        String strCustomerName = "客户名称";
        if (strId.equals("序号") && strName.equals("姓名") && strNumber.equals("身份证号") && strSocialNum.equals("社保号/医保号")
                && strAccumulationNum.equals("公积金账号") && strCity.equals("参保地") && strResidence.equals("户口性质")
                && strFirstMonth.equals("始缴月份") && strPayMonth.equals("缴费月份") && strSocialRedix.equals("社保缴费基数")
                && strSocialStatus.equals("参加各险种情况") && strCompanySimpleTotal.equals("公司部分小计")
                && strPersonSimpleTotal.equals("个人部分小计") && strHomeAccumulation.equals("住房公积金")
                && strOtherFee.equals("其它费用") && strDisabilityFee.equals("残保金") && strCompanyTotal.equals("公司部分合计")
                && strPersonTotal.equals("个人部分合计") && strServiceFee.equals("管理服务费") && strTotal.equals("合计")
                && strRemark.equals("备注") && strCustomerName.equals("客户名称")) {
            flag1 = true;
        } else {
            flag1 = false;
        }

        Row rowss = sheet.getRow(line + Integer.valueOf(1));
        for (int j = 10; j < rowss.getLastCellNum(); j++) {
            if (rows.getCell(j) == null) {
                rows.getCell(j).setCellValue("");
                continue;
            }
            rowss.getCell(j).setCellType(CellType.STRING);
        }

        String strPensionSocial = rowss.getCell(10).getStringCellValue() == "" ? ""
                : rowss.getCell(10).getStringCellValue().trim();
        String strUnemployment = rowss.getCell(13).getStringCellValue() == "" ? ""
                : rowss.getCell(13).getStringCellValue().trim();
        String strInjury = rowss.getCell(16).getStringCellValue() == "" ? ""
                : rowss.getCell(16).getStringCellValue().trim();
        String strProcreate = rowss.getCell(18).getStringCellValue() == "" ? ""
                : rowss.getCell(18).getStringCellValue().trim();
        String strMedicalTreatment = rowss.getCell(20).getStringCellValue() == "" ? ""
                : rowss.getCell(20).getStringCellValue().trim();

        String strAccumulationRedix = rowss.getCell(28).getStringCellValue() == "" ? ""
                : rowss.getCell(28).getStringCellValue().trim();
        String strAccumulationCompany = rowss.getCell(29).getStringCellValue() == "" ? ""
                : rowss.getCell(29).getStringCellValue().trim();
        String strAccumulationPerson = rowss.getCell(31).getStringCellValue() == "" ? ""
                : rowss.getCell(31).getStringCellValue().trim();
        String strOtherCompanyFee = rowss.getCell(33).getStringCellValue() == "" ? ""
                : rowss.getCell(33).getStringCellValue().trim();
        String strOtherPersonFee = rowss.getCell(34).getStringCellValue() == "" ? ""
                : rowss.getCell(34).getStringCellValue().trim();
        if (strPensionSocial.equals("养老保险") && strUnemployment.equals("失业保险") && strInjury.equals("工伤保险")
                && strProcreate.equals("生育保险") && strMedicalTreatment.equals("医疗保险")
                && strAccumulationRedix.equals("公积金基数") && strAccumulationCompany.equals("公司部分")
                && strAccumulationPerson.equals("个人部分") && strOtherCompanyFee.equals("其它费用公司")
                && strOtherPersonFee.equals("其它费用个人")) {
            flag2 = true;
        } else {
            flag2 = false;
        }
        Row rowsss = sheet.getRow(line + Integer.valueOf(2));
        for (int j = 10; j < rowsss.getLastCellNum(); j++) {
            if (rows.getCell(j) == null) {
                rows.getCell(j).setCellValue("");
                continue;
            }
            rowsss.getCell(j).setCellType(CellType.STRING);
        }
        String strRedixPension = rowsss.getCell(10).getStringCellValue() == "" ? ""
                : rowsss.getCell(10).getStringCellValue().trim();
        String strCompanyPension = rowsss.getCell(11).getStringCellValue() == "" ? ""
                : rowsss.getCell(11).getStringCellValue().trim();
        String strPersonPension = rowsss.getCell(12).getStringCellValue() == "" ? ""
                : rowsss.getCell(12).getStringCellValue().trim();
        String strRedixUnemployment = rowsss.getCell(13).getStringCellValue() == "" ? ""
                : rowsss.getCell(13).getStringCellValue().trim();
        String strCompanyUnemployment = rowsss.getCell(14).getStringCellValue() == "" ? ""
                : rowsss.getCell(14).getStringCellValue().trim();
        String strPersonUnemployment = rowsss.getCell(15).getStringCellValue() == "" ? ""
                : rowsss.getCell(15).getStringCellValue().trim();
        String strRedixInjury = rowsss.getCell(16).getStringCellValue() == "" ? ""
                : rowsss.getCell(16).getStringCellValue().trim();
        String strCompanyInjury = rowsss.getCell(17).getStringCellValue() == "" ? ""
                : rowsss.getCell(17).getStringCellValue().trim();
        // String strPersonInjury =
        // rowsss.getCell(18).getStringCellValue()==""?"":rowsss.getCell(18).getStringCellValue().trim();
        String strRedixProcreate = rowsss.getCell(18).getStringCellValue() == "" ? ""
                : rowsss.getCell(18).getStringCellValue().trim();
        String strCompanyProcreate = rowsss.getCell(19).getStringCellValue() == "" ? ""
                : rowsss.getCell(19).getStringCellValue().trim();
        // String strPersonProcreate =
        // rowsss.getCell(21).getStringCellValue()==""?"":rowsss.getCell(21).getStringCellValue().trim();
        String strRedixCompanyMedicalTreatment = rowsss.getCell(20).getStringCellValue() == "" ? ""
                : rowsss.getCell(20).getStringCellValue().trim();
        String strCompanyMedicalTreatment = rowsss.getCell(21).getStringCellValue() == "" ? ""
                : rowsss.getCell(21).getStringCellValue().trim();
        String strRedixPersonMedicalTreatment = rowsss.getCell(22).getStringCellValue() == "" ? ""
                : rowsss.getCell(22).getStringCellValue().trim();
        String strPersonMedicalTreatment = rowsss.getCell(23).getStringCellValue() == "" ? ""
                : rowsss.getCell(23).getStringCellValue().trim();
        String strCompanySeriousIllness = rowsss.getCell(24).getStringCellValue() == "" ? ""
                : rowsss.getCell(24).getStringCellValue().trim();
        String strPersonSeriousIllness = rowsss.getCell(25).getStringCellValue() == "" ? ""
                : rowsss.getCell(25).getStringCellValue().trim();

        String strCompanyAccumulationProportion = rowsss.getCell(29).getStringCellValue() == "" ? ""
                : rowsss.getCell(29).getStringCellValue().trim();
        String strCompanyAccumulationFee = rowsss.getCell(30).getStringCellValue() == "" ? ""
                : rowsss.getCell(30).getStringCellValue().trim();
        String strPersonAccumulationProportion = rowsss.getCell(31).getStringCellValue() == "" ? ""
                : rowsss.getCell(31).getStringCellValue().trim();
        String strPersonAccumulationFee = rowsss.getCell(32).getStringCellValue() == "" ? ""
                : rowsss.getCell(32).getStringCellValue().trim();
        if (strRedixPension.equals("基数") && strCompanyPension.equals("公司") && strPersonPension.equals("个人")
                && strRedixUnemployment.equals("基数") && strCompanyUnemployment.equals("公司")
                && strPersonUnemployment.equals("个人") && strRedixInjury.equals("基数") && strCompanyInjury.equals("公司")
                && strRedixProcreate.equals("基数") && strCompanyProcreate.equals("公司")
                && strRedixCompanyMedicalTreatment.equals("基数") && strCompanyMedicalTreatment.equals("公司")
                && strRedixPersonMedicalTreatment.equals("基数") && strPersonMedicalTreatment.equals("个人")
                && strCompanySeriousIllness.equals("大病企业") && strPersonSeriousIllness.equals("大病个人")
                && strCompanyAccumulationProportion.equals("比例") && strCompanyAccumulationFee.equals("金额")
                && strPersonAccumulationProportion.equals("比例") && strPersonAccumulationFee.equals("金额")) {
            flag3 = true;
        } else {
            flag3 = false;
        }
        if (flag1 == true && flag2 == true && flag3 == true) {
            return true;
        }
        return false;
    }

    // 日期格式转换
    private String getCaseMonth(String month) {
        String yearMonth = month.trim().substring(0, 4) + "-" + month.trim().substring(5);
        StringBuilder sb = new StringBuilder(month);
        sb.insert(month.length() - 2, "-");
        return yearMonth;
    }

    // 供应商分页
    public Map<String, Object> selectSupplierCostAll(Integer supplierId, String month, Integer currPage,
                                                     Integer pageSize) {
        Map<String, Object> map = new HashMap<String, Object>();
        // String city = supplierCostMapper.selectCity(supplierId, month);
        List<SupplierCost> supplierCostList = supplierCostMapper.selectSupplierCostAll(supplierId, month,
                (currPage - 1), pageSize);
        Integer totalPerson = supplierCostList.size();
        if (supplierCostList == null) {
            return new HashMap();
        }
        PageHelper.startPage(currPage, pageSize);
        List<SupplierShow> showList = new ArrayList<SupplierShow>();

        Double totalMoney = 0.00;
        Double socialTotalMoney = 0.00;
        Double accumulationTotalMoney = 0.00;
        for (SupplierCost supplierCost : supplierCostList) {
            SupplierShow shows = new SupplierShow();
            // Customer strs =
            // customerService.findCustomerInfo(supplierCost.getUserId(),supplierCost.getCustomerId());
            shows.setReportingPeriod(month);
            shows.setCustomerName(supplierCost.getCustomerName());

            Double Ppension = supplierCost.getPensionPersonPay();
            Double Punemployment = supplierCost.getUnemploymentPersonPay();
            Double PmedicalTreatment = supplierCost.getMedicalTreatmentPersonPay();
            Double PseriousIllness = supplierCost.getSeriousIllnessPersonPay();
            Double a = OwnHomeUtils.add(Ppension, Punemployment);
            Double b = OwnHomeUtils.add(PmedicalTreatment, PseriousIllness);
            Double personFee = OwnHomeUtils.add(a, b);

            Double Cpension = supplierCost.getPensionCompanyPay();
            Double Cunemployment = supplierCost.getUnemploymentCompanyPay();
            Double Cinjury = supplierCost.getInjuryCompanyPay();
            Double Cprocreate = supplierCost.getProcreateCompanyPay();
            Double CmedicalTreatment = supplierCost.getMedicalTreatmentCompanyPay();
            Double CseriousIllness = supplierCost.getSeriousIllnessCompanyPay();
            Double aa = OwnHomeUtils.add(Cpension, Cunemployment);
            Double bb = OwnHomeUtils.add(Cinjury, Cprocreate);
            Double cc = OwnHomeUtils.add(CmedicalTreatment, CseriousIllness);
            Double dd = OwnHomeUtils.add(aa, bb);
            Double companyFee = OwnHomeUtils.add(cc, dd);
            shows.setCompanySocialTotal(companyFee);
            shows.setPersonSocialTotal(personFee);

            Double socialTotal = OwnHomeUtils.add(personFee, companyFee);
            shows.setSocialTotalPay(socialTotal);

            Double Paccumulation = supplierCost.getAccumulationFundPersonPay();
            Double Caccumulation = supplierCost.getAccumulationFundCompanyPay();
            Double accumulationTotalFee = OwnHomeUtils.add(Paccumulation, Caccumulation);
            shows.setAccumulationTotalPay(accumulationTotalFee);

            Double CotherFee = supplierCost.getOtherChargesCompany();
            Double PotherFee = supplierCost.getOtherChargesPerson();
            Double companyRefund = supplierCost.getCompanyRefund();
            Double disabilityFee = supplierCost.getDisabilityBenefit();
            Double serviceFee = supplierCost.getServiceCharge();
            shows.setServiceFee(serviceFee);
            shows.setDisabilityBenefitFee(disabilityFee);
            shows.setCompanyAccumulation(Caccumulation);
            shows.setPersonAccumulation(Paccumulation);
            Double Total = OwnHomeUtils.add(
                    OwnHomeUtils.add(OwnHomeUtils.add(CotherFee, PotherFee),
                            OwnHomeUtils.add(companyRefund, disabilityFee)),
                    OwnHomeUtils.add(OwnHomeUtils.add(serviceFee, socialTotal), accumulationTotalFee));
            shows.setTotalFee(Total);
            showList.add(shows);

            totalMoney = OwnHomeUtils.add(totalMoney, Total);
            socialTotalMoney = OwnHomeUtils.add(socialTotalMoney, socialTotal);
            accumulationTotalMoney = OwnHomeUtils.add(accumulationTotalMoney, accumulationTotalFee);
        }
        PageInfo<SupplierShow> pageList = new PageInfo<SupplierShow>(showList);
        map.put("totalMoney", totalMoney);
        map.put("totalCount", totalPerson);
        map.put("socialTotalFee", socialTotalMoney);
        map.put("accumulationTotal", accumulationTotalMoney);
        map.put("supplierList", pageList);
        // pageList.getTotal();
        return map;
    }

    /**
     * 根据供应商id和月份查询参保地
     */
    public String selectCityBySupplierId(String yearMonth, Integer supplierId) {
        return supplierCostMapper.selectCity(supplierId, yearMonth);
    }

    // 供应商详情展示
    public List<SupplierParticularsShow> selectSupplierCostDetails(Integer supplierId, String yearMonth,
                                                                   Integer pageSize, Integer currPage) {
        List<SupplierCost> list = supplierCostMapper.selectSupplierCostAll(supplierId, yearMonth, (currPage - 1),
                pageSize);
        if (list == null) {
            return new ArrayList();
        }
        PageHelper.startPage(currPage, pageSize);
        List<SupplierParticularsShow> supplierCostList = getSupplierDetailShow(list);

        PageInfo<SupplierParticularsShow> pageList = new PageInfo<SupplierParticularsShow>(supplierCostList);
        return pageList.getList();
    }

    // 获取账单明细对象
    private ZhangdanMingxi getSupplierDetail(SupplierCost supplierCost) {
        ZhangdanMingxi zdmingxi = new ZhangdanMingxi();
        Double Ppension = supplierCost.getPensionPersonPay();
        Double Punemployment = supplierCost.getUnemploymentPersonPay();
        Double PmedicalTreatment = supplierCost.getMedicalTreatmentPersonPay();
        Double PseriousIllness = supplierCost.getSeriousIllnessPersonPay();

        Double Cpension = supplierCost.getPensionCompanyPay();
        Double Cunemployment = supplierCost.getUnemploymentCompanyPay();
        Double Cinjury = supplierCost.getInjuryCompanyPay();
        Double Cprocreate = supplierCost.getProcreateCompanyPay();
        Double CmedicalTreatment = supplierCost.getMedicalTreatmentCompanyPay();
        Double CseriousIllness = supplierCost.getSeriousIllnessCompanyPay();
        zdmingxi.setShijiYanglaoGongsi(Cpension);
        zdmingxi.setShijiYanglaoGeren(Ppension);
        zdmingxi.setShijiJibenYiliaoGongsi(CmedicalTreatment);
        zdmingxi.setShijiJibenYiliaoGeren(PmedicalTreatment);
        zdmingxi.setShijiDabingYiliaoGongsi(CseriousIllness);
        zdmingxi.setShijiDabingYiliaoGeren(PseriousIllness);
        zdmingxi.setShijiShiyeGongsi(Cunemployment);
        zdmingxi.setShijiShiyeGeren(Punemployment);
        zdmingxi.setShijiGongshangGongsi(Cinjury);
        zdmingxi.setShijiShengyuGongsi(Cprocreate);

        Double Paccumulation = supplierCost.getAccumulationFundPersonPay();
        Double Caccumulation = supplierCost.getAccumulationFundCompanyPay();
        zdmingxi.setShijiGongjijinGongsi(Caccumulation);
        zdmingxi.setShijiGongjijinGeren(Paccumulation);

        Double CotherFee = supplierCost.getOtherChargesCompany();
        Double PotherFee = supplierCost.getOtherChargesPerson();
        Double companyRefund = supplierCost.getCompanyRefund();
        Double disabilityFee = supplierCost.getDisabilityBenefit();
        Double serviceFee = supplierCost.getServiceCharge();
        zdmingxi.setShijiQitaGongsi(CotherFee);
        zdmingxi.setShijiQitaGeren(PotherFee);
        zdmingxi.setShijiCanbaojinGongsi(disabilityFee);
        zdmingxi.setShijiFuwufei(serviceFee);
        return zdmingxi;
    }

    // 写出供应商费用
    public String writeSupplierCostDetails(Integer supplierId, String yearMonth) throws IOException {
        List<SupplierCost> list = supplierCostMapper.selectSupplierCostAllBySupplierId(supplierId, yearMonth);
        String supplier = supplierCostMapper.selectSupplierName(supplierId, yearMonth);
        if (list == null || supplier == null) {
            return null;
        }
        Workbook wb = new HSSFWorkbook();
        HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
        String[] strs = {"姓名", "身份证号码", "社保号/医保号", "公积金账号", "参保地", "户口性质", "始缴月份", "缴费月份", "社保缴费基数", "参加各险种情况",
                "公司部分小计", "个人部分小计", "住房公积金", "其它费用", "残保金", "公司部分合计", "个人部分合计", "管理服务费", "合计", "备注", "客户名称"};
        String[] ssr = {"养老保险", "失业保险", "工伤保险", "生育保险", "医疗保险", "公积金基数", "公司部分", "个人部分", "其它费用公司", "其它费用个人"};
        String[] strArrs = {"基数", "公司", "个人", "基数", "公司", "个人", "基数", "公司", "基数", "公司", "基数", "公司", "基数", "个人", "大病企业",
                "大病个人", "比例", "金额", "比例", "金额"};
        HSSFSheet sheet = (HSSFSheet) wb.createSheet(supplierId + "号供应商" + yearMonth + "月费用明细");

        // 设置默认行高,列宽
        // sheet.setDefaultRowHeight((short)(20 * 256));
        sheet.setDefaultRowHeightInPoints(20);
        sheet.setDefaultColumnWidth(15);

        // 创建字体设置字体为宋体
        HSSFFont font = (HSSFFont) wb.createFont();
        font.setFontName("宋体");
        // 设置字体高度
        font.setFontHeightInPoints((short) 12);
        CreationHelper createHelper = wb.getCreationHelper();
        style.setFont(font);
        // 设置自动换行
        style.setWrapText(true);
        // 设置单元格内容垂直对其方式为居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
        style.setAlignment(HorizontalAlignment.CENTER);// 水平 居中格式

        HSSFRow rows = sheet.createRow((short) 0);
        rows.setHeightInPoints(30);
        for (int y = 0; y < strArrs.length + 22; y++) {
            HSSFCellStyle styles = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
            styles.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
            sheet.autoSizeColumn(y); // 自动调整宽度
            HSSFCell cells = rows.createCell(y, CellType.STRING);
            HSSFFont fonts = (HSSFFont) wb.createFont();
            fonts.setColor(BLUE.index);// HSSFColor.VIOLET.index //字体颜色
            // fonts.setFontName("宋体");
            fonts.setBold(true);
            styles.setFont(fonts); // 字体增粗
            fonts.setFontHeightInPoints((short) 18);
            cells.setCellStyle(styles);
            rows.getCell(0).setCellValue(yearMonth + "月-" + supplier + "费用明细");
        }
        // 合并单元格
        CellRangeAddress cra = new CellRangeAddress(0, (short) 0, 0, (short) (rows.getLastCellNum() - 1)); // 起始行,
        // 终止行,
        // 起始列,
        // 终止列
        sheet.addMergedRegion(cra);

        HSSFRow row = sheet.createRow((short) 1);
        row.setHeightInPoints(20);
        HSSFRow row2 = sheet.createRow((short) 2);
        row2.setHeightInPoints(20);
        HSSFRow row3 = sheet.createRow((short) 3);
        row3.setHeightInPoints(20);
        for (int i = 0; i < (strArrs.length) + 22; i++) {
            HSSFCellStyle stylet = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
            stylet.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
            stylet.setAlignment(HorizontalAlignment.CENTER);// 水平 // 创建一个居中格式
            HSSFCell ce = row.createCell(i);
            sheet.setColumnWidth(i, (short) (15 * 256));
            HSSFFont fontt = (HSSFFont) wb.createFont();
            fontt.setBold(true);
            stylet.setFont(fontt);
            ce.setCellStyle(stylet);
            if (i < 10) {
                ce.setCellValue(strs[i]); // 表格的第二行第一列显示的数据
            } else if (i == 25) {
                ce.setCellValue(strs[10]);
            } else if (i == 26) {
                ce.setCellValue(strs[11]);
            } else if (i == 27) {
                ce.setCellValue(strs[12]);
            } else if (i == 32) {
                ce.setCellValue(strs[13]);
            } else if (i == 34) {
                ce.setCellValue(strs[14]);
            } else if (i == 35) {
                ce.setCellValue(strs[15]);
            } else if (i == 36) {
                ce.setCellValue(strs[16]);
            } else if (i == 37) {
                ce.setCellValue(strs[17]);
            } else if (i == 38) {
                ce.setCellValue(strs[18]);
            } else if (i == 39) {
                ce.setCellValue(strs[19]);
            } else if (i == 40) {
                ce.setCellValue(strs[20]);
            } else {
                ce.setCellValue("");
            }

            HSSFCell ccc = row2.createCell(i);
            ccc.setCellStyle(stylet);
            if (i == 9) {
                ce.setCellValue(ssr[0]); // 表格的第二行第一列显示的数据
            } else if (i == 12) {
                ce.setCellValue(ssr[1]);
            } else if (i == 15) {
                ce.setCellValue(ssr[2]);
            } else if (i == 17) {
                ce.setCellValue(ssr[3]);
            } else if (i == 19) {
                ce.setCellValue(ssr[4]);
            } else if (i == 27) {
                ce.setCellValue(ssr[5]);
            } else if (i == 28) {
                ce.setCellValue(ssr[6]);
            } else if (i == 30) {
                ce.setCellValue(ssr[7]);
            } else if (i == 32) {
                ce.setCellValue(ssr[8]);
            } else if (i == 33) {
                ce.setCellValue(ssr[9]);
            } else {
                ce.setCellValue("");
            }

            HSSFCell ces = null;
            if (i >= 9 && i < 25) {
                ces = row3.createCell(i);
                sheet.setColumnWidth(i, (short) (15 * 256));
                ces.setCellStyle(stylet);
                ces.setCellValue(strArrs[i - 9]);
            } else if (i >= 28 && i < 32) {
                ces = row3.createCell(i);
                sheet.setColumnWidth(i, (short) (15 * 256));
                ces.setCellStyle(stylet);
                ces.setCellValue(ssr[i - 12]);
            } else {
                ces = row3.createCell(i);
                sheet.setColumnWidth(i, (short) (15 * 256));
                ces.setCellStyle(stylet);
                ces.setCellValue("");
            }
            if (i < 9 || i > 38) {
                sheet.autoSizeColumn(i); // 自动调整宽度
            }
        }
        for (int j = 0; j < 9; j++) {
            sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, j, (short) j)); // 设置单元格合并
        }
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 9, (short) 24)); // 第二行
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 3, 25, (short) 25));
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 3, 26, (short) 26));
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 27, (short) 31));
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 32, (short) 33));
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 3, 34, (short) 34));
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 3, 35, (short) 35));
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 3, 36, (short) 36));
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 3, 37, (short) 37));
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 3, 38, (short) 38));
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 3, 39, (short) 39));
        sheet.addMergedRegion(new CellRangeAddress(1, (short) 3, 40, (short) 40));

        sheet.addMergedRegion(new CellRangeAddress(2, (short) 2, 9, (short) 11)); // 第三行
        sheet.addMergedRegion(new CellRangeAddress(2, (short) 2, 12, (short) 14));
        sheet.addMergedRegion(new CellRangeAddress(2, (short) 2, 15, (short) 16));
        sheet.addMergedRegion(new CellRangeAddress(2, (short) 2, 17, (short) 18));
        sheet.addMergedRegion(new CellRangeAddress(2, (short) 2, 19, (short) 24));
        sheet.addMergedRegion(new CellRangeAddress(2, (short) 3, 27, (short) 27));
        sheet.addMergedRegion(new CellRangeAddress(2, (short) 2, 28, (short) 29));
        sheet.addMergedRegion(new CellRangeAddress(2, (short) 2, 30, (short) 31));
        sheet.addMergedRegion(new CellRangeAddress(2, (short) 3, 32, (short) 32));
        sheet.addMergedRegion(new CellRangeAddress(2, (short) 3, 33, (short) 33));

        List<SupplierParticularsShow> detailList = getSupplierDetailShow(list);
        if (detailList == null || list == null) {
            return null;
        }
        for (int i = 4; i < (detailList.size() + 4); i++) {
            List ll = new ArrayList();
            ll.add(detailList.get(i - 4).getName());
            ll.add(detailList.get(i - 4).getCertificateNumber());
            /*
             * ll.add(detailList.get(i-4).get);
             * ll.add(detailList.get(i-4).getCompanyTotal());
             *
             * ll.add(detailList.get(i-4).getPensionPersonTotal());
             * ll.add(detailList.get(i-4).getPensionCompanyTotal()); //*********
             * ll.add(detailList.get(i-4).getPensionRadix());
             *
             * ll.add(detailList.get(i-4).getUnemploymentPersonTotal());
             * ll.add(detailList.get(i-4).getUnemploymentCompanyTotal());
             * ll.add(detailList.get(i-4).getUnemploymentRadix());
             *
             * ll.add(detailList.get(i-4).getInjuryPersonTotal());
             * ll.add(detailList.get(i-4).getInjuryCompanyTotal());
             * ll.add(detailList.get(i-4).getInjuryCompanyRadix());
             *
             * ll.add(detailList.get(i-4).getProcreatePersonTotal());
             * ll.add(detailList.get(i-4).getProcreateCompanyTotal());
             * ll.add(detailList.get(i-4).getProcreateCompanyRadix());
             *
             * ll.add(detailList.get(i-4).getMedicalTreatmentPersonTotal());
             * ll.add(detailList.get(i-4).getMedicalTreatmentCompanyTotal());
             * //**********
             * ll.add(detailList.get(i-4).getMedicalTreatmentRadix());
             *
             * ll.add(detailList.get(i-4).getAccumulationRadix());
             * ll.add(detailList.get(i-4).getAccumulationCompanyTotal());
             * ll.add(detailList.get(i-4).getAccumulationPersonTotal());
             * ll.add(detailList.get(i-4).getAccumulationTotalFee());
             *
             * ll.add(detailList.get(i-4).getDisabilityBenefit());
             * ll.add(detailList.get(i-4).getServiceFee());
             * ll.add(detailList.get(i-4).getTotal());
             */
            HSSFRow rowLine = sheet.createRow((short) i);
            rowLine.setHeightInPoints(20);
            sheet.setDefaultColumnWidth(10);
            for (int x = 0; x < (strArrs.length + 5); x++) {
                HSSFCell cc = null;
                if (x == 0) {
                    cc = rowLine.createCell(x, CellType.STRING);
                    sheet.setColumnWidth(x, (short) (13 * 256));
                    cc.setCellStyle(style);
                    cc.setCellValue(ll.get(x) + "".trim());
                } else if (x == 1) {
                    cc = rowLine.createCell(x, CellType.STRING);
                    sheet.setColumnWidth(x, (short) (13 * 256));
                    cc.setCellStyle(style);
                    cc.setCellValue(ll.get(x) + "".trim());
                } else {
                    cc = rowLine.createCell(x, CellType.NUMERIC);
                    sheet.setColumnWidth(x, (short) (13 * 256));
                    cc.setCellStyle(style);
                    Object obj = ll.get(x);
                    if (obj == null) {
                        obj = 0.00;
                    }
                    Double value = (Double) obj;
                    cc.setCellValue(value);
                }
                if (x == 1) {
                    sheet.autoSizeColumn(x); // 自动调整宽度
                }
            }
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String excelPath = request.getSession().getServletContext().getRealPath("/") + "download" + File.separator
                + "excel" + File.separator + supplierId + File.separator;
        // String path =
        // "E:"+File.separator+"ownhomeInfo"+File.separator+supplierId+File.separator;
        // System.out.println("==============================================================写入成功");
        String filePath1 = excelPath + "编号" + supplierId + "的供应商" + yearMonth + "月社保费用明细.xls";
        /// String filePath2 =
        /// path+"编号"+supplierId+"的供应商"+yearMonth+"月社保费用明细.xls";
        File file1 = new File(excelPath);
        // File file2 = new File(path);
        if (!file1.exists() && !file1.isDirectory()) {
            file1.mkdirs();
        }
        /*
         * if(!file2.exists() && !file2.isDirectory()){ file2.mkdirs(); }
         */
        File files1 = new File(filePath1);
        // File files2 = new File(filePath2);
        if (!files1.exists() && !files1.isFile()) {
            files1.createNewFile();
        }
        /*
         * if(!files2.exists() && !files2.isFile()){ files2.createNewFile(); }
         */
        FileOutputStream out1 = null;
        // FileOutputStream out2=null;
        try {
            out1 = new FileOutputStream(files1);
            // out2 = new FileOutputStream(files2);
            wb.write(out1);
            // wb.write(out2);
            out1.flush();
            // out2.flush();
            return filePath1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (out1 != null) {
                out1.close();
            }
            out1 = null;
            // out2=null;
        }
    }

    // 获取供应商详情对象
    private List<SupplierParticularsShow> getSupplierDetailShow(List<SupplierCost> list) {
        List<SupplierParticularsShow> supplierCostList = new ArrayList<SupplierParticularsShow>();
        for (SupplierCost supplierCost : list) {
            SupplierParticularsShow show = new SupplierParticularsShow();
            show.setCertificateNumber(supplierCost.getCertificateNumber());
            show.setPersonSocialRedix(supplierCost.getSocialSecurityCardinalRadix());

            Double Cpension = supplierCost.getPensionCompanyPay();
            Double Ppension = supplierCost.getPensionPersonPay();
            Double pensionTotal = OwnHomeUtils.add(Cpension, Ppension);
            show.setPensionSocial(pensionTotal);

            Double Cunemployment = supplierCost.getUnemploymentCompanyPay();
            Double Punemployment = supplierCost.getUnemploymentPersonPay();
            Double unemploymentTotal = OwnHomeUtils.add(Cunemployment, Punemployment);
            show.setUnemploymentSocial(unemploymentTotal);

            Double injuryTotal = supplierCost.getInjuryCompanyPay();
            show.setInjurySocial(injuryTotal);
            Double procreateTotal = supplierCost.getProcreateCompanyPay();
            show.setProcreateSocial(procreateTotal);

            Double Pmedicaltreatment = supplierCost.getMedicalTreatmentPersonPay();
            Double Cmedicaltreatment = supplierCost.getMedicalTreatmentCompanyPay();
            Double medicaltreatmentTotal = OwnHomeUtils.add(Pmedicaltreatment, Cmedicaltreatment);
            show.setMedicaltreatmentSocial(medicaltreatmentTotal);

            Double Paccumulation = supplierCost.getAccumulationFundPersonPay();
            Double Caccumulation = supplierCost.getAccumulationFundCompanyPay();
            Double accumulationTotal = OwnHomeUtils.add(Paccumulation, Caccumulation);
            show.setAccumulationTotal(accumulationTotal);

            Double CotherFee = supplierCost.getOtherChargesCompany();
            Double PotherFee = supplierCost.getOtherChargesPerson();
            Double companyRefund = supplierCost.getCompanyRefund();
            Double disabilityFee = supplierCost.getDisabilityBenefit();
            Double serviceFee = supplierCost.getServiceCharge();
            Double CseriousIllness = supplierCost.getSeriousIllnessCompanyPay();
            Double PseriousIllness = supplierCost.getSeriousIllnessPersonPay();
            show.setAccumulationRedix(supplierCost.getAccumulationFundCompanyRedix());
            show.setCompanyAccumulation(Caccumulation);
            show.setPersonAccumulation(Paccumulation);
            show.setServiceFee(serviceFee);
            show.setDisabilityBenefitFee(disabilityFee);
            Double totalFees = supplierCost.getTotal();
            /*
             * int TotalPay =
             * ((pensionTotal+unemploymentTotal)+(injuryTotal+procreateTotal)+
             * (medicaltreatmentTotal+CseriousIllness)+(PseriousIllness+
             * CotherFee)+
             * (PotherFee+companyRefund)+(disabilityFee+serviceFee));
             */
            show.setTotalFee(totalFees);
            supplierCostList.add(show);
        }
        return supplierCostList;
    }

    // 批量删除供应商信息
    @Transactional
    public ResultJson deleteSupplierCostDetails(Integer supplierId, String yearMonth) {
        if (supplierId == null || StringUtils.isEmpty(yearMonth)) {
            return ResultUtil.success(RestEnum.PARAMETER_ERROR);
        }
        List<SupplierCost> list = supplierCostMapper.zhangdanMingxi(supplierId, yearMonth, null, null);
        int a = supplierCostMapper.deleteSupplierCostDetails(supplierId, yearMonth);
        if (list.size() != a) {
            throw new RuntimeException("删除数量有误！");
        }
        return ResultUtil.success(RestEnum.SUCCESS, "删除成功");
    }

    @Override
    public ResultJson supplierCostPageList(Integer currPage, Integer pageSize, String yearMonth, String paramName) {
        if (yearMonth == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        PageHelper.startPage(currPage, pageSize);
        paramName = StringUtils.isEmpty(paramName) ? null : paramName;
        List<SupplierCost> supplierPageList = supplierCostMapper.supplierCostPageList(yearMonth, paramName);
        PageInfo<SupplierCost> pageInfo = new PageInfo<>(supplierPageList);
        return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
    }

    @Override
    public ResultJson supplierCostStatistics(String yearMonth, User user) {
        if (yearMonth == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        Map<String, String> statisticsNum = supplierCostMapper.supplierCostStatistics(yearMonth, user.getUId(), user.getRoleType());
        return ResultUtil.success(RestEnum.SUCCESS, statisticsNum);
    }

}
