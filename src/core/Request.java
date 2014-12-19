package src.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trieda, ktorá reprezentuje HTTP požiadavku
 * @author Radovan
 */
public class Request {

    private InputStream inputStream;
    private String uri;
    private String method;
    private String protocol;
    private BufferedReader buffReaderFromClientInput;

    /**
     * Konštruktor
     * @param inputStream vstupný prúd dát od klienta.
     */
    public Request(InputStream inputStream) {
        this.inputStream = inputStream;
        this.parse();
    }

    /**
     * Metóda spracuje HTTP požiadavku - prvý riadok a nastavý atribúty method, uri, protokol
     */
    private void parse() {
        buffReaderFromClientInput = new BufferedReader(new InputStreamReader(inputStream));
        
        try {
            if (buffReaderFromClientInput.ready()) {
                String firstLine = buffReaderFromClientInput.readLine();
                
                //Nacita do pola typ metody[0] - URI[1] - protokol[2]
                String[] httpHeaderParts = firstLine.split(" ");
                System.out.println(firstLine);
                method = httpHeaderParts[0];
                uri = httpHeaderParts[1];
                protocol = httpHeaderParts[2];
            }
        } catch (IOException ioEx) {
            System.console().printf("Problem s bufferom od klienta " + ioEx);
        }
    }

    /**
     * 
     * @return vracia typ metódy požiadavky
     */
    public String getMethodType() {
        return method;
    }

    /**
     * 
     * @return vracia typ protokolu požiadavky
     */
    public String getProtocol() {
        return protocol;
    }
    
    /**
     * 
     * @return vracia URI požiadavky
     */
    public String getUri() {
        return uri;
    }

    public void setMethodType(String methodType) {
        this.method = methodType;
    }
}
