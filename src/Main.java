import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.json.*;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;

public class Main {

    public static final int width = 500;
    public static final int height = 500;
    public JLabel text = null;
    public Box box1;

    public static void main(String[] args) {
        Main main = new Main();
        main.buildwindow();
    }

    public void buildwindow() {
        Frame f = new Frame("hello world");
        f.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        f.addWindowListener(new WindowAdapter(){ public void windowClosing( WindowEvent e ){ System.exit( 0 );}});
        f.setSize(width, height);
        TextField tf = new TextField("http://173.255.230.249/zingot.json");
        Button button = new Button("click me to download");
        box1 = Box.createVerticalBox();
        box1.add(tf);
        box1.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unpack(tf.getText());
            }
        });
        f.add(box1);
        addtxt("welcome to James' mod installer thing\nplease wait until it says\ndone before you close it");
        f.setVisible(true);
    }

    public String unpack(String url) {
        String packjson = getdog(url);
        String mc = System.getenv("APPDATA") + "/.minecraft";
        String modfolder = "/mods/" + jsonparse(packjson, "version");
        try {
            FileUtils.cleanDirectory(new File(mc + "/scripts"));
            FileUtils.cleanDirectory(new File(mc + modfolder));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String zip = mc + "/scripts/scripts.zip";
        System.out.println(zip);
        download(jsonparse(packjson, "scripts"), zip);
        unzip(zip);
        new File(zip).delete();
        zip = mc + modfolder + "/mods.zip";
        System.out.println(zip);
        download(jsonparse(packjson, "mods"), zip);
        unzip(zip);
        new File(zip).delete();
        addtxt("done");
        return jsonparse(packjson, "name");
    }

    public static void unzip(String zip) {
        File zipFile = new File(zip);
        LocalFileHeader localFileHeader;
        int readLen;
        byte[] readBuffer = new byte[4096];
        try (FileInputStream fileInputStream = new FileInputStream(zipFile)) {
            ZipInputStream zipinstream = new ZipInputStream(fileInputStream);
            while ((localFileHeader = zipinstream.getNextEntry()) != null) {
                File extractedFile = new File(localFileHeader.getFileName());
                try (OutputStream outputStream = new FileOutputStream(zipFile.getParent() + "/" + extractedFile)) {
                    while ((readLen = zipinstream.read(readBuffer)) != -1) {
                        outputStream.write(readBuffer, 0, readLen);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String jsonparse(String str, String key) {
        return new JSONObject(str).getString(key);
    }

    public void addtxt(String str) {
        text = new JLabel(htmlformat(str));
        box1.add(text);
        box1.revalidate();
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
            fos.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}