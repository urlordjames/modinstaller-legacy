package installer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.util.Set;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.*;

public class Main {

    public static final int width = 1000;
    public static final int height = 500;
    public Box textbox;

    public static void main(String[] args) {
        Main main = new Main();
        main.buildwindow();
    }

    public void buildwindow() {
        Frame f = new Frame("Mod Updater/Installer");
        f.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        f.addWindowListener(new WindowAdapter(){ public void windowClosing( WindowEvent e ){ System.exit(0);}});
        f.setSize(width, height);
        TextField tf = new TextField();
        Button button = new Button("click me to download");
        Box box1 = Box.createVerticalBox();
        Box box2 = Box.createHorizontalBox();
        Box box3 = Box.createVerticalBox();
        box1.add(tf);
        box1.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unpack(tf.getText());
            }
        });
        box2.add(box1);
        box2.add(Box.createRigidArea(new Dimension(150, 0)));
        textbox = Box.createVerticalBox();
        box2.add(textbox);
        addtxt("welcome to James' mod installer thing please\nwait until it says done before you close it");
        box3.add(box2);
        ScrollPane scrollpane = new ScrollPane();
        JSONObject modpacks = new JSONObject(getdog("https://jamesvps.tk/static/mods/packs.json"));
        String[] list = getpacks(modpacks);
        tf.setText(modpacks.get(list[0]).toString());
        JList modlist = new JList(list);
        scrollpane.add(modlist);
        box3.add(scrollpane);
        Button button2 = new Button("select modpack");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int pos = modlist.getSelectedIndex();
                System.out.println(pos);
                tf.setText(modpacks.get(list[pos]).toString());
            }
        });
        box3.add(button2);
        f.add(box3);
        f.setVisible(true);
    }

    public static String[] getpacks(JSONObject json) {
        Set<String> mods = json.keySet();
        String[] modlist = new String[mods.size()];
        int i = 0;
        for (String mod : mods) {
            modlist[i++] = mod;
        }
        return modlist;
    }

    public void unpack(String url) {
        String packjson = getdog(url);
        String mc = System.getenv("APPDATA") + "/.minecraft";
        String modfolder = "/mods/" + jsonparse(packjson, "version");
        dofolder(mc + "/scripts", jsonparse(packjson, "scripts"));
        dofolder(mc + "/config", jsonparse(packjson, "config"));
        dohashed(mc + modfolder, jsonparse(packjson, "mods"));
        addtxt("done");
    }

    public static void dohashed(String folder, String url) {
        try {
            new File(folder).mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        String json = getdog(url);
        JSONObject jsonhashes = new JSONObject(json);
        JSONObject filehashes = new JSONObject();
        File dir = new File(folder);
        File[] files = dir.listFiles();
        for (File file : files) {
            String name = file.getName();
            String sha = "";
            try {
                FileInputStream filetohash = new FileInputStream(file);
                sha = sha256(filetohash);
                filetohash.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
            filehashes.put(sha, name);
        }
        for (String hash : filehashes.keySet()) {
            if (!inlist(jsonhashes, hash)) {
                System.out.println("downloaded mod not in server list, deleting");
                String delete = folder + "/" + filehashes.get(hash).toString();
                System.out.println(delete);
                try {
                    boolean success = new File(delete).delete();
                    if (!success) {
                        System.out.println("ERROR: file deletion failed (for some unknown reason)");
                        System.exit(-1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
        for (String hash : jsonhashes.keySet()) {
            if (!inlist(filehashes, hash)) {
                System.out.println("missing mod, downloading");
                String name = jsonhashes.get(hash).toString();
                System.out.println(name);
                try {
                    download(name, folder + "/" + FilenameUtils.getName(new URL(name).getFile()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    System.out.println("it would appear the server side list has an error, this isn't your fault, please wait for an update");
                    System.exit(-1);
                }
            }
        }
    }

    public static Boolean inlist(JSONObject haystack, String needle) {
        return haystack.has(needle);
    }

    public static void dofolder(String folder, String url) {
        try {
            new File(folder).mkdirs();
            FileUtils.cleanDirectory(new File(folder));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        String zip = folder + "/zip.zip";
        System.out.println(zip);
        download(url, zip);
        unzip(zip);
        boolean delete = new File(zip).delete();
        if (!delete) {
            System.out.println("ERROR: file deletion failed (for some unknown reason)");
            System.exit(-1);
        }
    }

    public static void unzip(String zip) {
        ZipFile zipfile = new ZipFile(zip);
        File file = zipfile.getFile();
        try {
            zipfile.extractAll(file.getParent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String jsonparse(String str, String key) {
        return new JSONObject(str).getString(key);
    }

    public void addtxt(String str) {
        System.out.println(str);
        textbox.add(new JLabel(htmlformat(str), JLabel.CENTER));
        textbox.revalidate();
    }

    public static String htmlformat(String text) {
        return "<html><div style='text-align: center;'>" + text.replaceAll("\n", "<br>") + "</div></html>";
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
            System.exit(-1);
        }
        return "une error";
    }

    public static String sha256(InputStream file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] sha = digest.digest(IOUtils.toByteArray(file));
            String shastring = bintohex(sha);
            return shastring;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return "";
    }

    public static String bintohex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(byte b: bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static void download(String request, String outname) {
        try {
            URL url = new URL(request);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(outname);
            FileChannel channel = fos.getChannel();
            channel.transferFrom(rbc, 0, Long.MAX_VALUE);
            channel.close();
            fos.close();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
