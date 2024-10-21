package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientProgram {
    public static void main(String[] args) {

        //Connects to socket on port on local machine
        try (Socket socket = new Socket("localhost", 6942)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            //Gets input from user
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter a string: ");
            String message = scanner.nextLine();

            //Uses writer to output string to server
            writer.println(message);

            //Receives the server responses for requested function
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String serverResponseString = reader.readline();

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
