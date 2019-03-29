package com.algaworks.brewer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Usuarios;
import com.algaworks.brewer.service.exception.GenericMessageException;
import com.algaworks.brewer.service.exception.SenhaMessageException;

@Service
public class CadastroUsuarioService {

	@Autowired
	private Usuarios usuarios;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Transactional
	public void salvar(Usuario usuario)  {
		// BCrypt used directly from the class (not using the bean injection)
		// usuario.setSenha(BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt())); 

		// checking password equality 
		// if (BCrypt.checkpw(usuario.getSenha(), stored_hash))
		
		Optional <Usuario> usuarioExisteComEsteEmail = usuarios.findByEmail(usuario.getEmail());
		
		if (usuarioExisteComEsteEmail.isPresent() && !usuarioExisteComEsteEmail.get().equals(usuario)) {
			throw new GenericMessageException("Email já cadastrado");
		}
		
		if (usuario.isNovo() && StringUtils.isEmpty(usuario.getSenha())) {
			throw new SenhaMessageException("Senha é obrigatório para novo usuario");
		}
		
		 
		if (usuario.isNovo() || !StringUtils.isEmpty(usuario.getSenha())) {
			usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
			usuario.setConfirmacaoSenha(usuario.getSenha());
		}else if (StringUtils.isEmpty(usuario.getSenha())) {
			usuario.setSenha(usuarioExisteComEsteEmail.get().getSenha());
			usuario.setConfirmacaoSenha(usuarioExisteComEsteEmail.get().getSenha());
		}
		
		if(!usuario.isNovo() && usuario.getAtivo() == null) {
			usuario.setAtivo(usuarioExisteComEsteEmail.get().getAtivo());
		}
		
		usuarios.save(usuario);
	}
	
	@Transactional
	public void alteraStatus (Long[] codigos, StatusUsuario statusUsuario) {
		statusUsuario.executar(codigos, usuarios);
	}
}
