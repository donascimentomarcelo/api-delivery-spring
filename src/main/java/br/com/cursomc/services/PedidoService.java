package br.com.cursomc.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.cursomc.domain.Categoria;
import br.com.cursomc.domain.Cliente;
import br.com.cursomc.domain.ItemPedido;
import br.com.cursomc.domain.PagamentoComBoleto;
import br.com.cursomc.domain.Pedido;
import br.com.cursomc.domain.enums.EstadoPagamento;
import br.com.cursomc.repositories.ClienteRepository;
import br.com.cursomc.repositories.ItemPedidoRepository;
import br.com.cursomc.repositories.PagamentoRepository;
import br.com.cursomc.repositories.PedidoRepository;
import br.com.cursomc.repositories.ProdutoRepository;
import br.com.cursomc.security.UserSpringSecurity;
import br.com.cursomc.services.exceptions.AuthorizationException;
import br.com.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EmailService emailService;
	
	public Pedido findById(Integer id) 
	{
		Pedido pedido = pedidoRepository.findOne(id);
		
		if(pedido == null)
		{
			throw new ObjectNotFoundException("Objeto não encontrado! Código: " + id
					+ ", Pacote: " + Categoria.class.getName());
		}
		
		return pedido;
	}

	public Pedido save(Pedido pedido) {

		validIfClientExist(pedido);
		
		pedido.setId(null);
		pedido.setInstante(new Date());
		pedido.setCliente(clienteRepository.findOne(pedido.getCliente().getId()));
		pedido.getPagamento().setEstadoPagamento(EstadoPagamento.PENDENTE);
		//Relaciona o pedido ao pagamento
		pedido.getPagamento().setPedido(pedido);
		
		if(pedido.getPagamento() instanceof PagamentoComBoleto)
		{
			PagamentoComBoleto boleto = (PagamentoComBoleto) pedido.getPagamento();
			boletoService.preencherPagamentoComBoleto(boleto, pedido.getInstante());
		}
		
		pedido = pedidoRepository.save(pedido);
		pagamentoRepository.save(pedido.getPagamento());
		
		for(ItemPedido itemPedido: pedido.getItens())
		{
			itemPedido.setDesconto(0.0);
			
			itemPedido.setProduto(produtoRepository.findOne(itemPedido.getProduto().getId()));
			
			itemPedido.setPreco(itemPedido.getProduto().getPreco());
			
			itemPedido.setPedido(pedido);
		}
		
		itemPedidoRepository.save(pedido.getItens());
		
		emailService.sendOrderConfirmationEmail(pedido);
		
		return pedido;
	}
	
	private void validIfClientExist(Pedido pedido)
	{
		if(clienteRepository.findOne(pedido.getCliente().getId()) == null)
		{
			throw new ObjectNotFoundException("Cliente não encontrado!");
		}
	}
	
	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction)
	{
		UserSpringSecurity usuarioLogado = UserService.authenticated();
		
		if(usuarioLogado == null)
		{
			throw new AuthorizationException("Acesso negado");
		}
		
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		Cliente cliente = clienteRepository.findOne(usuarioLogado.getId());
		
		return pedidoRepository.findByCliente(cliente, pageRequest);
	}
}
