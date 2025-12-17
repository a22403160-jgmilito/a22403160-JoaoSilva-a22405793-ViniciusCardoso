package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AppGUI extends JFrame {

    // Tema (cores) - Verde / Amarelo / Azul (alto contraste)
    private static final Color COR_FUNDO    = new Color(245, 247, 250);   // quase branco
    private static final Color COR_TOPO     = new Color(22, 163, 74);     // verde forte
    private static final Color COR_ESQUERDA = new Color(254, 249, 195);   // amarelo claro
    private static final Color COR_TEXTO    = new Color(17, 24, 39);      // quase preto

    // Botões (alto contraste)
    private static final Color COR_BOTAO_OK   = new Color(34, 197, 94);   // verde
    private static final Color COR_BOTAO_INFO = new Color(59, 130, 246);  // azul
    private static final Color COR_BOTAO_WARN = new Color(245, 158, 11);  // amarelo/laranja
    private static final Color COR_BOTAO_DANG = new Color(239, 68, 68);   // vermelho

    private final Despensa despensa;
    private final GestorReceitas gestor;
    private final SugeridorLLM sugeridor;
    private final GeradorReceitasLLM gerador;

    private JTextField tfNome, tfQtd, tfUnidade, tfPref;
    private DefaultListModel<String> modelDespensa;
    private JList<String> listDespensa;

    private JTextArea taSaida;

    // topo
    private JLabel lbStatus;
    private JButton btGerarLLM;
    private JButton btSugerir;

    // ✅ Construtor com engine (Opção 2)
    public AppGUI(LLMInteractionEngine engine) {
        super("Chef da Despensa — Sugestor de Receitas");

        despensa = new Despensa();
        gestor = new GestorReceitas();

        sugeridor = new SugeridorLLM(engine);
        gerador = new GeradorReceitasLLM(engine);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(null);

        setContentPane(criarLayout());

        // Gera receitas ao arrancar (sem bloquear)
        gerarReceitasLLMAsync();
    }

    private JPanel criarLayout() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(COR_FUNDO);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        root.add(criarTopo(), BorderLayout.NORTH);
        root.add(criarEsquerda(), BorderLayout.WEST);
        root.add(criarCentro(), BorderLayout.CENTER);

        return root;
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(COR_TOPO);
        topo.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel titulo = new JLabel("Chef da Despensa");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel subtitulo = new JLabel("Adicionar ingredientes, ver a despensa e pedir uma receita com base nas preferências");
        subtitulo.setForeground(new Color(255, 255, 255, 220));
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        lbStatus = new JLabel("Pronto.");
        lbStatus.setForeground(new Color(255, 255, 255, 220));
        lbStatus.setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);
        textos.add(Box.createVerticalStrut(6));
        textos.add(lbStatus);

        topo.add(textos, BorderLayout.WEST);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);

        btGerarLLM = criarBotao("Gerar receitas (LLM)", COR_BOTAO_INFO);
        btGerarLLM.addActionListener(e -> gerarReceitasLLMAsync());

        JButton btLimpar = criarBotao("Limpar saída", COR_BOTAO_DANG);
        btLimpar.addActionListener(e -> taSaida.setText(""));

        botoes.add(btGerarLLM);
        botoes.add(btLimpar);

        topo.add(botoes, BorderLayout.EAST);

        return topo;
    }

    private JPanel criarEsquerda() {
        JPanel left = new JPanel();
        left.setBackground(COR_ESQUERDA);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(12, 12, 12, 12));
        left.setPreferredSize(new Dimension(330, 0));

        left.add(tituloSecao("Adicionar ingrediente"));
        left.add(Box.createVerticalStrut(8));

        tfNome = criarCampo("ex: ovo");
        tfQtd = criarCampo("ex: 2");
        tfUnidade = criarCampo("ex: unidade");

        left.add(label("Nome"));
        left.add(tfNome);
        left.add(Box.createVerticalStrut(8));

        left.add(label("Quantidade (número)"));
        left.add(tfQtd);
        left.add(Box.createVerticalStrut(8));

        left.add(label("Unidade (g, ml, etc.)"));
        left.add(tfUnidade);
        left.add(Box.createVerticalStrut(10));

        JPanel linhaBotoes = new JPanel(new GridLayout(1, 2, 10, 10));
        linhaBotoes.setOpaque(false);

        JButton btAdicionar = criarBotao("Adicionar", COR_BOTAO_OK);
        btAdicionar.addActionListener(e -> adicionarIngrediente());
        linhaBotoes.add(btAdicionar);

        JButton btListar = criarBotao("Actualizar lista", COR_BOTAO_INFO);
        btListar.addActionListener(e -> actualizarListaDespensa());
        linhaBotoes.add(btListar);

        left.add(linhaBotoes);

        left.add(Box.createVerticalStrut(14));
        left.add(tituloSecao("Preferências"));
        left.add(Box.createVerticalStrut(8));

        tfPref = criarCampo("ex: rápido, vegetariano...");
        left.add(label("Texto livre (opcional)"));
        left.add(tfPref);

        left.add(Box.createVerticalStrut(10));
        btSugerir = criarBotao("Sugerir receita", COR_BOTAO_INFO);
        btSugerir.addActionListener(e -> sugerirReceitaAsync());
        left.add(btSugerir);

        left.add(Box.createVerticalStrut(14));
        left.add(tituloSecao("Despensa"));
        left.add(Box.createVerticalStrut(8));

        modelDespensa = new DefaultListModel<>();
        listDespensa = new JList<>(modelDespensa);
        listDespensa.setFont(new Font("SansSerif", Font.PLAIN, 13));
        listDespensa.setBackground(Color.WHITE);
        listDespensa.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JScrollPane sp = new JScrollPane(listDespensa);
        sp.setPreferredSize(new Dimension(0, 170));
        left.add(sp);

        left.add(Box.createVerticalStrut(10));
        JButton btRemover = criarBotao("Remover seleccionado", COR_BOTAO_DANG);
        btRemover.addActionListener(e -> removerIngredienteSelecionado());
        left.add(btRemover);

        return left;
    }

    private JPanel criarCentro() {
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(COR_FUNDO);

        centro.add(tituloSecaoCentro("Saída (receitas e mensagens)"), BorderLayout.NORTH);

        taSaida = new JTextArea();
        taSaida.setEditable(false);
        taSaida.setLineWrap(true);
        taSaida.setWrapStyleWord(true);
        taSaida.setFont(new Font("SansSerif", Font.PLAIN, 14));
        taSaida.setBackground(Color.WHITE);
        taSaida.setForeground(COR_TEXTO);
        taSaida.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane sp = new JScrollPane(taSaida);
        sp.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        centro.add(sp, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new GridLayout(1, 3, 10, 10));
        rodape.setOpaque(false);

        JButton btExemplo = criarBotao("Adicionar exemplo (ovo/sal)", COR_BOTAO_INFO);
        btExemplo.addActionListener(e -> {
            despensa.adicionarIngrediente(new Ingrediente("ovo", 2, "unidade"));
            despensa.adicionarIngrediente(new Ingrediente("sal", 1, "pitada"));
            escrever("Exemplo adicionado: ovo (2 unidade), sal (1 pitada)\n");
            actualizarListaDespensa();
        });

        JButton btListarCentro = criarBotao("Listar despensa na saída", COR_BOTAO_OK);
        btListarCentro.addActionListener(e -> listarDespensaNaSaida());

        JButton btAjuda = criarBotao("Ajuda rápida", COR_BOTAO_WARN);
        btAjuda.addActionListener(e -> escrever(
                "Como usar:\n" +
                        "1) Gerar receitas (LLM)\n" +
                        "2) Adicionar ingredientes (nome, quantidade, unidade)\n" +
                        "3) Escrever preferências (opcional)\n" +
                        "4) Carregar em 'Sugerir receita'\n\n"
        ));

        rodape.add(btExemplo);
        rodape.add(btListarCentro);
        rodape.add(btAjuda);

        centro.add(rodape, BorderLayout.SOUTH);

        return centro;
    }

    // -------- Ações --------

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
            qtd = Double.parseDouble(qtdStr.replace(",", "."));
        } catch (NumberFormatException ex) {
            escrever("Quantidade inválida. Usar um número (ex: 2 ou 2.5).\n");
            return;
        }

        despensa.adicionarIngrediente(new Ingrediente(nome, qtd, unidade));
        escrever("Ingrediente adicionado: " + nome + " (" + qtd + " " + unidade + ")\n");

        tfNome.setText("");
        tfQtd.setText("");
        tfUnidade.setText("");

        actualizarListaDespensa();
    }

    private void actualizarListaDespensa() {
        modelDespensa.clear();
        List<Ingrediente> lista = despensa.listarIngredientes();
        for (Ingrediente i : lista) modelDespensa.addElement(i.toString());
    }

    private void listarDespensaNaSaida() {
        List<Ingrediente> lista = despensa.listarIngredientes();
        if (lista.isEmpty()) {
            escrever("A despensa está vazia.\n\n");
            return;
        }

        escrever("Ingredientes na despensa:\n");
        for (Ingrediente i : lista) escrever(" - " + i + "\n");
        escrever("\n");
    }

    private void sugerirReceitaAsync() {
        String pref = tfPref.getText().trim();

        List<Receita> possiveis = gestor.sugerirReceitas(despensa);
        if (possiveis.isEmpty()) {
            escrever("Não há receitas possíveis com os ingredientes da despensa.\n\n");
            return;
        }

        setBusy(true, "A sugerir receita com o LLM...");

        new SwingWorker<Receita, Void>() {
            @Override
            protected Receita doInBackground() throws Exception {
                Receita escolhida = sugeridor.sugerirReceita(despensa, pref, possiveis);
                if (escolhida == null) escolhida = possiveis.get(0);
                sugeridor.gerarPassos(escolhida);
                return escolhida;
            }

            @Override
            protected void done() {
                try {
                    Receita r = get();
                    escrever("Receita sugerida:\n");
                    escrever(r.toString());
                    escrever("\n\n");
                } catch (Exception e) {
                    escrever("Falha ao sugerir receita (LLM): " + e.getMessage() + "\n\n");
                } finally {
                    setBusy(false, "Pronto.");
                }
            }
        }.execute();
    }

    private void gerarReceitasLLMAsync() {
        setBusy(true, "A gerar receitas com o LLM...");

        new SwingWorker<List<Receita>, Void>() {
            @Override
            protected List<Receita> doInBackground() throws Exception {
                return gerador.gerarReceitas(8, "rápidas, baratas, ingredientes comuns (Portugal)");
            }

            @Override
            protected void done() {
                try {
                    List<Receita> geradas = get();
                    gestor.listarReceitas().clear();
                    for (Receita r : geradas) gestor.adicionarReceita(r);

                    escrever("Receitas geradas pelo LLM: " + geradas.size() + "\n\n");
                    if (geradas.isEmpty()) {
                        escrever("Não foi possível gerar receitas agora. Tenta novamente.\n\n");
                    }
                } catch (Exception e) {
                    escrever("Falha ao gerar receitas (LLM): " + e.getMessage() + "\n\n");
                } finally {
                    setBusy(false, "Pronto.");
                }
            }
        }.execute();
    }

    private void removerIngredienteSelecionado() {
        int idx = listDespensa.getSelectedIndex();
        if (idx < 0) {
            escrever("Seleccionar um item na lista para remover.\n");
            return;
        }

        String item = modelDespensa.getElementAt(idx);
        String nome = item.split("\\s+-\\s+")[0].trim();

        boolean ok = despensa.removerIngrediente(nome);
        if (ok) {
            escrever("Ingrediente removido: " + nome + "\n");
        } else {
            escrever("Não foi possível remover: " + nome + "\n");
        }

        actualizarListaDespensa();
    }

    // -------- Helpers UI --------

    private void setBusy(boolean busy, String status) {
        lbStatus.setText(status);
        if (btGerarLLM != null) btGerarLLM.setEnabled(!busy);
        if (btSugerir != null) btSugerir.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private JLabel tituloSecao(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("SansSerif", Font.BOLD, 15));
        l.setForeground(COR_TEXTO);
        return l;
    }

    private JLabel tituloSecaoCentro(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("SansSerif", Font.BOLD, 15));
        l.setForeground(COR_TEXTO);
        l.setBorder(new EmptyBorder(0, 2, 0, 0));
        return l;
    }

    private JLabel label(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(COR_TEXTO);
        return l;
    }

    private JTextField criarCampo(String placeholder) {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setToolTipText(placeholder);
        return tf;
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(cor);

        // escolher cor do texto com base no brilho do fundo
        b.setForeground(isCorClara(cor) ? Color.BLACK : Color.WHITE);

        b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        return b;
    }

    private boolean isCorClara(Color c) {
        double luminancia = (0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue());
        return luminancia > 150;
    }

    private void escrever(String s) {
        taSaida.append(s);
        taSaida.setCaretPosition(taSaida.getDocument().getLength());
    }
}
