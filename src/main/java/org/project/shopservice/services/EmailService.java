package org.project.shopservice.services;

import lombok.RequiredArgsConstructor;
import org.project.shopservice.dtos.onResponse.SendEmailDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String emailSender;

	public void sendEmail(SendEmailDto dto) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(dto.to());
		mailMessage.setFrom(emailSender);
		mailMessage.setSubject(dto.subject());
		mailMessage.setText(dto.body());
		mailSender.send(mailMessage);
	}
}
