package br.com.cursomc.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.cursomc.domain.Cidade;
import br.com.cursomc.domain.Cliente;
import br.com.cursomc.domain.Endereco;
import br.com.cursomc.domain.enums.Perfil;
import br.com.cursomc.domain.enums.TipoCliente;
import br.com.cursomc.dto.ClienteDTO;
import br.com.cursomc.dto.ClienteNewDTO;
import br.com.cursomc.repositories.CidadeRepository;
import br.com.cursomc.repositories.ClienteRepository;
import br.com.cursomc.repositories.EnderecoRepository;
import br.com.cursomc.security.UserSpringSecurity;
import br.com.cursomc.services.exceptions.AuthorizationException;
import br.com.cursomc.services.exceptions.DataIntegrityException;
import br.com.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private ImageService imageService;
	
	@Value("${img.prefix.client.profile}")
	private String prefix;
	
	@Value("${img.profile.size}")
	private Integer size;
	
	public Cliente findById(Integer id) 
	{
		UserSpringSecurity usuarioLogado = UserService.authenticated();
		
		if(usuarioLogado == null || !usuarioLogado.hasRole(Perfil.ADMIN) && !id.equals(usuarioLogado.getId()))
		{
			throw new AuthorizationException("Acesso negado");
		}
		
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
	
	public Cliente save(Cliente cliente) 
	{	
		cliente.setId(null);
		clienteRepository.save(cliente);
		enderecoRepository.save(cliente.getEnderecos());
		return cliente;
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
		return new Cliente(clienteDTO.getId(), clienteDTO.getNome(), clienteDTO.getEmail(), null, null, null);
	}
	
	public Cliente fromDTO (ClienteNewDTO clienteNewDTO)
	{
		Cliente cliente = new Cliente(null, clienteNewDTO.getNome(), clienteNewDTO.getEmail(),
					clienteNewDTO.getCpfOuCnpj(), TipoCliente.toEnum(clienteNewDTO.getTipoCliente()), bCryptPasswordEncoder.encode(clienteNewDTO.getSenha()));
		
		Cidade cidade = cidadeRepository.findOne(clienteNewDTO.getCidadeId());
		
		Endereco endereco = new Endereco(null, clienteNewDTO.getLogradouro(), clienteNewDTO.getNumero(), clienteNewDTO.getComplemento(), clienteNewDTO.getBairro(),
				clienteNewDTO.getCep(), cliente, cidade);
		
		cliente.getEnderecos().add(endereco);
		
		cliente.getTelefones().add(clienteNewDTO.getTelefone1());
		
		if(clienteNewDTO.getTelefone2() != null)
		{
			cliente.getTelefones().add(clienteNewDTO.getTelefone2());
		}
		
		if(clienteNewDTO.getTelefone3() != null)
		{
			cliente.getTelefones().add(clienteNewDTO.getTelefone3());
		}
		
		return cliente;
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile)
	{
		UserSpringSecurity usuarioLogado = UserService.authenticated();
		
		if(usuarioLogado == null)
		{
			throw new AuthorizationException("Acesso negado!");
		}
		
		BufferedImage jpgImage = imageService.getJpgImageFromFile(multipartFile);
		jpgImage = imageService.cropSquare(jpgImage);
		jpgImage = imageService.resize(jpgImage, size);
		
		String fileName = prefix + usuarioLogado.getId() + ".jpg";
		
		return s3Service.uploadFile(imageService.getInputStream(jpgImage, "jpg"), fileName, "image");
		
	}
	
	public Cliente findByEmail(String email)
	{
		UserSpringSecurity usuarioLogado = UserService.authenticated();
		
		if(usuarioLogado == null || !usuarioLogado.hasRole(Perfil.ADMIN) && !email.equals(usuarioLogado.getUsername()))
		{
			throw new AuthorizationException("Acesso negado");
		}
		
		Cliente cliente = clienteRepository.findOne(usuarioLogado.getId());
		
		if(cliente == null)
		{
			throw new ObjectNotFoundException("Objeto não encontrado! Código: " + usuarioLogado.getId() + ", Tipo: " + Cliente.class.getName());
		}
		
		return cliente;
	}
	
}
