import javax.swing.*;
import java.io.*;
import java.net.Socket;

class Client implements Runnable {

    private int port;

    public Client() {
        this(Server.DEFAULT_PORT);
    }

    public Client(int port) {
        this.port = port;
    }

    public void run() {
        System.out.println("[Client] Connecting to localhost on port " + port);
        try {
            Socket socket = new Socket("127.0.0.1", port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            // send request
            System.out.println("[Client] Sending rts message");
            out.println("request-to-send");

            // getting string from user
            String message;
            inputStream: while ((message = in.readLine()) != null) {
                switch (message) {
                    case "clear-to-send":
                        String input = JOptionPane.showInputDialog(null, "Input a message to encode (0's and 1's only)",
                                "Message", JOptionPane.INFORMATION_MESSAGE);
                        while (!input.matches("[0-1]+")) {
                            input = JOptionPane.showInputDialog(null,
                                    "Input a message to encode. Must be 0's and 1's only!", "Message",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                        out.println(encode(input));
                        String childComponent = "Original Message: " + input + "\nEncoded message: " + encode(input);
                        JOptionPane.showMessageDialog(null, childComponent);
                        System.out.println("Encoded message: " + encode(input));
                        break;
                    case "complete":
                        System.out.println("[Client] Server received B8ZS stream");
                        break inputStream;
                    default:
                        out.println("[Client] Invalid message: " + message);
                        // break;
                }
                out.println("request-to-send");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[Client] Complete");
    }

    public static String encode(String message) {
        String[] toEncode = message.split("");

        boolean negativePolarity = true;
        String converted = "";

        for (int i = 0; i < toEncode.length; i++) {
            // if the value is 1
            if (toEncode[i].equals("1")) {
                negativePolarity = !negativePolarity;
                converted += negativePolarity ? "-" : "+";
                // if the value is 0
            } else if (toEncode[i].equals("0")) {
                // if a string of 7 following does not exist
                if (i + 7 >= toEncode.length) {
                    converted += "0";
                    // if a string of 7 exists
                } else {
                    if (toEncode[i + 1].equals("0") && toEncode[i + 2].equals("0") && toEncode[i + 3].equals("0") &&
                            toEncode[i + 4].equals("0") && toEncode[i + 5].equals("0") && toEncode[i + 6].equals("0") &&
                            toEncode[i + 7].equals("0")) {
                        i += 7;
                        if (negativePolarity) {
                            converted += "000-+0+-";
                        } else {
                            converted += "000+-0-+";
                        }
                    } else {
                        converted += "0";
                    }
                }
            }
        }
        return converted;
    }

    // start client
    public static void main(String[] args) {
        new Thread(new Client()).start();
    }
}