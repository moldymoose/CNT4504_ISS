package Server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ServerProgram {
    public static void main(String[] args) {

        long serverStartTime = System.currentTimeMillis();

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

                //Prepares output stream to send data to client to print response
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                //Date and Time Function
                String dateTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());

                writer.println("Server Date and Time: " + dateTime);
                writer.println("Successfully sent date and time to client");

                //Server Uptime Function
                long serverCurrentTime = System.currentTimeMillis();
                long serverUpTime = serverCurrentTime - serverStartTime;

                String upTimeString = String.format("%02d hours %02d minutes %02d seconds",
                        TimeUnit.MILLISECONDS.toHours(serverUpTime),
                        TimeUnit.MILLISECONDS.toMinutes(serverUpTime) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(serverUpTime) % 60);

                writer.println("Server Current Uptime: " + upTimeString);
                writer.println("Successfully sent server uptime to client");

                //Memory Use Calculation Function
                Runtime runtime = Runtime.getRuntime();
                long totalMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();
                long usedMemory = totalMemory - freeMemory;
                long usedMemoryMBFormat = usedMemory / (1024 * 1024);

                writer.println("Server Memory Usage: " + usedMemoryMBFormat);
                writer.println("Successfully sent server memory usage to client");


                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}