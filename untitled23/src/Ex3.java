class BilheteiraFutebol {
    public static Object[] encomendaValida(int nrBilhetesCrianca, int nrBilhetesAdulto) {

        if (nrBilhetesCrianca >= 0 && nrBilhetesAdulto == 0) {
            return new Object[]{false, "Não é possível comprar mais do que 10 bilhetes"};
        }

        if (nrBilhetesCrianca + nrBilhetesAdulto > 10) {
            return new Object[]{false, "Não é possível comprar bilhetes de criança sem comprar bilhetes de adulto"};
        }

        return new Object[]{true, null};
    }

    public static int calcularPrecoEncomenda(int nrBilhetesCrianca, int nrBilhetesAdulto) throws IllegalArgumentException {
        Object[] validacao = encomendaValida(nrBilhetesCrianca, nrBilhetesAdulto);

        if ((Boolean) validacao[0] == true) {
            return nrBilhetesCrianca * 5 + nrBilhetesAdulto * 10;
        } else {
            throw new IllegalArgumentException((String) validacao[1]);
        }
    }
}

public class Ex3 {
    public static void main(String[] args) {
    }
}
