package com.dianmi.mail;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import java.io.File;
import java.util.Date;

/**
 * created by www
 * 2017/9/26 16:11
 */
public class CommonsEmailUtils {
    private String host = "smtp.qq.com";
    private int port = 587;
    private String userName = "153549111@qq.com";
    private String password = "mqlcrwjntpugbggg";
    private String[] toCc = new String[]{"153549111@qq.com","153549111@qq.com"};
    private String[] to = new String[]{"153549111@qq.com","153549111@qq.com"};

    public static void main(String args[]){
       CommonsEmailUtils commonsEmailUtils = new CommonsEmailUtils();
        try {
            commonsEmailUtils.sendTextMail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送文本邮件
     *
     * @throws Exception
     */
    public void sendTextMail() throws Exception {
        SimpleEmail mail = new SimpleEmail();
        // 设置邮箱服务器信息
        mail.setSmtpPort(port);
        mail.setHostName(host);
        // 设置密码验证器
        mail.setAuthentication(userName, password);
        // 设置邮件发送者
        mail.setFrom(userName);
        // 设置邮件接收者
        mail.addTo(to);
        //设置邮件抄送者
        mail.addCc(toCc);
        // 设置邮件编码
        mail.setCharset("UTF-8");
        // 设置邮件主题
        mail.setSubject("Test Email");
        // 设置邮件内容
        mail.setMsg("this is a test Text mail");
        // 设置邮件发送时间
        mail.setSentDate(new Date());
        // 发送邮件
        mail.send();
    }

    /**
     * 发送Html邮件
     *
     * @throws Exception
     */
    public void sendHtmlMail() throws Exception {
        HtmlEmail mail = new HtmlEmail();
        // 设置邮箱服务器信息
        mail.setSmtpPort(port);
        mail.setHostName(host);
        // 设置密码验证器
        mail.setAuthentication(userName, password);
        // 设置邮件发送者
        mail.setFrom(userName);
        // 设置邮件接收者
        mail.addTo(to);
        // 设置邮件编码
        mail.setCharset("UTF-8");
        // 设置邮件主题
        mail.setSubject("Test Email");
        // 设置邮件内容
        mail.setHtmlMsg(
                "<html><body><img src='longzhiwei.jpg'/><div>this is a HTML email.</div></body></html>");
        // 设置邮件发送时间
        mail.setSentDate(new Date());
        // 发送邮件
        mail.send();
    }

    /**
     * 发送内嵌图片邮件
     *
     * @throws Exception
     */
    public void sendImageMail() throws Exception {
        HtmlEmail mail = new HtmlEmail();
        // 设置邮箱服务器信息
        mail.setSmtpPort(port);
        mail.setHostName(host);
        // 设置密码验证器
        mail.setAuthentication(userName, password);
        // 设置邮件发送者
        mail.setFrom(userName);
        // 设置邮件接收者
        mail.addTo(to);
        // 设置邮件编码
        mail.setCharset("UTF-8");
        // 设置邮件主题
        mail.setSubject("Test Email");
        mail.embed(new File("longzhiwei.jpg"), "image");
        // 设置邮件内容
        String htmlText = "<html><body><img src='cid:image'/><div>this is a HTML email.</div></body></html>";
        mail.setHtmlMsg(htmlText);
        // 设置邮件发送时间
        mail.setSentDate(new Date());
        // 发送邮件
        mail.send();
    }

    /**
     * 发送附件邮件
     *
     * @throws Exception
     */
    public void sendAttachmentMail() throws Exception {
        MultiPartEmail mail = new MultiPartEmail();
        // 设置邮箱服务器信息
        mail.setSmtpPort(port);
        mail.setHostName(host);
        // 设置密码验证器
        mail.setAuthentication(userName, password);
        // 设置邮件发送者
        mail.setFrom(userName);
        // 设置邮件接收者
        mail.addTo(to);
        // 设置邮件编码
        mail.setCharset("UTF-8");
        // 设置邮件主题
        mail.setSubject("Test Email");
        mail.setMsg("this is a Attachment email.this email has a attachment!");
        // 创建附件
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath("longzhiwei.jpg");
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setName("longzhiwei.jpg");
        mail.attach(attachment);
        // 设置邮件发送时间
        mail.setSentDate(new Date());
        // 发送邮件
        mail.send();
    }

    /**
     * 发送内嵌图片和附件邮件
     *
     * @throws Exception
     */
    public void sendImageAndAttachmentMail() throws Exception {
        HtmlEmail mail = new HtmlEmail();
        // 设置邮箱服务器信息
        mail.setSmtpPort(port);
        mail.setHostName(host);
        // 设置密码验证器
        mail.setAuthentication(userName, password);
        // 设置邮件发送者
        mail.setFrom(userName);
        // 设置邮件接收者
        mail.addTo(to);
        // 设置邮件编码
        mail.setCharset("UTF-8");
        // 设置邮件主题
        mail.setSubject("Test Email");
        mail.embed(new File("longzhiwei.jpg"), "image");
        // 设置邮件内容
        String htmlText = "<html><body><img src='cid:image'/><div>this is a HTML email.</div></body></html>";
        mail.setHtmlMsg(htmlText);
        // 创建附件
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath("longzhiwei.jpg");
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setName("longzhiwei.jpg");
        mail.attach(attachment);
        // 设置邮件发送时间
        mail.setSentDate(new Date());
        // 发送邮件
        mail.send();
    }
}