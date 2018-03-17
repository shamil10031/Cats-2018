import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    static ExecutorService executeIt = Executors.newFixedThreadPool(2);
    static int numbers = 0;

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(3838)) {
            while (!server.isClosed()) {
                System.out.println("Server socket created, waiting for clients...");
                Socket client = server.accept();
                System.out.println("New client!");
                executeIt.execute(new ClientHandler(client, numbers++));
                System.out.println("Connection accepted.");
            }
            executeIt.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}