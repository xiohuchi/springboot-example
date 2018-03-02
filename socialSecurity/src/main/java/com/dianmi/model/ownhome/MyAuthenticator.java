package com.dianmi.model.ownhome;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
/** 
 * 发邮件的身份验证器 
 */
@NoArgsConstructor
public class MyAuthenticator extends Authenticator {
	String userName=null;     
	String password=null;     
	        

	public MyAuthenticator(String username, String password) {      
	      this.userName = username;      
	      this.password = password;      
	}    
	protected PasswordAuthentication getPasswordAuthentication(){     
	      return new PasswordAuthentication(userName, password);     
	}     






}
