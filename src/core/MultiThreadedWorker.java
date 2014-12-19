package src.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pracovné vlákno, ktoré zabezpečuje obsluhu klientského socketu.
 * @author rado
 */
public class MultiThreadedWorker implements Runnable {

    private Socket clientSocket;

    public MultiThreadedWorker(Socket socket) {
        clientSocket = socket;
    }
    @Override
    public void run() {
        InputStream inputStreamFromClient;
        OutputStream outputStreamToClient;
        Request request;
        Response response;
        
        try {
            //Ku poslaniu bytov na clientsku stranu pouzivame objekt pre vystupny prud - instanciu OutpustStream
            outputStreamToClient = clientSocket.getOutputStream();

            //Prijatie vstupneho prudu od klienta
            inputStreamFromClient = clientSocket.getInputStream();

                    // TODO Logovanie pristupov
            //Vytvori instanciu HTTP poziadavky
            request = new Request(inputStreamFromClient);

            //Vytvori instanciu HTTP odpovede
            response = new Response(outputStreamToClient, request);

            //Odoslanie odpovede klientovy podla poziadavky
            response.send();

            inputStreamFromClient.close();
            outputStreamToClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(MultiThreadedWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
