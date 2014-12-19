package src.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Trieda predstavuje HTTP odpoveď, ktorú posiela server
 * @author Radovan
 */
public class Response {

    private OutputStream outputStream;
    private Request request;
    private final int BUFFER_SIZE = 2048;

    /**
     * Konštruktor
     * @param outputStreamToClient výstupný prúd dát do ktorého server zapisuje a klient číta
     * @param request požiadavka ktorá bola spracovaná z dát od klienta
     */
    public Response(OutputStream outputStreamToClient, Request request) {
        this.outputStream = outputStreamToClient;
        this.request = request;
    }

    /**
     * Metóda slúži na poslanie odpovede klientovi
     */
    public void send() {
        try {
            //PrintWriter printWriterToClient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)), true);

            FileInputStream fileInputStream = null;
            byte[] bytes = new byte[BUFFER_SIZE];

            try {
                String reqUri = request.getUri();
                String fileUri = WebServer.WEB_ROOT + reqUri;
                String uriBegin = "";

                //Ak pozadovana uri je v tvare localhost/ alebo <adresar>/ tak 
                //vrati stranku index.html v tomto adresar
                if ((reqUri.substring(reqUri.length() - 1)).equals("/")) {
                    fileUri += "index.html";
                } else {
                    uriBegin = fileUri.substring(fileUri.indexOf("webroot/") + "webroot".length(), fileUri.indexOf("webroot/") + "webroot/".length() + 2);
                }

                //Osetrenie vystupenia z korenoveho adresara
                if (!uriBegin.equals("/..")) {
                    File file = new File(fileUri);

                    if (file.exists()) {
                        fileInputStream = new FileInputStream(file);
                        //Pocet bytov suboru
                        int ch = fileInputStream.read(bytes, 0, BUFFER_SIZE);
                        
                        outputStream.write(getHtmlHttp200Header(ch).getBytes());

                        while (ch != -1) {
                            outputStream.write(bytes, 0, ch);
                            ch = fileInputStream.read(bytes, 0, BUFFER_SIZE);
                        }
                    } else {
                        outputStream.write(getHtmlHttp404Header().getBytes());
                    }
                } else {
                    outputStream.write(getHtmlHttp406Header().getBytes());
                }
            } catch (Exception ex) {
                ex.getStackTrace();
            } finally {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }

    /**
     * HTTP hlavička pre status odpovede 200 - OK
     * @param numberOfBytes počet bytov veľkosti obsahu
     * @return 
     */
    private String getHtmlHttp200Header(int numberOfBytes) {
        String message = "HTTP/1.1 200 Ok\r\n"
                + "Content-Type: text/html\r\n"
                + "Content-Length: " + numberOfBytes + "\r\n"
                + "\r\n";

        return message;
    }

    /**
     * HTTP hlavička pre status odpovede 404 - súbor nenájdený
     * @return 
     */
    private String getHtmlHttp404Header() {
        String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
                + "Content-Type: text/html\r\n"
                + "Content-Length: 23\r\n"
                + "\r\n"
                + "<h1>Subor nenajdeny</h1>";

        return errorMessage;
    }

    /**
     * HTTP hlavička pre status odpovede 406 - prístup odmietnutý
     * @return 
     */
    private String getHtmlHttp406Header() {
        String errorMessage = "HTTP/1.1 406 Not Acceptable\r\n"
                + "Content-Type: text/html\r\n"
                + "Content-Length: 23\r\n"
                + "\r\n"
                + "<h1>406 Not Acceptable</h1>";

        return errorMessage;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public Request getRequest() {
        return request;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

}
