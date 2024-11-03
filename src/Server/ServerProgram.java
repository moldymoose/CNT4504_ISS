package Server;

import java.io.*;
import java.net.*;

public class ServerProgram {
    public static void main(String[] args) {



        //Creates socket at port 6942
        try (ServerSocket serverSocket = new ServerSocket(4000)) {

            while (true) {
                //Accepts incoming connections
                Socket socket = serverSocket.accept();

                //Input output streams
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                //Reader/writer for converting input/output streams from byte data to usable strings
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                PrintWriter writer = new PrintWriter(output, true);

                String command = reader.readLine();

                Process p = Runtime.getRuntime().exec(command);

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                //BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    writer.println(s);
                }
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}