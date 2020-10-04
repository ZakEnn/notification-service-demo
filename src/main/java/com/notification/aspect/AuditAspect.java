package com.notification.aspect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
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
			Map<String, Object> audit = new HashMap<>();

			// to be continued ...
			rabbitTemplate.convertAndSend(audit);
		}

	}

	@AfterReturning("execution(* com.notification.rest.*.*(..))")
	public void logStatusNotification(JoinPoint joinPoint) {
		log.info("***** END AfterReturning **** " + joinPoint.getSignature().getName());
		log.info("Arguments : " + Arrays.toString(joinPoint.getArgs()));
		String name = joinPoint.getSignature().getName();

		if (actionAudited.contains(name)) {
			Map<String, Object> audit = new HashMap<>();
			// fill later request data ...
			log.info(audit);
			rabbitTemplate.convertAndSend(audit);
		}
	}
}
