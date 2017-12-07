import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Neuron {
    double out;
    double decay;
    double threshold;
    double forgetting;
    double gain;
    double newOut;
    HashMap<Neuron, Double> savedWeights;
    HashMap<Neuron,Double> weights;
    public Neuron(double decay, double threshold, double forgetting, double gain) {
        this.decay = decay;
        this.threshold = threshold;
        this.forgetting = forgetting;
        this.gain = gain;
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
    public void step(double time, double input) {
        double dy = -decay * out;
        double sum = 0;
        Iterator it = weights.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Neuron n = (Neuron)pair.getKey();
            if (n == this) continue;
            double w = (Double)pair.getValue();
            double newWeight = -forgetting * n.out;
            double potential = (n.out * w * time - threshold);
            System.out.println(potential);
            if (potential > 0) {
                sum += w * potential;
                newWeight += gain * out * potential;
                weights.replace(this, newWeight);
            }
        }
        dy += input * sum;
        newOut = dy / (1 + Math.abs(dy));
    }
    public void saveState() {
        savedWeights = new HashMap<Neuron,Double>(weights);
    }
    public void loadState() {
        weights = new HashMap<Neuron,Double>(savedWeights);
    }
}
