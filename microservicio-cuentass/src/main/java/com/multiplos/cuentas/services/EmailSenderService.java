package com.multiplos.cuentas.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class EmailSenderService {
	
	private SendGrid sendGrid;

	private static final Logger logger = LoggerFactory.getLogger(EmailSenderService.class);
		
	@Autowired
	public EmailSenderService(SendGrid sendGrid) {
		this.sendGrid = sendGrid;
	}
	  
	public String sendTextEmail(String asunto, String toEmail, String cuerpoEmail) {
		Email from = new Email("info@multiplolenders.com");
		String subject = asunto;
		Email to = new Email(toEmail);
		
		//Content content = new Content("text/plain", cuerpoEmail);
		Content content = new Content("text/html", cuerpoEmail);
		
		Mail mail = new Mail(from, subject, to, content);
		
		Request request = new Request();
		try {
		  	request.setMethod(Method.POST);
		   	request.setEndpoint("mail/send");
		   	request.setBody(mail.build());
		   	Response response = sendGrid.api(request);
		   	return response.getBody();
		} catch (IOException e) {
			logger.error(e.getMessage());
			return "error en envió de email";
		}	   
	}
	
//	public Boolean sendMailCambioDeEstado() {
//		
//	}
//	
	
	
	
	
	
	
	
	
}
