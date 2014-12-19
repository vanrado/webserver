/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shutdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Trieda slúži na naviazanie spojenia socketom na lokalny port 14999
 * a poslanie príkazu shutdown, čo umožní vypnutie servera
 * @author Radovan
 */
public class Shutdown {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 14999);
        OutputStream os = socket.getOutputStream();
        boolean autoflush = true;
        PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);

        // send an HTTP request to the web server
        out.println("GET /shutdown HTTP/1.1");
	
	out.close();
        socket.close();
    }
}
