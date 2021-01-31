package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {
	@Test
	public void test() {
		Assert.assertTrue(true);
		Assert.assertFalse(false);
		
		Assert.assertEquals("Erro de comparação: ",1, 1);
		Assert.assertEquals(0.51, 0.51,0.001);//Margem de erro 0.001
		Assert.assertEquals(Math.PI, 3.14, 0.01);
		
		int i = 5;
		Integer i2 =5;
		
		Assert.assertEquals(Integer.valueOf(i),i2);
		Assert.assertEquals(i,i2.intValue());
		
		Assert.assertEquals("bola","bola");
		Assert.assertNotEquals("bola","casa");//Não são iguais
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		Assert.assertTrue("bola".startsWith("bo"));
		
		Usuario u1 = new Usuario("Usuario 1 ");
		Usuario u2 = new Usuario("Usuario 1 ");
		Usuario u3 = null;
		
		Assert.assertEquals(u1, u2);//Compara se os objetos tem os mesmos nome, após implementar o Equals no usuário
		Assert.assertSame(u1, u1);//Compara se os objetos são os mesmos
		Assert.assertNotSame(u1, u2);
		Assert.assertNull(u3);
	}

}
