package src;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class LLMSugeridorReceitas {

    private final LLMInteractionEngine engine;

    public LLMSugeridorReceitas(LLMInteractionEngine engine) {
        this.engine = engine;
    }

    public Receita sugerirReceita(
            Despensa despensa,
            String preferencias,
            List<Receita> receitasDisponiveis
    ) throws IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {

        if (receitasDisponiveis == null || receitasDisponiveis.isEmpty()) {
            return null;
        }

        String prompt = buildPrompt(despensa, preferencias, receitasDisponiveis);

        String jsonResponse = engine.sendPrompt(prompt);
        String resposta = JSONUtils.getJsonString(jsonResponse, "text");

        if (resposta == null) {
            return receitasDisponiveis.get(0); // fallback
        }

        String nomeEscolhido = resposta.trim();

        for (Receita r : receitasDisponiveis) {
            if (r.getNome().equalsIgnoreCase(nomeEscolhido)) {
                return r;
            }
        }

        return receitasDisponiveis.get(0);
    }

    public void gerarPassos(Receita r)
            throws IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {

        if (r == null || !r.getPassos().isEmpty()) return;

        String prompt = buildPromptPassos(r);

        String jsonResponse = engine.sendPrompt(prompt);
        String resposta = JSONUtils.getJsonString(jsonResponse, "text");

        if (resposta == null) return;

        for (String linha : resposta.split("\\r?\\n")) {
            String passo = linha.trim()
                    .replaceFirst("^\\d+[.)]\\s*", "");
            if (!passo.isBlank()) {
                r.adicionarPasso(passo);
            }
        }
    }

    // ---------------- PROMPTS ----------------

    private String buildPrompt(
            Despensa d,
            String preferencias,
            List<Receita> receitas
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("És um assistente de cozinha.\n");
        sb.append("Escolhe APENAS uma receita da lista.\n");
        sb.append("Responde apenas com o nome exato da receita.\n\n");

        sb.append("Preferências: ").append(preferencias).append("\n\n");

        sb.append("Ingredientes disponíveis:\n");
        for (Ingrediente i : d.listarIngredientes()) {
            sb.append("- ").append(i.getNome()).append("\n");
        }

        sb.append("\nReceitas possíveis:\n");
        for (Receita r : receitas) {
            sb.append("- ").append(r.getNome())
                    .append(": ").append(r.getDescricao()).append("\n");
        }

        return sb.toString();
    }

    private String buildPromptPassos(Receita r) {
        StringBuilder sb = new StringBuilder();

        sb.append("Gera passos simples para a receita abaixo.\n");
        sb.append("Um passo por linha, sem texto extra.\n\n");

        sb.append("Receita: ").append(r.getNome()).append("\n");
        sb.append("Ingredientes:\n");
        for (Ingrediente i : r.getIngredientes()) {
            sb.append("- ").append(i.getNome()).append("\n");
        }

        return sb.toString();
    }
}
