import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.*;

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
        addtxt(jsonparse(getdog("http://173.255.230.249/zingot.json"), "name"), f);
        f.setVisible(true);
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
}