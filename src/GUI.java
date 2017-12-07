import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GUI extends JFrame{
    public boolean mouseDown = false;
    private int COLUMNS = 0;
    private int ROWS = 0;
    private int BUTTON_WIDTH = 100;
    private double time = 1;
    public Net brain;
    public ArrayList<Node> nodes;
    public GUI(int columns, int rows) {
        this.COLUMNS = columns;
        this.ROWS = rows;

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.X_AXIS));
        contentPane.setPreferredSize(new Dimension(COLUMNS * 100 + BUTTON_WIDTH, ROWS * 100));

        brain = new Net(COLUMNS * ROWS + 1, 0.001, 0.0,0.0001,1);
        // 1 extra for an outstar
        nodes = new ArrayList<Node>();

        setTitle("Neural Net");
        setSize(COLUMNS * 100,ROWS * 100);
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        setBackground(Color.BLACK);


        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.Y_AXIS));
        buttonPane.setPreferredSize(new Dimension(BUTTON_WIDTH,ROWS * 100));
        buttonPane.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        JButton clear = new JButton("Clear");
        clear.setAlignmentX(JButton.CENTER_ALIGNMENT);
        JButton step = new JButton("Step");
        step.setAlignmentX(JButton.CENTER_ALIGNMENT);
        JButton save = new JButton("Save Inputs");
        save.setAlignmentX(JButton.CENTER_ALIGNMENT);
        JButton load = new JButton("Load Inputs and Step");
        load.setAlignmentX(JButton.CENTER_ALIGNMENT);
        JButton saveState = new JButton("Save State");
        saveState.setAlignmentX(JButton.CENTER_ALIGNMENT);
        JButton loadState = new JButton("Load State");
        loadState.setAlignmentX(JButton.CENTER_ALIGNMENT);

        clear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (Node n : nodes) {
                    n.setOut(0);
                }
                redraw();
            }
        });

        step.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                step();
            }
        });

        save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (Node n : nodes) {
                    n.saved = n.out;
                }
            }
        });

        load.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (Node n : nodes) {
                    n.setOut(n.saved);
                }
                step();

            }
        });

        saveState.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                brain.save();
            }
        });
        loadState.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                brain.load();
            }
        });
        buttonPane.add(clear);
        buttonPane.add(step);
        buttonPane.add(save);
        buttonPane.add(load);
        buttonPane.add(saveState);
        buttonPane.add(loadState);


        JPanel nodePane = new JPanel();
        nodePane.setLayout(new BoxLayout(nodePane,BoxLayout.Y_AXIS));
        nodePane.setPreferredSize(new Dimension(COLUMNS * 100, ROWS * 100));

        int nodeNum = 0;

        JPanel outStar = new JPanel();
        outStar.setLayout(new BoxLayout(outStar, BoxLayout.X_AXIS));
        outStar.setAlignmentY(Component.TOP_ALIGNMENT);
        outStar.setBackground(Color.BLACK);
        Node star = new Node(this,brain.brain.get(nodeNum));
        star.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        nodes.add(star);
        outStar.add(star);
        nodePane.add(outStar);
        nodeNum++;

        for (int i = 0; i < ROWS; i++) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setAlignmentY(Component.TOP_ALIGNMENT);
            row.setBackground(Color.BLACK);
            for (int j = 0; j < COLUMNS; j++) {
                Node n = new Node(this,brain.brain.get(nodeNum));
                nodeNum++;
                row.add(n);
                nodes.add(n);
            }
            nodePane.add(row);
        }

        contentPane.add(buttonPane);
        contentPane.add(nodePane);
        this.setContentPane(contentPane);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setVisible(true);
    }
    public void loadValues() {
        for (int i = 0; i < brain.brain.size(); i++) {
            nodes.get(i).setOut(brain.brain.get(i).out);
        }
    }
    public void redraw() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).invalidate();
            nodes.get(i).repaint();
        }
    }
    public void step() {
        ArrayList<Double> inputs = new ArrayList<Double>();
        for (Node n : nodes) {
            inputs.add(n.out);
        }
        brain.step(inputs,time);
        loadValues();
        redraw();
        System.out.println("\n");
    }
    public static void main(String[] args) {
        GUI gui = new GUI(7,10);
    }
}
