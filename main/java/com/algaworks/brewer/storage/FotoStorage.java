package com.algaworks.brewer.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FotoStorage {

	public String salvarTemporariamente(MultipartFile[] files);

	public void salvar(String foto);
	
	public byte[] recuperarFotoTemporaria(String nome);
	
	public byte[] recuperarFotoThumb(String nome);
	
	public byte[] recuperarThumbnail(String fotoCerveja);

	public void excluir(String foto);

}
