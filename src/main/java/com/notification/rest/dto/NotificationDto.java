package com.notification.rest.dto;

import java.util.List;

import lombok.Data;

@Data
public class NotificationDto {
	private String sender;
	private List<String> receivers;
	private String object;
	private String message;
}
