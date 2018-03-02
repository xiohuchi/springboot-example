package com.dianmi.model.ownhome;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

import javax.mail.internet.MimeUtility;

//发送邮件的信息类
@Data
@NoArgsConstructor
public class MailSending {
	    // 发送邮件的服务器的IP和端口      
	    private String mailServerHost;      
	    private String mailServerPort = "25";      
	     
	    private String fromAddress;      // 邮件发送者的地址     
	        
	    private String[] toAddress;      // 邮件接收者的地址  
	       
	    private String userName;      // 登陆邮件发送服务器的用户名和密码   
	    private String password;      
	     
	    private boolean validate = false;      // 是否需要身份验证     
	         
	    private String subject;      // 邮件主题 
	        
	    private String content;      // 邮件的文本内容  
	     
	    private String fileName = "" ;      // 邮件附件的文件名     
	    
	    private Vector<File> file = new Vector<File>() ;  //附件文件集合  
	      
	    /**  
	     * 获得邮件会话属性     
	     */  
	    public Properties getProperties(){      
	      Properties p = new Properties();      
	      p.put("mail.smtp.host", this.mailServerHost);      
	      p.put("mail.smtp.port", this.mailServerPort);      
	      p.put("mail.smtp.auth", validate ? "true" : "false");      
	      return p;      
	    }
	    /**    
	      *    
	      * 方法说明：把主题转换为中文   
	      * 输入参数：String strText   
	      * 返回类型：    
	      */      
	     public String transferChinese(String strText) {      
	         try {      
	             strText = MimeUtility.encodeText(new String(strText.getBytes(),      
	                     "GB2312"), "GB2312", "B");      
	         } catch (Exception e) {      
	             e.printStackTrace();      
	         }      
	         return strText;      
	     }    
	 
}