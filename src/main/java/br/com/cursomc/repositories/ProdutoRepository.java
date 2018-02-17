package br.com.cursomc.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.cursomc.domain.Categoria;
import br.com.cursomc.domain.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer>{
	
	/*
	@Query("SELECT DISTINCT produto from Produto produto INNER JOIN produto.categorias c where produto.nome LIKE %:nome% AND c IN :categorias")
	Page<Produto> search(@Param("nome") String nome,@Param("categorias")  List<Categoria> categorias, Pageable pageRequest);
	*/
	
	@Transactional(readOnly=true)
	Page<Produto> findDistinctByNomeContainingAndCategoriasIn(String nome,List<Categoria> categorias, Pageable pageRequest);
	//Containing é o LIKE

}
