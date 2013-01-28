import java.util.Random;

import Jama.Matrix;

public class ComponentAnalysis {

    private final double[][] mData;

    private double[] mMeans;

    private double[][] mComponents;

    private final double ETA = 0.0001;

    private final int MAX_ITERATIONS = 3000;

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

        mMeans = means;

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

        Random random = new Random(System.currentTimeMillis());

        for (int j = 0; j < weights.length; j++) {
            for (int i = 0; i < weights[0].length; i++) {
                weights[j][i] = random.nextFloat();
            }
        }

        for (int i = 0; i < numComponents; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(weights[i][j] + " ");
            }
            System.out.println();
        }

        double[] y = new double[numComponents];

        for (int n = 0; n < MAX_ITERATIONS; n++) {

            for (double[] input : mData) {

                for (int i = 0; i < numComponents; i++) {
                    for (int j = 0; j < input.length; j++) {
                        y[i] += weights[i][j] * input[j];
                    }
                }

                for (int i = 0; i < numComponents; i++) {
                    for (int j = 0; j < input.length; j++) {

                        double inhibitSum = 0;

                        for (int k = 0; k <= i; k++) {
                            inhibitSum += y[k] * weights[k][j];
                        }

                        weights[i][j] += ETA * y[i] * (input[j] - inhibitSum);
                    }
                }
            }


            if (n % 100 == 0) {
                System.out.println(n);

                for (int i = 0; i < numComponents; i++) {
                    for (int j = 0; j < 5; j++) {
                        System.out.print(weights[i][j] + " ");
                    }
                    System.out.println();
                }
            }

        }

        System.out.println();

        for (int i = 0; i < numComponents; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(weights[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();


        mComponents = weights;

    }

    public double[][] getCalculatedComponents() {
        return mComponents;
    }

    public double[][] getTransformedData() {
        double[][] output = new double[mData.length][mComponents.length];

        Matrix components = new Matrix(mComponents);

        for (int i = 0; i < mData.length; i++) {

            double[][] row = new double[1][mData[0].length];
            row[0] = mData[i];
            Matrix input = new Matrix(row);

            Matrix transformed = input.times(components.transpose());

            output[i] = transformed.getRowPackedCopy();

        }

        return output;
    }

}
