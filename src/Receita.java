package src;

import java.util.ArrayList;
import java.util.List;

public class Receita {
    private String nome;
    private String descricao;
    private List<String> passos;
    private List<Ingrediente> ingredientes;

    public Receita(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.passos = new ArrayList<>();
        this.ingredientes = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<String> getPassos() {
        return passos;
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void adicionarPasso(String passo) {
        passos.add(passo);
    }

    public void adicionarIngrediente(Ingrediente ingrediente) {
        ingredientes.add(ingrediente);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Receita: ").append(nome).append("\n");
        sb.append("Descrição: ").append(descricao).append("\n");
        sb.append("Ingredientes:\n");
        for (Ingrediente i : ingredientes) {
            sb.append("  - ").append(i).append("\n");
        }
        sb.append("Passos:\n");
        int n = 1;
        for (String p : passos) {
            sb.append("  ").append(n++).append(". ").append(p).append("\n");
        }
        return sb.toString();
    }
}
