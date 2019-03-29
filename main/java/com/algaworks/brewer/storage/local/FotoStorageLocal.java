package com.algaworks.brewer.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

public class FotoStorageLocal implements FotoStorage {

	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);
	private static final String THUMBNAIL_PREFIX = "thumbnail.";
	
	private Path local;
	private Path localTemporario;
	
	public FotoStorageLocal () {
		
		this.local = getDefault().getPath(System.getProperty("user.home"), ".brewerfotos"); // C:\Users\jorge\.brewerfotos
		criarPastas();
	}
	
	@Override
	public String salvarTemporariamente(MultipartFile[] files) {
		
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0]; 
			String novoNome = renomearArquivo(arquivo.getOriginalFilename());
			try {
				arquivo.transferTo(new File(this.localTemporario.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome));
			} catch (IllegalStateException | IOException e) {
				throw new RuntimeException("Erro ao tentar gravar o arquivo no local temporario", e);
			}
			
			return novoNome;
		}
		
		throw new RuntimeException("Erro File vazio ou nulo.");
		
	}

	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		return recuperarFotoThumb(THUMBNAIL_PREFIX + fotoCerveja);
	}

	@Override
	public byte[] recuperarFotoTemporaria(String nome) {
		try {
			return Files.readAllBytes(this.localTemporario.resolve(nome));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo uma foto temporaria");
		}
	}

	@Override
	public byte[] recuperarFotoThumb(String nome) {
		try {
			return Files.readAllBytes(this.local.resolve(nome));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo uma foto thumb", e);
		}
	}

	@Override
	public void salvar(String foto) {
		try {
			Files.move(this.localTemporario.resolve(foto), this.local.resolve(foto));
		} catch (IOException e) {
			throw new RuntimeException("Erro ao mover foto de local temp", e);
		}
		
		try {
			Thumbnails.of(this.local.resolve(foto).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
		} catch (IOException e) {
			throw new RuntimeException("Erro criando Thumbnail", e);
		}
	}
	
	@Override
	public void excluir(String foto) {
		try {
			Files.deleteIfExists(this.local.resolve(foto));
			Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + foto));
		} catch (IOException e) {
			logger.warn(String.format("Erro ao tentar apagar foto %s. Mensagem: %s", foto, e.getMessage()));
		}
	}
	
	private void criarPastas() {
		 try {
			Files.createDirectories(this.local);
			this.localTemporario = getDefault().getPath(this.local.toString(),"temp");
			Files.createDirectories(this.localTemporario);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Pasats criadas para salvar fotos.");
				logger.debug("Pasta Default:" + this.local.toAbsolutePath());
				logger.debug("Pasta temporaria: {}", this.localTemporario.toAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar foto");
		}
	}
	
	private String renomearArquivo(String nomeOriginal) {
		String novoNome = nomeOriginal.substring(0, nomeOriginal.indexOf(".")) + "_" + UUID.randomUUID().toString() + nomeOriginal.substring(nomeOriginal.indexOf("."), nomeOriginal.length()); 
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("nome original %s, nome Novo %s", nomeOriginal, novoNome));
		}		
		
		return novoNome;
	}

}
