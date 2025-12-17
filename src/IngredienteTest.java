package src;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IngredienteTest {

    @Test
    void criarIngrediente_guardarDados() {
        Ingrediente i = new Ingrediente("ovo", 2, "unidade");

        // Se tiveres getters, troca por i.getNome(), etc.
        String s = i.toString().toLowerCase();

        assertTrue(s.contains("ovo"));
        assertTrue(s.contains("2"));
        assertTrue(s.contains("unidade"));
    }

    @Test
    void toString_naoDeveSerVazio() {
        Ingrediente i = new Ingrediente("sal", 1, "pitada");
        assertNotNull(i.toString());
        assertFalse(i.toString().trim().isEmpty());
    }
}
