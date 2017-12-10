import java.io.Serializable;
import java.util.ArrayList;

public class Layer implements Serializable {
    public ArrayList<Neuron> neurons;
    public Layer() {
        neurons = new ArrayList<Neuron>();
    }
}
