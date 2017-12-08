import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Vector;

public class GUI extends JFrame{
    public boolean mouseDown = false;
    private int COLUMNS = 0;
    private int ROWS = 0;
    private int BUTTON_WIDTH = 100;
    private double time = 1;
    private int nodeWidth = 5;
    private int nodeHeight = 5;
    private boolean shouldScroll = false;
    private JScrollPane scrollPane;
    private JPanel nodePane;
    private JPanel outPane;
    public Net brain;
    public ArrayList<Node> outStar;
    public ArrayList<Node> nodes;
    public GUI(int columns, int rows) {
        this.COLUMNS = columns;
        this.ROWS = rows;

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.X_AXIS));
        contentPane.setSize(new Dimension(COLUMNS * nodeWidth + BUTTON_WIDTH + 100, ROWS * nodeHeight));

        Vector<Integer> layers = new Vector<Integer>();
        layers.add(70);
        layers.add(16);
        layers.add(16);
        layers.add(1);
        this.COLUMNS = columns = layers.size();
        int largest = 0;
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).intValue() > largest) {
                largest = layers.get(i).intValue();
            }
        }
        this.ROWS = rows = largest;

        brain = new Net(layers, 0, 0.0,0.0000,1);
        // 1 extra for an outstar
        nodes = new ArrayList<Node>();
        outStar = new ArrayList<Node>();

        setTitle("Neural Net");
        setSize(100 + COLUMNS * nodeWidth + BUTTON_WIDTH,ROWS * nodeHeight    );
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        setBackground(Color.BLACK);


        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.Y_AXIS));
        buttonPane.setPreferredSize(new Dimension(BUTTON_WIDTH,ROWS * nodeHeight));
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
                for (Node n : outStar) {
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
                for (Node n : outStar) {
                    n.saved = n.out;
                }
            }
        });

        load.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (Node n : outStar) {
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

        nodePane = new JPanel();
        nodePane.setLayout(new BoxLayout(nodePane,BoxLayout.X_AXIS));
        nodePane.setSize(new Dimension(COLUMNS * nodeWidth, ROWS * nodeHeight));


        int nodeNum = 0;

        for (int i = 0; i < COLUMNS; i++) {
            JPanel column = new JPanel();
            column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
            column.setAlignmentY(Component.TOP_ALIGNMENT);
            column.setBackground(Color.BLACK);
            for (int k = 0; k < brain.brain.get(i).neurons.size(); k++) {
                Node node = new Node(this, brain.brain.get(i).neurons.get(k));
                if (i > 0) {
                    node.setSize(new Dimension(nodeWidth, nodeHeight));
                    column.add(node);
                } else {
                    outStar.add(node);
                }
                nodes.add(node);
            }
            nodePane.add(column);
        }

        outPane = new JPanel();
        outPane.setLayout(new BoxLayout(outPane,BoxLayout.Y_AXIS));
        outPane.setSize(100,70);
        int node = 0;
        for (int y = 0; y < 10; y++) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row,BoxLayout.X_AXIS));
            for (int x = 0; x < 7; x++) {
                Node n = nodes.get(node);
                node++;
                row.add(n);
            }
            outPane.add(row);
        }


        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(0, 0, columns * nodeWidth + 20, rows * nodeHeight * 20);
        scrollPane.setSize(columns * nodeWidth + 20, rows * nodeHeight * 20);
        scrollPane.getViewport().add(nodePane);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (e.getValueIsAdjusting()) {
                    if (e.getAdjustable().getValue() > e.getAdjustable().getMaximum() * 0.95) shouldScroll = true;
                    else shouldScroll = false;
                }
                if (shouldScroll) e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });

        contentPane.add(buttonPane);
        contentPane.add(outPane);
        contentPane.add(scrollPane);
        this.setContentPane(contentPane);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setVisible(true);
    }
    public void loadValues() {
        ArrayList<Layer> layers = brain.brain;
        for (Node node : nodes) {
            node.setOut(node.node.out);
        }
    }
    public void redraw() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).revalidate();
            nodes.get(i).repaint();
        }
        nodePane.revalidate();
        nodePane.repaint();
        scrollPane.revalidate();
        scrollPane.repaint();
        this.revalidate();
        this.repaint();
    }
    public void step() {
        ArrayList<Double> inputs = new ArrayList<Double>();
        for (Node n : outStar) {
            inputs.add(n.out);
        }
        brain.step(inputs,time);
        loadValues();
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(i + ": " + nodes.get(i));
        }
        redraw();
        System.out.println("\n");
    }
    public static void main(String[] args) {
        GUI gui = new GUI(7,10);
    }
}
