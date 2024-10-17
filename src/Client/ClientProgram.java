package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientProgram {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 6942)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter a string: ");
            String message = scanner.nextLine();

            writer.println(message);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
