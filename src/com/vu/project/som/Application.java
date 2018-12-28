package com.vu.project.som;

public class Application {

    private static final double LEARNING_RATE = 0.8;
    private static final int GRID_BORDER_SIZE = 10;
    private static final int EPOCH_COUNT = 1500;

    public static void main(String[] args) {
        final double[][] inputVectors = InputVectorReader.read("C:/Users/tatjanab/Desktop/Mokslai/DNT/Iris1.csv");
        final int inputVectorSize = getInputVectorSize(inputVectors);
        final Neuron[] neurons = buildSOM(inputVectorSize);
        trainSOM(inputVectors, neurons);
        applySOM(inputVectors, neurons);
        visualizeSOM(neurons);
    }

    static Neuron[] buildSOM(final int weighVectorSize) {
        final Neuron[] neurons = new Neuron[GRID_BORDER_SIZE * GRID_BORDER_SIZE];
        int neuronsCount = 0;
        for (int row = 0; row < GRID_BORDER_SIZE; row++) {
            for (int column = 0; column < GRID_BORDER_SIZE; column++) {
                neurons[neuronsCount] = new Neuron().withRowNum(row)
                        .withColumnNum(column)
                        .withRandomWeightsVectorSize(weighVectorSize);

                neuronsCount++;
            }
        }
        return neurons;
    }

    static void trainSOM(final double[][] inputVectors, final Neuron[] neurons) {
        int currentEpoch = 1;
        while(currentEpoch <= EPOCH_COUNT) {
            double sumDistance = 0;
            for (double[] inputVector : inputVectors) {
                Neuron winnerNeuron = findWinnerNeuron(inputVector, neurons);
                adjustSOMWeights(inputVector, neurons, winnerNeuron, currentEpoch);
                sumDistance += calculateEuclideanDistance(inputVector, winnerNeuron.getWeights());
            }
            System.out.println("AVERAGE DIST: " + sumDistance / inputVectors.length);
            currentEpoch++;
        }
    }

    static Neuron findWinnerNeuron(final double[] inputVector, final Neuron[] neurons) {
        Neuron closestNeuron = null;
        double smallestEuclideanDistance = Double.MAX_VALUE;
        for(Neuron neuron : neurons) {
            double euclideanDistance = calculateEuclideanDistance(inputVector, neuron.getWeights());
            if(euclideanDistance < smallestEuclideanDistance) {
                closestNeuron = neuron;
                smallestEuclideanDistance = euclideanDistance;
            }
        }
        return closestNeuron;
    }

    static double calculateEuclideanDistance(final double[] inputVector, final double[] weightsVector) {
        double distanceSquareSum = 0;
        for(int i = 0; i < weightsVector.length; i++) {
            distanceSquareSum += (weightsVector[i] - inputVector[i]) * (weightsVector[i] - inputVector[i]);
        }
        return Math.sqrt(distanceSquareSum);
    }

    static void adjustSOMWeights(final double[] inputVector, final Neuron[] neurons,
                                 final Neuron winnerNeuron, final int currentEpoch) {
        for(Neuron neuron : neurons) {
            //distance squared values used in order to avoid square roots
            double distanceToWinnerSqrd = (winnerNeuron.getRow() - neuron.getRow()) * (winnerNeuron.getRow() - neuron.getRow())
                    + (winnerNeuron.getColumn() - neuron.getColumn()) * (winnerNeuron.getColumn() - neuron.getColumn());
            double widthSqrd = calculateNeighbourhoodRadius(currentEpoch) * calculateNeighbourhoodRadius(currentEpoch);

            //Adjust only those neurons weights that are within the current iteration neighbourhood radius
            if (distanceToWinnerSqrd <= widthSqrd) {
                //Gaussian coefficient applied
                double influence = Math.exp(-(distanceToWinnerSqrd) / (2 * widthSqrd));
                for (int i = 0; i < neuron.getWeights().length; i++) {
                    double adjustment = calculateLearningRate(currentEpoch) * influence * (inputVector[i] - neuron.getWeights()[i]);
                    neuron.getWeights()[i] = neuron.getWeights()[i] + adjustment;
                }
            }
        }
    }

    //Algorithm borrowed from here: http://www.ai-junkie.com/ann/som/som3.html
    //Neighbourhood radius should shrink over time - exponential decay function applied
    static double calculateNeighbourhoodRadius(final int currentEpoch) {
        //mapRadius = (Math.max(gridLength, gridWidth) / 2) in case of not square SOM.
        final double mapRadius = (double) GRID_BORDER_SIZE / 2;
        final double timeConstant = EPOCH_COUNT/Math.log(mapRadius);
        return mapRadius * Math.exp(-currentEpoch/timeConstant);
    }

    //Algorithm borrowed from here: http://www.ai-junkie.com/ann/som/som3.html
    //Learning rate should decrease over time - exponential decay function applied
    static double calculateLearningRate(final int currentEpoch) {
        return LEARNING_RATE * Math.exp((double) -currentEpoch/EPOCH_COUNT);
    }

    static void applySOM(final double[][] inputVectors, final Neuron[] neurons) {
        for(double[] inputVector : inputVectors) {
            //last vector element represents a class
            int lastInputVectorIndex = inputVector.length - 1;
            Neuron winnerNeuron = findWinnerNeuron(inputVector, neurons);
            winnerNeuron.addClass(inputVector[lastInputVectorIndex]);
        }
    }

    static void visualizeSOM(final Neuron[] neurons) {
        for (int i = 0; i < GRID_BORDER_SIZE * GRID_BORDER_SIZE; i++) {
            System.out.print(" | ");
            System.out.print("WEIGHTS: " + neurons[i].getWeightsAsString());
            System.out.print(" CLASSES: " + neurons[i].getClassesAsString());
            //end of one row
            if ((i + 1) % GRID_BORDER_SIZE == 0) {
                System.out.print(" | ");
                System.out.print("\n");
                System.out.print("\n");
            }
        }
    }

    static int getInputVectorSize(final double[][] inputVectors) {
        if (inputVectors.length == 0) {
            throw new IllegalArgumentException("Empty data set passed as input.");
        }
        //last vector element represents a class
        return inputVectors[0].length - 1;
    }
}
