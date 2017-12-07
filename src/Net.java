import java.util.ArrayList;

public class Net {
    ArrayList<Neuron> brain;
    public Net(int size, double decay, double threshold, double forgetting, double gain) {
        brain = new ArrayList<Neuron>();
        for (int i = 0; i < size; i++) {
            brain.add(new Neuron(decay,threshold,forgetting,gain));
        }
        for (int i = 0; i < brain.size(); i++) {
            brain.get(i).setNeurons(brain);
        }
    }
    public void step(ArrayList<Double> inputs, double time) {
        if (inputs.size() != brain.size()) return;
        for (int i = 0; i < inputs.size(); i++) {
            brain.get(i).step(time,inputs.get(i));
        }
        for (Neuron n : brain) {
            n.out = n.newOut;
        }
    }
    public void clear() {
        for (Neuron n : brain) {
            n.out = 0;
        }
    }
    public void save() {
        for (Neuron n : brain) {
            n.saveState();
        }
    }
    public void load() {
        for (Neuron n : brain) {
            n.loadState();
        }
    }
}
