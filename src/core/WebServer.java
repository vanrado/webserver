package src.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trieda predstavuje implementáciu jednoduchého WebServeru, ktorý obsluhuje 
 * klientské požiadavky
 * @author Radovan
 */
public class WebServer implements Runnable {

    public static String WEB_ROOT = System.getProperty("user.dir") + File.separator;
    private static final int STANDARD_SERVER_PORT = 15000;
    private int port = STANDARD_SERVER_PORT;
    private String webroot = "webroot"; 
    
    /**
     * threadPool je zasobník vlakien, zvolil som fixnú veľkosť pre 10 vlákien.
     * Tento threadPool je zásobníkom pre vlákna, ktoré obsluhujú klientskú požiadavku.
     * ThreadPool som zvolil pre to aby som zlepšil obsluhu klientských požiadaviek.
     * V prípade, že by obsluha klientských požiadaviek fungovala tak, že by každej
     * prijatej požiadavke bolo pridelené jedno vlákno a vykonanie vlakna by trvalo 
     * napríklad 1s, tak ak by sme spracúvali 1000 požiadaviek súčastne
     * tak by vykonanie všetkých vlákien trvalo 1000s. Ale ak namiesto toho požiadavky
     * zaradíme do fronty a spracúvame ich dajme tomu súčastne len 10, tak prvých 10
     * požiadaviek obslúžime za 10s, ďalších 10 za 20s a posledných 10 požiadaviek by bolo
     * obslúžených po 1000. sekunde
     * 
     */
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private Thread serverThread;
    private ServerSocket serverSocket;
    private boolean shutdown;

    /**
     * Konštruktor
     * @param port špecifikovanie čísla portu, na ktorom bude server počúvať
     * @param webroot špecifikovanie koreňového adresára
     */
    public WebServer(int port, String webroot) {
        shutdown = false;
        this.port = port;
        
        File f = new File(WEB_ROOT + webroot);
        if (f.exists() && f.isDirectory()) {
           this.webroot = webroot;
        }else{
            System.out.println("Pouzitie standardneho adresara " + this.webroot);
        }
        
        WEB_ROOT += this.webroot;
    }

    /**
     * Metóda zabezpečuje to, že server socket cyklycky čaká na požiadavku
     * od klienta, ak nastane pripojenie, zasobniku vlákien predložíme pracovné vlákno
     * ktoré obslúži klientskú požiadavku - klientský socket.
     * @param paPort špecifikuje port na ktorom bude server socket očakávať požiadavky.
     * @param webroot špecifikuje koreňový adresár serveru.
     * @throws IOException 
     */
    private void await(int paPort, String webroot) throws IOException {
        int port = paPort;

        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("localhost"));
            System.out.println("Server spusteny na adrese " + serverSocket.getInetAddress().getHostName() + ":" + serverSocket.getLocalPort());
            System.out.println("Korenovy adresar: " + webroot);

            Socket clientSocket;
            while (true) {
                clientSocket = serverSocket.accept();

                if (!shutdown) {
                    //new Thread(new MultiThreadedWorker(clientSocket)).start();
                    threadPool.execute(new MultiThreadedWorker(clientSocket));
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            serverSocket.close();
            System.out.println("Ukoncujem server");
        }
    }

    /**
     * Metóda ktorá zariadi vypnutie servera.
     * Spôsob vypnutia servera spočíva v premennej shutdown, ak je nastavená na false
     * tak pri prijatej požiadavke, klient už nieje obslužený a ukončí sa cyklus
     * v ktorom server socket čaká na požiadavky.
     */
    public void shutDown() {
        shutdown = true;
        
        try {
            new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort()).close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        synchronized(this){
            serverThread = Thread.currentThread();
        }
        
        try {
            await(port, WEB_ROOT);
        } catch (IOException ex) {
            Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.threadPool.shutdown();
        while(!threadPool.isTerminated()){
        
        }
        if(threadPool.isTerminated()){
            System.out.println("Vsetko skoncilo");
        }
    }

}
