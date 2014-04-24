package prompter;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class Prompter extends JFrame {

    private static class PrompterText extends JEditorPane {

        private boolean reverse = true;
        private int scrollIncrement = 2;

        public PrompterText(int scrollIncrement) {
            setEditable(false);
            this.scrollIncrement = scrollIncrement;
            this.enableEvents(AWTEvent.KEY_EVENT_MASK);
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int orient, int direction) {
            return scrollIncrement;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform at = new AffineTransform();
            if (reverse) {
                at.concatenate(new AffineTransform(-1, 0, 0, 1, 0, 0));
                at.concatenate(AffineTransform.getTranslateInstance(-getWidth(), 0));
            }
            g2d.setTransform(at);
            super.paint(g);
        }

        private void reverse() {
            reverse = !reverse;
            repaint();
        }

        @Override
        protected void processEvent(AWTEvent ev) {
            if (ev.getID() == KeyEvent.KEY_TYPED) {
                KeyEvent ke = (KeyEvent) ev;
                if (Character.toLowerCase(ke.getKeyChar()) == 'r') {
                    reverse();
                }
            }
            if (ev.getID() == MouseEvent.MOUSE_CLICKED) {
                MouseEvent me = (MouseEvent) ev;
                if (me.getButton() == MouseEvent.BUTTON3) {
                    reverse();
                }
            }
        }
    }

    private final JEditorPane jta;
    private final JScrollPane jscroll;

    public Prompter(URL source, int scrollIncrement) {
        //setAlwaysOnTop(true);
        //setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jta = new PrompterText(scrollIncrement);

        try {
            jta.setPage(source);
        } catch (IOException ex) {
            System.err.println("Failed to open URL " + source);
            System.exit(1);
        }
        jta.setCaretPosition(0);
        jta.setAutoscrolls(true);
        jscroll = new JScrollPane(jta);
        add(jscroll, BorderLayout.CENTER);
        jscroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jscroll.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        setVisible(true);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Toolkit t = Toolkit.getDefaultToolkit();

        GraphicsConfiguration gc = getGraphicsConfiguration();
        Rectangle r = gc.getBounds();
        setBounds(r);
    }

    public static void main(String[] args) throws Throwable {
        if (args.length == 0) {
            System.err.println("Usage: java -jar prompter.jar <file-to-display> [scroll-increment]");
            System.exit(1);
        }

        File f = new File(args[0]);
        final URL pageURL = f.getAbsoluteFile().toURI().toURL();

        int si = 4;
        if (args.length >= 2) {
            si = Integer.parseInt(args[1]);
        }
        final int scrollIncrement = si;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Prompter(pageURL, scrollIncrement);
            }
        });
    }
}
