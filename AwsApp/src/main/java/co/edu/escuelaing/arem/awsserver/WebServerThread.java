package co.edu.escuelaing.arem.awsserver;

import co.edu.escuelaing.arem.awsserver.webapplication.WebApplication;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Juan David
 */

public class WebServerThread implements Runnable{
    
    private ServerSocket serverSocket;
    private ApplicationBean gc;
 

    WebServerThread(ServerSocket serverSocket,ApplicationBean gc) {
        this.serverSocket = serverSocket;
        this.gc=gc;
    }

    @Override
    public void run() {
        try {
            while (true) {
                init();
            }
        } catch (IOException ex) {
            Logger.getLogger(WebServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void init() throws IOException{
        Socket clientSocket = null;
        try {
            System.out.println("Listo para recibir ...");
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        
        String inputLine, outputLine;
        
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if(inputLine.startsWith("GET")){
                String query = inputLine.split(" ")[1];
                if(query.equals("/") || query.equals("/index.html")){
                    Resource uri = new ClassPathXmlApplicationContext("applicationContext.xml").getResource("/index.html");
                    String output = "";
                    try {
                        InputStream is = uri.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        
                        String line;
                        while ((line = br.readLine()) != null) {
                           output+=line;
                        } 
                        br.close();
                    } catch (IOException ex) {
                        Logger.getLogger(WebServerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n\r\n" + output;
                        out.println(outputLine);
                }
                /*else if(query.equals("/favicon.ico")){
                    File indexFile = new File(WebServerThread.class.getResource("/favico.ico").getFile());
                    String output = null;
                    try {
                        output = FileUtils.readFileToString(indexFile, StandardCharsets.UTF_8);
                    } catch (IOException ex) {
                        Logger.getLogger(WebServerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n\r\n" + output;
                        out.println(outputLine);
                }*/
                else if(query.contains("response")){
                    
                   // ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
                   // ApplicationBean gc = ac.getBean(ApplicationBeanImpl.class);
                    //System.out.println(gc.getMessage());
                   // System.out.println(apiWeb.  );
                  /**  System.out.println("DIOMEDAZOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                    File indexFile = new File(WebServerThread.class.getResource("/response.html").getFile());
                    **/
                    outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n\r\n" + gc.getAPIResponse(query.split("/")[2]);
                        out.println(outputLine);
                    
                   
                }
            }
            
            if (!in.ready()) {
                break;
            }
            
            if (inputLine.equals("")) break;
        }
        
        out.close();
        in.close();
        clientSocket.close();
    }
    
}
