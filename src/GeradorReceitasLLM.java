package src;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class GeradorReceitasLLM {

    private final LLMInteractionEngine engine;

    public GeradorReceitasLLM(LLMInteractionEngine engine) {
        this.engine = engine;
    }

    public List<Receita> gerarReceitas(int quantidade, String estilo)
            throws IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {

        if (quantidade <= 0) quantidade = 5;
        if (estilo == null) estilo = "";

        int tentativasMax = 3;

        for (int tentativa = 1; tentativa <= tentativasMax; tentativa++) {

            String prompt = buildPrompt(quantidade, estilo);
            String resp = engine.sendPrompt(prompt);

            // 1) Se veio HTML (504 etc.)
            if (resp != null && resp.trim().startsWith("<")) {
                System.out.println("LLM devolveu HTML (tentativa " + tentativa + ")");
                if (tentativa < tentativasMax) Thread.sleep(2000);
                continue;
            }

            // 2) Extrair texto
            String texto = JSONUtils.getCompletionText(resp);
            if (texto == null || texto.isBlank()) {
                System.out.println("LLM sem campo text (tentativa " + tentativa + ")");
                System.out.println(resp); // <-- importante para debug
                if (tentativa < tentativasMax) Thread.sleep(2000);
                continue;
            }

            // 3) Normalizar \n literais
            texto = texto.replace("\\n", "\n").replace("\\r", "\r");

            // 4) Parse
            List<Receita> receitas = parseReceitas(texto);

            if (!receitas.isEmpty()) return receitas;

            System.out.println("Parse deu 0 receitas (tentativa " + tentativa + ")");
            System.out.println(texto); // <-- importante para ver formato real
            if (tentativa < tentativasMax) Thread.sleep(2000);
        }

        return new ArrayList<>();
    }



    private String buildPrompt(int quantidade, String estilo) {
        StringBuilder sb = new StringBuilder();

        sb.append("Gera ").append(quantidade).append(" receitas no formato abaixo.\n");
        sb.append("As receitas devem ser simples e realistas.\n");
        sb.append("Estilo/Preferências: ").append(estilo).append("\n\n");

        sb.append("FORMATO OBRIGATÓRIO (sem texto extra fora do formato):\n");
        sb.append("NOME: <nome da receita>\n");
        sb.append("DESC: <descrição curta>\n");
        sb.append("ING: <nome>|<quantidade>|<unidade>\n");
        sb.append("ING: <nome>|<quantidade>|<unidade>\n");
        sb.append("---\n");
        sb.append("Regras:\n");
        sb.append("- Usar '.' como separador decimal, se necessário (ex: 0.5).\n");
        sb.append("- Cada receita deve ter 3 a 7 ingredientes.\n");
        sb.append("- Separar receitas com linha '---'.\n");

        return sb.toString();
    }

    private List<Receita> parseReceitas(String texto) {
        List<Receita> out = new ArrayList<>();

        String[] blocos = texto.split("(?m)^---\\s*$"); // separa por linha "---"
        for (String bloco : blocos) {
            Receita r = parseBloco(bloco.trim());
            if (r != null) out.add(r);
        }

        return out;
    }

    private Receita parseBloco(String bloco) {
        if (bloco.isBlank()) return null;

        String nome = null;
        String desc = null;
        List<Ingrediente> ingredientes = new ArrayList<>();

        String[] linhas = bloco.split("\\r?\\n");
        for (String linha : linhas) {
            linha = linha.trim();
            if (linha.isBlank()) continue;

            if (linha.startsWith("NOME:")) {
                nome = linha.substring("NOME:".length()).trim();
            } else if (linha.startsWith("DESC:")) {
                desc = linha.substring("DESC:".length()).trim();
            } else if (linha.startsWith("ING:")) {
                String payload = linha.substring("ING:".length()).trim();
                Ingrediente ing = parseIngrediente(payload);
                if (ing != null) ingredientes.add(ing);
            }
        }

        if (nome == null || nome.isBlank()) return null;
        if (desc == null) desc = "";

        Receita r = new Receita(nome, desc);
        for (Ingrediente ing : ingredientes) r.adicionarIngrediente(ing);

        // passos podem ser gerados depois pelo teu SugeridorLLM.gerarPassos(r)
        return r;
    }

    private Ingrediente parseIngrediente(String payload) {
        // formato: nome|quantidade|unidade
        String[] parts = payload.split("\\|");
        if (parts.length != 3) return null;

        String nome = parts[0].trim();
        String qtdStr = parts[1].trim();
        String unidade = parts[2].trim();

        if (nome.isBlank() || unidade.isBlank()) return null;

        double qtd;
        try {
            qtd = Double.parseDouble(qtdStr);
        } catch (NumberFormatException e) {
            return null;
        }
        return new Ingrediente(nome, qtd, unidade);
    }
}
