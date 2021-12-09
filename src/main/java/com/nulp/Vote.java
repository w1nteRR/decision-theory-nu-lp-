package com.nulp;

import java.util.List;

public class Vote {
    private int count;
    private List<Candidates> priority;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Candidates> getPriority() {
        return priority;
    }

    public void setPriority(List<Candidates> priority) {
        this.priority = priority;
    }
}
