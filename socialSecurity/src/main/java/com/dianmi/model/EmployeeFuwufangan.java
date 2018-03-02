package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeFuwufangan {
    private Integer efId;

    private Integer zaiceId;

    private Double yanglaoGongsi;

    private Double yanglaoGeren;

    private Double jibenYiliaoGongsi;

    private Double jibenYiliaoGeren;

    private Double shiyeGongsi;

    private Double shiyeGeren;

    private Double dabingYiliaoGongsi;

    private Double dabingYiliaoGeren;

    private Double shengyuQiye;

    private Double gongshangQiye;

    private Double fuwufeiGongsi;

    private Double canlianjinGongsi;

    private Double qitaGongsi;

    private Double qitaGeren;

    public EmployeeFuwufangan(Integer zaiceId, Double yanglaoGongsi, Double yanglaoGeren, Double jibenYiliaoGongsi,
			Double jibenYiliaoGeren, Double shiyeGongsi, Double shiyeGeren, Double dabingYiliaoGongsi,
			Double dabingYiliaoGeren, Double shengyuQiye, Double gongshangQiye, Double fuwufeiGongsi,
			Double canlianjinGongsi, Double qitaGongsi, Double qitaGeren) {
		super();
		this.zaiceId = zaiceId;
		this.yanglaoGongsi = yanglaoGongsi;
		this.yanglaoGeren = yanglaoGeren;
		this.jibenYiliaoGongsi = jibenYiliaoGongsi;
		this.jibenYiliaoGeren = jibenYiliaoGeren;
		this.shiyeGongsi = shiyeGongsi;
		this.shiyeGeren = shiyeGeren;
		this.dabingYiliaoGongsi = dabingYiliaoGongsi;
		this.dabingYiliaoGeren = dabingYiliaoGeren;
		this.shengyuQiye = shengyuQiye;
		this.gongshangQiye = gongshangQiye;
		this.fuwufeiGongsi = fuwufeiGongsi;
		this.canlianjinGongsi = canlianjinGongsi;
		this.qitaGongsi = qitaGongsi;
		this.qitaGeren = qitaGeren;
	}

	public EmployeeFuwufangan(Integer efId, Integer zaiceId, Double yanglaoGongsi, Double yanglaoGeren, Double jibenYiliaoGongsi, Double jibenYiliaoGeren, Double shiyeGongsi, Double shiyeGeren, Double dabingYiliaoGongsi, Double dabingYiliaoGeren, Double shengyuQiye, Double gongshangQiye, Double fuwufeiGongsi, Double canlianjinGongsi, Double qitaGongsi, Double qitaGeren) {
        this.efId = efId;
        this.zaiceId = zaiceId;
        this.yanglaoGongsi = yanglaoGongsi;
        this.yanglaoGeren = yanglaoGeren;
        this.jibenYiliaoGongsi = jibenYiliaoGongsi;
        this.jibenYiliaoGeren = jibenYiliaoGeren;
        this.shiyeGongsi = shiyeGongsi;
        this.shiyeGeren = shiyeGeren;
        this.dabingYiliaoGongsi = dabingYiliaoGongsi;
        this.dabingYiliaoGeren = dabingYiliaoGeren;
        this.shengyuQiye = shengyuQiye;
        this.gongshangQiye = gongshangQiye;
        this.fuwufeiGongsi = fuwufeiGongsi;
        this.canlianjinGongsi = canlianjinGongsi;
        this.qitaGongsi = qitaGongsi;
        this.qitaGeren = qitaGeren;
    }
}