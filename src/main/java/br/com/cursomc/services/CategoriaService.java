package br.com.cursomc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.cursomc.domain.Categoria;
import br.com.cursomc.dto.CategoriaDTO;
import br.com.cursomc.repositories.CategoriaRepository;
import br.com.cursomc.services.exceptions.DataIntegrityException;
import br.com.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	public Categoria findById(Integer id) 
	{
		Categoria categoria = categoriaRepository.findOne(id);
		
		if(categoria == null)
		{
			throw new ObjectNotFoundException("Objeto não encontrado! Código: " + id
					+ ", Pacote: " + Categoria.class.getName());
		}
		
		return categoria;
	}

	public Categoria save(Categoria categoria) 
	{	
		categoria.setId(null);
		return categoriaRepository.save(categoria);
	}

	public Categoria update(Categoria categoria) {
		Categoria newCategoria = findById(categoria.getId());
		updateData(newCategoria, categoria);
		return categoriaRepository.save(newCategoria);
	}
	
	private void updateData(Categoria newCategoria, Categoria categoria)
	{
		newCategoria.setNome(categoria.getNome());
	}

	public void delete(Integer id) {
		findById(id);
		
		try {
		categoriaRepository.delete(id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Exite um ou mais produtos vinculados a essa categoria!");
		}
	}

	public List<Categoria> list() {
		return categoriaRepository.findAll();
	}
	
	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String orderBy, String direction)
	{
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return categoriaRepository.findAll(pageRequest);
	}
	
	public Categoria fromDTO (CategoriaDTO categoriaDTO)
	{
		return new Categoria(categoriaDTO.getId(), categoriaDTO.getNome());
	}
}
