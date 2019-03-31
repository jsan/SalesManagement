package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.repository.Vendas;

@Controller
public class DashboardController {
	
	@Autowired
	private Vendas vendas;	
	
	@Autowired
	private Cervejas cervejas;

	@Autowired
	private Clientes clientes;
	
	@GetMapping("/")
	public ModelAndView dashboard() {
		ModelAndView mav = new ModelAndView("Dashboard");
		mav.addObject("vendasNoAno", vendas.valorTotalNoAno());
		mav.addObject("vendasNoMes", vendas.valorTotalNoMes());
		mav.addObject("ticketMedio", vendas.valorTicketMedio());
		mav.addObject("quantdadeNoEstoque", cervejas.quantidadeNoEstoque());
		mav.addObject("valorNoEstoque", cervejas.valorNoEstoque());
		mav.addObject("totalDeClientes", clientes.totalDeClientes());
		return mav;
	}

}
