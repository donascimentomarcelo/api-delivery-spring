package br.com.cursomc.services;

import org.springframework.security.core.context.SecurityContextHolder;

import br.com.cursomc.security.UserSpringSecurity;

public class UserService {
	
	public static UserSpringSecurity authenticated()
	{
		try {
			//retorna o usuario logado
			return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}catch (Exception e) {
			return null;
		}
	}
}
