package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientProgram {
    public static void main(String[] args) {
        // Get host address and port
        String host = getHost();
        int port = getPort();

        String command = "invalid";
        // Loop until exit command is issued
        while (!command.equals("exit")) {
            // Draws menu and prompts user for command
            command = menuChoice();

            // If a valid command is provided, user is prompted for number of runs and threads are created/runs
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
                if (port >= 1025 && port <= 4998) {
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

    // Draws menu
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

    // Gets input from user and returns the corresponding command
    public static String menuChoice() {
        Scanner scanner = new Scanner(System.in);
        String choice;
        drawMenu();
        choice = scanner.nextLine();
        switch (choice) {
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

    // Gets number of runs from the user
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

    // Creates specified number of threads to run the specified command
    public static void threadHandler(int numRuns, String command, String host, int port) {
        long startTime = System.nanoTime();

        // Array of threads
        Thread[] threads = new Thread[numRuns];
        // Array to store durations
        long[] threadDurations = new long[numRuns];

        // Loop through array creating new commandThreads and starting them
        for (int i = 0; i < numRuns; i++) {
            threads[i] = new commandThread(command, host, port, threadDurations, i);
            threads[i].start();
        }

        // Loop through thread array and wait for threads to be completed
        for (Thread j : threads) {
            try {
                j.join(); // Wait for each thread to finish
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        long endTime = System.nanoTime();
        long durationNano = endTime - startTime;

        long totalThreadDuration = 0;
        for (long duration : threadDurations) {
            totalThreadDuration += duration; // Sum of all thread durations
        }

        double averageDuration = totalThreadDuration / (1_000_000_000.0 * numRuns); // Average in seconds
        double duration = durationNano / 1_000_000_000.0; // Total duration in seconds

        System.out.printf("----Total Time: %.3f seconds----\n", duration);
        System.out.printf("----Average Time: %.3f seconds----\n", averageDuration);

        // Write results to CSV with command-specific filename
        writeToCSV(command, numRuns, threadDurations, duration, averageDuration);
    }

    // Writes results to a CSV file
    public static void writeToCSV(String command, int numRuns, long[] threadDurations, double totalTime, double averageTime) {
        // Sanitize command name to make it a valid filename
        String filename = command.replaceAll("[^a-zA-Z0-9]", "_") + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            // Write header if it's the first time writing to the file
            File file = new File(filename);
            if (file.length() == 0) {
                writer.write("Command,Num Runs,Thread Time (s),Total Time (s),Average Time (s)\n");
            }

            // Write data for each thread
            for (int i = 0; i < numRuns; i++) {
                writer.write(String.format("%s,%d,%.3f,%.3f,%.3f\n",
                        command, numRuns, threadDurations[i] / 1_000_000_000.0, totalTime, averageTime));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class commandThread extends Thread {
    private String command;
    private String host;
    private int port;
    private long[] threadDurations;
    private int index;

    public commandThread(String command, String host, int port, long[] threadDurations, int index) {
        this.command = command;
        this.host = host;
        this.port = port;
        this.threadDurations = threadDurations;
        this.index = index;
    }

    public void run() {
        long startTime = System.nanoTime();

        StringBuilder sb = new StringBuilder();
        try (Socket socket = new Socket(host, port)) {
            // Input-output streams
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            // Reader/writer for converting input/output streams from byte data to usable strings
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter(output, true);

            // Sends the unix command to the server
            writer.println(command);

            // Reads output from the unix command sent by the server and prints it
            String s;
            while ((s = reader.readLine()) != null) {
                sb.append(s);
                sb.append("\n");
            }
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        } finally {
            // Calculates thread time and stores the result
            long endTime = System.nanoTime();
            long durationNano = endTime - startTime;
            double duration = durationNano / 1_000_000_000.0; // duration in seconds

            sb.append(String.format("--Thread Time: %.3f seconds--\n", duration));
            System.out.println(sb.toString());

            // Store the duration in the correct index of the array
            threadDurations[index] = durationNano;
        }
    }
}