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

import javax.mail.BodyPart;
import javax.mail.MessagingException;

import javax.mail.internet.*;

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
			MimeMessageHelper msgHelper = new MimeMessageHelper(message, true);
			msgHelper.setFrom(data.getSender());
			msgHelper.setTo(data.getReceivers().stream().toArray(String[]::new));
			msgHelper.setSubject(data.getObject());

			String htmlText = data.getMessage() + "<br><img src='data:image/png;base64," + appFooterImg + "' alt='embedded footer' />";
			msgHelper.setText(htmlText, true);

			addAttachments(data, msgHelper);

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
		byte[] file = Base64.getUrlDecoder().decode(fileB64.get("value"));
		try {
			helper.addAttachment(fileName, new ByteArrayResource(file));
			log.debug("Added a file attachment: " + fileName);
		} catch (MessagingException ex) {
			log.error("Failed to add a file attachment: " + fileName, ex);
		}
	}

}
