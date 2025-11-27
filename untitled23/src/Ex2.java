class Produto {
    String titulo;
    double preco;
    int stock;
    Produto(String titulo, double preco, int stock) {
        this.titulo = titulo;
        this.preco = preco;
        this.stock = stock;
    }
    public double calcularPreco(int quantidade, double taxaDesconto) {
        double subTotal = preco * quantidade;
        double desconto = subTotal * taxaDesconto;
        double imposto = (subTotal - desconto) * 0.23;
        return subTotal - desconto + imposto;
    }
    public String possivelEncomendar(int quant) {
        if(quant < stock) {
            return "Possível encomendar";
        }
        else {
            return "Não é possível encomendar";
        }
    }
    public String devolverLinhaFactura(int quantidade, double taxaDesconto) {
        return titulo + " (" + quantidade + " )" + calcularPreco(quantidade, taxaDesconto) + "€ (IVA: 0.23 )";
    }
}

class Ex2 {
    public static void main(String[] args) {

    }
}
