package com.multiplos.cuentas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.Response;

import io.github.flashvayne.chatgpt.service.ChatgptService;

@RestController
@RequestMapping("${route.service.contextPath}")

public class ChatbotController {

	

	@Autowired	
	private ChatgptService chatService;

	



	@PostMapping("/public/chat")
	public ResponseEntity<?> chat(@RequestParam String message ) throws Exception {
		Object response;
		response= this.chatService.sendMessage(message);
		return ResponseEntity.ok().body(new Response(response));
	}
	
	
	
	
}
