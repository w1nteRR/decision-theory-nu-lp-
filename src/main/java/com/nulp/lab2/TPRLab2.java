package com.nulp.lab2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class TPRLab2 {
    public static void main(String[] args) {

        IssueSolution[] solutions = new IssueSolution[2];
        solutions[0] = new IssueSolution();
        solutions[1] = new IssueSolution();
        float[] percent = new float[2];

        int best = 0;
        int max = 0;

        float buff;
        String[] inputElements = readLineFromFile();
        int counter = 0;
        for (int i = 0; i < 3; i++) {
            if (i != 2) {
                for (int j = 0; j < 5; j++) {
                    switch (j) {
                        case 0:
                            assert inputElements != null;
                            solutions[i].setCost(Integer.parseInt(inputElements[counter]));
                            break;

                        case 1:
                            solutions[i].setProfit(Integer.parseInt(inputElements[counter]));
                            break;

                        case 2:
                            solutions[i].setPercentage(Float.parseFloat(inputElements[counter]));
                            break;

                        case 3:
                            solutions[i].setLoss(Integer.parseInt(inputElements[counter]));
                            break;
                    }
                    counter++;
                }
            } else {
                for (int j = 0; j < 3; j++) {
                    switch (j) {
                        case 0:
                        case 2:
                            int index = j == 0 ? 0 : 1;
                            percent[index] = Float.parseFloat(inputElements[counter]);
                            break;

                    }
                    counter++;
                }
            }

        }

        System.out.println("Level tree:\n");
        System.out.println("First level :     (?)\n");
        System.out.println("                 /  |  \\\n");
        System.out.println("Second level:  [-1] {M1} {M2}\n");
        System.out.println("              /  \\\n");
        System.out.println("Third level:  {M1^} {M2^}\n\n");

        for (int i = 0; i < 4; i++) {

            buff = (i < 2 ? (solutions[i].getProfit() * 5) : (solutions[i - 2].getProfit() * 4));
            if (max < buff) {
                best = i;
                max = (int) buff;
            }

            switch (i) {
                case 0:
                case 1:
                    System.out.println("Solution M" + (1 + i) + ": \n");
                    System.out.println("\tCost: " + solutions[i].getCost());
                    System.out.println("\tProfit:" + solutions[i].getProfit() * 5 + " and " + Math.round(solutions[i].getPercentage() * 100) + "%");
                    System.out.println("\tLoss: " + solutions[i].getLoss() * 5 + " and " + Math.round((1. - solutions[i].getPercentage()) * 100) + "%");
                    break;

                case 2:
                case 3:
                    System.out.println("Solution M" + (-1 + i) + "^: \n");
                    System.out.println("\tCost: " + solutions[i - 2].getCost());
                    System.out.println("\tProfit:" + solutions[i - 2].getProfit() * 4 + " and " + Math.round(percent[i - 2] * 100) + "%");
                    System.out.println("\tLoss: " + solutions[i - 2].getLoss() * 4 + " and " + Math.round((1. - percent[i - 2]) * 100) + "%");
                    break;

                default:
                    break;
            }
            System.out.println();
        }

        String result = String.format("%d%c\0", best < 2 ? best + 1 : best - 1, best > 1 ? '^' : '\0');
        System.out.printf("Best solution is M%s\n", result);
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = TPRLab2.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }

    }

    private static String[] readLineFromFile() {
        try {
            List<String> allLines = Files.readAllLines(getFileFromResource("input.txt").toPath());
            return allLines.stream().findFirst().map(firstLine -> firstLine.split(" ")).orElse(null);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
