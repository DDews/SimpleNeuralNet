import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class NetTest {
    public static void main(String[] args) {
        Vector<Integer> layers = new Vector<Integer>();
        layers.add(3);
        layers.add(3);
        layers.add(1);
        Scanner in = new Scanner(System.in);
        System.out.println("Creating a neural net with layers: " + layers);


        Net brain = new Net(layers,0,0,0,1);


        ArrayList<Double> inputVals = new ArrayList<Double>();
        while (inputVals.size() != brain.brain.get(0).neurons.size() - 1) {
            System.out.println("Enter a " + layers.get(0) + " dimensional vector, values separated by commas for inputs: ");
            String input = in.nextLine();
            String[] words = input.split(",\\s*");
            inputVals = new ArrayList<Double>();
            try {
                for (String word : words) {
                    double val = Double.parseDouble(word);
                    if (val < 0 || val > 1) throw new Exception("Invalid, not clamped input.");
                    inputVals.add(val);
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Make sure to use double values clamped between 0 and 1.");
            }
        }
        brain.step(inputVals,1);

        System.out.println("Output vector of Neural Net using input vector " + inputVals + ": ");
        System.out.println(brain.brain.get(brain.brain.size() - 1).neurons);

        System.out.println("Enter a " + brain.brain.get(brain.brain.size() - 1).neurons.size() + " dimensional vector, values separated by commas for target output: ");
        ArrayList<Double> targets = new ArrayList<Double>();
        while (targets.size() != brain.brain.get(brain.brain.size() - 1).neurons.size()) {
            String input = in.nextLine();
            String[] words = input.split(",\\s*");
            targets = new ArrayList<Double>();
            try {
                for (String word : words) {
                    double val = Double.parseDouble(word);
                    if (val < 0 || val > 1) throw new Exception("Invalid, not clamped input.");
                    targets.add(val);
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Make sure to use double values clamped between 0 and 1.");
            }
        }

        brain.backPropogate(targets);
        brain.step(inputVals,1);
        System.out.println("Back propogate succeeded to reduce error within 0.005");
        System.out.println("Output of network with initial given input vector " + inputVals + ":");
        System.out.println(brain.brain.get(brain.brain.size() - 1).neurons);
        ArrayList<Double> newTarget = new ArrayList<Double>();
        for (int i = 0; i < targets.size(); i++) {
            newTarget.add(1 - targets.get(i));
        }

        System.out.println("This demonstrates Hebbian learning, as back propagation nudges weights in directions proportionate to other neurons with similar activations.");
        System.out.println();

        System.out.println("Demonstrating forgetting....");
        System.out.println();

        System.out.println("Back propagating with random targets: " + newTarget);
        brain.backPropogate(newTarget);
        System.out.println("Done.");
        System.out.println("Output vector of initial inputs " + inputVals + ":");
        brain.step(inputVals,1);
        System.out.println(brain.brain.get(brain.brain.size() - 1).neurons);

        System.out.println();
        System.out.println("This demonstrates forgetting, as the activation of the output is now different with the same inputs.");
    }
}
