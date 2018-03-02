package com.dianmi.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CopyFile {
	public static void copyFile(String fromFilePath, String toFilePath) {
		try {
			File fromFile = new File(fromFilePath);
			File toFile = new File(toFilePath);
			if (toFile.exists()) {
				toFile.delete();
			} 
			FileInputStream ins = new FileInputStream(fromFile);
			FileOutputStream outs = new FileOutputStream(toFile);
			byte[] b = new byte[1024];
			int n = 0;
			while ((n = ins.read(b)) != -1) {
				outs.write(b, 0, n);
			}
			ins.close();
			outs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}