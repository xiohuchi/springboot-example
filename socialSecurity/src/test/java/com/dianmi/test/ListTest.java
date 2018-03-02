package com.dianmi.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author:create by lzw
 * @Date:2017年12月5日 下午3:09:30
 * @Description:
 */
public class ListTest {

	public static void main(String args[]) {
		List<String> 集合1 = new ArrayList<String>();
		集合1.add("A");
		集合1.add("B");
		List<String> 集合2 = new ArrayList<String>();
		集合2.add("A");
		集合2.add("B");
		集合2.add("C");
		集合2.add("D");
		集合2.add("E");
		List<String> 集合3 = new ArrayList<String>(集合1);
		集合3.removeAll(集合2);
		System.out.println(集合3);
		List<String> 集合4 = new ArrayList<String>(集合1);
		集合4.removeAll(集合3);
		System.out.println(集合4);
	}
}