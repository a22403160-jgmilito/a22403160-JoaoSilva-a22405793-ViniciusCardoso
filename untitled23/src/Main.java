class Calculadora {
    public static float aDicionar(int a, int b) {
        return a + b;
    }
    public static int dividir(int a, int b) {
        if ( b == 0 ) {
            return a;
        }
        else{
            return a / b;
        }
    }
}

class Main {
    public static void main(String[] args) {
        // Aqui estão os meus testes
        System.out.println(Calculadora.aDicionar(10, 0));
        System.out.println(Calculadora.aDicionar(10, 1));
        System.out.println(Calculadora.dividir(10, 2));
        System.out.println(Calculadora.dividir(10, 0));
    }
}

