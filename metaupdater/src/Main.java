import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.update();
        main.run();
    }

    public void update() {
        try {
            new File("install.jar").delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        download("https://jamesvps.tk/static/mods/modinstaller.jar", "install.jar");
    }

    public void run() {
        try {
            new ProcessBuilder("java", "-jar",  "install.jar").start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void download(String request, String outname) {
        try {
            URL url = new URL(request);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(outname);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
