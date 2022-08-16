package br.ce.wcaquino.servicos;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.configuration.MockAnnotationProcessor;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.databuilder.FilmeBuilder;
import br.ce.wcaquino.databuilder.UsuarioBuilder;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    @Parameter
    public List<Filme> filmes;

    @Parameter(value = 1)
    public Double valorLocacao;

    @Parameter(value = 2)
    public String cenario;

    @InjectMocks
    public LocacaoService service;

    @Mock
    private SPCService spc;

    @Mock
    private LocacaoDAO dao;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        // service = new LocacaoService();
        // dao = Mockito.mock(LocacaoDAO.class);
        // // LocacaoDAO dao = new LocacaoDAOFake();
        // service.setLocacaoDAO(dao);
        // spc = Mockito.mock(SPCService.class);
        // service.setSPCService(spc);

    }

    private static Filme filme1 = FilmeBuilder.umFilme().agora();
    private static Filme filme2 = FilmeBuilder.umFilme().agora();
    private static Filme filme3 = FilmeBuilder.umFilme().agora();
    private static Filme filme4 = FilmeBuilder.umFilme().agora();
    private static Filme filme5 = FilmeBuilder.umFilme().agora();
    private static Filme filme6 = FilmeBuilder.umFilme().agora();
    private static Filme filme7 = FilmeBuilder.umFilme().agora();

    // @Parameters(name = "Teste {index} = {0} - {1}")
    @Parameters(name = "{2}")
    public static Collection<Object[]> getParametros() {
        return Arrays.asList(new Object[][] {
                { Arrays.asList(filme1, filme2), 8.0, "2 Filmes: Sem Desconto" },
                { Arrays.asList(filme1, filme2, filme3), 11.0, "3 Filmes: 25%" },
                { Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 Filmes: 50%" },
                { Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 Filmes: 75%" },
                { Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 Filmes: 100%" },
                { Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "7 Filmes: Sem Desconto" },
        });
    }

    @Test
    public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        // Usuario usuario = new Usuario("Usuario 1");
        Usuario usuario = UsuarioBuilder.umUsuario().agora();

        // acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        // verificacao
        assertThat(resultado.getValor(), CoreMatchers.is(valorLocacao));

        // System.out.println("!");
    }

    @Test
    public void print(){
        // System.out.println(valorLocacao);
    }

}