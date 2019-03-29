package com.algaworks.brewer.storage;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;

public class FotoStorageRunnable implements Runnable {

	private MultipartFile[] files;
	private DeferredResult<FotoDTO> resultado;
	private FotoStorage fotoStorage;
	
	public FotoStorageRunnable(MultipartFile[] files, DeferredResult<FotoDTO> resultado, FotoStorage fotoStorage) {
		this.files = files;
		this.resultado = resultado;
		this.fotoStorage = fotoStorage;
	}
	@Override
	public void run() {
		System.out.println("file name:"+files[0].getOriginalFilename());
		
		String novoNome = this.fotoStorage.salvarTemporariamente(files);
		resultado.setResult(new FotoDTO(novoNome ,files[0].getContentType()));
	}

}
