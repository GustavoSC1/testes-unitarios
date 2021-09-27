package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Usuario;

public class UsuarioBuilder {
	
	private Usuario usuario;
	
	//Está privado para que ninguem possa criar instâncias do builder externamente ao próprio builder
	private UsuarioBuilder() {}
	
	//Ficou public e static para que ele possa ser acessado externamente sem a necessidade de uma instância
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
