package com.algaworks.brewer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.repository.filters.EstiloFilter;
import com.algaworks.brewer.service.CadastroEstiloService;
import com.algaworks.brewer.service.exception.GenericMessageException;


@Controller
@RequestMapping("/estilos")
public class EstilosController {
	
	private static final Logger logger = LogManager.getLogger(EstilosController.class);	
	
	@Autowired
	private CadastroEstiloService cadastroEstiloService;

	@Autowired
	private Estilos estilos;
	
	@RequestMapping("/novo")
	public ModelAndView novo (Estilo estilo) {
		
		ModelAndView mav = new ModelAndView("estilo/CadastroEstilo");
		return mav;
	
	}
	
	@GetMapping
	public ModelAndView pesquisaEstilo(EstiloFilter estiloFilter, 
			BindingResult result, 
			@PageableDefault(size=3) Pageable pageable,
			HttpServletRequest req) {
		
		System.out.println("estilo:"+estiloFilter.getNome());
		
		
		ModelAndView mav = new ModelAndView("estilo/PesquisaEstilos");
		
		PageWrapper<Estilo> paginaWrapper = new PageWrapper<>(estilos.filtrar(estiloFilter, pageable), req);
		mav.addObject("pagina", paginaWrapper);
		return mav;
	}
	
	@RequestMapping(value = "/novo", method = RequestMethod.POST)
	public ModelAndView cadastrar(@Valid Estilo estilo, BindingResult result, Model model, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return novo(estilo);
		}
		
		try {
			cadastroEstiloService.salvar(estilo);
		} catch (GenericMessageException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(estilo);
		}
		attributes.addFlashAttribute("menssagem", "Cerveja salva com sucesso!");
		return new ModelAndView("redirect:/estilos/novo");

	}
	
	// Rapid style create 
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid Estilo estilo, BindingResult result) {
		
		long startTime = System.currentTimeMillis();
		
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());
		}
		
		try {
			estilo = cadastroEstiloService.salvar(estilo);
		} catch (GenericMessageException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
		long stopTime = System.currentTimeMillis();
		logger.info("[INFO ] tempo grava estilo rapido:{}", stopTime - startTime);
		return ResponseEntity.ok(estilo);
	}
}
