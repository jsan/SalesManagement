var Brewer = Brewer || {};

Brewer.MaskMoney = (function() {
	
	function MaskMoney(){
		this.decimal = $('.js-decimal');
		this.plain = $('.js-plain');
	}

	MaskMoney.prototype.enable = function () {
		this.decimal.maskMoney({decimal: ',', thousands: '.'});
		this.plain.maskMoney({precision: 0, thousands: '.'});
	}
	
	return MaskMoney;
	
}());


Brewer.MaskPhoneNumber = (function(){
	
	function MaskPhoneNumber(){
		this.inputPhoneNumber = $('.js-phone-number');
		
	}
	
	MaskPhoneNumber.prototype.enable = function(){
		
		var SPMaskBehavior = function (val) {
		  return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00009';
		},
		spOptions = {
		  onKeyPress: function(val, e, field, options) {
		      field.mask(SPMaskBehavior.apply({}, arguments), options);
		    }
		};

		this.inputPhoneNumber.mask(SPMaskBehavior, spOptions);
		
	}
	
	return MaskPhoneNumber;
	
}());
 

Brewer.MaskDate = (function(){
	
	function MaskDate(){
		this.inputDate = $('.js-date');
		this.inputDate.datepicker({
			orientation: 'bottom',
			language: 'pt-BR',
			autoclose: true
		});
	}
	
	MaskDate.prototype.enable = function(){
		this.inputDate.mask('00/00/0000');
	}
	
	return MaskDate;
	
}());

Brewer.MaskHour = (function(){
	
	function MaskHour(){
		this.inputHour = $('.js-hour');
	}
	
	MaskHour.prototype.enable = function(){
		this.inputHour.mask('00:00');
	}
	
	return MaskHour;
	
}());

Brewer.MaskCepNumber = (function(){
	
	function MaskCepNumber(){
		this.inputCepNumber = $('.js-cep');
		this.inputLogradouro = $('.js-logradouro');
	}
	
	MaskCepNumber.prototype.enable = function(){
		this.inputCepNumber.mask('00000-000');
		this.inputCepNumber.on('blur', onBlurBuscaCep.bind(this));
	}
	
	function onBlurBuscaCep(){
		var cep = this.inputCepNumber.val().trim();
		$.ajax({
			url: 'buscaCep?buscaCep='+cep,
			type: 'GET',
			error: onErroBuscandoCep.bind(this),
			success: onAchouCep.bind(this)
		});
	}
	
	function onAchouCep(obj){
		this.inputLogradouro.val(obj.logradouro);
	}
	
	function onErroBuscandoCep(obj){
		this.inputLogradouro.val("");
		console.log("CEP não encontrado: ", this.inputCepNumber.val());
	}
	
	return MaskCepNumber;
	
}());

Brewer.Security = (function() {
	
	function Security() {
		this.token = $('input[name=_csrf]').val();
		this.header = $('input[name=_csrf_header]').val();
	}
	
	Security.prototype.enable = function() {
		$(document).ajaxSend(function(event, jqxhr, settings) {
			jqxhr.setRequestHeader(this.header, this.token);
		}.bind(this));
	}
	
	return Security;
	
}());


numeral.language('pt-br');

Brewer.formatarMoeda = function(valor) {
	return numeral(valor).format('0,0.00');
}

Brewer.recuperaValor = function (valorFormatado){
	return numeral().unformat(valorFormatado);
}

$(function() {
    var maskMoney = new Brewer.MaskMoney();
    maskMoney.enable();
    
    var maskPhoneNumber = new Brewer.MaskPhoneNumber();
    maskPhoneNumber.enable(); 

    var maskCepNumber = new Brewer.MaskCepNumber();
    maskCepNumber.enable(); 

    var maskDate = new Brewer.MaskDate();
    maskDate.enable(); 
    
	var security = new Brewer.Security();
	security.enable();
});




