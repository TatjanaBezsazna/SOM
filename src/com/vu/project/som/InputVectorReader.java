package com.vu.project.som;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class InputVectorReader {

    private static final String csvSeparator = ",";

    static double[][] read(final String fileLocation) {
        final List<double[]> listOfRows = new ArrayList<>();
        double[][] inputVectorsArr = new double[0][0];
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(fileLocation))) {
            int oneRowSize = 0;
            while ((line = br.readLine()) != null) {
               String[] input = line.replaceAll("\"","")
                       .replaceAll("\uFEFF", "")
                       .split(csvSeparator);

               double[] inputInDoubles = Arrays.stream(input)
                       .mapToDouble(Double::parseDouble)
                       .toArray();

               listOfRows.add(inputInDoubles);
               if(oneRowSize == 0) {
                   oneRowSize = inputInDoubles.length;
               }
            }

            inputVectorsArr = new double[listOfRows.size()][oneRowSize];
            for(int i = 0; i < listOfRows.size(); i++) {
                inputVectorsArr[i] = listOfRows.get(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  inputVectorsArr;
    }
}
