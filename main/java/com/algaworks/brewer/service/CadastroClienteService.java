package com.algaworks.brewer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.service.exception.GenericMessageException;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class CadastroClienteService {

	@Autowired
	private Clientes clientes;

	@Transactional
	public void salvar(Cliente cliente) {


		Optional<Cliente> cli = clientes.findByCpfOuCnpj(cliente.getCpfOuCnpjSemFormatacao());

		if(cli.isPresent() && cli.get().isNovo()) {
			throw new GenericMessageException("Este Cpf/Cnpj já existe...");
		}

		clientes.save(cliente);

	}

	@Transactional
	public void excluir(Cliente cliente) {

		try {
			clientes.delete(cliente);
		} catch (DataIntegrityViolationException e) { // o java lanca esta excecao qdo cervejas.delete tenta apagar uma cerveja que é referenciada na table item_venda
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cliente. Já foi usado em alguma venda.");
		}
	}
}
