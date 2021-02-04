package com.notification.service;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.notification.rest.dto.NotificationDto;

import javax.mail.MessagingException;

import javax.mail.internet.MimeMessage;

import java.util.Base64;
import java.util.Map;

@Service
@CommonsLog
public class NotificationService {

	@Autowired
	JavaMailSender mailSender;

	@Value("${footer-img-b64}")
	String appFooterImg;

	public NotificationDto sendMail(NotificationDto data) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(data.getSender());
		msg.setTo(data.getReceivers().stream().toArray(String[]::new));
		msg.setSubject(data.getObject());
		msg.setText(data.getMessage());

		mailSender.send(msg);
		return data;
	}

	public NotificationDto sendMailWithAttachment(NotificationDto data){
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper msg = new MimeMessageHelper(message, true);
			msg.setFrom(data.getSender());
			msg.setTo(data.getReceivers().stream().toArray(String[]::new));
			msg.setSubject(data.getObject());
			msg.setText(data.getMessage());
			byte[] doc = Base64.getUrlDecoder().decode(appFooterImg);
			msg.addAttachment("footer.png", new ByteArrayResource(doc));
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return data;
	}

	private void addAttachments(NotificationDto message, MimeMessageHelper helper) {
		message.getFiles().forEach(file -> addAttachment(file, helper));
	}

	private void addAttachment(Map<String,String> fileB64, MimeMessageHelper helper) {
		String fileName = fileB64.get("name");
		byte[] file = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(fileB64.get("value"));
		try {
			helper.addAttachment(fileName, new ByteArrayResource(file));
			log.debug("Added a file atachment: " + fileName);
		} catch (MessagingException ex) {
			log.error("Failed to add a file atachment: " + fileName, ex);
		}
	}

}
