package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientProgram {
    public static void main(String[] args) {

        //Requests a connection from the local machine server program at port 6942
        try (Socket socket = new Socket("localhost", 6942)) {
            //Input output streams
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            //Reader/writer for converting input/output streams from byte data to usable strings
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter(output, true);

            mainMenu();

            //socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String mainMenu() {

        //scanner for menu
        Scanner scanner = new Scanner(System.in);
        String choice;

        //draws the menu and gets the input
        drawMenu();
        choice = scanner.nextLine();

        switch(choice) {
            case "1":
                return "date";
            case "2":
                return "uptime";
            case "3":
                return "free -h";
            case "4":
                return "netstat -tuln";
            case "5":
                return "users";
            case "6":
                return "ps aux";
            case "7":
                System.out.println("Exiting the menu.");
                return "exit";
            default:
                System.out.println("Invalid choice. Please try again.");
                return "invalid";
        }
    }

    public static void drawMenu() {
        System.out.println("----Menu Options----");
        System.out.println("1. Date and Time");
        System.out.println("2. Uptime");
        System.out.println("3. Memory Use");
        System.out.println("4. Netstat");
        System.out.println("5. Current Users");
        System.out.println("6. Running Processes");
        System.out.println("7. Exit");
        System.out.print("\nInput: ");
    }
}
