package src;

public class JSONUtils {

    /* -------------------------------------------------
     * Apenas para debug visual do JSON
     * ------------------------------------------------- */
    static String quickJSONFormater(String json) {
        if (json == null) return null;

        StringBuilder out = new StringBuilder();
        boolean inStr = false, esc = false;
        int indent = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (esc) {
                out.append(c);
                esc = false;
                continue;
            }

            if (c == '\\') {
                out.append(c);
                esc = true;
                continue;
            }

            if (c == '"') {
                inStr = !inStr;
                out.append(c);
                continue;
            }

            if (inStr) {
                out.append(c);
                continue;
            }

            switch (c) {
                case '{':
                case '[':
                    out.append(c)
                            .append('\n')
                            .append("  ".repeat(++indent));
                    break;

                case '}':
                case ']':
                    out.append('\n')
                            .append("  ".repeat(--indent))
                            .append(c);
                    break;

                case ',':
                    out.append(c)
                            .append('\n')
                            .append("  ".repeat(indent));
                    break;

                case ':':
                    out.append(": ");
                    break;

                default:
                    if (!Character.isWhitespace(c)) {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }

    /* -------------------------------------------------
     * Extrai um campo string simples: "key": "value"
     * (método ingénuo, mas rápido)
     * ------------------------------------------------- */
    static String getJsonString(String json, String key) {
        if (json == null || key == null) return null;

        String pattern = "\"" + key + "\"";
        int keyPos = json.indexOf(pattern);
        if (keyPos < 0) return null;

        int colonPos = json.indexOf(':', keyPos + pattern.length());
        if (colonPos < 0) return null;

        int firstQuote = json.indexOf('"', colonPos + 1);
        if (firstQuote < 0) return null;

        int secondQuote = json.indexOf('"', firstQuote + 1);
        while (secondQuote > 0 && json.charAt(secondQuote - 1) == '\\') {
            secondQuote = json.indexOf('"', secondQuote + 1);
        }
        if (secondQuote < 0) return null;

        return json.substring(firstQuote + 1, secondQuote);
    }

    /* -------------------------------------------------
     * Extrai o texto de uma resposta /v1/completions
     *
     * Suporta:
     *  - {"text":"..."}
     *  - {"choices":[{"text":"..."}]}
     * ------------------------------------------------- */
    static String getCompletionText(String json) {
        if (json == null) return null;

        // 1) tentativa direta
        String direct = getJsonString(json, "text");
        if (direct != null) return direct;

        // 2) fallback: procurar a primeira ocorrência de "text":"..."
        String key = "\"text\"";
        int pos = json.indexOf(key);
        if (pos < 0) return null;

        int colon = json.indexOf(':', pos + key.length());
        if (colon < 0) return null;

        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) return null;

        int secondQuote = json.indexOf('"', firstQuote + 1);
        while (secondQuote > 0 && json.charAt(secondQuote - 1) == '\\') {
            secondQuote = json.indexOf('"', secondQuote + 1);
        }
        if (secondQuote < 0) return null;

        return json.substring(firstQuote + 1, secondQuote);
    }
}
