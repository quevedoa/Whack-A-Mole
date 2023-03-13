package Ejecutable;

import Server.Server;
import Views.LoginView;

public class Ejecutable {

    public static void main(String[] args) {
        int rows = 10;
        int columns = 10;
        int moleDelayMillis = 1000;
        int numJugadores = 3;
        int maxScore = 5;

        Thread hiloServidor = new Thread(() -> {
            try {
                new Server(rows,columns, moleDelayMillis, maxScore);
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        hiloServidor.start();

        for (int i = 0; i < numJugadores; i++) {
            new LoginView();
        }
    }
}
