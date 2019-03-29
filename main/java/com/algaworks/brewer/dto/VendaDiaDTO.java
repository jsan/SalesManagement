package com.algaworks.brewer.dto;

public class VendaDiaDTO {

	
	private String dia;
	private Integer total;
	
	public VendaDiaDTO() {
	}
	
	public VendaDiaDTO(String dia, Integer total) {
		this.dia = dia;
		this.total = total;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	
}
