import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.json.*;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;

public class Main {

    public static final int width = 500;
    public static final int height = 500;
    public JLabel text = null;

    public static void main(String[] args) {
        Main main = new Main();
        main.buildwindow();
    }

    public void buildwindow() {
        Frame f = new Frame("hello world");
        f.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        f.addWindowListener(new WindowAdapter(){ public void windowClosing( WindowEvent e ){ System.exit( 0 );}});
        f.setSize(width, height);
        String packjson = getdog("http://173.255.230.249/zingot.json");
        addtxt(jsonparse(packjson, "name"), f);
        download(jsonparse(packjson, "scripts"), "scripts.zip");
        f.setVisible(true);
    }

    public void unzip(File zipFile) {
        LocalFileHeader localFileHeader;
        int readLen;
        byte[] readBuffer = new byte[4096];
        try (FileInputStream fileInputStream = new FileInputStream(zipFile)) {
            ZipInputStream zipinstream = new ZipInputStream(fileInputStream);
            while ((localFileHeader = zipinstream.getNextEntry()) != null) {
                File extractedFile = new File(localFileHeader.getFileName());
                try (OutputStream outputStream = new FileOutputStream(extractedFile)) {
                    while ((readLen = zipinstream.read(readBuffer)) != -1) {
                        outputStream.write(readBuffer, 0, readLen);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String jsonparse(String str, String key) {
        return new JSONObject(str).getString(key);
    }

    public void addtxt(String str, Frame f) {
        text = new JLabel(htmlformat(str));
        f.add(text);
    }

    public static String htmlformat(String text) {
        return "<html>" + text.replaceAll("\n", "<br>") + "</html>";
    }

    public static String getdog(String request) {
        try {
            URL url = new URL(request);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            InputStream is = con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append("\n");
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "une error";
    }

    public static void download(String request, String outname) {
        try {
            URL url = new URL(request);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(outname);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}