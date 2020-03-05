import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        Frame f = new Frame( "hello world" );
        f.addWindowListener( new WindowAdapter(){ public void windowClosing( WindowEvent e ){ System.exit( 0 ); } } );
        f.setSize( 500, 500 );
        f.setVisible(true);
    }
}
