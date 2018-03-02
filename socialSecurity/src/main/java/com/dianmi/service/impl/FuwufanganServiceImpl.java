package com.dianmi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dianmi.mapper.FuwufanganMapper;
import com.dianmi.model.Fuwufangan;
import com.dianmi.model.po.FuwufanganPo;
import com.dianmi.service.FuwufanganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author www
 */
@Service
public class FuwufanganServiceImpl implements FuwufanganService {

    @Autowired
    private FuwufanganMapper fuwufanganMapper;

    public List<FuwufanganPo> getAll(String city) {
        return fuwufanganMapper.selectAll("%" + city + "%");
    }

    /**
     * @param employeeInfo
     * @return
     */
    public List<Map<String, Object>> getSuppliers(int userId) {
        return fuwufanganMapper.getSupplier(userId);
    }

    /**
     * @param strArr
     * @return
     */
    public List<String> arrayRemoveDuplicate(String[] strArr) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < strArr.length; i++) {
            if (!list.contains(strArr[i])) {
                list.add(strArr[i]);
            }
        }
        return list;
    }

    /**
     * @param list
     * @return
     */
    public static List<Map<String, Object>> listRemoveDuplicate(List<Map<String, Object>> list) {
        List<Map<String, Object>> listTemp = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < list.size(); i++) {
            if (!listTemp.contains(list.get(i))) {
                listTemp.add(list.get(i));
            }
        }
        return listTemp;
    }

    /**
     * @param fuwufanganStr
     */
    public void add(String fuwufanganStr) {
        fuwufanganStr = fuwufanganStr.substring(fuwufanganStr.indexOf("{"), fuwufanganStr.lastIndexOf("}") + 1);
        Fuwufangan fuwufangan = JSONObject.parseObject(fuwufanganStr, Fuwufangan.class);

//		JSONObject jsonObject = JSONObject.parseObject(fuwufanganStr);
//		Fuwufangan fuwufangan = new Fuwufangan(jsonObject.getByte("type"), jsonObject.getString("cityId"),
//				jsonObject.getInteger("supplierId"), jsonObject.getInteger("customerId"),
//				jsonObject.getString("productName"), jsonObject.getString("householdProperty"),
//				jsonObject.getString("socialSecurityType"), jsonObject.getString("implementDate"),
//				jsonObject.getDouble("socialSecurityMinBase"), jsonObject.getDouble("pensionCompanyPay"),
//				jsonObject.getDouble("pensionPersonPay"), jsonObject.getDouble("medicalCompanyPay"),
//				jsonObject.getDouble("medicalPersonPay"), jsonObject.getDouble("unemploymentCompanyPay"),
//				jsonObject.getDouble("unemploymentPersonPay"), jsonObject.getDouble("seriousIllnessCompanyPay"),
//				jsonObject.getDouble("seriousIllnessPersonPay"), jsonObject.getDouble("procreateCompanyPay"),
//				jsonObject.getDouble("otherCompanyPay"), jsonObject.getDouble("otherPersonPay"),
//				jsonObject.getDouble("injuryCompanyPay"), jsonObject.getDouble("serviceCharge"),
//				jsonObject.getDouble("disabilityGuaranteeFund"), jsonObject.getString("version"));
        fuwufanganMapper.insertSelective(fuwufangan);
    }

    /**
     *
     */
    public Fuwufangan getById(int fwId) {
        return fuwufanganMapper.selectByPrimaryKey(fwId);
    }

    public void update(String fuwufanganStr) {
        fuwufanganStr = fuwufanganStr.substring(fuwufanganStr.indexOf("{"), fuwufanganStr.lastIndexOf("}") + 1);
        Fuwufangan fuwufangan = JSONObject.parseObject(fuwufanganStr, Fuwufangan.class);

//        JSONObject jsonObject = JSONObject.parseObject(fuwufanganStr);
//        Fuwufangan fuwufangan = new Fuwufangan(jsonObject.getInteger("fwId"), jsonObject.getByte("type"), jsonObject.getString("cityId"),
//                jsonObject.getInteger("supplierId"), jsonObject.getInteger("customerId"),
//                jsonObject.getString("productName"), jsonObject.getString("householdProperty"),
//                jsonObject.getString("socialSecurityType"), jsonObject.getString("implementDate"),
//                jsonObject.getDouble("socialSecurityMinBase"), jsonObject.getDouble("pensionCompanyPay"),
//                jsonObject.getDouble("pensionPersonPay"), jsonObject.getDouble("medicalCompanyPay"),
//                jsonObject.getDouble("medicalPersonPay"), jsonObject.getDouble("unemploymentCompanyPay"),
//                jsonObject.getDouble("unemploymentPersonPay"), jsonObject.getDouble("seriousIllnessCompanyPay"),
//                jsonObject.getDouble("seriousIllnessPersonPay"), jsonObject.getDouble("procreateCompanyPay"),
//                jsonObject.getDouble("otherCompanyPay"), jsonObject.getDouble("otherPersonPay"),
//                jsonObject.getDouble("injuryCompanyPay"), jsonObject.getDouble("serviceCharge"),
//                jsonObject.getDouble("disabilityGuaranteeFund"), jsonObject.getString("version"));
        fuwufanganMapper.updateByPrimaryKeySelective(fuwufangan);
    }

    public void delete(int fwId) {
        fuwufanganMapper.deleteByPrimaryKey(fwId);
    }

}