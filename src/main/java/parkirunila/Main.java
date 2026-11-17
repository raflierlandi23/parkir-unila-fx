package parkirunila;

import javafx.application.Application;

/**
 * Titik masuk program.
 * Class ini sengaja TIDAK mewarisi Application agar program dapat dijalankan
 * dari NetBeans tanpa error "JavaFX runtime components are missing".
 */
public class Main {

    public static void main(String[] args) {
        Application.launch(AplikasiParkir.class, args);
    }
}
