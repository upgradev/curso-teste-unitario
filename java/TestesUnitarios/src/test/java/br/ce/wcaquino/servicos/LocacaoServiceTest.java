package br.ce.wcaquino.servicos;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
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

    public LocacaoService service;

    // definição contador
    // private static int contador = 0;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        service = new LocacaoService();
        // // incremento
        // contador++;
        // // impressao contador
        // System.out.println(contador);
    }

    // @After
    // public void tearDown() {
    // System.out.println("after");
    // }

    // @BeforeClass
    // public static void setup2() {
    // System.out.println("Before class");

    // }

    // @AfterClass
    // public static void tearDown2() {
    // System.out.println("after class");
    // }

    @Test
    public void deveAlugarFilmeComSucesso() throws Exception {

        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        // cenario

        Usuario usuario = new Usuario("Usuario 1");
        // Filme filme = new Filme("Filme 1", 2, 5.0);
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));

        // acao
        Locacao locacao = service.alugarFilme(usuario, filmes);
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
    public void naoDeveAlugarFilmeSemEstoque() throws Exception {
        // cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 4.0));

        // acao
        service.alugarFilme(usuario, filmes);
    }

    // Forma robusta
    @Test
    public void naoDeveAlugarFilmesSemUsuario() throws FilmeSemEstoqueException {
        // cenario
        // LocacaoService service = new LocacaoService();
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));

        // acao
        try {
            service.alugarFilme(null, filmes);
            Assert.fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), CoreMatchers.is("Usuário Vazio"));
        }

        // System.out.println("Forma robusta");

    }

    // Forma nova
    @Test
    public void naoDevealugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        // LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme Vazio");

        // acao
        service.alugarFilme(usuario, null);

        // System.out.println("Forma nova");
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

    @Test
    public void devePagar75PorcentoNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 3", 2, 4.0));
        // acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        // verificacao
        // 4+4+3 = 11
        assertThat(resultado.getValor(), CoreMatchers.is(11.0));
    }

    @Test
    public void devePagar50PorcentoNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0));
        // acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        // verificacao
        // 4+4+3+2 = 13
        assertThat(resultado.getValor(), CoreMatchers.is(13.0));
    }

    @Test
    public void devePagar25PorcentoNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), new Filme("Filme 5", 2, 4.0));
        // acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        // verificacao
        // 4+4+3+2+1 = 14
        assertThat(resultado.getValor(), CoreMatchers.is(14.0));
    }

    @Test
    public void devePagar0PorcentoNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), new Filme("Filme 5", 2, 4.0),
                new Filme("Filme 6", 2, 4.0));
        // acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        // verificacao
        // 4+4+3+2+1 = 14
        assertThat(resultado.getValor(), CoreMatchers.is(14.0));
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        // cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));

        // acao
        Locacao retorno = service.alugarFilme(usuario, filmes);

        //verificacao
        boolean ehSegunda =  DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
        Assert.assertTrue(ehSegunda);
    }

}