package com.algaworks.brewer.service.event.venda;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.service.exception.GenericMessageException;

@Component
public class VendaListener {
	
	@Autowired
	private Cervejas cervejas;

	@EventListener
	public void vendaEmitida(VendaEvent vendaEvent) {
		List<ItemVenda> listaDeItens = vendaEvent.getVenda().getItens();
		
		for(ItemVenda iv : listaDeItens) {
			if(iv.getQuantidade() > iv.getCerveja().getQuantidadeEstoque()) {
				throw new GenericMessageException("Quantidade do estoque não é suficiente para esta venda!");
			}
		}
		for(ItemVenda iv : listaDeItens) {
			iv.getCerveja().setQuantidadeEstoque(iv.getCerveja().getQuantidadeEstoque() - iv.getQuantidade());
			cervejas.save(iv.getCerveja());
		}
		
	}
	
}
