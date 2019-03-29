Brewer = Brewer || {};

Brewer.PesquisaRapidaCliente = (function() {
	
	function PesquisaRapidaCliente(){
		this.pesquisaRapidaClientesModal = $('#pesquisaRapidaClientes');
		this.nomeInput = $('#nomeInput');
		this.pesquisaRapidaBtn = $('.js-pesquisa-rapida-clientes-btn');
		this.containerTabelaPesquisa = $('#containerTabelaPesquisaRapidaClientes'); 
		this.htmlTabelaPesquisa = $('#tabela-pesquisa-rapida-cliente').html();
		this.template = Handlebars.compile(this.htmlTabelaPesquisa);
		this.mensagemErro = $('.js-mensagem-erro');
	}
	
	PesquisaRapidaCliente.prototype.iniciar = function (){
		this.pesquisaRapidaBtn.on('click', onPesquisaRapidaClick.bind(this));
		this.pesquisaRapidaClientesModal.on('shown.bs.modal', onModalShow.bind(this));
	}
	
	function onModalShow() {
		this.nomeInput.focus();
	}
	
	function onPesquisaRapidaClick(event){
		event.preventDefault(); // evita que o evento default execute (submeter formulario)

		$.ajax({
			url: this.pesquisaRapidaClientesModal.find('form').attr('action'),
			method: 'GET',
			contentType: 'application/json',
			data: {
				nome: this.nomeInput.val()
			},
			success: onPesquisaConcluida.bind(this),
			error: onErroPesquisa.bind(this)
		});
	}
	
	function onPesquisaConcluida(resultado) {
		var html = this.template(resultado);
		this.containerTabelaPesquisa.html(html);
		this.mensagemErro.addClass('hidden');

		var tabelaClientePesquisaRapida = new Brewer.TabelaClientePesquisaRapida(this.pesquisaRapidaClientesModal);
		tabelaClientePesquisaRapida.iniciar();

	}
	
	function onErroPesquisa(){
		this.mensagemErro.removeClass('hidden');
	}
	
	return PesquisaRapidaCliente;
	
}());


Brewer.TabelaClientePesquisaRapida = (function(){
	
	function TabelaClientePesquisaRapida (modal){
		this.modalCliente = modal;
		this.cliente = $('.js-cliente-pesquisa-rapida');
	}
	
	TabelaClientePesquisaRapida .prototype.iniciar = function(){
		this.cliente.on('click', onClienteSelecionado.bind(this));
	}
	function  onClienteSelecionado(evento){
		this.modalCliente.modal('hide');
		
		var clienteSelecionado = $(evento.currentTarget);
		$('#nomeCliente').val(clienteSelecionado.data('nome'));
		$('#codigoCliente').val(clienteSelecionado.data('codigo'));
	}
	return TabelaClientePesquisaRapida ;

}());

$(function(){
	var pesquisaRapidaCliente = new Brewer.PesquisaRapidaCliente();
	pesquisaRapidaCliente.iniciar();
});