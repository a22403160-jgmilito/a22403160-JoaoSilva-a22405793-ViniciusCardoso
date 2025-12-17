package src;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * TODO - documentar
 */
public class LLMInteractionEngine {

    String url;
    String apiKey;
    String model;
    boolean useHack;

    /**
     * TODO - documentar
     *
     * @param url TODO - documentar
     * @param apiKey TODO - documentar
     * @param model TODO - documentar
     * @param useHack TODO - documentar
     */
    LLMInteractionEngine(String url, String apiKey, String model, boolean useHack) {
        this.url = url;
        this.apiKey = apiKey;
        this.model = model;
        this.useHack = useHack;
    }

    /**
     * TODO - documentar
     *
     * @param url TODO - documentar
     * @param apiKey TODO - documentar
     * @param model TODO - documentar
     */
    LLMInteractionEngine(String url, String apiKey, String model) {
        this.url = url;
        this.apiKey = apiKey;
        this.model = model;
        this.useHack = false;
    }
    String escapeJson(String s) {
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Constrói a representação JSON usada no pedido HTTP enviado ao modelo.
     *
     * @param model modelo a utilizar
     * @param prompt texto enviado para o modelo
     * @return JSON em formato de String
     */
    String buildJSON(String model, String prompt) {
        String p = escapeJson(prompt);
        return "{"
                + "\"model\":\"" + model + "\","
                + "\"prompt\":\"" + p + "\""
                + "}";
    }

    /**
     * Envia um prompt para o modelo através de HTTP. Se {@code useHack} for verdadeiro,
     * utiliza o método alternativo que ignora certificados SSL.
     *
     * @param prompt texto a enviar
     * @return resposta do modelo
     * @throws IOException erro de comunicação
     * @throws InterruptedException pedido interrompido
     * @throws NoSuchAlgorithmException erro ao criar contexto SSL
     * @throws KeyManagementException erro ao inicializar contexto SSL
     */
    String sendPrompt(String prompt) throws IOException, InterruptedException, NoSuchAlgorithmException, KeyManagementException {
        if(useHack) {
            return sendPrompt_Hack(prompt);
        }
        HttpClient client = HttpClient.newHttpClient();
        String json = buildJSON(model, prompt);
        return sendRequestToClientAndGetReply(client, url, apiKey, json);
    }

    /**
     * Envia um prompt utilizando um cliente HTTP configurado para ignorar
     * validação de certificados SSL.
     *
     * @param prompt texto a enviar
     * @return resposta recebida do servidor
     * @throws IOException erro de comunicação
     * @throws InterruptedException pedido interrompido
     * @throws NoSuchAlgorithmException erro ao criar contexto SSL
     * @throws KeyManagementException erro ao inicializar contexto SSL
     */
    String sendPrompt_Hack(String prompt) throws IOException, InterruptedException, NoSuchAlgorithmException, KeyManagementException {

        // *************
        // hack por causa dos certificados
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{ new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] c, String a) {}
            public void checkServerTrusted(X509Certificate[] c, String a) {}
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        }}, new SecureRandom());

        HttpClient insecureClient = HttpClient.newBuilder().sslContext(sc).build();
        // fim do hack
        // *************

        String json = buildJSON(model, prompt);

        return sendRequestToClientAndGetReply(insecureClient, url, apiKey, json);
    }

    /**
     * Envia um pedido HTTP POST para um cliente fornecido e devolve o corpo da resposta.
     *
     * @param client cliente HTTP configurado
     * @param url endereço do serviço
     * @param apiKey chave de autenticação
     * @param json conteúdo JSON do pedido
     * @return corpo da resposta
     * @throws IOException erro de comunicação
     * @throws InterruptedException pedido interrompido
     */
    String sendRequestToClientAndGetReply(HttpClient client, String url, String apiKey, String json) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        System.out.println("HTTP STATUS: " + resp.statusCode());
        return resp.body();
    }
}
