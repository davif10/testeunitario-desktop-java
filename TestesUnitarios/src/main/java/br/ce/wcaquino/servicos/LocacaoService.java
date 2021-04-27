package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme>listaFilmes) throws FilmeSemEstoqueException, LocadoraException{
		
		
		if(usuario == null) {
			throw new LocadoraException("Usuário vazio");
		
		}
		
		if(listaFilmes == null || listaFilmes.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}
		
		
		for(Filme filme: listaFilmes) {
			if(filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}
		}
		
		boolean negativado;
		
		try {
			 negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas com SPC, tente novamente");
		}
		
		if(negativado) {
			throw new LocadoraException("Usuário negativado");
		}
		
			Locacao locacao= new Locacao();
			locacao.setListaFilmes(listaFilmes);
			locacao.setUsuario(usuario);
			locacao.setDataLocacao(Calendar.getInstance().getTime());
			locacao.setValor(calcularValorLocacao(listaFilmes));
	
		//Entrega no dia seguinte
		Date dataEntrega = Calendar.getInstance().getTime();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}

	private Double calcularValorLocacao(List<Filme> listaFilmes) {
		Double valorTotal =  0d;
		for(int i = 0; i < listaFilmes.size(); i++) {
			Filme filme = listaFilmes.get(i);
			Double valorFilme = filme.getPrecoLocacao();
			
			switch(i) {
			case 2 :valorFilme = valorFilme * 0.75;
			break;
			case 3 :valorFilme = valorFilme * 0.50;
			break;
			case 4 :valorFilme = valorFilme * 0.25;
			break;
			case 5 :valorFilme = 0d;
			break;
			
			}
			
			valorTotal += valorFilme;
		}
		return valorTotal;
	}
	
	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for(Locacao locacao:locacoes) {
			if(locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
			
		}
	}
	
	
	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setListaFilmes(locacao.getListaFilmes());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);
		dao.salvar(novaLocacao);
	}
	
	
}