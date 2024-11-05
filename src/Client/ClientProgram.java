package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientProgram {
    public static void main(String[] args) {
        //get host address and port
        String host = getHost();
        int port = getPort();

        String command = "invalid";
        //Loops until exit command is issued
        while (!command.equals("exit")) {
            //draws menu and prompts user for command
            command = menuChoice();

            //if a valid command is provided user is prompted for a number of runs and threads are created/ran
            if (!command.equals("invalid") && !command.equals("exit")) {
                int numRuns = getNumRuns();
                threadHandler(numRuns, command, host, port);
            }
        }
    }

    public static int getPort() {
        int port = -1;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Pick a port from 1025-4998: ");
        while (port < 1025) {
            if (scanner.hasNextInt()) {
                port = scanner.nextInt();
                if (port >=1025 && port <=4998) {
                    return port;
                }
            } else scanner.next();
            System.out.print("That's not a valid port. Please pick a port from 1025-4998: ");
        }
        return port;
    }
    public static String getHost() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your host address: ");
        String host = scanner.nextLine();
        return host;
    }

    //draws menu
    public static void drawMenu() {
        System.out.println("\n====Menu Options====");
        System.out.println("1. Date and Time");
        System.out.println("2. Uptime");
        System.out.println("3. Memory Use");
        System.out.println("4. Netstat");
        System.out.println("5. Current Users");
        System.out.println("6. Running Processes");
        System.out.println("7. Exit");
        System.out.print("\nInput: ");
    }
    //gets input from user and returns the corosponding command
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
    //gets number of runs from the user
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

    //creates specified number of threads to run specified command
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
        long durationNano = endTime - startTime;
        long averageNano = durationNano/numRuns;
        double duration = durationNano /1_000_000_000.0; // duration in nano seconds
        double average = averageNano/1_000_000_000.0;

        System.out.printf("----Total Time: %.3f seconds----\n", duration);
        System.out.printf("----Average Time: %.3f seconds----\n", average);
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

        StringBuilder sb = new StringBuilder();
        try (Socket socket = new Socket(host, port)) {
            //Input output streams
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            //Reader/writer for converting input/output streams from byte data to usable strings
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter(output, true);

            //sends unix command to server
            writer.println(command);

            //reads output from unix command sent by server and prints it
            String s;
            while ((s = reader.readLine()) != null) {
                sb.append(s);
                sb.append("\n");
            }
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        } finally {
            //calculates thread time and prints
            long endTime = System.nanoTime();
            long durationNano = endTime - startTime;
            double duration = durationNano /1_000_000_000.0; // duration in nano seconds

            sb.append(String.format("--Thread Time: %.3f seconds--\n", duration));
            System.out.println(sb.toString());
        }
    }
}