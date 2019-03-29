package com.algaworks.brewer.repository.filters;

import com.algaworks.brewer.model.TipoPessoa;

public class ClienteFilter {

	private String nome;
	private String cpfOuCnpj;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCpfOuCnpj() {
		return cpfOuCnpj;
	}
	public void setCpfOuCnpj(String cpfOuCnpj) {
		this.cpfOuCnpj = cpfOuCnpj;
	}
	public String getCpfOuCnpjSemFormatar() {
		return TipoPessoa.removerFormatacao(cpfOuCnpj);
	}
	
}
