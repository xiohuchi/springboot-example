package com.dianmi.common;
import java.io.File;  
import java.util.Date;  
import java.util.Enumeration;  
import java.util.Properties;  
import java.util.Vector;  
  
import javax.activation.DataHandler;  
import javax.activation.FileDataSource;  
import javax.mail.Address;  
import javax.mail.BodyPart;  
import javax.mail.Message;  
import javax.mail.MessagingException;  
import javax.mail.Multipart;  
import javax.mail.Session;  
import javax.mail.Transport;  
import javax.mail.internet.InternetAddress;  
import javax.mail.internet.MimeBodyPart;  
import javax.mail.internet.MimeMessage;  
import javax.mail.internet.MimeMultipart;

import com.dianmi.model.ownhome.MailSending;
import com.dianmi.model.ownhome.MyAuthenticator;

    
public class OwnSimpleMail  {      
/** *//**    
  * 以文本格式发送邮件    
  * @param mailInfo 待发送的邮件的信息    
  */      
    public boolean sendTextMail(MailSending mailInfo) {      
      // 判断是否需要身份认证      
      MyAuthenticator authenticator = null;      
      Properties pro = mailInfo.getProperties();     
      if (mailInfo.isValidate()) {      
      // 如果需要身份认证，则创建一个密码验证器      
        authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());      
      }     
      // 根据邮件会话属性和密码验证器构造一个发送邮件的session      
      Session sendMailSession = Session.getDefaultInstance(pro,authenticator);      
      try {      
      // 根据session创建一个邮件消息      
      Message mailMessage = new MimeMessage(sendMailSession);      
      // 创建邮件发送者地址    authenticator  
      Address from = new InternetAddress(mailInfo.getFromAddress());      
      // 设置邮件消息的发送者      
      mailMessage.setFrom(from);      
      // 创建邮件的接收者地址，并设置到邮件消息中      
      String[] mailToAddress = mailInfo.getToAddress() ;  
      int len = mailToAddress.length ;  
      Address to[] = new InternetAddress[len] ;  
      for(int i=0;i<len;i++){  
          to[i] = new InternetAddress(mailToAddress[i]) ;  
      }  
      // Message.RecipientType.TO属性表示接收者的类型为TO      
      //TO表示主要接收人，CC表示抄送人，BCC表示秘密抄送人。  
      mailMessage.setRecipients(Message.RecipientType.TO,to);  
      // 设置邮件消息的主题      
      mailMessage.setSubject(mailInfo.getSubject());      
      // 设置邮件消息发送的时间      
      mailMessage.setSentDate(new Date());      
      // 设置邮件消息的主要内容      
      String mailContent = mailInfo.getContent();      
      mailMessage.setText(mailContent);      
      // 发送邮件      
      Transport.send(mailMessage);     
      return true;      
      } catch (MessagingException ex) {      
          ex.printStackTrace();      
      }      
      return false;      
    }      
         
    /** *//**    
      * 以HTML格式发送邮件    
      * @param mailInfo 待发送的邮件信息    
      */      
    public boolean sendHtmlMail(MailSending mailInfo){      
      // 判断是否需要身份认证      
      MyAuthenticator authenticator = null;     
      Properties pro = mailInfo.getProperties();     
      //如果需要身份认证，则创建一个密码验证器       
      if (mailInfo.isValidate()) {      
        authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());     
      }      
      // 根据邮件会话属性和密码验证器构造一个发送邮件的session      
      Session sendMailSession = Session.getDefaultInstance(pro,authenticator);      
      try {      
      // 根据session创建一个邮件消息      
      Message mailMessage = new MimeMessage(sendMailSession);      
      // 创建邮件发送者地址      
      Address from = new InternetAddress(mailInfo.getFromAddress());      
      // 设置邮件消息的发送者      
      mailMessage.setFrom(from);      
      // 创建邮件的接收者地址，并设置到邮件消息中      
      String[] mailToAddress = mailInfo.getToAddress() ;  
      int len = mailToAddress.length ;  
      Address to[] = new InternetAddress[len] ;  
      for(int i=0;i<len;i++){  
          to[i] = new InternetAddress(mailToAddress[i]) ;  
      }  
      // Message.RecipientType.TO属性表示接收者的类型为TO  
      //mailMessage.setRecipient(Message.RecipientType.TO, to) ;  
      mailMessage.setRecipients(Message.RecipientType.TO,to);      
      // 设置邮件消息的主题      
      mailMessage.setSubject(mailInfo.getSubject());      
      // 设置邮件消息发送的时间      
      mailMessage.setSentDate(new Date());      
      // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象      
      Multipart mainPart = new MimeMultipart();      
      // 创建一个包含HTML内容的MimeBodyPart      
      BodyPart html = new MimeBodyPart();      
      // 设置HTML内容      
      html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");      
      mainPart.addBodyPart(html);      
      //向multipart中添加附件  
      Vector<File> file = mailInfo.getFile() ;  
      String fileName = mailInfo.getFileName() ;  
      Enumeration<File> efile = file.elements() ;  
      while(efile.hasMoreElements()){  
          MimeBodyPart mdpFile = new MimeBodyPart() ;  
          fileName = efile.nextElement().toString() ;  
          FileDataSource fds = new FileDataSource(fileName) ;  
          mdpFile.setDataHandler(new DataHandler(fds)) ;  
          //这个方法可以解决乱码问题  
          String fileName1 = new String(fds.getName().getBytes(),"ISO-8859-1") ;  
          mdpFile.setFileName(fileName1) ;  
          mainPart.addBodyPart(mdpFile) ;  
      }  
      file.removeAllElements() ;  
      // 将MiniMultipart对象设置为邮件内容      
      mailMessage.setContent(mainPart);      
      // 发送邮件      
      Transport.send(mailMessage);      
      return true;      
      } catch (Exception ex) {      
          ex.printStackTrace();      
      }      
      return false;      
    }      
}     
