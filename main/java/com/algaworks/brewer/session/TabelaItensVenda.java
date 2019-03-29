package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;


/**
 * Esta classe representa a
 * Lista de itens de um pedido (itens do carrinho)
 */

class TabelaItensVenda { // private class (Acesso somente para o package)
	
	private String uuid;

	private List<ItemVenda> listaDeItens = new ArrayList<>();
	
	public TabelaItensVenda(String uuid) {
		this.uuid = uuid;
	}

	// getValorTotalDoPedido = valorTotalPorItem * numero de ocorrencias na listaDeItens (iterate add)
	public BigDecimal getValorTotalDoPedido() {
		return listaDeItens.stream()
				.map(ItemVenda::getValorTotalPorItem) // getValorTotalPorItem = valorItem * quantidade
				.reduce(BigDecimal::add) // incrementa cada ocorrencia o getValorTotalPorItem no resultado 
				.orElse(BigDecimal.ZERO);
	}
	
	// Item de Venda 
	public void adicionarItem(Cerveja cerveja, Integer quantidade) {

		Optional<ItemVenda> itemVendaOptional = buscarItemPorCerveja(cerveja);
		
		ItemVenda itemVenda = null;
		// se item ja existe, somar quantidade senao, adicionar item
		if (itemVendaOptional.isPresent()) {
			itemVenda = itemVendaOptional.get();
			itemVenda.setQuantidade(itemVenda.getQuantidade() + quantidade);
		}else {
			itemVenda = new ItemVenda();
			itemVenda.setCerveja(cerveja);
			itemVenda.setQuantidade(quantidade);
			itemVenda.setValorUnitario(cerveja.getValor());
			listaDeItens.add(0, itemVenda);
		}
		
	}
	
	public void modificarQuantidade(Cerveja cerveja, Integer quantidade) {
		ItemVenda itemVenda = buscarItemPorCerveja(cerveja).get();
			itemVenda.setQuantidade(quantidade);
	}

	public void removerItem(Cerveja cerveja) {
		listaDeItens.removeIf(i -> i.getCerveja().equals(cerveja));
	}

	private Optional<ItemVenda> buscarItemPorCerveja(Cerveja cerveja) {
		return listaDeItens.stream()
				.filter(i -> i.getCerveja().equals(cerveja))
				.findAny();
	}
	
	public int totalDeItens() {
		return listaDeItens.size();
	}

	public List<ItemVenda> getItens() {
		return listaDeItens;
	}
	
	public String getUuid() {
		return uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TabelaItensVenda other = (TabelaItensVenda) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	
}
