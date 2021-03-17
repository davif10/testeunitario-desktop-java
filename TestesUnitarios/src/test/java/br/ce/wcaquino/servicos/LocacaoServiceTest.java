package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiEm;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;

public class LocacaoServiceTest {
	private LocacaoService service;
	private SPCService spc;
	private LocacaoDAO dao;
	private EmailService email;
	
	//Definicao do contador
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		service = new LocacaoService();
		dao = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao);
		spc = Mockito.mock(SPCService.class);
		service.setSPCService(spc);
		email = Mockito.mock(EmailService.class);
		service.setEmailService(email);
	}
	
	@After
	public void tearDown() {
		
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//Verifica que não é sábado, pois o método só funciona de segunda a sexta
		
		
		//Cenario 
		//Usuario usuario = new Usuario("Usuario 1");
		Usuario usuario = umUsuario().agora();
		List<Filme> listaFilmes = Arrays.asList(umFilme().comValor(5.0).agora());
	
		
		//Ação 
		Locacao locacao = service.alugarFilme(usuario, listaFilmes);
			
			//Verificação
			error.checkThat(locacao.getValor(), is(equalTo(5.0)));
			error.checkThat(locacao.getDataLocacao(), ehHoje());
			error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
			
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
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//Método Somente funciona no sábado
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> listaFilmes = Arrays.asList(umFilme().agora());
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, listaFilmes);
		
		//Verificacao
		Assert.assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
		Assert.assertThat(retorno.getDataRetorno(), caiNumaSegunda());
	}
	
	public static void main(String[] args) {
		new BuilderMaster().gerarCodigoClasse(Locacao.class);
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws FilmeSemEstoqueException {
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
			e.printStackTrace();
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
