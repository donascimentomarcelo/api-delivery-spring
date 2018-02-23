package br.com.cursomc.dto;

import java.io.Serializable;

import br.com.cursomc.domain.Cidade;

public class CidadeDTO implements Serializable{	
	private static final long serialVersionUID = 1L;
	
	private String nome;
	
	public CidadeDTO() 
	{
	
	}
	
	public CidadeDTO(Cidade cidade) 
	{
		nome = cidade.getNome();
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
