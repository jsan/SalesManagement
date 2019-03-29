package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.filters.ClienteFilter;
import com.algaworks.brewer.service.CadastroClienteService;
import com.algaworks.brewer.service.exception.GenericMessageException;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/clientes")
public class ClientesController {

	@Autowired
	private CadastroClienteService cadastroClienteService;

	@Autowired
	private Clientes clientes;
	
	@Autowired
	private Estados estados;

	@RequestMapping("/novo")
	public ModelAndView novo (Cliente cliente) {
		ModelAndView mav = new ModelAndView("cliente/CadastroCliente");
		mav.addObject("tiposPessoa", TipoPessoa.values());
		mav.addObject("estados", estados.findAll());
		return mav;
	}
	
	@RequestMapping
	public ModelAndView pesquisarClientes(ClienteFilter clienteFilter, 
			BindingResult result, 
			@PageableDefault(size=3) Pageable pageable,
			HttpServletRequest req) {
		
		ModelAndView mav = new ModelAndView("cliente/PesquisaClientes");
		PageWrapper<Cliente> pageWrapper = new PageWrapper<>(clientes.filtrar(clienteFilter, pageable), req);
		mav.addObject("pagina", pageWrapper);
		return mav;
	}

	@PostMapping("/novo")
	public ModelAndView salvar(@Valid Cliente cliente, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			//throw new RuntimeException();
			return novo(cliente);
		}
		try {
			cadastroClienteService.salvar(cliente);
			attributes.addFlashAttribute("menssagem", "Cliente salva com sucesso!");
			return new ModelAndView("redirect:/clientes/novo");
		} catch (GenericMessageException e) {
			result.rejectValue("cpfOuCnpj", e.getMessage(), e.getMessage());
			return novo(cliente);
		}

	}
	
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Long codigo ) { 
		Cliente cliente = clientes.buscarComCidadeEstado(codigo);
		ModelAndView  mav = novo(cliente); 
		mav.addObject(cliente); 
		return mav;
	
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cliente cliente) {
		try {
			cadastroClienteService.excluir(cliente);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage()); // este e.getMessage vai ser mostrado pelo javaScript
		}
		return ResponseEntity.ok().build();
	}
	

	@RequestMapping(value = "buscaCep", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
	public ResponseEntity<String> buscaCep(String buscaCep) {
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = null;
			response = restTemplate.getForEntity(
					"http://api.postmon.com.br/v1/cep/"+buscaCep, String.class);
			return response;
	}
	
	@RequestMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody List<Cliente> pesquisar(String nome){
		validarTamanhoNome(nome);
		return clientes.findByNomeStartingWithIgnoreCase(nome);
	}
	
	// ExceptionHandle in this case handles only exceptions of this controller
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> tratarIllegalArgumentException(IllegalArgumentException e){
		return ResponseEntity.badRequest().build();
	}
	@ExceptionHandler(RestClientException.class)
	public ResponseEntity<Void> tratarRestClientException(RestClientException e){
		return ResponseEntity.badRequest().build();
	}
	
	private void validarTamanhoNome(String nome) {
		if (StringUtils.isEmpty(nome) || nome.length() < 3) {
			throw new IllegalArgumentException(); 
		}
	}
	
}
