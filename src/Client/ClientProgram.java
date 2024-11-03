package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientProgram {
    public static void main(String[] args) {

        String command = "invalid";
        int numRuns;
        String host = "139.62.210.155";
        int port = 4000;

        while (!command.equals("exit")) {
            if (!command.equals("invalid") && !command.equals("exit")) {
                numRuns = getNumRuns();
                threadHandler(numRuns, command, host, port);
            }
            command = menuChoice();
        }
    }

    public static String menuChoice() {

        //scanner for menu
        Scanner scanner = new Scanner(System.in);
        String choice;
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

    public static int getNumRuns() {
        Scanner scanner = new Scanner(System.in);
        int numRuns = -1;

        System.out.print("How many times would you like to run: ");
        while (numRuns < 0) {
            if (scanner.hasNextInt()) {
                numRuns = scanner.nextInt();
                if (numRuns >= 0) {
                    return numRuns;
                }
            } else scanner.next();
            System.out.print("That's not a valid integer. Please enter a positive integer: ");
        }
        return numRuns;
    }
    public static void threadHandler(int numRuns, String command, String host, int port) {
        long startTime = System.nanoTime();

        //array of threads
        Thread[] threads = new Thread[numRuns];

        //loops through array creating new commandThreads and starting them
        for (int i = 0; i < numRuns; i++) {
            threads[i] = new commandThread(command, host, port);
            threads[i].start();
        }

        //loops through thread array and waits for threads to be completed
        for (Thread j : threads) {
            try {
                j.join(); // Wait for each thread to finish
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000000; // duration in seconds
        long average = duration/numRuns;

        System.out.println("----Total Time: " + duration + " seconds----");
        System.out.println("----Average Time: " + average + " seconds----");
    }
}

class commandThread extends Thread {
    private String command;
    private String host;
    private int port;

    public commandThread(String command, String host, int port) {
        this.command = command;
        this.host = host;
        this.port = port;
    }

    public void run() {
        long startTime = System.nanoTime();

        try (Socket socket = new Socket(host, port)) {
            //Input output streams
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            //Reader/writer for converting input/output streams from byte data to usable strings
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter(output, true);

            writer.println(command);

            String s;
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
            }
            System.out.println(reader.readLine());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000000; // duration in seconds

        System.out.println("----Thread Time: " + duration + " seconds----");
    }
}