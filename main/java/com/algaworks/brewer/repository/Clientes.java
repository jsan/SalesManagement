package com.algaworks.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.helper.cliente.ClientesQueries;

public interface Clientes extends JpaRepository<Cliente , Long>, ClientesQueries {
	
	public Optional<Cliente> findByCpfOuCnpj (String cpfCnpj);
	public List<Cliente> findByNomeStartingWithIgnoreCase(String nome);


// substituido pelo count() do JPA repository no controller	
//	@Query(value = "SELECT count(*) FROM Cliente", nativeQuery = true)
//	public Integer totalDeClientes();

}
