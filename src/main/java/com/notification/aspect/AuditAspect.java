package com.notification.aspect;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.notification.rest.dto.NotificationDto;
import com.notification.utils.NotificationConstants;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@Component
@Aspect
public class AuditAspect {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	private List<String> actionAudited = Arrays.asList("sendNotification", "somethingElse");

	@AfterThrowing("execution(* com.notification.rest.NotificationController.*(..))")
	public void logAfter(JoinPoint joinPoint) {
		log.info("***** END AfterThrowing  **** " + joinPoint.getSignature().getDeclaringType().getSimpleName());
		String name = joinPoint.getSignature().getName();
		if (actionAudited.contains(name)) {
			Map<String, Object> audit = getAudit();
			audit.put(NotificationConstants.ACTION, name);
			log.info(audit);

			rabbitTemplate.convertAndSend(audit);
		}

	}

	@AfterReturning("execution(* com.notification.rest.*.*(..))")
	public void logStatusNotification(JoinPoint joinPoint) {
		log.info("***** END AfterReturning **** " + joinPoint.getSignature().getName());
		log.info("Arguments : " + Arrays.toString(joinPoint.getArgs()));
		String name = joinPoint.getSignature().getName();

		if (actionAudited.contains(name)) {
			Map<String, Object> audit = getAudit();
			audit.put(NotificationConstants.ACTION, name);

			NotificationDto notificationData = (NotificationDto) joinPoint.getArgs()[0];
			audit.put(NotificationConstants.MAIL_SENDER, notificationData.getSender());
			audit.put(NotificationConstants.MAIL_RECEIVERS, notificationData.getReceivers());
			audit.put(NotificationConstants.MAIL_OBJECT, notificationData.getObject());
			audit.put(NotificationConstants.MAIL_CONTENT, notificationData.getMessage());

			log.info(audit);
			rabbitTemplate.convertAndSend(audit);
		}
	}

	private Map<String, Object> getAudit() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();

		Map<String, Object> audit = new HashMap<>();
		audit.put(NotificationConstants.SERVICE, "notification-service");
		audit.put(NotificationConstants.USER_AGENT, request.getHeader(NotificationConstants.USER_AGENT));
		audit.put(NotificationConstants.METHOD, request.getMethod());
		audit.put(NotificationConstants.DATE, new Date().getTime());

		return audit;
	}
}
