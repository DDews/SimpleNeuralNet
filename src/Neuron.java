import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Neuron implements Serializable {
    double out;
    double decay;
    double threshold;
    double forgetting;
    double gain;
    double delta;
    boolean bias = false;
    public Node node;
    HashMap<Neuron, Double> savedWeights;
    HashMap<Neuron,Double> weights;
    public Neuron(double decay, double threshold, double forgetting, double gain, boolean bias) {
        this.decay = decay;
        this.threshold = threshold;
        this.forgetting = forgetting;
        this.gain = gain;
        this.bias = bias;
        weights = new HashMap<Neuron,Double>();
        savedWeights = new HashMap<Neuron,Double>(weights);
        out = Math.random();
    }
    public void setNeurons(ArrayList<Neuron> neurons) {
        weights = new HashMap<Neuron,Double>();
        for(Neuron n : neurons) {
            weights.put(n,Math.random());
        }
    }
    public void step(double time) {
        double dy = 0;
        double sum = 0;
        Iterator it = weights.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Neuron n = (Neuron)pair.getKey();
            double potential = 0;
            if (!n.bias) {
                double w = (Double) pair.getValue();
                potential = (n.out * w);
            } else {
                potential = (Double)pair.getValue();
            }
            //System.out.println(potential);
            sum += potential;
        }
        dy += sum;
        out = 1 / (1 + Math.exp(-dy));
    }
    @Override
    public String toString() {
        return "" + out;
    }
    public void step(double time, double input) {
        out = input;
    }
    public void saveState() {
        savedWeights = new HashMap<Neuron,Double>(weights);
    }
    public void loadState() {
        weights = new HashMap<Neuron,Double>(savedWeights);
    }
}