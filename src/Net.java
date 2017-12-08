import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Net {
    ArrayList<Layer> brain;
    public Net(Vector<Integer> size, double decay, double threshold, double forgetting, double gain) {
        brain = new ArrayList<Layer>();
        for (int i = 0; i < size.size(); i++) {
            Layer layer = new Layer();
            for (int j = 0; j < size.get(i).intValue(); j++) {
                layer.neurons.add(new Neuron(decay, threshold, forgetting, gain));
            }
            brain.add(layer);
            if (i > 0) {
                for (int k = 0; k < layer.neurons.size(); k++) {
                    layer.neurons.get(k).setNeurons(brain.get(i - 1).neurons);
                }
            }
        }

    }
    public void step(ArrayList<Double> inputs, double time) {
        if (brain.size() > 0) {
            Layer first = brain.get(0);
            ArrayList<Neuron> neurons = first.neurons;
            for (int i = 0; i < inputs.size(); i++) {
                Neuron n = neurons.get(i);
                n.step(time,inputs.get(i));
            }
        }
        if (brain.size() > 1) {
            for (int i = 1; i < brain.size(); i++) {
                Layer next = brain.get(i);
                ArrayList<Neuron> neurons = next.neurons;
                for (int j = 0; j < neurons.size(); j++) {
                    Neuron n = neurons.get(j);
                    n.step(time);
                }
            }
        }
    }
    public void save() {
        for (Layer L : brain) {
            for (Neuron n : L.neurons) {
                n.savedWeights = new HashMap<Neuron,Double>(n.weights);
            }
        }
    }
    public void load() {
        for (Layer L : brain) {
            for (Neuron n : L.neurons) {
                n.weights = new HashMap<Neuron,Double>(n.savedWeights);
            }
        }
    }
}
