package com.algaworks.brewer.session;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.session.TabelaItensVenda;

public class TabelaItensVendaTest {

	private TabelaItensVenda tabelaItensVenda;
	
	@Before
	public void setup() {
		this.tabelaItensVenda = new TabelaItensVenda("1");  
	}
	
	@Test
	public void calcularValorTotalSemItens() throws Exception {
		assertEquals(BigDecimal.ZERO, tabelaItensVenda.getValorTotalDoPedido());
	}

	@Test
	public void calcularValorTotalComUmItem() throws Exception {
		Cerveja cerveja = new Cerveja();
		BigDecimal valorCerveja = new BigDecimal("8.90");
		cerveja.setValor(valorCerveja);
		
		tabelaItensVenda.adicionarItem(cerveja, 1);
		
		assertEquals(valorCerveja, tabelaItensVenda.getValorTotalDoPedido());
	}
	
	@Test
	public void calcularValorTotalComMaisItem() throws Exception {
		Cerveja c1 = new Cerveja();
		BigDecimal v1 = new BigDecimal("8.90");
		c1.setCodigo(1L);
		c1.setValor(v1);
		
		Cerveja c2 = new Cerveja();
		BigDecimal v2 = new BigDecimal("4.99");
		c1.setCodigo(2L);
		c2.setValor(v2);
		
		tabelaItensVenda.adicionarItem(c1, 1);
		tabelaItensVenda.adicionarItem(c2, 2);
		
		assertEquals(new BigDecimal("18.88"), tabelaItensVenda.getValorTotalDoPedido());
		
	}
	
	@Test
	public void deveManterListaSizeAoAdicionarItemJaExistente() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setCodigo(1L);
		c1.setValor(new BigDecimal ("4.50"));
		
		tabelaItensVenda.adicionarItem(c1, 1);
		tabelaItensVenda.adicionarItem(c1, 1);
		
		assertEquals(1, tabelaItensVenda.totalDeItens());
		assertEquals(new BigDecimal("9.00"), tabelaItensVenda.getValorTotalDoPedido());
		
	}

	@Test
	public void deveAlterarQuantidadeItem() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setCodigo(1L);
		c1.setValor(new BigDecimal ("5.00"));
		
		tabelaItensVenda.adicionarItem(c1, 1);
		tabelaItensVenda.modificarQuantidade(c1, 5);
		
		assertEquals(1, tabelaItensVenda.totalDeItens());
		assertEquals(new BigDecimal("25.00"), tabelaItensVenda.getValorTotalDoPedido());
		
	}
	@Test
	public void deveApagarItem() throws Exception {
		Cerveja c1 = new Cerveja();
		BigDecimal v1 = new BigDecimal("8.90");
		c1.setCodigo(1L);
		c1.setValor(v1);
		
		Cerveja c2 = new Cerveja();
		BigDecimal v2 = new BigDecimal("4.99");
		c1.setCodigo(2L);
		c2.setValor(v2);
		
		tabelaItensVenda.adicionarItem(c1, 1);
		tabelaItensVenda.adicionarItem(c2, 10);
		
		tabelaItensVenda.removerItem(c1);
		
		assertEquals(1, tabelaItensVenda.totalDeItens());
		assertEquals(new BigDecimal("49.90"), tabelaItensVenda.getValorTotalDoPedido());

		
	}
	
}
