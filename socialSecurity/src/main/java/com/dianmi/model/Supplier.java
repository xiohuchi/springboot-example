package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Supplier {
    private Integer sId;

    private Integer userId;
    
    private Byte type;

    private String manager;

    private String province;

    private String city;

    private Byte canbaoType;

    private Byte isCooperation;

    private String supplierName;

    private String supplierContacts;

    private String supplierPhone;

    private String supplierEmail;

    private String supplierDockingPeople;

    private String supplierDockingPeoplePhone;

    private String supplierDockingPeopleEmail;

    private String supplierDockingPeopleQq;

    private String serviceCharge;

    private String paymentTime;

    private String contractBeginTime;

    private String contractEndTime;

    private String supplierAccountBankName;

    private String supplierAccountName;

    private String supplierBankAccount;

    private String supplierCompanyAddress;

    private String agreementPaymentMethod;

    private String remark;

    private Date activeTime;

    public Supplier(Integer sId, Integer userId,Byte type, String manager, String province, String city, Byte canbaoType, Byte isCooperation, String supplierName, String supplierContacts, String supplierPhone, String supplierEmail, String supplierDockingPeople, String supplierDockingPeoplePhone, String supplierDockingPeopleEmail, String supplierDockingPeopleQq, String serviceCharge, String paymentTime, String contractBeginTime, String contractEndTime, String supplierAccountBankName, String supplierAccountName, String supplierBankAccount, String supplierCompanyAddress, String agreementPaymentMethod, String remark, Date activeTime) {
        this.sId = sId;
        this.userId = userId;
        this.type = type;
        this.manager = manager;
        this.province = province;
        this.city = city;
        this.canbaoType = canbaoType;
        this.isCooperation = isCooperation;
        this.supplierName = supplierName;
        this.supplierContacts = supplierContacts;
        this.supplierPhone = supplierPhone;
        this.supplierEmail = supplierEmail;
        this.supplierDockingPeople = supplierDockingPeople;
        this.supplierDockingPeoplePhone = supplierDockingPeoplePhone;
        this.supplierDockingPeopleEmail = supplierDockingPeopleEmail;
        this.supplierDockingPeopleQq = supplierDockingPeopleQq;
        this.serviceCharge = serviceCharge;
        this.paymentTime = paymentTime;
        this.contractBeginTime = contractBeginTime;
        this.contractEndTime = contractEndTime;
        this.supplierAccountBankName = supplierAccountBankName;
        this.supplierAccountName = supplierAccountName;
        this.supplierBankAccount = supplierBankAccount;
        this.supplierCompanyAddress = supplierCompanyAddress;
        this.agreementPaymentMethod = agreementPaymentMethod;
        this.remark = remark;
        this.activeTime = activeTime;
    }
}