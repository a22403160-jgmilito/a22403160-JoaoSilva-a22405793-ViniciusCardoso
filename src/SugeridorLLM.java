package src;

import java.util.List;

public class SugeridorLLM {
    private String modelo;

    public SugeridorLLM(String modelo) {
        this.modelo = modelo;
    }

    // Aqui poderias passar também o GestorReceitas, mas o enunciado não obriga.
    public Receita sugerirReceita(Despensa d, String preferencias, List<Receita> receitasDisponiveis) {
        // Implementação super simples: devolve a primeira receita sugerida pela despensa
        for (Receita r : receitasDisponiveis) {
            boolean podeFazer = true;
            for (Ingrediente ingRec : r.getIngredientes()) {
                boolean found = false;
                for (Ingrediente ingDesp : d.listarIngredientes()) {
                    if (ingRec.getNome().equalsIgnoreCase(ingDesp.getNome())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    podeFazer = false;
                    break;
                }
            }
            if (podeFazer) {
                return r;
            }
        }
        return null; // se não houver nenhuma
    }

    public void gerarPassos(Receita r) {
        // Se a receita não tiver passos, gera uns genéricos
        if (r.getPassos().isEmpty()) {
            r.adicionarPasso("Preparar todos os ingredientes.");
            r.adicionarPasso("Misturar os ingredientes principais.");
            r.adicionarPasso("Cozinhar até ficar pronto.");
            r.adicionarPasso("Servir.");
        }
    }
}

