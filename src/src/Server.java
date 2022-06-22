import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Server implements Runnable {
    public static int DEFAULT_PORT = 8585;

    private int port;
    private boolean requestToSend = false;

    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;
    }

    public void run() {
        System.out.println("[Server]Starting on port " + port);
        try {
            Socket client = new ServerSocket(port).accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"), true);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("[Server] Received message '" + message + "'");
                if (message.equals("request-to-send")) {
                    System.out.println("[Server] Sending clear to send message");
                    out.println("clear-to-send");
                    requestToSend = true;
                } else {
                    if (!requestToSend) {
                        System.err.println("[Server] Client has not requested to send data");
                        continue;
                    }
                    System.out.println("[Server] Received encoded message " + message);
                    System.out.println("[Server] Decoded message is: '" + decode(message) + "'");
                    out.println("complete");
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("[Server]An unexpected error has occurred");
        }
        System.out.println("[Server] Finished");
    }
    
    // function to decode message
    public static String decode(String message) {
        // replace the message with 0s and 1s
        return message.replace("000-+0+-", "00000000")
                .replace("000+-0-+", "00000000")
                .replaceAll("[^0]", "1");
    }
    
    // start server
    public static void main(String[] args) {
        new Thread(new Server()).start();
    }

}