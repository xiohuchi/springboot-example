package com.dianmi.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListGroupingUtil {
 
	public static void main(String[] args) {
     
		SkuVo sku1 = new SkuVo(1L, "p1", 100L);
		SkuVo sku2 = new SkuVo(2L, "p2", 101L);
		SkuVo sku3 = new SkuVo(3L, "p3", 102L);
		SkuVo sku4 = new SkuVo(3L, "p4", 103L);
		SkuVo sku5 = new SkuVo(2L, "p5", 100L);
		SkuVo sku6 = new SkuVo(5L, "p6", 100L);
		List<SkuVo> skuVoList = Arrays.asList(new SkuVo[] { sku1, sku2, sku3, sku4, sku5, sku6 });
		/* 2、分组算法 **/
		Map<Long, List<SkuVo>> skuIdMap = new HashMap<>();
		for (SkuVo skuVo : skuVoList) {
			List<SkuVo> tempList = skuIdMap.get(skuVo.getId());
			/* 如果取不到数据,那么直接new一个空的ArrayList **/
			if (tempList == null) {
				tempList = new ArrayList<>();
				tempList.add(skuVo);
				skuIdMap.put(skuVo.getId(), tempList);
			} else {
				/* 某个sku之前已经存放过了,则直接追加数据到原来的List里 **/
				tempList.add(skuVo);
			}
		}
		/* 3、遍历map,验证结果 **/
		for (Long skuId : skuIdMap.keySet()) {
			System.out.println(skuIdMap.get(skuId));
		}
	}

	@Override
	public String toString() {
		return "ListGroupingUtil [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}

class SkuVo {
	private Long id;
	private String name;
	private Long size;

	public SkuVo(Long id, String name, Long size) {
		super();
		this.id = id;
		this.name = name;
		this.size = size;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "SkuVo [id=" + id + ", name=" + name + ", size=" + size + "]";
	}
	
}
