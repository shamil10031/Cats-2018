import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private static Socket clientDialog;
    private static final String rootPath = "/home/shamil/CatsNeuro";
    private static final String scriptFileName = "use_catsnn.py";
    private static final String photoFileName = "cat";
    private static final String format = ".jpg";
    private int idNumber;

    private String photoPath;
    private String scriptPath = rootPath + '/' + scriptFileName;

    public ClientHandler(Socket client, int idNumber) {
        ClientHandler.clientDialog = client;
        this.idNumber = idNumber;
        this.photoPath = rootPath + '/' + photoFileName + idNumber + format;
    }

    @Override
    public void run() {

        try {
            DataOutputStream out = new DataOutputStream(clientDialog.getOutputStream());
            DataInputStream in = new DataInputStream(clientDialog.getInputStream());
            FileOutputStream fos = new FileOutputStream(photoPath);

            if (!clientDialog.isClosed()) {

                byte[] bytes = new byte[1024];
                int read_count, currentSize = 0, realSize;
                System.out.println("Start reading originalSize...");
                realSize = in.readInt();
                System.out.println("Start reading " + realSize + " bytes...");
                while (currentSize < realSize
                        && (read_count = in.read(bytes, 0, bytes.length)) != -1) {
                    currentSize += read_count;
                    System.out.println(read_count + ", size = " + currentSize);
                    fos.write(bytes, 0, read_count);
                }
                fos.flush();
                fos.close();

                System.out.println("Done! Cat here : " + photoPath);
                out.writeUTF(getCatBreed(photoPath, scriptPath));
                Thread.sleep(5000);
            }
            System.out.println("Client disconnected!");
            System.out.println("Closing connections & channels...");

            out.flush();
            in.close();
            out.close();
            clientDialog.close();
            Server.numbers--;
            System.out.println("Closed!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getCatBreed(String photoPath, String scriptPath) {
        String msg, errors, catBreed = "null";
        String[] cmd = {"python3", scriptPath, photoPath};
        try {
            System.out.println("Start identifying cat's breed...");
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(cmd);
            System.out.println("CMD launched...");
            System.out.println("Command to CMD: python" + " " + scriptPath + " " + photoPath);
            BufferedReader bfr = new BufferedReader
                    (new InputStreamReader(pr.getInputStream()));
            while ((msg = bfr.readLine()) != null) {
                System.out.println("From CMD: " + msg);
                if (msg.startsWith("br("))
                    catBreed = msg.substring(3, msg.length() - 1);
            }
            BufferedReader bfrErrors = new BufferedReader
                    (new InputStreamReader(pr.getErrorStream()));
            System.out.println("Errors:");
            while ((errors = bfrErrors.readLine()) != null)
                System.out.println("    E: " + errors);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return catBreed;
    }
}