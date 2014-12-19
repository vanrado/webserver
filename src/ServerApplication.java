package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import src.core.WebServer;

/**
 * Hlavná trieda aplikácie obsahujúca triedu Main
 * @author rado
 */
public class ServerApplication {
    private static final int ADMIN_COMMAND_PORT = 14999;

    public static void main(String[] args) throws IOException {
        int port = 15000;
        String webroot = args[1];
        
        if(Integer.parseInt(args[0]) > 0){
            port = Integer.parseInt(args[0]);
        }
        
        WebServer webserver = new WebServer(port, webroot);
        Thread serverThread = new Thread(webserver);
        serverThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

        waitForAdminCommand();

        webserver.shutDown();
        while(serverThread.isAlive()){
        
        }
        System.out.println("Server ukonceny");
        System.in.read();
    }

    /**
     * Metóda slúži na obsluhu servera z pohľadu správcu.
     * Príkazy na správu servera sa posielajú požiadavkou GET na port číslo 14999
     * @throws IOException 
     */
    private static void waitForAdminCommand() throws IOException {
        ServerSocket commandSocket = null;
        InputStream is;
        OutputStream os;

        try {
            commandSocket = new ServerSocket(ADMIN_COMMAND_PORT);
            Socket adminSocket = null;

            while (true) {
		//Caka na spravcovsky prikaz na porte 14999
                adminSocket = commandSocket.accept();

                try {
                    is = adminSocket.getInputStream();
                    os = adminSocket.getOutputStream();
                    BufferedReader readerInput = new BufferedReader(new InputStreamReader(is));
                    PrintWriter printOutput = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));

                    String command = "";
                    //readLine() blokuje dokym su data dostupne
		    String str = readerInput.readLine();
		    command = parseCommand(str);

                    readerInput.close();
                    printOutput.close();

                    if (command.equals("/shutdown")) {
                        break;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    adminSocket.close();
                }
            }

            if (adminSocket != null) {
                adminSocket.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (commandSocket != null) {
            commandSocket.close();
        }
    }

    /**
     * Metóda slúži na parsovanie správcovského príkazu
     * z prvého riadku HTTP hlavičky
     * @param firstLine prvý riadok z hlavičky HTTP protokolu
     * @return vráti URI z HTTP požiadavky
     */
    private static String parseCommand(String firstLine) {
        //Nacita do pola typ metody[0] - URI[1] - protokol[2]
        String[] httpHeaderParts = firstLine.split(" ");
        String uri = httpHeaderParts[1];

        return uri;
    }
}