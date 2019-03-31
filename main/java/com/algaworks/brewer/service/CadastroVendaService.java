package com.algaworks.brewer.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.service.exception.GenericMessageException;

@Service
public class CadastroVendaService {

	@Autowired 
	private Vendas vendas;
	
	@Autowired
	private Cervejas cervejas;
	
	private boolean isSalvarEmitir ;
	
	@Transactional
	public Venda salvar (Venda venda) {
		if (venda.isSalvarProibido() && !isSalvarEmitir) {
			throw new RuntimeException("Usuário tentando salvar uma venda proibida!");
		}
		
		if (venda.isNova()) {
			venda.setDataCriacao(LocalDateTime.now());
		}else {
			Venda vendaExistente = vendas.findOne(venda.getCodigo());
			venda.setDataCriacao(vendaExistente.getDataCriacao());
		}
		
		if(venda.getDataEntrega() != null) {
			venda.setDataHoraEntrega(LocalDateTime.of(venda.getDataEntrega(), 
					venda.getHoraEntrega() != null ? venda.getHoraEntrega() : LocalTime.NOON));
		}
		
		isSalvarEmitir = false;

		return vendas.saveAndFlush(venda);
	}

	@Transactional
	public void emitir(Venda venda) {
		List<ItemVenda> listaDeItens = venda.getItens();
		
		for(ItemVenda iv : listaDeItens) {
			if(iv.getQuantidade() > iv.getCerveja().getQuantidadeEstoque()) {
				throw new GenericMessageException("Quantidade do estoque não é suficiente para esta venda!");
			}
		}
		for(ItemVenda iv : listaDeItens) {
			iv.getCerveja().setQuantidadeEstoque(iv.getCerveja().getQuantidadeEstoque() - iv.getQuantidade());
			cervejas.save(iv.getCerveja());
		}
		
		venda.setStatus(StatusVenda.EMITIDA);
		isSalvarEmitir = true;
		
		salvar(venda);
	}

	// SecutityConfig.java: @EnableGlobalMethodSecurity(prePostEnabled = true)
	// #venda é a venda que está recebendo no methodo ou seja quem criou a venda / principal usuario e´o usuario loggado
	@PreAuthorize("#venda.usuario == principal.usuario or hasRole('CANCELAR_VENDA')")
	@Transactional
	public void cancelar(Venda venda) {
		Venda vendaExistente = vendas.findOne(venda.getCodigo());
		
		vendaExistente.setStatus(StatusVenda.CANCELADA);
		vendas.save(vendaExistente);
	}
}
