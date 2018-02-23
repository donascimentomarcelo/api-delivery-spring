package br.com.cursomc.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.cursomc.domain.Cidade;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Integer>{
	
	@Query("Select cidade from Cidade cidade WHERE cidade.estado.id = :estado_id ORDER BY cidade.nome")
	public List<Cidade> findCidades(@Param("estado_id") Integer estado_id);
}
