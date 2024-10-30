package Server;

import java.io.*;
import java.net.*;

public class ServerProgram {
    public static void main(String[] args) {



        //Creates socket at port 6942
        try (ServerSocket serverSocket = new ServerSocket(6942)) {

            while (true) {
                //Accepts incoming connections
                Socket socket = serverSocket.accept();

                //Input output streams
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                //Reader/writer for converting input/output streams from byte data to usable strings
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                PrintWriter writer = new PrintWriter(output, true);

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