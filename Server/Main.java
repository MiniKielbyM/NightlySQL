import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;

class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                out.println("Echo: " + inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port or listening for a connection");
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}

public class Main {
    private static int PORT = 0000;
    public static void main(String args[]){

        try {
            // Load configuration properties from .cfg file
            Properties prop = new Properties();
            File configFile = new File("./.cfg");
            if (configFile.exists()) {
                try (FileInputStream input = new FileInputStream(configFile)) {
                    prop.load(input);
                    PORT = Integer.parseInt(prop.getProperty("db.port", "8080")); // Default to 8080 if not set
                    System.out.println("Configuration loaded successfully.");
                } catch (IOException e) {
                    System.err.println("Error loading config file: " + e.getMessage());
                }
            } else {
                System.err.println("Config file not found. Please run Setup.java first.");
                return;
            }
        } catch (Exception e) {
            System.err.println("Error during server setup: " + e.getMessage());
        }
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                // Create a new thread to handle the client
                new ClientHandler(socket).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}