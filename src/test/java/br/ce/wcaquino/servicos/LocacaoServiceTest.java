package br.ce.wcaquino.servicos;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	// Informa que os objetos @Mock serão injetados nessa classe de testes
	@InjectMocks @Spy
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
		System.out.println("Iniciando 2...");
		CalculadoraTest.ordem.append("2");
	}
	
	//Será executado depois de cada teste
	@After
	public void tearDown() {
		System.out.println("finalizando 2...");
	}
	//Será executado depois da classe ser finalizada
	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}
	/*
	//Será executado antes da classe ser instanciada
	@BeforeClass
	public static void setupClass() {
		System.out.println("Before Class");
	}
	*/
	
	
	@Test
	public void deveAlugarFilme() throws Exception {
		//Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario		
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().comValor(5.0).agora());
				
		Mockito.doReturn(DataUtils.obterData(5, 2, 2021)).when(service).obterData();
								
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));		
		//error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		//error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
		//error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));
		//error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(1));		
		
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(5, 2, 2021)), CoreMatchers.is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(6, 2, 2021)), CoreMatchers.is(true));
		/*
		//Assert.assertEquals(Valor Atual, Valor Esperado);
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.not(6.0)));
		Assert.assertThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		Assert.assertThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));
		*/
	}
	
	//Teste esperando exceção, caso  o filme não esteja no estoque. Para o teste passar a exceção deverá ser lançada.
	//Para utilizar a forma elegante você têm que garantir que a exception está vindo por um único motivo, nesse caso foi criado
	//uma exception exclusiva
	//Teste Elegante
	@Test(expected=FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora());
				
		//acao
		service.alugarFilme(usuario, filmes);		
	}
	
	//Forma Robusta
	//É a forma que possibilita ter maior poder sobre a execução
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		//cenario
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());		
		
		//acao		
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio"));
		}
			
	}
	
	//Forma Nova
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
						
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		//acao
		service.alugarFilme(usuario, null);
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		//Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.doReturn(DataUtils.obterData(6, 2, 2021)).when(service).obterData();
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		//boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		//Assert.assertTrue(ehSegunda);
		
		//Usando Matchers Próprios
		//Assert.assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
		//Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
		Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
		
		//Verificar se o construtor sem argumentos foi chamado
		//PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
		

		//Verificação de chamada de métodos estáticos
		//PowerMockito.verifyStatic(Calendar.class, Mockito.times(2));
		//Calendar.getInstance();
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//A comparação da expectativa com o usuario enviado é feita a partir do equals
		//Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true);
		
		Mockito.when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
				
		//acao
		try {
			service.alugarFilme(usuario, filmes);
			//verificacao 
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuário Negativado"));
		}
			
		Mockito.verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarParaLocacoesAtrasados() {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario em dia").agora();
		Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Outro atrasado").agora();
		List<Locacao> locacoes = Arrays.asList(
				LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario).agora(),
				LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
				LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).agora(),
				LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).agora());
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
		
		//verificacao
		
		//Verifique no mock email que serão realizadas três execuções ao método notificarAtraso passando como
		//parâmetro qualquer instância ca classe Usuário
		Mockito.verify(email, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
		//Verifica se o método notificarAtraso(usuario) foi chamado para o usuario		
		Mockito.verify(email).notificarAtraso(usuario);
		//O método é invocado duas vezes para o usuário3     .atLast(2)   .atMost(5)  .atLeastOnce()
		Mockito.verify(email, Mockito.times(2)).notificarAtraso(usuario3);
		Mockito.verify(email, Mockito.never()).notificarAtraso(usuario2);
		//Verificar que não vai acontecer mais nenhuma interação com email, além das descritas anteriormente
		Mockito.verifyNoMoreInteractions(email);
		//Mockito.verifyZeroInteractions(spc);
	}
	
	@Test
	public void deveTratarErrosSPC() throws Exception {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = LocacaoBuilder.umLocacao().agora();
		
		//acao
		service.prorrogarLocacao(locacao, 3);		
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		//Captura o argumento passado para o método .salvar()
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), CoreMatchers.is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(3));
	}
		
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//cenario
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//acao
		//Testando métodos privados diretamente utilizando a api reflection
		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);
				
		//verificacao
		Assert.assertThat(valor, CoreMatchers.is(4.0));
	}
	
}
