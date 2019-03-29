package com.algaworks.brewer.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.apache.commons.beanutils.BeanUtils;

import com.algaworks.brewer.validation.AtributoConfirmacao;

public class AtributoConfirmacaoValidator implements ConstraintValidator<AtributoConfirmacao, Object> {

	private String atributo;
	private String atributoConfirmacao;

	@Override
	public void initialize(AtributoConfirmacao constraintAnnotation) {
		this.atributo = constraintAnnotation.atributo();
		this.atributoConfirmacao = constraintAnnotation.atributoConfirmacao();

	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {
		boolean valido = false;
		try {
			Object valorAtributo = BeanUtils.getProperty(obj, this.atributo);
			Object valorAtributoConfirmacao = BeanUtils.getProperty(obj, this.atributoConfirmacao);
			valido = isOsDoisSaoNull(valorAtributo, valorAtributoConfirmacao ) || isOsdoisSaoIguaisIguais(valorAtributo, valorAtributoConfirmacao );

		} catch (Exception e) {
			throw new RuntimeException("Errom recuperando valores dos atributos de senha", e);
		}
		
		if (!valido) {
			context.disableDefaultConstraintViolation();
			String mensagem = context.getDefaultConstraintMessageTemplate();
			ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(mensagem);
			violationBuilder.addPropertyNode(atributoConfirmacao).addConstraintViolation();
		}
		
		return valido;

	}

	private boolean isOsdoisSaoIguaisIguais(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo != null && valorAtributo.equals(valorAtributoConfirmacao);
	}

	private boolean isOsDoisSaoNull(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo == null && valorAtributoConfirmacao == null;
	}

}
