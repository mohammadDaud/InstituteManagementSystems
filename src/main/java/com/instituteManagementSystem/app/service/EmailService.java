package com.instituteManagementSystem.app.service;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.instituteManagementSystem.app.constent.EmailConstant;
import com.sun.mail.smtp.SMTPTransport;

@Service
public class EmailService {
	
	public void sendNewPasswordEmail(String firstName,String password,String email) 
	{
		Message message=createEmail(firstName, password, email);
		try {
			SMTPTransport smtpTransport=(SMTPTransport) getEmailSession().getTransport(EmailConstant.SIMPLE_MAIL_TRANSFER_PROTOCAL);
			smtpTransport.connect(EmailConstant.GMAIL_SMTP_SERVER,EmailConstant.USERNAME,EmailConstant.PASSWORD);
			smtpTransport.sendMessage(message, message.getAllRecipients());
			smtpTransport.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private Message createEmail(String firstName,String password,String email) 
	{
		Message message=new MimeMessage(getEmailSession());
		
		try {
				message.setFrom(new InternetAddress(EmailConstant.FROM_EMAIL));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email,false));
				message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailConstant.CC_EMAIL,false));
				message.setSubject(EmailConstant.EMAIL_SUBJECT);
				message.setText("Hello "+firstName+",\n\n your IMS accout password is:"+password+"\n\n The Support Team Of Institute Management Systems");
				message.setSentDate(new Date());
				message.saveChanges();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		
		return message;
	}
	private Session getEmailSession()
	{
		Properties properties=System.getProperties();
		properties.put(EmailConstant.SMTP_HOST, EmailConstant.GMAIL_SMTP_SERVER);
		properties.put(EmailConstant.SMTP_AUTH, true);
		properties.put(EmailConstant.SMTP_PORT, EmailConstant.DEFAULT_PORT);
		properties.put(EmailConstant.SMTP_STARTTLS_ENABLE, true);
		properties.put(EmailConstant.SMTP_STARTTLS_REQUIRED, true);
		return Session.getInstance(properties,null);
	}
}
