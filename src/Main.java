package src;

import javax.swing.*;

public class Main {

    static String apiKey = "sk-h2XXOp1URDKGL722wonNnA"; // evita deixar isto no repositório
    static String url = "https://modelos.ai.ulusofona.pt/v1/completions";
    static String model = "gpt-4-turbo";
    static boolean useHack = true;

    public static void main(String[] args) {

        // Criar o engine UMA vez e reutilizar na app toda
        LLMInteractionEngine engine = new LLMInteractionEngine(url, apiKey, model, useHack);

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            AppGUI gui = new AppGUI(engine); // <-- passa o engine para a GUI
            gui.setVisible(true);
        });
    }
}
