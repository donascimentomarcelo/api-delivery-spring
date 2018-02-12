package br.com.cursomc.services.exceptions;

public class ObjectNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public ObjectNotFoundException(String msg)
	{
		//Super chama a classe que foi feito o extends e passa o parametro msg
		super(msg);
	}
	
	public ObjectNotFoundException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}
