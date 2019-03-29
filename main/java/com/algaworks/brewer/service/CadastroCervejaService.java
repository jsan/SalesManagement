package com.algaworks.brewer.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.service.event.cerveja.CervejaSalvaEvent;
import com.algaworks.brewer.service.exception.GenericMessageException;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.storage.FotoStorage;

@Service
public class CadastroCervejaService {

	@Autowired
	private Cervejas cervejas;

	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	FotoStorage fotoStorage;

	@Transactional
	public void salvar(Cerveja cerveja) {

		Cerveja findBySku = cervejas.findBySku(cerveja.getSku());
		
		if (findBySku != null && cerveja.isNova()) {
			throw new GenericMessageException("Esta identificação Sku já existe...");
		}
		cervejas.save(cerveja);

		// CervejaListener.java para salvar foto (mover do dir temp para o dir definitivo)
		publisher.publishEvent(new CervejaSalvaEvent(cerveja));
	}
	
	@Transactional
	public void excluir(Cerveja cerveja) {
		try {
			String foto = cerveja.getFoto();
			cervejas.delete(cerveja);
			cervejas.flush();
			fotoStorage.excluir(foto);
		} catch (PersistenceException e) { // o java lanca esta excecao qdo cervejas.delete tenta apagar uma cerveja que é referenciada na table item_venda
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cerveja. Já foi usada em alguma venda.");
		}
	}
	
	/// Test
	@Transactional
	public void excluirTeste(String skuToDelete) {

		Cerveja cervejaToDelete = cervejas.findBySku(skuToDelete);

		if (cervejaToDelete != null) {
			cervejas.delete(cervejaToDelete);
		} else {
			throw new GenericMessageException("Esta identificação Sku não existe...");
		}
	}

}