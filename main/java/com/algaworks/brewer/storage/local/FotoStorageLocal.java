package com.algaworks.brewer.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Profile("local")
@Component
public class FotoStorageLocal implements FotoStorage {

	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);
	private static final String THUMBNAIL_PREFIX = "thumbnail.";
	
	private Path local;
	
	public FotoStorageLocal () {
		
		this.local = getDefault().getPath(System.getProperty("user.home"), ".brewerfotos"); // C:\Users\jorge\.brewerfotos
		criarPastas();
	}
	
	@Override
	public String salvar(MultipartFile[] files) {
		
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0]; 
			String novoNome = renomearArquivo(arquivo.getOriginalFilename());
			try {
				arquivo.transferTo(new File(this.local.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome));
			} catch (IllegalStateException | IOException e) {
				throw new RuntimeException("Erro ao tentar gravar o arquivo", e);
			}
			
			try {
				Thumbnails.of(this.local.resolve(novoNome).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
			} catch (IOException e) {
				throw new RuntimeException("Erro criando Thumbnail", e);
			}
		
			return novoNome;
		}
		
		throw new RuntimeException("Erro File vazio ou nulo.");
		
	}
	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		return recuperar(THUMBNAIL_PREFIX + fotoCerveja);
	}

	@Override
	public byte[] recuperar(String nome) {
		try {
			return Files.readAllBytes(this.local.resolve(nome));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo uma foto thumb", e);
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

	@Override
	public String getUrl(String nomeFoto) {
		return "Http://localhost:8080/brewer/fotos/" + nomeFoto;
	}
	
	private void criarPastas() {
		 try {
			Files.createDirectories(this.local);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Pasats criadas para salvar fotos.");
				logger.debug("Pasta Default:" + this.local.toAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar foto");
		}
	}
	
}
