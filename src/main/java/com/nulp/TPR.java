package com.nulp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static com.nulp.Candidates.*;

public class TPR {

    public static void main(String[] args) throws URISyntaxException, IOException {
        int votesCount = 5;

        ObjectMapper objectMapper = new ObjectMapper();

        Vote[] votes = objectMapper.readValue(getFileFromResource("input.json"), Vote[].class);
        //Borda

        System.out.println("Borda - ");

        int[] points = new int[3];

        for (int i = 0; i < votesCount; i++) {
            for (int j = 0; j < 3; j++) {
                switch (votes[i].getPriority().get(j)) {
                    case A:
                        points[0] += votes[i].getCount() * (3 - j);
                        break;

                    case B:
                        points[1] += votes[i].getCount() * (3 - j);
                        break;

                    case C:
                        points[2] += votes[i].getCount() * (3 - j);
                        break;

                    default:
                        break;
                }
            }
        }

        Integer winnerNumber = 0;

        System.out.println("Results: \n");
        for (int i = 0; i < 3; i++) {
            System.out.println(points[i]);
            winnerNumber = points[i] > points[winnerNumber] ? i : winnerNumber;
        }

        System.out.println("Winner by Borda - " + Candidates.forValue(winnerNumber));

        //Kondorse

        System.out.println("\n Kondorse: \n");

        int pointsMatrix[][] = new int[3][3];

        for (int v = 0; v < votesCount; v++) {
            for (int i = 0; i < 2; i++) {
                for (int j = i + 1; j < 3; j++) {
                    pointsMatrix[votes[v].getPriority().get(i).getValue()][votes[v].getPriority().get(j).getValue()] += votes[v].getCount();
                }
            }

        }

        System.out.println("Results: ");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(pointsMatrix[i][j] + " ");
            }
            System.out.println("\n");
        }

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                if (i == k) continue;
                if (pointsMatrix[i][k] > pointsMatrix[k][i]) {
                    winnerNumber = i;
                } else {
                    winnerNumber = null;
                    break;
                }
            }
            if (winnerNumber != null) break;
        }

        System.out.println("Winner by Kondorse: " + Candidates.forValue(winnerNumber));

    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = TPR.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }

    }
}
