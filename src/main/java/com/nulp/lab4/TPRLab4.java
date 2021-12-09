package com.nulp.lab4;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TPRLab4 {
    public static void main(String[] args) {
        List<Float> weights = new ArrayList<>();
        List<Integer[]> points = IntStream.range(0, 5).mapToObj(i -> new Integer[7]).collect(Collectors.toList());
        Double[] summ = new Double[7];
        List<String> parameters = Arrays.asList(
                "Надійність",
                "Швидкодія",
                "Розмір",
                "Якість",
                "Ціна"
        );
        List<String> choices = Arrays.asList(
                "Mi 5",
                "Redmi note",
                "Samsung A5",
                "Mi6",
                "Iphone 12",
                "Iphone 11",
                "Pacco"
        );

        int best = 0;
        double max = 0;
        int counter = 0;
        List<String[]> lines = readLinesFromFile();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 8; j++) {
                if (j == 0) {
                    weights.add(Float.parseFloat(lines.get(i)[j]));
                } else {
                    points.get(i)[j - 1] = Integer.parseInt(lines.get(i)[j]);
                }
            }
        }
        for (int i = 0; i < 7; i++) {
            summ[i] = 0.0;
            for (int j = 0; j < 5; j++) {
                summ[i] += weights.get(j) * points.get(j)[i];
            }
            if (summ[i] > max) {
                max = summ[i];
                best = i;
            }
        }


        System.out.print("\n\n| № \t\t\t| Criteria \t\t\t\t\t| Weight \t\t\t\t|    Mi 5  \t | Redmi note| Samsung A5 | Mi6      | Iphone 12 | Iphone 11 |   Pacco   |\n");

        for (int i = 0; i < 69; i++) System.out.print("-");
        System.out.println();

        for (int i = 0; i < 5; i++) {
            System.out.printf("|\t\t %d \t\t|\t\t %-13s \t\t|\t\t %6.1f \t\t|\t\t", i, parameters.get(i), weights.get(i));
            for (int j = 0; j < 7; j++) {
                System.out.printf(" %3d |\t\t", points.get(i)[j]);
            }
            System.out.println();
        }

        for (int i = 0; i < 69; i++) System.out.print("-");
        System.out.println();

        System.out.print("| Summ \t\t\t \t\t\t \t\t\t \t\t\t \t\t       |");

        for (int j = 0; j < 7; j++) {
            if (best == j) {
                System.out.printf("best - [%3.1f]|", summ[j]);
            } else {
                System.out.printf("\t  %3.1f  \t |", summ[j]);
            }
        }

        System.out.println("\n\n");

        System.out.println(" ---------- Best choice is - " + choices.get(best) + " ------------");
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = TPRLab4.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }

    }

    private static List<String[]> readLinesFromFile() {
        try {
            List<String> allLines = Files.readAllLines(getFileFromResource("lab4_input.txt").toPath());
            return allLines.stream().map(firstLine -> firstLine.split(" ")).collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
