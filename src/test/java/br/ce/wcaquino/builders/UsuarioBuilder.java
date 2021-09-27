package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Usuario;

public class UsuarioBuilder {
	
	private Usuario usuario;
	
	//Est� privado para que ninguem possa criar inst�ncias do builder externamente ao pr�prio builder
	private UsuarioBuilder() {}
	
	//Ficou public e static para que ele possa ser acessado externamente sem a necessidade de uma inst�ncia
	public static UsuarioBuilder umUsuario() {
		UsuarioBuilder builder = new UsuarioBuilder();
		builder.usuario = new Usuario();
		builder.usuario.setNome("Usuario 1");
		return builder;
	}
	
	public UsuarioBuilder comNome(String nome) {
		usuario.setNome(nome);
		return this;
	}
	
	public Usuario agora() {
		return usuario;
	}

}
