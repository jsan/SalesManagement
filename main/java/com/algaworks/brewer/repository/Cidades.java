package com.algaworks.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.algaworks.brewer.model.Cidade;

public interface Cidades extends JpaRepository<Cidade, Long>{
	
	public List<Cidade> findByEstadoCodigo(Long codigoEstado);
	
	@Query(value = "SELECT * FROM cidade WHERE nome = ?1 and codigo_estado = ?2", nativeQuery = true)
	public Optional<Cidade> findByNomeAndEstado(String nome, Long codEstado);

}
