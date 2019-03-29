package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.dto.CervejaDTO;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Origem;
import com.algaworks.brewer.model.Sabor;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.repository.filters.CervejaFilter;
import com.algaworks.brewer.service.CadastroCervejaService;
import com.algaworks.brewer.service.exception.GenericMessageException;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;

// teste para o Git

@Controller
@RequestMapping("/cervejas")
public class CervejasController {

	private static final Logger logger = LogManager.getLogger(CervejasController.class);
	

	@Autowired
	private CadastroCervejaService cadastroCervejaService;
	
	@Autowired
	private Estilos estilos;
	
	@Autowired
	private Cervejas cervejas;
	
	@RequestMapping("/nova")
	public ModelAndView nova(Cerveja cerveja) {
		ModelAndView mav = new ModelAndView("cerveja/CadastroCerveja");
		mav.addObject("sabores", Sabor.values());
		mav.addObject("estilos", estilos.findAll());
		mav.addObject("origens", Origem.values());
		return mav;
	} 

	@RequestMapping(value = {"/nova", "{\\d+}"}, method = RequestMethod.POST)
	public ModelAndView salvar(@Valid Cerveja cerveja, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			logger.info("[INFO] Erro de validacao de campos: {}", result.getFieldError());
			return nova(cerveja);
		}
		
		try {
			cadastroCervejaService.salvar(cerveja);
			attributes.addFlashAttribute("menssagem", "Cerveja salva com sucesso!");
		} catch (GenericMessageException e) {
			result.rejectValue("sku", e.getMessage(), e.getMessage());
			return nova(cerveja);
		}
		return new ModelAndView("redirect:/cervejas/nova");
	}
	
	@GetMapping
	public ModelAndView pesquisaCerveja(CervejaFilter cervejaFilter, 
			BindingResult result, 
			@PageableDefault(size=3) Pageable pageable,
			HttpServletRequest req) {
		ModelAndView mav = new ModelAndView("cerveja/PesquisaCervejas");
		mav.addObject("sabores", Sabor.values());
		mav.addObject("estilos", estilos.findAll());
		mav.addObject("origens", Origem.values());
		
		PageWrapper<Cerveja> paginaWrapper = new PageWrapper<>(cervejas.filtrar(cervejaFilter, pageable), req);
		mav.addObject("pagina", paginaWrapper);
		return mav;
	}
	
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CervejaDTO> pesquisar(String skuOuNome) {
		return cervejas.porSkuOuNome(skuOuNome);
	}

	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cerveja cerveja) {
		try {
			cadastroCervejaService.excluir(cerveja);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage()); // este e.getMessage vai ser mostrado pelo javaScript
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Cerveja cerveja ) { // codigoCerveja direto no objeto cerveja (webConfig DomainClassConverter) Alternativa para o findOne(cerveja.getCodigo()) 		
		ModelAndView  mav = nova(cerveja); // carrega o estilo sabor e origem
		mav.addObject(cerveja); 
		return mav;
	
	}
	
	///test methods
	
	@RequestMapping(value = "/excluirTeste", method = RequestMethod.GET)
	public ModelAndView excluirTeste(@RequestParam String sku, RedirectAttributes attributes) {
		
		cadastroCervejaService.excluirTeste(sku);
		attributes.addFlashAttribute("menssagem", "Cerveja excluída com sucesso!");
		return new ModelAndView("redirect:/cervejas");
	}


	@RequestMapping(value = "/teste", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody Page<Cerveja> teste(Pageable page) {
		
		Page<Cerveja> cerva = cervejas.findAll(page);
		return cerva;
	}
	
}
