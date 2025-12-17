package src;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class SugeridorLLM {

    private final LLMInteractionEngine engine;

    public SugeridorLLM(LLMInteractionEngine engine) {
        this.engine = engine;
    }

    public Receita sugerirReceita(Despensa d, String preferencias, List<Receita> receitasDisponiveis)
            throws IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {

        if (receitasDisponiveis == null || receitasDisponiveis.isEmpty()) return null;

        String prompt = buildPromptSugestao(d, preferencias, receitasDisponiveis);

        String jsonResponse = engine.sendPrompt(prompt);
        String resposta = JSONUtils.getJsonString(jsonResponse, "text");
        if (resposta == null) return receitasDisponiveis.get(0);

        String nomeEscolhido = limpar(resposta);

        // Garantir que é uma das receitas possíveis
        for (Receita r : receitasDisponiveis) {
            if (r.getNome().equalsIgnoreCase(nomeEscolhido)) {
                return r;
            }
        }

        // fallback
        return receitasDisponiveis.get(0);
    }

    public void gerarPassos(Receita r) throws IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
        if (r == null) return;
        if (!r.getPassos().isEmpty()) return; // já tem passos

        String prompt = buildPromptPassos(r);

        String jsonResponse = engine.sendPrompt(prompt);
        String resposta = JSONUtils.getJsonString(jsonResponse, "text");
        if (resposta == null) {
            // fallback simples
            r.adicionarPasso("Preparar todos os ingredientes.");
            r.adicionarPasso("Executar a receita passo a passo.");
            r.adicionarPasso("Servir.");
            return;
        }

        // Parse simples: uma linha = um passo
        List<String> linhas = splitLines(resposta);
        for (String linha : linhas) {
            String p = limpar(linha);

            // remover numeração tipo "1) " / "1. "
            p = p.replaceFirst("^\\s*\\d+\\s*[\\)\\.]\\s*", "");

            if (!p.isBlank()) r.adicionarPasso(p);
        }

        // se o LLM devolveu tudo numa linha só
        if (r.getPassos().isEmpty()) {
            r.adicionarPasso(limpar(resposta));
        }
    }

    private String buildPromptSugestao(Despensa d, String preferencias, List<Receita> receitasDisponiveis) {
        StringBuilder sb = new StringBuilder();

        sb.append("És um assistente de cozinha. Tens de escolher UMA receita da lista.\n");
        sb.append("Regras:\n");
        sb.append("- Responder APENAS com o nome exato de uma receita da lista (sem aspas, sem pontuação extra).\n");
        sb.append("- Não inventar receitas.\n\n");

        sb.append("Preferências do utilizador: ").append(preferencias == null ? "" : preferencias).append("\n\n");

        sb.append("Ingredientes na despensa:\n");
        for (Ingrediente i : d.listarIngredientes()) {
            sb.append("- ").append(i.getNome()).append(" (").append(i.getQuantidade()).append(" ").append(i.getUnidade()).append(")\n");
        }

        sb.append("\nReceitas disponíveis (escolher uma destas):\n");
        for (Receita r : receitasDisponiveis) {
            sb.append("- ").append(r.getNome()).append(" :: ").append(r.getDescricao()).append("\n");
        }

        return sb.toString();
    }

    private String buildPromptPassos(Receita r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Gera passos simples e claros (4 a 8 passos) para a receita abaixo.\n");
        sb.append("Regras:\n");
        sb.append("- Um passo por linha.\n");
        sb.append("- Não usar texto extra antes/depois.\n\n");

        sb.append("Receita: ").append(r.getNome()).append("\n");
        sb.append("Descrição: ").append(r.getDescricao()).append("\n");
        sb.append("Ingredientes:\n");
        for (Ingrediente i : r.getIngredientes()) {
            sb.append("- ").append(i.getNome()).append(" (").append(i.getQuantidade()).append(" ").append(i.getUnidade()).append(")\n");
        }

        return sb.toString();
    }

    private String limpar(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("[\\r\\n]+", "\n").trim();
    }

    private List<String> splitLines(String s) {
        List<String> out = new ArrayList<>();
        for (String line : s.split("\\r?\\n")) out.add(line);
        return out;
    }
}
