package br.com.cursomc.services.exceptions;

public class DataIntegrityException extends RuntimeException{

	/**
	 * This is for treat data integrity exception
	 */
	private static final long serialVersionUID = 1L;
	
	public DataIntegrityException(String msg)
	{
		super(msg);
	}
	
	public DataIntegrityException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
	
}
