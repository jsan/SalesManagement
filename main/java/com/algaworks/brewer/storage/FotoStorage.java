package com.algaworks.brewer.storage;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface FotoStorage {

	public final String THUMBNAIL_PREFIX = "thumbnail.";
	
	public String salvar(MultipartFile[] files);

	public byte[] recuperar(String nome);
	
	public byte[] recuperarThumbnail(String fotoCerveja);
	
	public String getUrl(String nomeFoto);

	public void excluir(String foto);
	
	default String renomearArquivo(String nomeOriginal) {
		return nomeOriginal.substring(0, nomeOriginal.indexOf(".")) + "_" + UUID.randomUUID().toString() + nomeOriginal.substring(nomeOriginal.indexOf("."), nomeOriginal.length()); 
	}

}
