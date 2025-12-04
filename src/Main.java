package src;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Despensa despensa = new Despensa();
        GestorReceitas gestor = new GestorReceitas();
        SugeridorLLM sugeridor = new SugeridorLLM("modelo-falso");

        // Cria algumas receitas de exemplo
        Receita r1 = new Receita("Omelete simples", "Omelete básica de ovos");
        r1.adicionarIngrediente(new Ingrediente("ovo", 2, "unidade"));
        r1.adicionarIngrediente(new Ingrediente("sal", 1, "pitada"));
        gestor.adicionarReceita(r1);

        Receita r2 = new Receita("Sandes de queijo", "Sandes rápida");
        r2.adicionarIngrediente(new Ingrediente("pão", 2, "fatia"));
        r2.adicionarIngrediente(new Ingrediente("queijo", 1, "fatia"));
        gestor.adicionarReceita(r2);

        int opcao;
        do {
            System.out.println("\n--- MENU ---");
            System.out.println("1 - Adicionar ingrediente à despensa");
            System.out.println("2 - Listar ingredientes da despensa");
            System.out.println("3 - Sugerir receita");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");
            opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {
                case 1:
                    System.out.print("Nome do ingrediente: ");
                    String nome = sc.nextLine();
                    System.out.print("Quantidade (número): ");
                    double qtd = Double.parseDouble(sc.nextLine());
                    System.out.print("Unidade (g, ml, etc.): ");
                    String unidade = sc.nextLine();
                    despensa.adicionarIngrediente(new Ingrediente(nome, qtd, unidade));
                    System.out.println("Ingrediente adicionado.");
                    break;

                case 2:
                    System.out.println("Ingredientes na despensa:");
                    for (Ingrediente i : despensa.listarIngredientes()) {
                        System.out.println(" - " + i);
                    }
                    break;

                case 3:
                    System.out.print("Preferências (ex: vegetariano, rápido, etc.): ");
                    String pref = sc.nextLine();

                    // Primeiro pede ao GestorReceitas as receitas compatíveis
                    List<Receita> possiveis = gestor.sugerirReceitas(despensa);

                    if (possiveis.isEmpty()) {
                        System.out.println("Não há receitas possíveis com os ingredientes da despensa.");
                    } else {
                        Receita escolhida = sugeridor.sugerirReceita(despensa, pref, possiveis);
                        if (escolhida == null) {
                            // fallback: escolhe a primeira
                            escolhida = possiveis.get(0);
                        }

                        // Gera passos se for preciso
                        sugeridor.gerarPassos(escolhida);

                        System.out.println("\nReceita sugerida:\n");
                        System.out.println(escolhida);
                    }
                    break;

                case 0:
                    System.out.println("A sair...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);

        sc.close();
    }
}

