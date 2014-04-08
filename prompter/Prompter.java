package prompter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileReader;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class Prompter extends JFrame {

    private static class PrompterText extends JTextArea {
        @Override
        public int getScrollableUnitIncrement(Rectangle r, int orient, int direction) {
            return 2;
        }
        
        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform at = new AffineTransform();
            at.concatenate(new AffineTransform(-1, 0, 0, 1, 0, 0));
            at.concatenate(AffineTransform.getTranslateInstance(-getWidth(), 0));

            g2d.setTransform(at);
            super.paint(g);
        }
    }

    private JTextArea jta = new PrompterText();
    private JScrollPane jscroll = new JScrollPane(jta);

    public Prompter(String text, int fontSize) {
        setAlwaysOnTop(true);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jscroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jscroll.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jta.setForeground(Color.WHITE);
        jta.setBackground(Color.BLACK);
        jta.setText(text);
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        jta.setFont(new Font("Sans", Font.BOLD, fontSize));
        jta.setCaretPosition(0);
        jta.setAutoscrolls(true);
        add(jscroll, BorderLayout.CENTER);
        setBounds(0, 0, 1280, 720);
        setVisible(true);

    }

    @Override
    public void addNotify() {
        super.addNotify();
        Toolkit t = Toolkit.getDefaultToolkit();

        GraphicsConfiguration gc = getGraphicsConfiguration();
        Rectangle r = gc.getBounds();
        setBounds(r); // do this later!

    }

    public static void main(String[] args) throws Throwable {
        if (args.length == 0) {
            System.err.println("Usage: java -jar prompter.jar <file-to-display> [font-size]");
            System.exit(1);
        }
        
        File f = new File(args[0]);
        long size = f.length();
        FileReader fr = new FileReader(f);
        char[] buffer = new char[(int) ((size * 12) / 10)];

        size = fr.read(buffer);
        final String text = new String(buffer, 0, (int) size);

        int fs = 64;
        if (args.length == 2) {
            fs = Integer.parseInt(args[1]);
        }
        final int fontSize = fs;
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Prompter(text, fontSize);
            }
        });
    }

}
