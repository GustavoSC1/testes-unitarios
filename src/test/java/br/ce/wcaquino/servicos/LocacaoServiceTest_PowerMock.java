package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.DiaSemanaMatcher;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.utils.DataUtils;

//Avisa ao Junit que essa execução deve ser gerenciada pelo PowerMock
@RunWith(PowerMockRunner.class)
//Diz para preparar a classe LocacaoService para testes
//@PrepareForTest({LocacaoService.class, DataUtils.class})
@PrepareForTest({LocacaoService.class})
public class LocacaoServiceTest_PowerMock {
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private LocacaoDAO dao;		
	@Mock
	private SPCService spc;	
	@Mock
	private EmailService email;
	
	//TestRule
	//ErrorCollector: coleta vários erros em um método de teste
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	//TestRule
	//ExpectedException: permite que você verifique se o seu código lança uma exceção específica
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	//Será executado antes de cada teste
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = PowerMockito.spy(service);
		System.out.println("iniciando 4...");
		CalculadoraTest.ordem.append("4");
	}
	
	@After
	public void tearDown() {
		System.out.println("finalizando 4...");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {		
		//cenario		
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().comValor(5.0).agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(5, 2, 2021));
		
		/*
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 5);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.YEAR, 2021);
		
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		*/
						
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
		
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(5, 2, 2021)), CoreMatchers.is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(6, 2, 2021)), CoreMatchers.is(true));		
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//Quando eu solicitar uma nova instância do Date utilizando um construtor que não possui argumentos
		//então retorne uma determinada data
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(6, 2, 2021));
		
		
		//Resolvendo o problema da Data através do Calendar, ao invés do Date
		/*Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 6);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.YEAR, 2021);
		
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		*/
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		//boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		//Assert.assertTrue(ehSegunda);
		//Assert.assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
		//Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
		Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
		
		//Verificar se o construtor sem argumentos foi chamado
		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
		

		//Verificação de chamada de métodos estáticos
		/*
		PowerMockito.verifyStatic(Calendar.class, Mockito.times(2));
		Calendar.getInstance();
		*/
		
	}
	
	@Test
	public void deveAlugarFilme_SemCalcularValor() throws Exception {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//Mockando métodos privados
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario,  filmes);
		
		//verificacao
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//cenario
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//acao
		//Testando métodos privados diretamente
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		
		//verificacao
		Assert.assertThat(valor, CoreMatchers.is(4.0));
	}
	
}
