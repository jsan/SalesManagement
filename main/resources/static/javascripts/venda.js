Brewer.Venda = (function(){
	
	function Venda(tabelaItens){
		this.tabelaItens = tabelaItens;
		this.valorTotalBox = $('.js-valor-total-box');
		this.valorFreteInput = $('#valorFrete');
		this.valorDescontoInput = $('#valorDesconto');
		this.valorTotalBoxContainer = $('.js-valor-total-box-container');
		
		this.valorTotalItens = this.tabelaItens.valorTotal();
		this.valorFrete = this.valorFreteInput.data('valor');
		this.valorDesconto = this.valorDescontoInput.data('valor') ;
	}
	
	Venda.prototype.iniciar = function(){
		this.tabelaItens.on('tabela-itens-atualizada', onTabelaItensAtualizada.bind(this));
		this.valorFreteInput.on('keyup', onFreteAlterado.bind(this));
		this.valorDescontoInput.on('keyup', onDesconAlterado.bind(this));
		
		this.tabelaItens.on('tabela-itens-atualizada', onValoresAlterados.bind(this));
		this.valorFreteInput.on('keyup', onValoresAlterados.bind(this));
		this.valorDescontoInput.on('keyup', onValoresAlterados.bind(this));
		
		onValoresAlterados.call(this);
	}
	
	function onTabelaItensAtualizada(evento,valorTotalItens){
		this.valorTotalItens = valorTotalItens == null ? 0 : valorTotalItens;
	}
	
	function onFreteAlterado(evento){
		this.valorFrete = Brewer.recuperaValor($(evento.target).val());
	}
	
	function onDesconAlterado(evento){
		this.valorDesconto = Brewer.recuperaValor($(evento.target).val());
	}
	
	function onValoresAlterados (){
		var valorTotal = numeral(this.valorTotalItens) + numeral(this.valorFrete) - numeral(this.valorDesconto);
		this.valorTotalBox.html(Brewer.formatarMoeda(valorTotal));
		
		this.valorTotalBoxContainer.toggleClass('negativo', valorTotal < 0);
	}

	return Venda;
	
}());


$(function() {
	
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();
	
	var tabelaItens = new Brewer.TabelaItens(autocomplete);
	tabelaItens.iniciar();

	var venda = new Brewer.Venda(tabelaItens);
	venda.iniciar();
	
})