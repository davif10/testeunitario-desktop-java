package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	private LocacaoService service;
	
	//Definicao do contador
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		service = new LocacaoService();
	}
	
	@After
	public void tearDown() {
		
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		//Cenario 
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> listaFilmes = Arrays.asList(new Filme("Filme 1",2,5.0));
	
		
		//A��o 
		Locacao locacao = service.alugarFilme(usuario, listaFilmes);
			
			//Verifica��o
			error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),CoreMatchers.is(true));
			
			
			Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
			Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.not(6.0)));
			assertThat(locacao.getValor(), is(5.0));
			Assert.assertEquals(5.0,locacao.getValor(),0.01);
			Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
			Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
			Assert.assertThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),CoreMatchers.is(true));
			
		
		
	}
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
				//Cenario 
				Usuario usuario = new Usuario("Usuario 1");
				List<Filme> listaFilmes = Arrays.asList(new Filme("Filme 1",0,4.0));
				
				//A��o 
				service.alugarFilme(usuario, listaFilmes);
	}
	
	//Forma mais robusta
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		//Cenario
		List<Filme> listaFilmes = Arrays.asList(new Filme("Filme 1",1,5.0));
		
		//Acao
			try {
				service.alugarFilme(null, listaFilmes);
				Assert.fail();
			}  catch (LocadoraException e) {
				Assert.assertThat(e.getMessage(),is("Usu�rio vazio"));
			}
			
		
	}
	//Forma nova
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		//Cenario 
		Usuario usuario = new Usuario("Usuario 1");
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		//Acao
		service.alugarFilme(usuario, null);
		
	}
	
	@Test
	public void devePagar75PctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
		
		//Cen�rio
		
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> listaFilmes = Arrays.asList(
				new Filme("Filme 1",2,4.0),
				new Filme("Filme 2",2,4.0),
				new Filme("Filme 3",2,4.0));
		
		//A��o 
		
		Locacao resultado  = service.alugarFilme(usuario, listaFilmes);
		
		
		//Verifica��o
		
		assertThat(resultado.getValor(),is(11.0));
		
		
	}
	
	@Test
	public void devePagar50PctNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
		
		//Cen�rio
		
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> listaFilmes = Arrays.asList(
				new Filme("Filme 1",2,4.0),
				new Filme("Filme 2",2,4.0),
				new Filme("Filme 3",2,4.0),
				new Filme("Filme 4",2,4.0));
		
		//A��o 
		
		Locacao resultado  = service.alugarFilme(usuario, listaFilmes);
		
		
		//Verifica��o
		
		assertThat(resultado.getValor(),is(13.0));
		
		
	}
	
	@Test
	public void devePagar25PctNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
		
		//Cen�rio
		
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> listaFilmes = Arrays.asList(
				new Filme("Filme 1",2,4.0),
				new Filme("Filme 2",2,4.0),
				new Filme("Filme 3",2,4.0),
				new Filme("Filme 4",2,4.0),
				new Filme("Filme 5",2,4.0));
		
		//A��o 
		
		Locacao resultado  = service.alugarFilme(usuario, listaFilmes);
		
		
		//Verifica��o
		
		assertThat(resultado.getValor(),is(14.0));
		
		
	}
	
	@Test
	public void devePagar0PctNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
		
		//Cen�rio
		
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> listaFilmes = Arrays.asList(
				new Filme("Filme 1",2,4.0),
				new Filme("Filme 2",2,4.0),
				new Filme("Filme 3",2,4.0),
				new Filme("Filme 4",2,4.0),
				new Filme("Filme 5",2,4.0),
				new Filme("Filme 6",2,4.0));
		
		//A��o 
		
		Locacao resultado  = service.alugarFilme(usuario, listaFilmes);
		
		
		//Verifica��o
		
		assertThat(resultado.getValor(),is(14.0));
		
		
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> listaFilmes = Arrays.asList(new Filme("Filme 1",1,5.0));
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, listaFilmes);
		
		//Verificacao
		boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		Assert.assertTrue(ehSegunda);
	}
	
	/*
	//Forma mais robusta
	@Test
	public void testLocacao_filmeSemEstoque2() {
		//Cenario 
				LocacaoService service = new LocacaoService();
				Usuario usuario = new Usuario("Usuario 1");
				Filme filme = new Filme("Filme 1",0,5.0);
				
				//A��o 
				try {
					service.alugarFilme(usuario, filme);
					Assert.fail("Deveria ter lan�ado uma exce��o");
				} catch (Exception e) {
					Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
				}
	}
	
	//Forma Nova
	
	@Test
	public void testLocacao_filmeSemEstoque3() throws Exception {
		//Cenario 
				LocacaoService service = new LocacaoService();
				Usuario usuario = new Usuario("Usuario 1");
				Filme filme = new Filme("Filme 1",0,5.0);
				
				exception.expect(Exception.class);
				exception.expectMessage("Filme sem estoque");
				
				//A��o 
				service.alugarFilme(usuario, filme);
				
				
				
	}*/

}
