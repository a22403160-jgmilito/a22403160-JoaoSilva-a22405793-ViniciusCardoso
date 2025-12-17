package src;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DespensaTest {

    @Test
    void despensa_inicialmente_vazia() {
        Despensa d = new Despensa();
        List<Ingrediente> lista = d.listarIngredientes();
        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    void adicionarIngrediente_deveAparecerNaLista() {
        Despensa d = new Despensa();
        d.adicionarIngrediente(new Ingrediente("ovo", 2, "unidade"));

        List<Ingrediente> lista = d.listarIngredientes();
        assertEquals(1, lista.size());
        assertTrue(lista.get(0).toString().toLowerCase().contains("ovo"));
    }

    @Test
    void adicionarVariosIngredientes_ordemNaoImporta_masQuantidadeSim() {
        Despensa d = new Despensa();
        d.adicionarIngrediente(new Ingrediente("ovo", 2, "unidade"));
        d.adicionarIngrediente(new Ingrediente("sal", 1, "pitada"));
        d.adicionarIngrediente(new Ingrediente("pão", 2, "fatia"));

        List<Ingrediente> lista = d.listarIngredientes();
        assertEquals(3, lista.size());

        String tudo = lista.toString().toLowerCase();
        assertTrue(tudo.contains("ovo"));
        assertTrue(tudo.contains("sal"));
        assertTrue(tudo.contains("pão") || tudo.contains("pao")); // caso sem acento
    }
}
