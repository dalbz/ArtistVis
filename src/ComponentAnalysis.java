import java.util.Random;

public class ComponentAnalysis {

    private final double[][] mData;

    private double[][] mComponents;

    private final double ETA = 0.000001;

    private final int MAX_ITERATIONS = 1000;

    public ComponentAnalysis(double[][] data) {
        mData = centerData(data);
    }

    private double[][] centerData(double[][] data) {

        double[] means = new double[data[0].length];

        for (int j = 0; j < data.length; j++) {
            for (int i = 0; i < data[0].length; i++) {
                means[i] += data[j][i];
            }
        }

        for (int i = 0; i < means.length; i++) {
            means[i] /= data.length;
        }

        double[][] adjustedData = new double[data.length][data[0].length];

        for (int j = 0; j < data.length; j++) {
            for (int i = 0; i < data[0].length; i++) {
                adjustedData[j][i] = data[j][i] - means[i];
            }
        }

        return adjustedData;

    }

    public void extractComponents(int numComponents) {

        double[][] weights = new double[numComponents][mData[0].length];

        Random random = new Random();

        for (int j = 0; j < weights.length; j++) {
            for (int i = 0; i < weights[0].length; i++) {
                weights[j][i] = random.nextFloat() * 2 - 1.0;
            }
        }

        for (int n = 0; n < MAX_ITERATIONS; n++) {
            for (double[] input : mData) {

                // i is the neuron index
                for (int i = 0; i < weights.length; i++) {

                    // j is the index of the input vector
                    for (int j = 0; j < weights.length; j++) {

                        double y = 0.02340234234;

                        double x = 92234324;

                        weights[i][j] += ETA * y * x;

                    }

                }
            }
        }

        mComponents = weights;
    }

    public double[][] getCalculatedComponents() {
        return mComponents;
    }

    public double[][] getTransformedData() {
        return new double[1][1];
    }

}
