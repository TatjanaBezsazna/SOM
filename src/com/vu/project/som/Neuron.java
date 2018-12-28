package com.vu.project.som;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class Neuron {
    private double[] weights;
    private int row;
    private int column;
    private Set<Object> classList = new HashSet<>();

    public Neuron withRandomWeightsVectorSize(final int weightCount) {
        this.weights = new double[weightCount];
        for(int i = 0; i < weights.length; i++) {
            weights[i] = new Random().nextDouble();
        }
        return this;
    }

    public Neuron withRowNum(final int row) {
        this.row = row;
        return this;
    }

    public Neuron withColumnNum(final int column) {
        this.column = column;
        return this;
    }

    public double[] getWeights() {
        return weights;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void addClass(final Object className) {
        classList.add(className);
    }

    public String getClassesAsString() {
        StringBuilder classes = new StringBuilder();
        classList.stream().forEach(className -> classes.append(String.format("%.0f", (double) className)).append(", "));
        return classes.toString();
    }

    public String getWeightsAsString() {
        StringBuilder weightsList = new StringBuilder();
        Arrays.stream(weights).forEach(weight -> weightsList.append(String.format("%.2f", weight)).append(", "));
        return weightsList.toString();
    }
}
