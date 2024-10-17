package Server;

import java.io.*;
import java.net.*;

public class ServerProgram {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(6942)) {

            while (true) {
                Socket socket = serverSocket.accept();
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String message = reader.readLine();

                System.out.println("Recieved from client:" + message);

                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}