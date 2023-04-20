package com.example.demo;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {
	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtpout.secureserver.net");
		mailSender.setPort(465);

		mailSender.setUsername("accounts@madhurams.co.uk");
		mailSender.setPassword("Madhurams@2022$#");

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.smtp.host", "smtpout.secureserver.net");
		props.put("mail.from", "accounts@madhurams.co.uk");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.auth.login.disable", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		return mailSender;
	}
}
