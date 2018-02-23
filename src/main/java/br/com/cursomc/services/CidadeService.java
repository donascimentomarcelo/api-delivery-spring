package br.com.cursomc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cursomc.domain.Cidade;
import br.com.cursomc.repositories.CidadeRepository;
import br.com.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CidadeService {
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	public List<Cidade> find(Integer id)
	{
		List<Cidade> cidade = cidadeRepository.findCidades(id);
		
		if(cidade.isEmpty())
		{
			throw new ObjectNotFoundException("Não existem cidades para esse estado!");
		}
		
		return cidade;
	}
}
