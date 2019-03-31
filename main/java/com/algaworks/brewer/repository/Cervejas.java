package com.algaworks.brewer.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.helper.cerveja.CervejasQueries;

@Repository
public interface Cervejas extends JpaRepository<Cerveja, Long>, CervejasQueries {

	public Cerveja findBySku(String sku);
	
	@Query(value = "SELECT SUM(quantidade_estoque) FROM Cerveja", nativeQuery = true)
	public Integer quantidadeNoEstoque();

	@Query(value = "SELECT SUM(quantidade_estoque) * SUM(valor) FROM Cerveja", nativeQuery = true)
	public BigDecimal valorNoEstoque();



}