package com.dianmi.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * created by www 2017/10/10 16:19
 */
@SuppressWarnings("Duplicates")
public class DownloadFile {
	// 文件下载
	public static void downloadFile(String filePath, String fileName) throws IOException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		response.setHeader("content-type", "application/octet-stream");
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		long fileLength = new File(filePath).length();
		// 判断浏览器类型并解决下载在主流浏览器文件中文命名乱码问题
		if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {// 火狐浏览器
			fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
		} else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {// IE浏览器
			fileName = URLEncoder.encode(fileName, "UTF-8");
		} else if (request.getHeader("User-Agent").toUpperCase().indexOf("CHROME") > 0) {// CHROME浏览器
			fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
		}
		response.reset();
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		//response.setContentType("application/octet-stream");
		response.setHeader("Access-Control-Allow-Origin", "*");//解决跨域问题
		response.setHeader("Content-disposition", "attachment;filename=\"" + fileName + "\"");
		response.setHeader("Content-Length", String.valueOf(fileLength));
		bis = new BufferedInputStream(new FileInputStream(filePath));
		bos = new BufferedOutputStream(response.getOutputStream());
		byte[] buff = new byte[2048];
		int bytesRead;
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			bos.write(buff, 0, bytesRead);
		}
		bis.close();
		bos.close();
	}

	public static ResponseEntity<byte[]> springMVCDownloadFile(String filePath) throws IOException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		File file = new File(filePath);
		HttpHeaders headers = new HttpHeaders();
		String filename = file.getName();
		if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {// 火狐浏览器
			filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
		} else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
			filename = URLEncoder.encode(filename, "UTF-8");
		} else if (request.getHeader("User-Agent").toUpperCase().indexOf("CHROME") > 0) {
			filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
		}
		headers.setContentDispositionFormData("attachment", filename);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
	}
}
