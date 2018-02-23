package br.com.cursomc.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.cursomc.domain.Cidade;
import br.com.cursomc.domain.Estado;
import br.com.cursomc.dto.CidadeDTO;
import br.com.cursomc.dto.EstadoDTO;
import br.com.cursomc.services.CidadeService;
import br.com.cursomc.services.EstadoService;

@RestController
@RequestMapping("/estados")
public class EstadoResource {
	
	@Autowired
	private EstadoService estadoService;
	
	@Autowired
	private CidadeService cidadeService;
	
	@RequestMapping( method = RequestMethod.GET)
	public ResponseEntity<List<EstadoDTO>> findState()
	{
		List<Estado> list = estadoService.find();
		List<EstadoDTO> listDto = list.stream().map(estado -> new EstadoDTO(estado)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDto);
	}
	
	@RequestMapping(value = "/{id}/cidades", method = RequestMethod.GET)
	public ResponseEntity<List<CidadeDTO>> findState(@PathVariable Integer id)
	{
		List<Cidade> list = cidadeService.find(id);
		List<CidadeDTO> listDto = list.stream().map(cidade -> new CidadeDTO(cidade)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDto);
	}

}
