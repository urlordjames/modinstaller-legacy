import java.awt.*;
import java.awt.event.*;

public class Main {

    public static final int width = 500;
    public static final int height = 500;

    public static void main(String[] args) {
        Frame f = new Frame( "hello world" );
        f.setLayout(null);
        f.addWindowListener( new WindowAdapter(){ public void windowClosing( WindowEvent e ){ System.exit( 0 ); } } );
        f.setSize( width, height );
        TextField text = new TextField(3);
        text.setBounds(250, 250, 100,  24);
        f.add(text);
        f.setVisible(true);
    }
}
