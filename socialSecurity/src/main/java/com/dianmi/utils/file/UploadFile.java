package com.dianmi.utils.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * created by www 2017/11/10 16:18
 */
public class UploadFile {
	
	/**
	 * @param file
	 * @return
	 * 上傳文件
	 */
	public static String uploadFile(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		String filePath = request.getSession().getServletContext().getRealPath("/") + "upload" + File.separator
				+ "excel" + File.separator;
		String newFileName = UUID.randomUUID() + fileExtension;
		try {
			upload(file, filePath, newFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath + newFileName;
	}

	public static void upload(byte[] file, String filePath, String fileName) throws Exception {
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		FileOutputStream out = new FileOutputStream(filePath + fileName);
		out.write(file);
		out.flush();
		out.close();
	}

	public static void upload(MultipartFile file, String filePath, String fileName) throws IOException {
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		FileOutputStream out = new FileOutputStream(filePath + fileName);
		out.write(file.getBytes());
		out.flush();
		out.close();
	}
}