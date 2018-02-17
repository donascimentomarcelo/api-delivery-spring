package br.com.cursomc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.cursomc.domain.Categoria;
import br.com.cursomc.domain.Produto;
import br.com.cursomc.repositories.CategoriaRepository;
import br.com.cursomc.repositories.ProdutoRepository;
import br.com.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ProdutoService {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	public Produto findById(Integer id) 
	{
		Produto produto = produtoRepository.findOne(id);
		
		if(produto == null)
		{
			throw new ObjectNotFoundException("Objeto não encontrado! Código: " + id
					+ ", Pacote: " + Categoria.class.getName());
		}
		
		return produto;
	}
	
	public Page<Produto> search(String nome, List<Integer> ids, Integer page, Integer linesPerPage, String orderBy, String direction)
	{
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		List<Categoria> categorias = categoriaRepository.findAll(ids);
		
		return produtoRepository.findDistinctByNomeContainingAndCategoriasIn(nome, categorias, pageRequest);
	}
}
