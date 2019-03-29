Brewer = Brewer || {};

Brewer.Autocomplete = (function() {
	
	function Autocomplete() {
		this.skuOuNomeInput = $('.js-sku-nome-cerveja-input');
		var htmlTemplateAutocomplete = $('#template-autocomplete-cerveja').html();
		this.template = Handlebars.compile(htmlTemplateAutocomplete);
		this.emitter = $({}); // emissor de eventos
		this.on = this.emitter.on.bind(this.emitter);
	}
	// começca na aula 22.7
	Autocomplete.prototype.iniciar = function() {
		var options = {
			url: function(skuOuNome) {
				return this.skuOuNomeInput.data('url') + '?skuOuNome=' + skuOuNome;
			}.bind(this),
			getValue: 'nome',
			minCharNumber: 3,
			requestDelay: 300,
			ajaxSettings: {
				contentType: 'application/json'
			},
			template: {
				type: 'custom',
				method: template.bind(this) 
			},
			
			list: {
				onChooseEvent: onItemSelecionado.bind(this)
			}
		};

		function onItemSelecionado() {
			this.emitter.trigger('item-selecionado', this.skuOuNomeInput.getSelectedItemData());
			this.skuOuNomeInput.val('');
			this.skuOuNomeInput.focus();
		}
		
		function template(nome, cerveja){
				cerveja.valorFormatado = Brewer.formatarMoeda(cerveja.valor);
				return this.template(cerveja);
		}
		
		this.skuOuNomeInput.easyAutocomplete(options);
	}
	
	return Autocomplete
	
}());


