package src;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReceitaTest {

    @Test
    void adicionarIngrediente_aReceita_naoDeveFalhar() {
        Receita r = new Receita("Omelete", "Simples");
        r.adicionarIngrediente(new Ingrediente("ovo", 2, "unidade"));
        r.adicionarIngrediente(new Ingrediente("sal", 1, "pitada"));

        String s = r.toString().toLowerCase();
        assertTrue(s.contains("omelete"));
        assertTrue(s.contains("ovo"));
        assertTrue(s.contains("sal"));
    }

    @Test
    void receita_toString_naoDeveSerVazio() {
        Receita r = new Receita("Sandes", "Rápida");
        assertNotNull(r.toString());
        assertFalse(r.toString().trim().isEmpty());
    }
}
