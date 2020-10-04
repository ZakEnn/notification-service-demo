package com.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

	@Value("${rabbitmq.url}")
	private String url;
	@Value("${rabbitmq.port}")
	private int port;
	@Value("${rabbitmq.user}")
	private String user;
	@Value("${rabbitmq.password}")
	private String password;
	@Value("${rabbitmq.queue}")
	private String queue;

	@Bean
	public Queue auditQueue() {
		return new Queue(queue);
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(url);
		connectionFactory.setPort(port);
		connectionFactory.setUsername(user);
		connectionFactory.setPassword(password);
		return connectionFactory;
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setRoutingKey("audit-service-queue");
		template.setMessageConverter(jsonMessageConverter());
		return template;
	}

}
