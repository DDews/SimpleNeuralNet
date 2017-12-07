import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Node extends JPanel {
    Neuron node;
    GUI frame;
    public JLabel text;
    public double out;
    public double saved;
    private boolean entered = false;
    public Node(GUI frame, Neuron node) {
        this.frame = frame;
        this.node = node;
        this.out = node.out;
        this.saved = node.out;
        text = new JLabel("" + node.out);
        text.setAlignmentX(JLabel.CENTER);
        text.setAlignmentY(JLabel.CENTER);
        text.setForeground(Color.WHITE);
        add(text);
        JPanel p = this;
        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                frame.mouseDown = true;
                entered = true;
                click();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                frame.mouseDown = false;
                entered = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (frame.mouseDown) {
                    if (!entered) {
                        entered = true;
                        click();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                entered = false;
            }
        });
    }
    private void click() {
        if (out > 0) out = 0;
        else out = 1;
        text.setText(String.format("%.2f",out));
        revalidate();
        repaint();
    }
    public void setOut(double out) {
        this.out = out;
        text.setText(String.format("%.2f",out));
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(Color.HSBtoRGB((((float)out  + 1f) / 2f) * 0.6f,1,1)));
        g.fillRect(0,0,this.getWidth(),this.getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(0,0,this.getWidth(),this.getHeight());
    }
}
