package com.algaworks.brewer.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Vendas;

@Service
public class CadastroVendaService {

	@Autowired 
	private Vendas vendas;
	
	@Transactional
	public Venda salvar (Venda venda) {
		if (venda.isSalvarProibido()) {
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
		
		return vendas.saveAndFlush(venda);
	}

	@Transactional
	public void emitir(Venda venda) {
		venda.setStatus(StatusVenda.EMITIDA);
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
