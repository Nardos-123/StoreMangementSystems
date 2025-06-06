package store;

import com.store.gui.WelcomeFrame;

public class Main {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new WelcomeFrame().setVisible(true);
        });
    }
}