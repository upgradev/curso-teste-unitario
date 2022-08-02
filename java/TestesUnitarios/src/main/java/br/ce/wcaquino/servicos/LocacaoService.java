package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Date;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

public class LocacaoService {

	public String vPublica;
	protected String vProtegida;
	// private String vPrivada;
	String vDefault;

	public Locacao alugarFilme(Usuario usuario, Filme filme) throws FilmeSemEstoqueException, LocadoraException {

		if (usuario == null) {
			throw new LocadoraException("Usuário Vazio");
		}

		if (filme == null) {
			throw new LocadoraException("Filme Vazio");
		}

		if (filme.getEstoque() == 0) {
			throw new FilmeSemEstoqueException();
		}

		Locacao locacao = new Locacao();
		locacao.setFilme(filme);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(filme.getPrecoLocacao());

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		// TODO adicionar método para salvar

		return locacao;
	}

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