# notification-service-demo
Spring boot application that uses javaMail to send notification and enable logging capabilities using AOP and RabbitMQ.

# Application setup
Run the app using maven command:

mvn spring-boot:run -Dspring-boot.run.arguments=--spring.cloud.config.uri=http://localhost:8888, --spring.profiles.active=dev
