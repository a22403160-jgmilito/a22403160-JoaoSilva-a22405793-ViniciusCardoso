package src;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GestorReceitasTest {

    private GestorReceitas criarGestorComReceitasBase() {
        GestorReceitas gestor = new GestorReceitas();

        Receita r1 = new Receita("Omelete simples", "Omelete básica de ovos");
        r1.adicionarIngrediente(new Ingrediente("ovo", 2, "unidade"));
        r1.adicionarIngrediente(new Ingrediente("sal", 1, "pitada"));
        gestor.adicionarReceita(r1);

        Receita r2 = new Receita("Sandes de queijo", "Sandes rápida");
        r2.adicionarIngrediente(new Ingrediente("pão", 2, "fatia"));
        r2.adicionarIngrediente(new Ingrediente("queijo", 1, "fatia"));
        gestor.adicionarReceita(r2);

        Receita r3 = new Receita("Massa com molho de tomate", "Massa simples");
        r3.adicionarIngrediente(new Ingrediente("massa", 100, "g"));
        r3.adicionarIngrediente(new Ingrediente("molho de tomate", 100, "ml"));
        r3.adicionarIngrediente(new Ingrediente("sal", 1, "pitada"));
        gestor.adicionarReceita(r3);

        return gestor;
    }

    @Test
    void sugerirReceitas_despensaVazia_deveDarListaVazia() {
        GestorReceitas gestor = criarGestorComReceitasBase();
        Despensa d = new Despensa();

        List<Receita> possiveis = gestor.sugerirReceitas(d);
        assertNotNull(possiveis);
        assertTrue(possiveis.isEmpty());
    }

    @Test
    void sugerirReceitas_comIngredientesDaOmelete_deveConterOmelete() {
        GestorReceitas gestor = criarGestorComReceitasBase();

        Despensa d = new Despensa();
        d.adicionarIngrediente(new Ingrediente("ovo", 2, "unidade"));
        d.adicionarIngrediente(new Ingrediente("sal", 1, "pitada"));

        List<Receita> possiveis = gestor.sugerirReceitas(d);

        String nomes = possiveis.toString().toLowerCase();
        assertTrue(nomes.contains("omelete"));
    }

    @Test
    void sugerirReceitas_faltaUmIngrediente_naoDeveSugerir() {
        GestorReceitas gestor = criarGestorComReceitasBase();

        Despensa d = new Despensa();
        d.adicionarIngrediente(new Ingrediente("ovo", 2, "unidade"));
        // falta "sal"

        List<Receita> possiveis = gestor.sugerirReceitas(d);
        String nomes = possiveis.toString().toLowerCase();

        assertFalse(nomes.contains("omelete"));
    }

    @Test
    void sugerirReceitas_comIngredientesDeVariasReceitas_deveSugerirMaisQueUma() {
        GestorReceitas gestor = criarGestorComReceitasBase();

        Despensa d = new Despensa();
        d.adicionarIngrediente(new Ingrediente("ovo", 2, "unidade"));
        d.adicionarIngrediente(new Ingrediente("sal", 1, "pitada"));
        d.adicionarIngrediente(new Ingrediente("pão", 2, "fatia"));
        d.adicionarIngrediente(new Ingrediente("queijo", 1, "fatia"));

        List<Receita> possiveis = gestor.sugerirReceitas(d);
        assertTrue(possiveis.size() >= 2);
    }
}
