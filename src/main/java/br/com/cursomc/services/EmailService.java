package br.com.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import br.com.cursomc.domain.Cliente;
import br.com.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido pedido);
	
	void sendEmail(SimpleMailMessage message);

    void sendNewPasswordEmail(Cliente cliente, String newPassword);
}
