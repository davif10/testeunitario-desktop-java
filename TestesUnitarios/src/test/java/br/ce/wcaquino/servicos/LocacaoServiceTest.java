package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
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
import org.mockito.Spy;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.runners.ParallelRunner;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(ParallelRunner.class)
public class LocacaoServiceTest {
	
	@InjectMocks @Spy
	private LocacaoService service;
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService email;
	
	//Definicao do contador
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		System.out.println("Iniciando 2...");
	}
	
	
	@After
	public void tearDown() {
		System.out.println("Finalizando 2 ...");
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		//Cenario 
		//Usuario usuario = new Usuario("Usuario 1");
		Usuario usuario = umUsuario().agora();
		List<Filme> listaFilmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		Mockito.doReturn(DataUtils.obterData(28, 4, 2017)).when(service).obterData();
		
		//Ação 
		Locacao locacao = service.alugarFilme(usuario, listaFilmes);
			
			//Verificação
			error.checkThat(locacao.getValor(), is(equalTo(5.0)));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
			
			/*
			error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(),new Date()), is(true));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),CoreMatchers.is(true));
			Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
			Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.not(6.0)));
			assertThat(locacao.getValor(), is(5.0));
			Assert.assertEquals(5.0,locacao.getValor(),0.01);
			Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
			Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
			Assert.assertThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),CoreMatchers.is(true));
			*/
		
		
	}
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
				//Cenario 
				Usuario usuario = umUsuario().agora();
				List<Filme> listaFilmes = Arrays.asList(umFilmeSemEstoque().agora());
				
				//Ação 
				service.alugarFilme(usuario, listaFilmes);
	}
	
	//Forma mais robusta
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		//Cenario
		List<Filme> listaFilmes = Arrays.asList(umFilme().agora());
		
		//Acao
			try {
				service.alugarFilme(null, listaFilmes);
				Assert.fail();
			}  catch (LocadoraException e) {
				Assert.assertThat(e.getMessage(),is("Usuário vazio"));
			}
			
		
	}
	//Forma nova
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		//Cenario 
		Usuario usuario = umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		//Acao
		service.alugarFilme(usuario, null);
		
	}
	
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
	
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> listaFilmes = Arrays.asList(umFilme().agora());

		Mockito.doReturn(DataUtils.obterData(29, 4, 2017)).when(service).obterData();	
		//acao
		Locacao retorno = service.alugarFilme(usuario, listaFilmes);
		
		//Verificacao
		Assert.assertThat(retorno.getDataRetorno(), caiNumaSegunda());
	
		
	}
	
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		
		//Acao
		try {
			service.alugarFilme(usuario, filmes);
			//Verificacao
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuário negativado"));
			
		}
		
		
		Mockito.verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//Cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
		Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
	
		List<Locacao> locacoes = Arrays.asList(
				umLocacao().atrasado().comUsuario(usuario).agora(),
				umLocacao().comUsuario(usuario2).agora(),
				umLocacao().atrasado().comUsuario(usuario3).agora(),
				umLocacao().atrasado().comUsuario(usuario3).agora());
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//Acao
		service.notificarAtrasos();
		
		//Verificacao
		verify(email, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
		verify(email).notificarAtraso(usuario);
		verify(email, Mockito.atLeastOnce()).notificarAtraso(usuario3);
		verify(email,never()).notificarAtraso(usuario2);
		verifyNoMoreInteractions(email);
	
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		//acao
		service.alugarFilme(usuario, filmes);
		
		
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = umLocacao().agora();
		//acao
		service.prorrogarLocacao(locacao, 3);
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
	}

	
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//Cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		//Acao
		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);
		
		//Verificacao
		Assert.assertThat(valor, is(4.0));
	}
	
	/*
	//Forma mais robusta
	@Test
	public void testLocacao_filmeSemEstoque2() {
		//Cenario 
				LocacaoService service = new LocacaoService();
				Usuario usuario = new Usuario("Usuario 1");
				Filme filme = new Filme("Filme 1",0,5.0);
				
				//Ação 
				try {
					service.alugarFilme(usuario, filme);
					Assert.fail("Deveria ter lançado uma exceção");
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
				
				//Ação 
				service.alugarFilme(usuario, filme);
				
				
				
	}*/

}
