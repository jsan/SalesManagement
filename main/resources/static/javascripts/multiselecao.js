Brewer = Brewer || {};

Brewer.MultiSelecao = (function() {
	
	function MultiSelecao() {
		this.statusBtn = $('.js-status-btn');
		this.selecaoCheckbox = $('.js-selecao');
		this.selecaoTodosCheckbox = $('.js-selecao-todos');
	}
	
	MultiSelecao.prototype.iniciar = function() {
		this.statusBtn.on('click', onStatusBtnClicado.bind(this));
		this.selecaoTodosCheckbox.on('click', onSelecaoTodosClicado.bind(this));
		this.selecaoCheckbox.on('click', onSelecaoClicado.bind(this));
	}
	
	function onStatusBtnClicado(event) {
		var botaoClicado = $(event.currentTarget); // gets tag that was clicked: <button>
		var status = botaoClicado.data('status');  // gets data:status value: ATIVAR/DESATIVAR
		var url = botaoClicado.data('url');
		
		var checkBoxSelecionados = this.selecaoCheckbox.filter(':checked'); // gets all the checked
		var codigos = $.map(checkBoxSelecionados, function(c) {
			return $(c).data('codigo'); 			// gets all data:codigo values and puts into var codigos 
		});
		
		if (codigos.length > 0) {
			$.ajax({
				url: url,
				method: 'PUT',
				data: {
					codigos: codigos,
					status: status
				}, 
				success: function() {
					window.location.reload();
				}
			});
			
		}
	}
	
	function onSelecaoTodosClicado() {
		var status = this.selecaoTodosCheckbox.prop('checked');
		this.selecaoCheckbox.prop('checked', status);
		statusBotaoAcao.call(this, status);
	}
	
	function onSelecaoClicado() {
		var selecaoCheckboxChecados = this.selecaoCheckbox.filter(':checked');
		this.selecaoTodosCheckbox.prop('checked', selecaoCheckboxChecados.length >= this.selecaoCheckbox.length);
		statusBotaoAcao.call(this, selecaoCheckboxChecados.length);
	}
	
	function statusBotaoAcao(ativar) {
		ativar ? this.statusBtn.removeClass('disabled') : this.statusBtn.addClass('disabled');
	}
	
	return MultiSelecao;
	
}());

$(function() {
	var multiSelecao = new Brewer.MultiSelecao();
	multiSelecao.iniciar();
});
