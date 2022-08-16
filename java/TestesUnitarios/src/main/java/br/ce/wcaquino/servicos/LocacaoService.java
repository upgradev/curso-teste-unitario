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

	public String vPublica;
	protected String vProtegida;
	// private String vPrivada;
	String vDefault;

	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

		if (usuario == null) {
			throw new LocadoraException("Usuário Vazio");
		}

		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme Vazio");
		}
		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}
		}

		boolean negativado;
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas com SPC, tente novamente");
		}

		if (negativado) {
			throw new LocadoraException("Usuário Negativado");
		}

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		Double valotTotal = 0d;
		for (int i = 0; i < filmes.size(); i++) {
			Filme filme = filmes.get(i);
			Double valorFilme = filme.getPrecoLocacao();
			switch (i) {
				case 2:
					valorFilme = valorFilme * 0.75;
					break;
				case 3:
					valorFilme = valorFilme * 0.5;
					break;
				case 4:
					valorFilme = valorFilme * 0.25;
					break;
				case 5:
					valorFilme = 0d;
					break;

				default:
					break;
			}

			valotTotal += valorFilme;
		}
		locacao.setValor(valotTotal);

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		// TODO adicionar método para salvar

		dao.salvar(locacao);

		return locacao;
	}

	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for (Locacao locacao : locacoes) {
			if (locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}

	public void prorrogarLocacao(Locacao locacao, int dias){

		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);
		dao.salvar(novaLocacao);

	}

	// public void setLocacaoDAO(LocacaoDAO dao) {
	// this.dao = dao;
	// }

	// public void setSPCService(SPCService spc) {
	// spcService = spc;
	// }

	// public void setEmailService(EmailService emailService) {
	// this.emailService = emailService;
	// }

	// @Test
	// public void teste() {

	// // cenario
	// LocacaoService service = new LocacaoService();
	// Usuario usuario = new Usuario("Usuario 1");
	// Filme filme = new Filme("Filme 1", 2, 5.0);

	// // acao
	// Locacao locacao;
	// try {
	// locacao = service.alugarFilme(usuario, filme);
	// // verificacao
	// System.out.println(locacao.getValor() == 5.0);
	// System.out.println(DataUtils.isMesmaData(locacao.getDataLocacao(), new
	// Date()));
	// System.out.println(DataUtils.isMesmaData(locacao.getDataRetorno(),
	// DataUtils.obterDataComDiferencaDias(1)));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }

	// }

	// public static void main(String[] args) {

	// // cenario
	// LocacaoService service = new LocacaoService();
	// Usuario usuario = new Usuario("Usuario 1");
	// Filme filme = new Filme("Filme 1", 2, 5.0);

	// // acao
	// Locacao locacao = service.alugarFilme(usuario, filme);

	// // verificacao
	// System.out.println(locacao.getValor() == 5.0);
	// System.out.println(DataUtils.isMesmaData(locacao.getDataLocacao(), new
	// Date()));
	// System.out.println(DataUtils.isMesmaData(locacao.getDataRetorno(),
	// DataUtils.obterDataComDiferencaDias(1)));

	// }
}