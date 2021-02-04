package com.notification.rest.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class NotificationDto {
	private String sender;
	private List<String> receivers;
	private String object;
	private String message;
	private List<Map<String, String>> files;
}
