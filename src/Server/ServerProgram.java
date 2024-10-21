package Server;

import java.io.*;
import java.net.*;

public class ServerProgram {
    public static void main(String[] args) {

        //Creates socket at port 6942
        try (ServerSocket serverSocket = new ServerSocket(6942)) {

            //Accepts connections, gets input/buffered reader
            while (true) {
                Socket socket = serverSocket.accept();
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                //reads line from client input
                String message = reader.readLine();

                System.out.println("Recieved from client:" + message);

                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}