package org.project.shopservice.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.project.shopservice.dtos.onResponse.SendEmailDto;
import org.project.shopservice.services.template.services.TemplateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender javaMailSender;
	private final TemplateService templateService;
	@Value("${spring.mail.username}")
	private String emailSender;
	public void sendEmail(SendEmailDto dto) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		try {
			mimeMessage.setFrom(emailSender);
			helper.setTo(dto.to());
			helper.setSubject(dto.subject());

			String content = templateService.renderTemplate(dto.templateName(), dto.contextData());
			helper.setText(content, true); // false for non html message
		} catch (MessagingException e){
			e.printStackTrace();
		}
		javaMailSender.send(mimeMessage);
	}
}
