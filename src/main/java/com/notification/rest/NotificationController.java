package com.notification.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.notification.rest.dto.NotificationDto;
import com.notification.service.NotificationService;

import lombok.extern.apachecommons.CommonsLog;

@RestController
@CommonsLog
public class NotificationController {

	@Autowired
	NotificationService notificationService;

	@PostMapping("/send-notification")
	public ResponseEntity<NotificationDto> sendNotification(@RequestBody NotificationDto data) {
		log.info("mail info : " + data);
		NotificationDto sentNotification = notificationService.sendMailWithAttachment(data);
		return ResponseEntity.status(HttpStatus.OK).body(sentNotification);
	}

}
