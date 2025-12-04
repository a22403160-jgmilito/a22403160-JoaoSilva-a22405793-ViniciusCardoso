package src;

public class Ingrediente {
    private String nome;
    private double quantidade;
    private String unidade;

    public Ingrediente(String nome, double quantidade, String unidade) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade;
    }

    public String getNome() {
        return nome;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return nome + " - " + quantidade + " " + unidade;
    }
}

