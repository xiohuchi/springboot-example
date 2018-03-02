package com.dianmi.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.mapper.*;
import com.dianmi.model.*;
import com.dianmi.model.owndetailshow.ZaiceDataShow;
import com.dianmi.model.po.ZaicePo;
import com.dianmi.service.ZaiceService;
import com.dianmi.utils.ParseUtil;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.DownloadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * created by www 2017/10/25 19:23
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ZaiceServiceImpl implements ZaiceService {

    @NonNull
    private ZaiceMapper zaiceMapper;
    @NonNull
    private EmployeeFuwufanganMapper employeeFuwufanganMapper;
    @NonNull
    private ZhangdanMingxiMapper zhangdanMingxiMapper;
    @NonNull
    private FuwufanganMapper fuwufanganMapper;
    @NonNull
    private CustomerMapper customerMapper;


    /**
     * 同步上一个月实际在册数据到本月
     */
    public void syncPreMonthZaice() {
        // 获取当前时间的上一个月份
        String preMonth = preMonth();
        // 获取当前月份
        String currMonth = currMonth();
        // 取出上月目标在册
        List<Zaice> preMonthMubiaoZaiceList = zaiceMapper.getMubiaoZaice(preMonth);
        Date currDateTime = new Date();
        for (Zaice mbZaice : preMonthMubiaoZaiceList) {
            Zaice zaice = new Zaice(mbZaice.getUserId(), mbZaice.getDeptId(), mbZaice.getCustomerId(),
                    mbZaice.getSupplierId(), mbZaice.getFuwufanganId(), mbZaice.getFinancialAccountingUnit(),
                    mbZaice.getCustomerName(), mbZaice.getEmployeeName(), mbZaice.getCertificateType(),
                    mbZaice.getCertificateNumber(), mbZaice.getNation(), mbZaice.getMobilePhone(),
                    mbZaice.getIsExternalCall(), mbZaice.getHouseholdCity(), mbZaice.getHouseholdProperty(),
                    mbZaice.getCity(), mbZaice.getSocialSecurityCardNumber(), mbZaice.getSocialSecurityBase(),
                    mbZaice.getSocialSecurityType(), mbZaice.getSupplier(), mbZaice.getSocialSecurityBeginTime(),
                    mbZaice.getAccumulationFundNumber(), mbZaice.getAccumulationFundCardinalNumber(),
                    mbZaice.getAccumulationFundRatio(), mbZaice.getAccumulationFundBeginTime(), currDateTime,
                    mbZaice.getRemark(), currMonth, (byte) 0, (byte) 0, mbZaice.getDeleteFlag());
            zaiceMapper.insert(zaice);
            createYuqiZhangdan(mbZaice);

        }
    }

    /**
     * 获取当前年月
     */
    public String currMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String time = format.format(c.getTime());
        return time;
    }

    /**
     * 获取上一个月份
     */
    public String preMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String time = format.format(c.getTime());
        return time;
    }

    // 生成目标在册预期账单
    public void createYuqiZhangdan(Zaice zaice) {
        String gongjijinRatioStr = zaice.getAccumulationFundRatio();// 5%+5%
        double gongjijinGongsiPay = 0;
        double gongjijinGerenPay = 0;
        if (!StringUtils.isEmpty(gongjijinRatioStr)
                && (!StringUtils.isEmpty(zaice.getAccumulationFundCardinalNumber()))) {
            float[] ratioArr = ParseUtil.parseGongjijinStrRatioToFloat(gongjijinRatioStr);
            double accumulationFundCardinalNumber = Double
                    .parseDouble(zaice.getAccumulationFundCardinalNumber().trim());
            gongjijinGongsiPay = accumulationFundCardinalNumber * ratioArr[0];
            gongjijinGerenPay = accumulationFundCardinalNumber * ratioArr[1];
        }
        Fuwufangan fuwufangan = fuwufanganMapper.selectByPrimaryKey(zaice.getFuwufanganId());
        int zaiceId = zaice.getZcId();
        double yanglaoGongsi = fuwufangan.getPensionCompanyPay();
        double yanglaoGeren = fuwufangan.getPensionPersonPay();
        double shiyeGongsi = fuwufangan.getUnemploymentCompanyPay();
        double shiyeGeren = fuwufangan.getUnemploymentPersonPay();
        double gongshangGongsi = fuwufangan.getInjuryCompanyPay();
        double shengyuGongsi = fuwufangan.getProcreateCompanyPay();
        double jibenYiliaoGongsi = fuwufangan.getMedicalCompanyPay();
        double jibenYiliaoGeren = fuwufangan.getMedicalPersonPay();
        double dabingYiliaoGongsi = fuwufangan.getSeriousIllnessCompanyPay();
        double dabingYiliaoGeren = fuwufangan.getSeriousIllnessPersonPay();
        double gongjijinGongsi = gongjijinGongsiPay;
        double gongjijinGeren = gongjijinGerenPay;
        double canbaojinGongsi = fuwufangan.getDisabilityGuaranteeFund();
        double qitaGongsi = fuwufangan.getOtherCompanyPay();
        double qitaGeren = fuwufangan.getOtherPersonPay();
        double fuwufei = fuwufangan.getServiceCharge();
        // 新增员工服务方案信息
        employeeFuwufanganMapper.insert(new EmployeeFuwufangan(zaiceId, yanglaoGongsi, yanglaoGeren, jibenYiliaoGongsi,
                jibenYiliaoGeren, shiyeGongsi, shiyeGeren, dabingYiliaoGongsi, dabingYiliaoGeren, shengyuGongsi,
                gongjijinGongsi, fuwufei, canbaojinGongsi, qitaGongsi, qitaGeren));
        // 新增员工账单
        zhangdanMingxiMapper.insert(new ZhangdanMingxi(zaiceId, yanglaoGongsi, yanglaoGeren, jibenYiliaoGongsi,
                jibenYiliaoGeren, dabingYiliaoGongsi, dabingYiliaoGeren, shiyeGongsi, shiyeGeren, gongshangGongsi,
                shengyuGongsi, gongjijinGongsi, gongjijinGeren, qitaGongsi, qitaGeren, canbaojinGongsi, fuwufei, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
    }

    public List<ZaicePo> zaicePaging(int type, String reportingPeriod, Integer userId, String paramName, Integer supplierId, Integer customerId, String cityName) {
        return zaiceMapper.zaicePaging(type, reportingPeriod, userId, "%" + paramName + "%", supplierId, customerId, cityName);
    }

    public Zaice selectByEmployeeMsg(String city, String reportingPeriod, String certificateNumber) {
        List<Zaice> list = zaiceMapper.selectByEmployeeMsg(city, reportingPeriod, certificateNumber);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    // 删除实际在册
    public int updateDeleteFlag(Integer zcId) {
        return zaiceMapper.updateDeleteFlag(zcId);
    }

    @Override
    public void updateBillDetailStatus(Integer zaiceId, Byte isMatchs) {
        zaiceMapper.updateStatusByZaiceId(zaiceId, isMatchs);
    }

    @Override
    public String selectSupplierBySupplierId(String city, Integer supplierId) {
        return zaiceMapper.selectSupplierBySupplierId(city, supplierId);
    }

    @Override
    public String selectHouseType(String cITY, String yearMonth, String certificateNumber) {
        return zaiceMapper.selectHouseType(cITY, yearMonth, certificateNumber);
    }

    @Override
    public Integer getSupplierIdBySupplier(String city, String supplier, String certificateNumber) {
        return zaiceMapper.getSupplierIdBySupplier(city, supplier, certificateNumber);
    }

    @Override
    public String findAccountingUnit(String city, Integer supplierId, String yearMonth, Integer userId) {
        return zaiceMapper.findAccountingUnit(city, supplierId, yearMonth, userId);
    }

    @Override
    public Map<String, Object> selectZaiceMsg(String yearMonth, String city, String certificateNumber) {
        return zaiceMapper.selectZaiceMsg(yearMonth, city, certificateNumber);
    }

    // 根据城市，月份，身份证号修改在册
    public void updateAddSocialStatus(String yearMonth, String city, String certificateNumber) {
        zaiceMapper.updateAddSocialStatus(yearMonth, city, certificateNumber);
    }

    @Override
    public String exportZaice(int type, String yearMonth, Integer userId, Integer supplierId, Integer customerId, String cityName) {
        /* type:1、当月目标在册2、当月实际在册3、当月增员、4当月减员 */
        List<ZaicePo> list = zaicePaging(type, yearMonth, userId, null, supplierId, customerId, cityName);
        // 创建excel工作簿
        SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), 100);
        // 创建第一个sheet（页），命名
        Sheet zengyuanSheet = wb.createSheet("增员");
        Row zyTitleRow = zengyuanSheet.createRow((short) 0);
        String[] titleArr = new String[]{"财务核算单位", "客户名称", "员工姓名", "证件类型", "证件号码", "民族", "手机", "是否外呼", "户籍城市", "户口性质",
                "参保城市", "社保/医保号", "社保基数", "参保类型", "供应商", "社保起缴年月", "公积金账号", "公积金基数", "公积金比例", "公积金起缴年月", "备注"};
        for (int zyTitleIndex = 0; zyTitleIndex < titleArr.length; zyTitleIndex++) {
            zyTitleRow.createCell(zyTitleIndex).setCellValue(titleArr[zyTitleIndex]);
        }
        for (int i1 = 0; i1 < list.size(); i1++) {
            ZaicePo zaicePo = list.get(i1);
            Row row = zengyuanSheet.createRow((short) i1 + 1);
            row.createCell(0).setCellValue(zaicePo.getFinancialAccountingUnit());
            row.createCell(1).setCellValue(zaicePo.getCustomerName());
            row.createCell(2).setCellValue(zaicePo.getEmployeeName());
            row.createCell(3).setCellValue(zaicePo.getCertificateType());
            row.createCell(4).setCellValue(zaicePo.getCertificateNumber());
            row.createCell(5).setCellValue(zaicePo.getNation());
            row.createCell(6).setCellValue(zaicePo.getMobilePhone());
            row.createCell(7).setCellValue(zaicePo.getIsExternalCall());
            row.createCell(8).setCellValue(zaicePo.getHouseholdCity());
            row.createCell(9).setCellValue(zaicePo.getHouseholdProperty());
            row.createCell(10).setCellValue(zaicePo.getCity());
            row.createCell(11).setCellValue(zaicePo.getSocialSecurityCardNumber());
            row.createCell(12).setCellValue(zaicePo.getSocialSecurityBase());
            row.createCell(13).setCellValue(zaicePo.getSocialSecurityType());
            row.createCell(14).setCellValue(zaicePo.getSupplier());
            row.createCell(15).setCellValue(zaicePo.getSocialSecurityBeginTime());
            row.createCell(16).setCellValue(zaicePo.getAccumulationFundNumber());
            row.createCell(17).setCellValue(zaicePo.getAccumulationFundCardinalNumber());
            row.createCell(18).setCellValue(zaicePo.getAccumulationFundRatio());
            row.createCell(19).setCellValue(zaicePo.getAccumulationFundBeginTime());
            row.createCell(20).setCellValue(zaicePo.getRemark());
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String realPath = request.getSession().getServletContext().getRealPath("/");
        String filePath = realPath + "download" + File.separator + "excel" + File.separator
                + UUID.randomUUID().toString() + ".xlsx";
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(filePath);
            wb.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public Zaice findAccumulationInfos(String city, String month, String certificateNum) {
        return zaiceMapper.findAccumulationInfos(city, month, certificateNum);
    }

    @Override
    public String findCertificateType(String name, String certificateNum, Integer supplierIds) {
        return zaiceMapper.findCertificateType(name, certificateNum, supplierIds);
    }

    /**
     * 查询公积金始缴年月
     */
    public String findAccumulationFundBeginTime(String city, Integer supplierId, String month, String certificateNum) {
        return zaiceMapper.findAccumulationFundBeginTime(city, supplierId, month, certificateNum);
    }

    @Override
    public ResultJson findCustomerAndSupplierAndCity(String paramName, User user) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("customerList", customerMapper.customerSpinner("%" + paramName + "%", user.getUId(), user.getRoleType())); // 客户下拉数据
        resultMap.put("supplierList", zaiceMapper.supplierSpinner("%" + paramName + "%", user.getUId(), user.getRoleType()));// 供应商下拉数据
        resultMap.put("cityList", zaiceMapper.citySpinner("%" + paramName + "%", user.getUId(), user.getRoleType()));// 城市下拉数据
        return ResultUtil.success(RestEnum.SUCCESS, resultMap);
    }

    @Override
    public ResultJson baseZaicePageList(Integer currPage, Integer pageSize, String customerName, String yearMonth, Integer type, User user) {
        if (yearMonth == null || type == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }

        if (type != 1 && type != 2 && type != 3 && type != 4) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        Integer status = null;
        Integer zaiceflay = null;
        if (type == 1 || type == 2) {
            status = type;
        } else {
            zaiceflay = type == 3 ? 0 : 1;
        }

        PageHelper.startPage(currPage, pageSize);
        List<ZaiceDataShow> zaiceList = zaiceMapper.baseZaicePageList("%" + customerName + "%", yearMonth, status, zaiceflay, user.getUId(), user.getRoleType());
        PageInfo<ZaiceDataShow> pageInfo = new PageInfo<>(zaiceList);
        return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
    }


    @Override
    public ResultJson exportZaiceSummary(String yearMonth, Integer type, User user) {
        if (yearMonth == null || type == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        String fileName = type == 1 ? "当月增员汇总" : type == 2 ?
                "当月减员汇总" : type == 3 ? "目标在册汇总" : type == 4 ? "实际在册汇总" : "在册数据汇总";
        Integer status = null;
        Integer zaiceflay = null;
        if (type == 1 || type == 2) {
            status = type;
        } else {
            zaiceflay = type == 3 ? 0 : 1;
        }
        List<ZaiceDataShow> zaiceList = zaiceMapper.baseZaicePageList(null, yearMonth, status, zaiceflay, /*user.getUId()*/null, /*user.getRoleType()*/null);
        // 创建excel工作簿
        SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), 100);
        // 创建第一个sheet（页），命名
        Sheet sheet = wb.createSheet(fileName);
        Row zyTitleRow = sheet.createRow(0);
        String[] titleArr = new String[]{"序号", "客户名称", "人员数量", "公司缴纳社保费", "个人缴纳社保费", "社保合计", "公司残保金", "公司公积金", "个人公积金", "公积金合计", "服务费", "合计"};
        for (int zyTitleIndex = 0; zyTitleIndex < titleArr.length; zyTitleIndex++) {
            zyTitleRow.createCell(zyTitleIndex).setCellValue(titleArr[zyTitleIndex]);
        }
        for (int i1 = 0; i1 < zaiceList.size(); i1++) {
            ZaiceDataShow zaicePo = zaiceList.get(i1);
            Row row = sheet.createRow((short) i1 + 1);
            row.createCell(0).setCellValue(i1 + 1);
            row.createCell(1).setCellValue(zaicePo.getCustomerName());
            row.createCell(2).setCellValue(zaicePo.getCoundNum());
            row.createCell(3).setCellValue(zaicePo.getShebaoGongsiHeji());
            row.createCell(4).setCellValue(zaicePo.getShebaoGerenHeji());
            row.createCell(5).setCellValue(zaicePo.getShebaoHeji());
            row.createCell(6).setCellValue(zaicePo.getCanbaojinGongsi());
            row.createCell(7).setCellValue(zaicePo.getGongjijinGongsi());
            row.createCell(8).setCellValue(zaicePo.getGongjijinGeren());
            row.createCell(9).setCellValue(zaicePo.getGongjijinHeji());
            row.createCell(10).setCellValue(zaicePo.getFuwufei());
            row.createCell(11).setCellValue(zaicePo.getHeji());
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String realPath = request.getSession().getServletContext().getRealPath("/");
        String filePath = realPath + "download" + File.separator + "excel" + File.separator
                + UUID.randomUUID().toString() + ".xlsx";
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            wb.write(fileOut);
            fileOut.close();
            DownloadFile.downloadFile(filePath, fileName + ".xls");
            DeleteFile.DeleteFolder(filePath);
        } catch (IOException e) {
            log.error(fileName, "导出失败:{}", e);
            return ResultUtil.error(RestEnum.FAILD);
        }
        return ResultUtil.success(RestEnum.SUCCESS);
    }

    @Override
    public ResultJson zaiceStatistics(String yearMonth, User user) {
        if (yearMonth == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        Map<String, String> zaiceMap = zaiceMapper.zaiceStatistics(yearMonth, user.getUId(), user.getRoleType());
        return ResultUtil.success(RestEnum.SUCCESS, zaiceMap);
    }

    /**
     * @param yearMonth
     * @param supplierId
     * @return
     */
    public List<Zaice> getZengyuanBySupplierId(String yearMonth, int supplierId) {
        return zaiceMapper.getZengyuanBySupplierId(yearMonth, supplierId);
    }

    /**
     * @param yearMonth
     * @param supplierId
     * @return
     */
    public List<Zaice> getJianyuanBySupplierId(String yearMonth, int supplierId) {
        return zaiceMapper.getJianyuanBySupplierId(yearMonth, supplierId);
    }

}
