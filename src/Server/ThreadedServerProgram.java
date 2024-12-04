package Server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ThreadedServerProgram {
    public static void main(String[] args) {

        //gets port number from user and creates socket
        int port = getPort();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            //accepts incoming connections
            while (true) {
                Socket socket = serverSocket.accept();
                new ServiceThread(socket).start();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    //prompts user for a port number and returns it
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
}

class ServiceThread extends Thread {
    private Socket socket;

    public ServiceThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
                //Input output streams
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                //Reader/writer for converting input/output streams from byte data to usable strings
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                PrintWriter writer = new PrintWriter(output, true);
                ) {

            //reads line for incoming commands
            String command = reader.readLine();

            //executes command and reads output into stdInput buffered reader
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            //loops through multiple lines of command output and prints them to the client
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                writer.println(s);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}