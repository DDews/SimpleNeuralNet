import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
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
    private boolean showingWeights = false;
    public Net brain;
    public ArrayList<Node> outStar;
    public ArrayList<Node> nodes;
    public ArrayList<ArrayList<Double>> inputs;
    public ArrayList<ArrayList<Double>> targets;
    public GUI(int columns, int rows) {
        this.COLUMNS = columns;
        this.ROWS = rows;

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.X_AXIS));
        contentPane.setSize(new Dimension(COLUMNS * nodeWidth + BUTTON_WIDTH + 100, ROWS * nodeHeight));

        Vector<Integer> layers = new Vector<Integer>();
        layers.add(columns * rows);
        layers.add(4);
        layers.add(1);
        this.COLUMNS = layers.size();
        int largest = 0;
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).intValue() > largest) {
                largest = layers.get(i).intValue();
            }
        }
        this.ROWS = largest;

        brain = new Net(layers, 0, 0.0,0.0000,1);
        // 1 extra for an outstar
        nodes = new ArrayList<Node>();
        outStar = new ArrayList<Node>();
        inputs = new ArrayList<ArrayList<Double>>();
        targets = new ArrayList<ArrayList<Double>>();

        setTitle("Neural Net");
        setSize(100 + COLUMNS * nodeWidth + BUTTON_WIDTH,ROWS * nodeHeight    );
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        setBackground(Color.BLACK);


        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.Y_AXIS));
        buttonPane.setPreferredSize(new Dimension(BUTTON_WIDTH,ROWS * nodeHeight));
        buttonPane.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        JButton clear = new JButton("Clear Inputs");
        clear.setAlignmentX(JButton.CENTER_ALIGNMENT);
        JButton step = new JButton("Step");
        step.setAlignmentX(JButton.CENTER_ALIGNMENT);
        JButton save = new JButton("Add to Training");
        save.setAlignmentX(JButton.CENTER_ALIGNMENT);
        JButton train = new JButton("Train");
        train.setAlignmentX(JButton.CENTER_ALIGNMENT);
        JButton saveState = new JButton("Save State");
        saveState.setAlignmentX(JButton.CENTER_ALIGNMENT);
        JButton loadState = new JButton("Load State");
        loadState.setAlignmentX(JButton.CENTER_ALIGNMENT);
        JButton weights = new JButton("Show Weights");
        weights.setAlignmentX(JButton.CENTER_ALIGNMENT);
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
                ArrayList<Double> selectedInputs = new ArrayList<Double>();
                for (Node n : outStar) {
                    selectedInputs.add(n.out);
                }
                inputs.add(selectedInputs);
                ArrayList<Double> selectedOutputs = new ArrayList<Double>();
                ArrayList<Neuron> outputs = brain.brain.get(brain.brain.size() - 1).neurons;
                for (int i = 1; i <= outputs.size(); i++) {
                    selectedOutputs.add(nodes.get(nodes.size() - i).out);
                }
                targets.add(selectedOutputs);
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

        weights.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (showingWeights) {
                    showingWeights = false;
                } else {
                    showingWeights = true;
                }
                invalidate();
                repaint();
            }
        });
        train.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 1000000; i++) {
                    for (int j = 0; j < inputs.size(); j++) {
                        brain.step(inputs.get(j),1);
                        brain.backPropogate(targets.get(j));
                    }
                    System.out.println(i);
                }
            }
        });
        buttonPane.add(clear);
        buttonPane.add(step);
        buttonPane.add(save);
        buttonPane.add(train);
        buttonPane.add(saveState);
        buttonPane.add(loadState);
        buttonPane.add(weights);

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
        outPane.setSize(100,columns * rows);
        int node = 0;
        for (int y = 0; y < rows; y++) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row,BoxLayout.X_AXIS));
            for (int x = 0; x < columns; x++) {
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
        /*for (int i = 0; i < nodes.size(); i++) {
            System.out.println(i + ": " + nodes.get(i));
        }*/
        redraw();
        //System.out.println("\n");
    }
    public void backPropogate() {
        ArrayList<Double> targets = new ArrayList<Double>();
        ArrayList<Neuron> outputs = brain.brain.get(brain.brain.size() - 1).neurons;
        for (int i = 1; i <= outputs.size(); i++) {
            targets.add(nodes.get(nodes.size() - i).out);
        }
        brain.backPropogate(targets);
        redraw();
        //System.out.println("\n");
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (showingWeights) {
            for (Node n : nodes) {
                Iterator it = n.node.weights.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Neuron, Double> entry = (Map.Entry<Neuron, Double>) it.next();
                    Neuron neuron = entry.getKey();
                    if (neuron != null) {
                        double weight = entry.getValue();
                        if (Math.abs(weight) < 0.1) continue;
                        Node node = neuron.node;
                        int x = node.getX() + (int) (node.getWidth() * 0.5);
                        int y = node.getY() + (int) (node.getHeight() * 0.5);
                        Container parent = node.getParent();
                        while (parent != null) {
                            x += parent.getX();
                            y += parent.getY();
                            parent = parent.getParent();
                        }
                        int x2 = n.getX() + (int) (n.getWidth() * 0.5);
                        int y2 = n.getY() + (int) (n.getHeight() * 0.5);
                        parent = n.getParent();
                        while (parent != null) {
                            x2 += parent.getX();
                            y2 += parent.getY();
                            parent = parent.getParent();
                        }
                        g.setColor(new Color(Color.HSBtoRGB((float) weight * 0.7f, 1, 1)));
                        g.drawLine(x, y, x2, y2);
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        GUI gui = new GUI(2,1);
    }
}
