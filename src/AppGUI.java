package src;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AppGUI extends JFrame {

    private final Despensa despensa;
    private final GestorReceitas gestor;
    private final SugeridorLLM sugeridor;

    // Campos (Adicionar ingrediente)
    private JTextField tfNome;
    private JTextField tfQtd;
    private JTextField tfUnidade;

    // Campos (Preferências)
    private JTextField tfPreferencias;

    // Área de saída
    private JTextArea taSaida;

    public AppGUI() {
        super("Gestor de Receitas");

        // Modelo (reaproveita o teu código)
        despensa = new Despensa();
        gestor = new GestorReceitas();
        sugeridor = new SugeridorLLM("modelo-falso");
        criarReceitasDeExemplo();

        // UI
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        add(criarPainelEsquerda(), BorderLayout.WEST);
        add(criarPainelCentro(), BorderLayout.CENTER);
    }

    private JPanel criarPainelEsquerda() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.setPreferredSize(new Dimension(280, 0));

        p.add(new JLabel("Adicionar ingrediente"));
        p.add(Box.createVerticalStrut(8));

        tfNome = new JTextField();
        tfQtd = new JTextField();
        tfUnidade = new JTextField();

        p.add(new JLabel("Nome"));
        p.add(tfNome);
        p.add(Box.createVerticalStrut(6));

        p.add(new JLabel("Quantidade (número)"));
        p.add(tfQtd);
        p.add(Box.createVerticalStrut(6));

        p.add(new JLabel("Unidade (g, ml, etc.)"));
        p.add(tfUnidade);
        p.add(Box.createVerticalStrut(10));

        JButton btAdicionar = new JButton("Adicionar à despensa");
        btAdicionar.addActionListener(e -> adicionarIngrediente());
        p.add(btAdicionar);

        p.add(Box.createVerticalStrut(15));
        JButton btListar = new JButton("Listar despensa");
        btListar.addActionListener(e -> listarDespensa());
        p.add(btListar);

        p.add(Box.createVerticalStrut(15));
        p.add(new JLabel("Preferências (ex: rápido)"));
        tfPreferencias = new JTextField();
        p.add(tfPreferencias);

        p.add(Box.createVerticalStrut(10));
        JButton btSugerir = new JButton("Sugerir receita");
        btSugerir.addActionListener(e -> sugerirReceita());
        p.add(btSugerir);

        return p;
    }

    private JScrollPane criarPainelCentro() {
        taSaida = new JTextArea();
        taSaida.setEditable(false);
        taSaida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return new JScrollPane(taSaida);
    }

    private void adicionarIngrediente() {
        String nome = tfNome.getText().trim();
        String qtdStr = tfQtd.getText().trim();
        String unidade = tfUnidade.getText().trim();

        if (nome.isEmpty() || qtdStr.isEmpty() || unidade.isEmpty()) {
            escrever("Preencher nome, quantidade e unidade.\n");
            return;
        }

        double qtd;
        try {
            qtd = Double.parseDouble(qtdStr);
        } catch (NumberFormatException ex) {
            escrever("Quantidade inválida. Usar um número (ex: 2 ou 2.5).\n");
            return;
        }

        despensa.adicionarIngrediente(new Ingrediente(nome, qtd, unidade));
        escrever("Ingrediente adicionado: " + nome + " (" + qtd + " " + unidade + ")\n");

        tfNome.setText("");
        tfQtd.setText("");
        tfUnidade.setText("");
    }

    private void listarDespensa() {
        List<Ingrediente> lista = despensa.listarIngredientes();
        if (lista.isEmpty()) {
            escrever("A despensa está vazia.\n");
            return;
        }

        escrever("Ingredientes na despensa:\n");
        for (Ingrediente i : lista) {
            escrever(" - " + i + "\n");
        }
        escrever("\n");
    }

    private void sugerirReceita() {
        String pref = tfPreferencias.getText().trim();

        List<Receita> possiveis = gestor.sugerirReceitas(despensa);

        if (possiveis.isEmpty()) {
            escrever("Não há receitas possíveis com os ingredientes da despensa.\n\n");
            return;
        }

        Receita escolhida = sugeridor.sugerirReceita(despensa, pref, possiveis);
        if (escolhida == null) {
            escolhida = possiveis.get(0);
        }

        sugeridor.gerarPassos(escolhida);

        escrever("Receita sugerida:\n");
        escrever(escolhida.toString());
        escrever("\n\n");
    }

    private void escrever(String s) {
        taSaida.append(s);
        taSaida.setCaretPosition(taSaida.getDocument().getLength());
    }

    private void criarReceitasDeExemplo() {
        Receita r1 = new Receita("Omelete simples", "Omelete básica de ovos");
        r1.adicionarIngrediente(new Ingrediente("ovo", 2, "unidade"));
        r1.adicionarIngrediente(new Ingrediente("sal", 1, "pitada"));
        gestor.adicionarReceita(r1);

        Receita r2 = new Receita("Sandes de queijo", "Sandes rápida");
        r2.adicionarIngrediente(new Ingrediente("pão", 2, "fatia"));
        r2.adicionarIngrediente(new Ingrediente("queijo", 1, "fatia"));
        gestor.adicionarReceita(r2);

        Receita r3 = new Receita("Massa com molho de tomate", "Massa simples para o dia a dia");
        r3.adicionarIngrediente(new Ingrediente("massa", 100, "g"));
        r3.adicionarIngrediente(new Ingrediente("molho de tomate", 100, "ml"));
        r3.adicionarIngrediente(new Ingrediente("sal", 1, "pitada"));
        gestor.adicionarReceita(r3);

        Receita r4 = new Receita("Arroz com atum", "Prato rápido de arroz com atum enlatado");
        r4.adicionarIngrediente(new Ingrediente("arroz", 1, "g"));
        r4.adicionarIngrediente(new Ingrediente("atum", 1, "lata"));
        r4.adicionarIngrediente(new Ingrediente("azeite", 1, "colher de sopa"));
        r4.adicionarIngrediente(new Ingrediente("sal", 1, "pitada"));
        gestor.adicionarReceita(r4);

        Receita r5 = new Receita("Iogurte com fruta", "Sobremesa simples e fresca");
        r5.adicionarIngrediente(new Ingrediente("iogurte", 1, "unidade"));
        r5.adicionarIngrediente(new Ingrediente("banana", 1, "unidade"));
        r5.adicionarIngrediente(new Ingrediente("mel", 1, "colher de sopa"));
        gestor.adicionarReceita(r5);
    }
}