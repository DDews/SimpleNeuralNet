import javafx.util.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.List;

public class GUI extends JFrame{
    public boolean mouseDown = false;
    private int COLUMNS = 0;
    private int ROWS = 0;
    private int BUTTON_WIDTH = 300;
    private double time = 1;
    private int nodeWidth = 10;
    private int nodeHeight = 10;
    private boolean shouldScroll = false;
    private JScrollPane scrollPane;
    private JPanel nodePane;
    private JPanel outPane;
    private boolean showingWeights = false;
    public Net brain;
    //public Net forgerer;
    public ArrayList<Node> outStar;
    public ArrayList<Node> nodes;
    public ArrayList<Pair<ArrayList<Double>,ArrayList<Double>>> inputs;
    public ArrayList<ArrayList<Double>> targets;
    public ArrayList<Node> forged;
    public GUI(Vector<Integer> layers, int columns, int rows) throws Exception {
        this.COLUMNS = columns;
        this.ROWS = rows;

        GridBagLayout gl = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 0;
        gc.gridy = 0;
        JFrame frame = this;
        JPanel contentPane = new JPanel();
        contentPane.setLayout(gl);
        contentPane.setSize(new Dimension(columns * nodeWidth + BUTTON_WIDTH + 100, rows * nodeHeight));

        this.COLUMNS = layers.size();
        int largest = 0;
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).intValue() > largest) {
                largest = layers.get(i).intValue();
            }
        }
        this.ROWS = largest;

        brain = new Net(layers, 0, 0.0,0.0000,1);
        Collections.reverse(layers);
        //forgerer = new Net(layers,0,0.0,0.0000,1);
        // 1 extra for an outstar
        nodes = new ArrayList<Node>();
        outStar = new ArrayList<Node>();
        inputs = new ArrayList<Pair<ArrayList<Double>,ArrayList<Double>>>();
        forged = new ArrayList<Node>();
        setTitle("Neural Net");
        setPreferredSize(new Dimension(100 + COLUMNS * nodeWidth + BUTTON_WIDTH,400));
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.Y_AXIS));
        buttonPane.setMinimumSize(new Dimension(BUTTON_WIDTH,ROWS * nodeHeight));
        buttonPane.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        JButton clear = new JButton("Clear Inputs");
        clear.setAlignmentX(JButton.CENTER_ALIGNMENT);
        clear.setAlignmentY(JButton.CENTER_ALIGNMENT);
        JButton step = new JButton("Step");
        step.setAlignmentX(JButton.CENTER_ALIGNMENT);
        step.setAlignmentY(JButton.CENTER_ALIGNMENT);
        JButton save = new JButton("Add to Training");
        save.setAlignmentX(JButton.CENTER_ALIGNMENT);
        save.setAlignmentY(JButton.CENTER_ALIGNMENT);
        JButton loadTraining = new JButton("Load images");
        loadTraining.setAlignmentX(JButton.CENTER_ALIGNMENT);
        loadTraining.setAlignmentY(JButton.CENTER_ALIGNMENT);
        JButton train = new JButton("Train");
        train.setAlignmentX(JButton.CENTER_ALIGNMENT);
        train.setAlignmentY(JButton.CENTER_ALIGNMENT);
        JButton clearTraining = new JButton("Clear Training");
        clearTraining.setAlignmentX(JButton.CENTER_ALIGNMENT);
        clearTraining.setAlignmentY(JButton.CENTER_ALIGNMENT);
        JButton saveState = new JButton("Save State");
        saveState.setAlignmentX(JButton.CENTER_ALIGNMENT);
        saveState.setAlignmentY(JButton.CENTER_ALIGNMENT);
        JButton loadState = new JButton("Load State");
        loadState.setAlignmentX(JButton.CENTER_ALIGNMENT);
        loadState.setAlignmentY(JButton.CENTER_ALIGNMENT);
        JButton weights = new JButton("Show Weights");
        weights.setAlignmentX(JButton.CENTER_ALIGNMENT);
        weights.setAlignmentY(JButton.CENTER_ALIGNMENT);
        JButton loadImage = new JButton("Load Image");
        loadImage.setAlignmentX(JButton.CENTER_ALIGNMENT);
        loadImage.setAlignmentY(JButton.CENTER_ALIGNMENT);
        clear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (Node node : outStar) {
                    node.setOut(0);
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
                ArrayList<Double> selectedOutputs = new ArrayList<Double>();
                ArrayList<Neuron> outputs = brain.brain.get(brain.brain.size() - 1).neurons;
                for (int i = outputs.size(); i >= 1; i--) {
                    selectedOutputs.add(nodes.get(nodes.size() - i).out);
                }
                inputs.add(new Pair<ArrayList<Double>,ArrayList<Double>>(selectedInputs,selectedOutputs));
            }
        });

        saveState.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    brain.save();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        loadState.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    brain = Net.load(frame);
                    int nodeNum = 0;
                    for (Layer layer : brain.brain) {
                        for (Neuron neuron : layer.neurons) {
                            nodes.get(nodeNum).node = neuron;
                            nodes.get(nodeNum).setOut(neuron.out);
                            nodeNum++;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j <= inputs.size() - 5; j += 5) {
                        List<Pair<ArrayList<Double>,ArrayList<Double>>> batch = inputs.subList(j,j + 4);
                        ArrayList<Double> emptyInput = new ArrayList<Double>();
                        ArrayList<Double> emptyTarget = new ArrayList<Double>();
                        for (int n = 0; n < outStar.size(); n++) {
                            emptyInput.add(0d);
                        }
                        for (int n = 0; n < brain.brain.get(brain.brain.size() - 1).neurons.size(); n++) {
                            emptyTarget.add(0d);
                        }
                        batch.add(new Pair(emptyInput,emptyTarget));
                        Collections.shuffle(batch);
                        for (int k = 0; k < batch.size(); k++) {
                            brain.step(batch.get(k).getKey(), 1);
                            brain.backPropogate(batch.get(k).getValue());
                        }
                    }
                    System.out.println(i);
                }
            }
        });
        clearTraining.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                inputs.clear();
            }
        });
        loadTraining.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File[] images = new File("training_set/").listFiles();
                try {
                    for (File file : images) {
                        BufferedImage image = ImageIO.read(file);
                        BufferedImage after = new BufferedImage(columns,rows,BufferedImage.TYPE_INT_ARGB);
                        AffineTransform at = AffineTransform.getScaleInstance((float)columns / image.getWidth(),(float)rows / image.getHeight());
                        AffineTransformOp scaleOp = new AffineTransformOp(at,AffineTransformOp.TYPE_BILINEAR);
                        after = scaleOp.filter(image,after);
                        image = after;
                        ArrayList<Double> input = new ArrayList<Double>();
                        ArrayList<Double> target = new ArrayList<Double>();
                        int n = 0;
                        for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) {
                            for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) {
                                Color c = new Color(image.getRGB(xPixel, yPixel));
                                float[] hsb = new float[3];
                                Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
                                input.add((double)hsb[2]);
                                outStar.get(n).setOut((double) hsb[2]);
                                n++;
                            }
                        }
                        String name = file.getName().split("-")[0];
                        int firstNum = Integer.parseInt(name) - 1;
                        name = "" + firstNum;
                        System.out.println(firstNum);
                        JOptionPane.showMessageDialog(frame,"" + firstNum);
                        for (int i = 0; i < 10; i++) {
                            if (firstNum == i) target.add(1d);
                            else target.add(0d);
                        }
                        System.out.println(input.size());
                        inputs.add(new Pair<ArrayList<Double>,ArrayList<Double>>(input,target));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                JOptionPane.showMessageDialog(frame,"Done");
            }
        });
        loadImage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
                FileNameExtensionFilter filter = new FileNameExtensionFilter(".png files","png");
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                        BufferedImage after = new BufferedImage(columns,rows,BufferedImage.TYPE_INT_ARGB);
                        AffineTransform at = AffineTransform.getScaleInstance((float)columns / image.getWidth(),(float)rows / image.getHeight());
                        AffineTransformOp scaleOp = new AffineTransformOp(at,AffineTransformOp.TYPE_BILINEAR);
                        after = scaleOp.filter(image,after);
                        image = after;
                        int n = 0;
                        for (int x = 0; x < image.getWidth(); x++) {
                            for (int y = 0; y < image.getHeight(); y++) {
                                Color c = new Color(image.getRGB(x,y));
                                float[] hsb = new float[3];
                                Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),hsb);
                                outStar.get(n).setOut((double) hsb[2]);
                                n++;
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                redraw();
            }
        });
        buttonPane.add(clear);
        buttonPane.add(step);
        buttonPane.add(save);
        buttonPane.add(loadTraining);
        buttonPane.add(train);
        buttonPane.add(clearTraining);
        buttonPane.add(saveState);
        buttonPane.add(loadState);
        buttonPane.add(weights);
        buttonPane.add(loadImage);

        nodePane = new JPanel();
        nodePane.setLayout(new BoxLayout(nodePane,BoxLayout.X_AXIS));
        nodePane.setSize(new Dimension(columns * nodeWidth, rows * nodeHeight));


        int nodeNum = 0;

        for (int i = 0; i < COLUMNS; i++) {
            JPanel column = new JPanel();
            column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
            column.setAlignmentY(Component.TOP_ALIGNMENT);
            column.setBackground(Color.BLACK);
            for (int k = 0; k < brain.brain.get(i).neurons.size(); k++) {
                Node node = new Node(this, brain.brain.get(i).neurons.get(k));
                node.setSize(new Dimension(nodeWidth, nodeHeight));
                if (i > 0) {
                    column.add(node);
                    nodePane.add(column);
                } else {
                    outStar.add(node);
                }
                nodes.add(node);
            }
        }

        outPane = new JPanel();
        outPane.setLayout(new BoxLayout(outPane,BoxLayout.X_AXIS));
        outPane.setSize(columns * nodeWidth,rows * nodeHeight);
        int node = 0;
        for (int x = 0; x < columns; x++) {
            JPanel column = new JPanel();
            column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
            column.setAlignmentY(Component.TOP_ALIGNMENT);
            column.setBackground(Color.BLACK);
            for (int y = 0; y < rows; y++) {
                Node n = nodes.get(node);
                n.setSize(new Dimension(nodeWidth,nodeHeight));
                node++;
                column.add(n);
            }
            outPane.add(column);
        }

        /*JPanel forgedPane = new JPanel();
        forgedPane.setLayout(new BoxLayout(forgedPane,BoxLayout.X_AXIS));
        forgedPane.setSize(columns * nodeWidth,rows * nodeHeight);
        ArrayList<Neuron> neurons = forgerer.brain.get(forgerer.brain.size() - 1).neurons;
        int forgeNum = 0;
        for (int i = 0; i < columns; i++) {
            JPanel column = new JPanel();
            column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
            column.setAlignmentY(Component.TOP_ALIGNMENT);
            column.setBackground(Color.BLACK);
            for (int k = 0; k < rows; k++) {
                Node n = new Node(this,neurons.get(forgeNum));
                forgeNum++;
                n.setSize(new Dimension(nodeWidth, nodeHeight));
                column.add(n);
            }
            forgedPane.add(column);
        }
        */
        scrollPane = new JScrollPane(nodePane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //scrollPane.setBounds(0, 0, columns * nodeWidth + 20, rows * nodeHeight);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (e.getValueIsAdjusting()) {
                    if (e.getAdjustable().getValue() > e.getAdjustable().getMaximum() * 0.95) shouldScroll = true;
                    else shouldScroll = false;
                }
                if (shouldScroll) e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });
        JPanel scrollingPane = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = rows * nodeHeight;
        c.gridwidth = columns * nodeWidth;
        c.weightx = 100;
        c.weighty = 100;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        scrollingPane.setLayout(gridBag);
        scrollingPane.add(scrollPane,c);
        gc.weightx = 40;
        gc.weighty = 100;
        gc.anchor = GridBagConstraints.WEST;
        contentPane.add(buttonPane,gc);
        gc.weightx = 50;
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.CENTER;
        contentPane.add(outPane,gc);
        gc.gridx = 2;
        gc.weightx = 10;
        gc.anchor = GridBagConstraints.EAST;
        contentPane.add(scrollingPane,gc);
       /* gc.gridx = 3;
        gl.setConstraints(forgedPane,gc);
        contentPane.add(forgedPane);*/
        this.setContentPane(contentPane);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                        g.setColor(new Color(Color.HSBtoRGB(1f - (float) weight * 0.3f, 1, 1)));
                        g.drawLine(x, y, x2, y2);
                    }
                }
            }
        }
    }
    public static void main(String[] args) throws Exception {
        int columns = 10;
        int rows = 10;
        int[] L = new int[] {columns * rows, 5, 3};
        Vector<Integer> layers = new Vector<Integer>();
        for (int d : L) {
            layers.add(d);
        }
        GUI gui = new GUI(layers, columns, rows);
    }
}
