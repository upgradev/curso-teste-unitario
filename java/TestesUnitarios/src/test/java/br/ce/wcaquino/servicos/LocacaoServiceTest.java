package br.ce.wcaquino.servicos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.databuilder.FilmeBuilder;
import br.ce.wcaquino.databuilder.LocacaoBuilder;
import br.ce.wcaquino.databuilder.UsuarioBuilder;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

    @InjectMocks
    public LocacaoService service;

    @Mock
    private SPCService spc;

    @Mock
    private LocacaoDAO dao;

    @Mock
    private EmailService emailService;

    // definição contador
    // private static int contador = 0;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        // service = new LocacaoService();
        // dao = Mockito.mock(LocacaoDAO.class);
        // service.setLocacaoDAO(dao);
        // spc = Mockito.mock(SPCService.class);
        // service.setSPCService(spc);
        // emailService = Mockito.mock(EmailService.class);
        // service.setEmailService(emailService);

        // LocacaoDAO dao = new LocacaoDAOFake();
        // service.setLocacaoDAO(dao);
        // // incremento
        // contador++;
        // // impressao contador
        // System.out.println(contador);
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        // cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        // List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

        // acao
        Locacao retorno = service.alugarFilme(usuario, filmes);

        // verificacao
        // boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(),
        // Calendar.MONDAY);
        // Assert.assertTrue(ehSegunda);
        // assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
        // assertThat(retorno.getDataRetorno(),
        // MatchersProprios.caiEm(Calendar.SUNDAY));
        assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());

    }

    @Test
    public void deveAlugarFilmeComSucesso2() throws Exception {

        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        // cenario

        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        // List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().comValor(5.0).agora());

        // acao
        Locacao locacao = service.alugarFilme(usuario, filmes);
        error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
        error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
        error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHojeComDiferencaDias(0));

    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
        // cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();

        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

        Mockito.when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
        // Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true);

        // exception.expect(LocadoraException.class);
        // exception.expectMessage("Usuário Negativado");

        // acao
        try {
            service.alugarFilme(usuario, filmes);
            // verificacao
            Assert.fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), CoreMatchers.is("Usuário Negativado"));
        }

        Mockito.verify(spc).possuiNegativacao(usuario);
    }

    @Test
    public void deveEnvialEmailParaLocacoesAtrasadas() {
        // cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
        Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Usuario 3").agora();
        List<Locacao> locacoes = Arrays
                .asList(
                        LocacaoBuilder.umLocacao()
                                .comUsuario(usuario)
                                .atrasado().agora(),
                        LocacaoBuilder.umLocacao()
                                .comUsuario(usuario2)
                                .agora(),
                        LocacaoBuilder.umLocacao()
                                .comUsuario(usuario3)
                                .atrasado().agora(),
                        LocacaoBuilder.umLocacao()
                                .comUsuario(usuario3)
                                .atrasado().agora());
        Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        // acao
        service.notificarAtrasos();

        // verificacao
        verify(emailService, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
        verify(emailService).notificarAtraso(usuario);
        // verify(emailService, times(2)).notificarAtraso(usuario3);
        verify(emailService, Mockito.atLeastOnce()).notificarAtraso(usuario3);
        verify(emailService, Mockito.never()).notificarAtraso(usuario2);
        verifyNoMoreInteractions(emailService);
        // verifyZeroInteractions(spc);
    }

    @Test
    public void deveAlugarFilmeComSucesso() throws Exception {

        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        // cenario

        // Usuario usuario = new Usuario("Usuario 1");
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        // Filme filme = new Filme("Filme 1", 2, 5.0);
        // List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().comValor(5.0).agora());

        // acao
        Locacao locacao = service.alugarFilme(usuario, filmes);
        error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
        // error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()),
        // CoreMatchers.is(true));
        error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
        // error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(),
        // DataUtils.obterDataComDiferencaDias(1)),
        // CoreMatchers.is(true));
        error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHojeComDiferencaDias(0));

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
        // Usuario usuario = new Usuario("Usuario 1");
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        // List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 4.0));
        // List<Filme> filmes =
        // Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora());
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilmeSemEstoque().agora());

        // acao
        service.alugarFilme(usuario, filmes);
    }

    // Forma robusta
    @Test
    public void naoDeveAlugarFilmesSemUsuario() throws FilmeSemEstoqueException {
        // cenario
        // LocacaoService service = new LocacaoService();
        // List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

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
        // Usuario usuario = new Usuario("Usuario 1");
        Usuario usuario = UsuarioBuilder.umUsuario().agora();

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme Vazio");

        // acao
        service.alugarFilme(usuario, null);

        // System.out.println("Forma nova");
    }

    @Test
    public void deveTratarErroNoSPC() throws Exception {
        // cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

        Mockito.when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrofica"));
        exception.expect(LocadoraException.class);
        // exception.expectMessage("Falha catastrofica");
        exception.expectMessage("Problemas com SPC, tente novamente");

        // acao
        service.alugarFilme(usuario, filmes);

        // verificacao

    }

    @Test
    public void deveProrrogarLocacao() {
        // cenario
        Locacao locacao = LocacaoBuilder.umLocacao().agora();

        // acao
        service.prorrogarLocacao(locacao, 3);

        // verificacao
        ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argCapt.capture());
        Locacao locacaoRetornada = argCapt.getValue();

        error.checkThat(locacaoRetornada.getValor(), CoreMatchers.is(12.0));
        error.checkThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(3));

    }

    // / anotacao //

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

    // @Test
    // public void devePagar75PorcentoNoFilme3() throws FilmeSemEstoqueException,
    // LocadoraException {
    // // cenario
    // Usuario usuario = new Usuario("Usuario 1");
    // List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new
    // Filme("Filme 2", 2, 4.0),
    // new Filme("Filme 3", 2, 4.0));
    // // acao
    // Locacao resultado = service.alugarFilme(usuario, filmes);

    // // verificacao
    // // 4+4+3 = 11
    // assertThat(resultado.getValor(), CoreMatchers.is(11.0));
    // }

    // @Test
    // public void devePagar50PorcentoNoFilme4() throws FilmeSemEstoqueException,
    // LocadoraException {
    // // cenario
    // Usuario usuario = new Usuario("Usuario 1");
    // List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new
    // Filme("Filme 2", 2, 4.0),
    // new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0));
    // // acao
    // Locacao resultado = service.alugarFilme(usuario, filmes);

    // // verificacao
    // // 4+4+3+2 = 13
    // assertThat(resultado.getValor(), CoreMatchers.is(13.0));
    // }

    // @Test
    // public void devePagar25PorcentoNoFilme5() throws FilmeSemEstoqueException,
    // LocadoraException {
    // // cenario
    // Usuario usuario = new Usuario("Usuario 1");
    // List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new
    // Filme("Filme 2", 2, 4.0),
    // new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), new Filme("Filme
    // 5", 2, 4.0));
    // // acao
    // Locacao resultado = service.alugarFilme(usuario, filmes);

    // // verificacao
    // // 4+4+3+2+1 = 14
    // assertThat(resultado.getValor(), CoreMatchers.is(14.0));
    // }

    // @Test
    // public void devePagar0PorcentoNoFilme6() throws FilmeSemEstoqueException,
    // LocadoraException {
    // // cenario
    // Usuario usuario = new Usuario("Usuario 1");
    // List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new
    // Filme("Filme 2", 2, 4.0),
    // new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), new Filme("Filme
    // 5", 2, 4.0),
    // new Filme("Filme 6", 2, 4.0));
    // // acao
    // Locacao resultado = service.alugarFilme(usuario, filmes);

    // // verificacao
    // // 4+4+3+2+1 = 14
    // assertThat(resultado.getValor(), CoreMatchers.is(14.0));
    // }

}