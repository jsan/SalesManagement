package com.algaworks.brewer.service.exception;

public class GenericMessageException extends RuntimeException{

	private static final long serialVersionUID = 2900915920759299514L;

	public GenericMessageException (String message) {
		super(message);
	}
}
