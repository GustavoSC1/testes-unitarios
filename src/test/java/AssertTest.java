

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {
	
	@Test
	public void test() {
		Assert.assertTrue(true);
		Assert.assertFalse(false);
		
		//Assert.assertEquals(Mensagem, Valor Esperado, Valor Atual);
		Assert.assertEquals("Erro de comparação",1, 1);
		
		//Para comprar dois doubles é preciso sempre informar um delta de comparação, esse delta seria uma margem de erro de comparação. 
		Assert.assertEquals(0.51234, 0.512, 0.001);
		Assert.assertEquals(Math.PI, 3.14, 0.01);
		
		int i = 5;
		Integer i2 = 5;
		
		Assert.assertEquals(Integer.valueOf(i), i2);
		Assert.assertEquals(i, i2.intValue());
		
		Assert.assertEquals("bola", "bola");
		Assert.assertNotEquals("bola", "casa");
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		Assert.assertTrue("bola".startsWith("bo"));
		
		Usuario u1 = new Usuario("Usuario 1");
		Usuario u2 = new Usuario("Usuario 1");
		Usuario u3 = null;
		
		//Utiliza o método equals da classe
		Assert.assertEquals(u1, u2);
		
		//Faz a comparação em nível de instância
		Assert.assertSame(u2, u2);
		Assert.assertNotSame(u1, u2);
		
		Assert.assertNull(u3);
		Assert.assertNotNull(u2);
	}

}
