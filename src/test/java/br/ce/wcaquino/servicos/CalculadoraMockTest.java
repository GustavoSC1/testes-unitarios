package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {
	
	@Mock
	private Calculadora calcMock;
	
	@Spy
	private Calculadora calcSpy;
	
	@Before
	public void stup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void devoMostrarDiferencaEntreMockSpy() {
		Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
		
		//O Mock ir� chamar a implementa��o do m�todo real (n�o pode ser uma interface)
		//Mockito.when(calcMock.somar(1, 2)).thenCallRealMethod();
		
		//A implementa��o do m�todo calcSpy � executado no momento da execu��o da expectativa
		//Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
		
		//A implementa��o do m�todo m�todo n�o � executado na execu��o da expectativa
		Mockito.doReturn(5).when(calcSpy).somar(1, 2);
		
		//Faz com que o m�todo void n�o seja executado pelo Spy
		Mockito.doNothing().when(calcSpy).imprime();
				
		System.out.println("Mock: " + calcMock.somar(1, 2));
		System.out.println("Spy: " + calcSpy.somar(1, 2));
	}
	
	
	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		//Se um m�todo que estou verificando possuir mais que um par�metro e eu utilizar matcher em algum desses
		//param�tros, ent�o eu terei que utilizar em todos
		//Quando eu fizer de algo igual a 1 e qualquer inteiro ent�o o retorno deve ser 5
		Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
		
		Assert.assertEquals(5, calc.somar(1, 10000000));
	}
	
	@Test
	public void teste2() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		Assert.assertEquals(5, calc.somar(12345, -234));
		//System.out.println(argCapt.getAllValues());
	}

}
