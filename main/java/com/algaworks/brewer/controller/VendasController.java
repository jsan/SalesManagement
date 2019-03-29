package com.algaworks.brewer.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.repository.filters.VendaFilter;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.CadastroVendaService;
import com.algaworks.brewer.session.TabelaItensSession;

@Controller
@RequestMapping("/vendas")
public class VendasController {
	
	@Autowired
	private Cervejas cervejas;

	@Autowired
	private Vendas vendas;
	
	@Autowired
	private TabelaItensSession tabelaItens;
	
	@Autowired
	CadastroVendaService cadastroVendaService;
	
	@Autowired
	VendaValidator vendaValidator;
	
	@Autowired
	Mailer mailer;
	
	
//  Este metodo fornecido pelo curso NAO é necessario, pois a 
//  validacao está sendo feita na explicitamente (no metodo salvar .validate)
//	@InitBinder("venda") 
//	public void inicializarValidador(WebDataBinder binder) {
//		binder.setValidator(vendaValidator);
//	}
	
	@GetMapping
	public ModelAndView pesquisaVenda(VendaFilter vendaFilter, 
			BindingResult result, 
			@PageableDefault(size=3) Pageable pageable,
			HttpServletRequest req) {
		ModelAndView mav = new ModelAndView("venda/PesquisaVendas");
		mav.addObject("todosStatus", StatusVenda.values());
		
		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendas.filtrar(vendaFilter, pageable), req);
		mav.addObject("pagina", paginaWrapper);
		return mav;
	}
	
	
	@GetMapping("/nova")
	public ModelAndView nova(Venda venda ) {
		ModelAndView mav = new  ModelAndView("venda/CadastroVenda");

		setUuid(venda);
		
		mav.addObject("itens", venda.getItens());
		mav.addObject("valorFrete", venda.getValorFrete());
		mav.addObject("valorDesconto", venda.getValorDesconto());
		mav.addObject("valorTotalItens", tabelaItens.getValorTotal(venda.getUuid()));
		
		return mav;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Venda venda = vendas.buscaComItens(codigo);
		
		setUuid(venda);
		for(ItemVenda item : venda.getItens()) {
			tabelaItens.adicionarItem(venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}
		
		ModelAndView mav = nova(venda);
		mav.addObject(venda);
		return mav;
	}
	
	@PostMapping(value = "/nova", params = "cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result
				, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		try {
			cadastroVendaService.cancelar(venda);
		} catch (AccessDeniedException e) {
			return new ModelAndView("/403");
		}
		
		attributes.addFlashAttribute("mensagem", "Venda cancelada com sucesso");
		return new ModelAndView("redirect:/vendas/" + venda.getCodigo());
	}

	@PostMapping(value = "/nova", params = "salvar")
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("menssagem", "Venda salva com sucesso!");
		return new ModelAndView("redirect:/vendas/nova");
	}

	@PostMapping(value = "/nova", params = "emitir")
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("menssagem", "Venda emitida com sucesso!");
		return new ModelAndView("redirect:/vendas/nova");
	}

	@PostMapping(value = "/nova", params = "enviarEmail")
	public ModelAndView enviarEmail(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		venda = cadastroVendaService.salvar(venda);
		
		mailer.enviar(venda);
		
		attributes.addFlashAttribute("menssagem", String.format("Venda nº %d salva com sucesso e e-mail enviado!", venda.getCodigo()));
		return new ModelAndView("redirect:/vendas/nova");
	}

	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
		
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItens.adicionarItem(uuid, cerveja, 1);
		return mavTabelaItensVenda(uuid);
	}
	
	@PutMapping("/item")
	public ModelAndView modificarQuantidade(Long codigoCerveja, Integer quantidade, String uuid) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItens.modificarQuantidade(uuid,  cerveja, quantidade);
		return mavTabelaItensVenda(uuid);
	}
	
	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	public ModelAndView apagarItem(@PathVariable("codigoCerveja") Cerveja cerveja, @PathVariable("uuid") String uuid) { 
		// aqui o java ja liga o codigoCerveja com o objeto cerveja (webConfig DomainClassConverter) Alternativa para o findOne do metodo putMapping acima 
		tabelaItens.removerItem(uuid, cerveja);
		return mavTabelaItensVenda(uuid);
	}
	
	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMes> listarTotalDeVendasPorMes(){
		return vendas.totalPorMes();
	}
	
	private void setUuid(Venda venda) {
		if (StringUtils.isEmpty(venda.getUuid())) {
			venda.setUuid(UUID.randomUUID().toString()); // new tabelaItens :: new id
		} 		
	}
	
	private ModelAndView mavTabelaItensVenda(String uuid) {
		ModelAndView mav = new ModelAndView("venda/TabelaItensVenda");
		mav.addObject("itens", tabelaItens.getItens(uuid));
		mav.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		return mav;
	}
	
	private void validarVenda(Venda venda, BindingResult result) {
		venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));
		venda.calcularValorTotal();
		
		vendaValidator.validate(venda, result);
	}
}
