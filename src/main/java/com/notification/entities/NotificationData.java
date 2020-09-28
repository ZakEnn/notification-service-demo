package com.notification.entities;

import java.util.List;

import lombok.Data;

@Data
public class NotificationData {
	private List<String> receivers;
	private String object;
	private String message;
}
