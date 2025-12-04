package src;

import java.util.ArrayList;
import java.util.List;

public class GestorReceitas {
    private List<Receita> receitas = new ArrayList<>();

    public void adicionarReceita(Receita r) {
        receitas.add(r);
    }

    public boolean removerReceita(String nome) {
        return receitas.removeIf(r -> r.getNome().equalsIgnoreCase(nome));
    }

    public Receita obterReceita(String nome) {
        for (Receita r : receitas) {
            if (r.getNome().equalsIgnoreCase(nome)) {
                return r;
            }
        }
        return null;
    }

    public List<Receita> listarReceitas() {
        return receitas;
    }

    public List<Receita> sugerirReceitas(Despensa d) {
        List<Receita> sugeridas = new ArrayList<>();
        List<Ingrediente> ingredientesDespensa = d.listarIngredientes();

        for (Receita r : receitas) {
            boolean temTodos = true;
            for (Ingrediente ingRec : r.getIngredientes()) {
                boolean encontrado = false;
                for (Ingrediente ingDes : ingredientesDespensa) {
                    if (ingRec.getNome().equalsIgnoreCase(ingDes.getNome())) {
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    temTodos = false;
                    break;
                }
            }
            if (temTodos) {
                sugeridas.add(r);
            }
        }

        return sugeridas;
    }
}

