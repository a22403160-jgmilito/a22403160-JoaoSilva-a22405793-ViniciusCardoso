package src;

import java.util.ArrayList;
import java.util.List;

public class Despensa {
    private List<Ingrediente> ingredientes = new ArrayList<>();

    public void adicionarIngrediente(Ingrediente i) {
        ingredientes.add(i);
    }

    public boolean removerIngrediente(String nome) {
        return ingredientes.removeIf(ing -> ing.getNome().equalsIgnoreCase(nome));
    }

    public List<Ingrediente> listarIngredientes() {
        return ingredientes;
    }
}
