package br.com.cursomc.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cursomc.domain.Categoria;
import br.com.cursomc.domain.ItemPedido;
import br.com.cursomc.domain.PagamentoComBoleto;
import br.com.cursomc.domain.Pedido;
import br.com.cursomc.domain.enums.EstadoPagamento;
import br.com.cursomc.repositories.ClienteRepository;
import br.com.cursomc.repositories.ItemPedidoRepository;
import br.com.cursomc.repositories.PagamentoRepository;
import br.com.cursomc.repositories.PedidoRepository;
import br.com.cursomc.repositories.ProdutoRepository;
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
			
			itemPedido.setPreco(produtoRepository.findOne(itemPedido.getProduto().getId()).getPreco());
			
			itemPedido.setPedido(pedido);
		}
		
		itemPedidoRepository.save(pedido.getItens());
		return pedido;
	}
	
	private void validIfClientExist(Pedido pedido)
	{
		if(clienteRepository.findOne(pedido.getCliente().getId()) == null)
		{
			throw new ObjectNotFoundException("Cliente não encontrado!");
		}
	}
}
