package br.ce.wcaquino.servicos;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
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

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testeLocacao() throws Exception {

        // cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1", 2, 5.0);

        // acao
        Locacao locacao = service.alugarFilme(usuario, filme);
        error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),
                CoreMatchers.is(true));

        // try {
        // locacao = service.alugarFilme(usuario, filme);
        // error.checkThat(locacao.getValor(),
        // CoreMatchers.is(CoreMatchers.equalTo(5.0)));
        // error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()),
        // CoreMatchers.is(true));
        // error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(),
        // DataUtils.obterDataComDiferencaDias(1)),
        // CoreMatchers.is(true));
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // fail("Não deveria lancar excecao");
        // }

        // verificacao
        // assertTrue(locacao.getValor() == 5.0);
        // assertEquals(5.0, locacao.getValor(), 0.01);
        // assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
        // assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.not(6.0)));

        // assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
        // assertThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()),
        // CoreMatchers.is(true));

        // assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(),
        // DataUtils.obterDataComDiferencaDias(1)));
        // assertThat(DataUtils.isMesmaData(locacao.getDataRetorno(),
        // DataUtils.obterDataComDiferencaDias(1)),
        // CoreMatchers.is(false));

    }

    // forma elegante
    @Test(expected = FilmeSemEstoqueException.class)
    public void testLocacao_filmeSemERstoque() throws Exception {
        // cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1", 0, 5.0);

        // acao
        service.alugarFilme(usuario, filme);
    }

    // Forma robusta
    @Test
    public void tesLocacao_UsuarioVazio() throws FilmeSemEstoqueException {
        // cenario
        LocacaoService service = new LocacaoService();
        Filme filme = new Filme("Filme 2", 1, 4.0);

        // acao
        try {
            service.alugarFilme(null, filme);
            Assert.fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), CoreMatchers.is("Usuário Vazio"));
        }

        System.out.println("Forma robusta");

    }

    // Forma nova
    @Test
    public void tesLocacao_FilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme Vazio");

        // acao
        service.alugarFilme(usuario, null);

        System.out.println("Forma nova");
    }

    // @Test
    // public void testLocacao_filmeSemERstoque2() {
    // // cenario
    // LocacaoService service = new LocacaoService();
    // Usuario usuario = new Usuario("Usuario 1");
    // Filme filme = new Filme("Filme 1", 0, 5.0);

    // // acao
    // try {
    // service.alugarFilme(usuario, filme);
    // Assert.fail("Deveria ter lançado uma exceção");
    // } catch (Exception e) {
    // assertThat(e.getMessage(),CoreMatchers.is("Filme sem estoque"));
    // }
    // }

    // @Test
    // public void testLocacao_filmeSemERstoque3() throws Exception {
    // // cenario
    // LocacaoService service = new LocacaoService();
    // Usuario usuario = new Usuario("Usuario 1");
    // Filme filme = new Filme("Filme 1", 0, 5.0);

    // exception.expect(Exception.class);
    // exception.expectMessage("Filme sem estoque");

    // // acao
    // service.alugarFilme(usuario, filme);

    // }

}