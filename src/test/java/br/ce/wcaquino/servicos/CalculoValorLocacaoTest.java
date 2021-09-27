package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

//Testes Parametrizáveis
@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private LocacaoDAO dao;
	
	@Mock
	private SPCService spc;
	
	//Faz um link entre a variável e os parametros de getParametros
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value=1)
	public Double valorLocacao;
	
	@Parameter(value=2)
	public String cenario;
	
	//Será executado antes de cada teste
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		System.out.println("iniciando 3...");
		CalculadoraTest.ordem.append("3");		
	}
	
	@After
	public void tearDown() {
		System.out.println("finalizando 3...");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}
	
	private static Filme filme1 = FilmeBuilder.umFilme().agora();
	private static Filme filme2 = FilmeBuilder.umFilme().agora();
	private static Filme filme3 = FilmeBuilder.umFilme().agora();
	private static Filme filme4 = FilmeBuilder.umFilme().agora();
	private static Filme filme5 = FilmeBuilder.umFilme().agora();
	private static Filme filme6 = FilmeBuilder.umFilme().agora();
	
	//Informa ao JUnit que esse método será a fonte de dados
	//O método que define a coleção deve ser estático pq é ele que vai definir a quantidade de execuções de cada
	//método de teste
	@Parameters(name="{2}") //<- Melhora a legibilidade do resultados no terminal
	public static Collection<Object[]> getParametros(){
		return Arrays.asList(new Object[][] {
			{Arrays.asList(filme1, filme2, filme3), 11.0, "3 Filmes: 25%"},
			{Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 Filmes: 50%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 FIlmes: 75%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 Filmes: 100%"}
		});
	}
	
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
				
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		Assert.assertThat(resultado.getValor(), CoreMatchers.is(valorLocacao));
	}
	
}
