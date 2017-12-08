import java.util.*;

public class Net {
    ArrayList<Layer> brain;
    ArrayList<Double> inputs;
    public Net(Vector<Integer> size, double decay, double threshold, double forgetting, double gain) {
        brain = new ArrayList<Layer>();
        for (int i = 0; i < size.size(); i++) {
            Layer layer = new Layer();
            for (int j = 0; j < size.get(i).intValue(); j++) {
                layer.neurons.add(new Neuron(decay, threshold, forgetting, gain, false));
            }
            if (i < size.size() - 2) layer.neurons.add(new Neuron(decay, threshold, forgetting ,gain, true));
            brain.add(layer);
            if (i > 0) {
                for (int k = 0; k < layer.neurons.size(); k++) {
                    layer.neurons.get(k).setNeurons(brain.get(i - 1).neurons);
                }
            }
        }

    }
    public void step(ArrayList<Double> inputs, double time) {
        this.inputs = inputs;
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
    public void backPropogate(ArrayList<Double> targets) {
        if (brain.size() < 2) return;
        ArrayList<Neuron> outputs = brain.get(brain.size() - 1).neurons;
        if (outputs.size() != targets.size()) return;
        ArrayList<Neuron> secondLast = brain.get(brain.size() - 2).neurons;
        double cost = 0;

        for (int i = 0; i < outputs.size(); i++) {
            double o = outputs.get(i).out;
            double target = targets.get(i);
            cost += Math.pow(o - target,2);
        }
        while (cost > 0.005) {
            for (int i = 0; i < outputs.size(); i++) {
                double o = outputs.get(i).out;
                double delta = o * (1 - o) * (o - targets.get(i));
                outputs.get(i).delta = delta;
            }
            propogate(2);
            for (int i = brain.size() - 1; i >= 1; i--) {
                ArrayList<Neuron> layer = brain.get(i).neurons;
                for (int j = 0; j < layer.size(); j++) {
                    Neuron n = layer.get(j);
                    Iterator it = n.weights.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Neuron, Double> entry = (Map.Entry<Neuron, Double>) it.next();
                        Neuron from = entry.getKey();
                        double weight = entry.getValue();
                        if (!from.bias) {
                            n.weights.put(from, weight - n.delta * from.out);
                        } else {
                            n.weights.put(from, weight - n.delta);
                        }
                       // System.out.println(n.weights.get(from));
                    }
                }
            }
            step(inputs,1);
            cost = 0;

            for (int i = 0; i < outputs.size(); i++) {
                double o = outputs.get(i).out;
                double target = targets.get(i);
                cost += Math.pow(o - target,2);
            }
            //System.out.println(cost);
        }
    }
    public void propogate(int depth) {
        if (depth >= brain.size()) return;
        ArrayList<Neuron> neurons = brain.get(brain.size() - depth).neurons;
        ArrayList<Neuron> ahead = brain.get(brain.size() - depth + 1).neurons;
        for (int i = 0; i < neurons.size(); i++) {
            Neuron neuronJ = neurons.get(i);
            double o = neuronJ.out;
            double delta = o * (1 - o);
            double sum = 0;
            for (int k = 0; k < ahead.size(); k++) {
                Neuron neuronK = ahead.get(k);
                sum += neuronK.delta * neuronK.weights.get(neuronJ);
            }
            delta *= sum;
            neuronJ.delta = delta;
        }
        propogate(depth + 1);
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
