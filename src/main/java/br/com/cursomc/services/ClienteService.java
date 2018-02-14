package br.com.cursomc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.cursomc.domain.Cliente;
import br.com.cursomc.dto.ClienteDTO;
import br.com.cursomc.repositories.ClienteRepository;
import br.com.cursomc.services.exceptions.DataIntegrityException;
import br.com.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	public Cliente findById(Integer id) 
	{
		Cliente cliente = clienteRepository.findOne(id);
		
		if(cliente == null)
		{
			throw new ObjectNotFoundException("Objeto não encontrado! Código: " + id
					+ ", Pacote: " + Cliente.class.getName());
		}
		
		return cliente;
	}
	
	public Cliente update(Cliente cliente) {
		Cliente NewCliente = findById(cliente.getId());
		updateData(NewCliente, cliente);
		return clienteRepository.save(NewCliente);
		
	}
	
	private void updateData(Cliente NewCliente, Cliente cliente)
	{
		NewCliente.setNome(cliente.getNome());
		NewCliente.setEmail(cliente.getEmail());
	}

	public void delete(Integer id) {
		findById(id);
		
		try {
		clienteRepository.delete(id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Exite um ou mais pedidos vinculados a essa cliente!");
		}
	}

	public List<Cliente> list() {
		return clienteRepository.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction)
	{
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return clienteRepository.findAll(pageRequest);
	}
	
	public Cliente fromDTO (ClienteDTO clienteDTO)
	{
		//throw new UnsupportedOperationException();
		return new Cliente(clienteDTO.getId(), clienteDTO.getNome(), clienteDTO.getEmail(), null, null);
	}
}
