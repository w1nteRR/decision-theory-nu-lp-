package com.nulp;

public enum Candidates {
    A(0), B(1), C(2);

    private int value;

    Candidates(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static Candidates forValue(Integer i) {
        if(i == 0) {
            return A;
        } else if(i == 1) {
            return B;
        } else if (i == 2) {
            return C;
        }
        return null;
    }
}
