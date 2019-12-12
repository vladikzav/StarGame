package com.star.app.game.helpers;

public class Counter {
    private int current;
    private int max;

    public int getCurrent() {
        return current;
    }

    public int getMax() {
        return max;
    }

    public void increaseMax(int amount) {
        max += amount;
    }

    public void fill() {
        current = max;
    }

    public int increase(int amount) {
        int oldValue = current;
        current += amount;
        if (current > max) {
            current = max;
        }
        return current - oldValue;
    }

    public void dec() {
        current--;
    }

    public void decrease(int amount) {
        current -= amount;
    }

    public boolean isAboveZero() {
        return current > 0;
    }

    public Counter(int max, boolean maxed) {
        this.max = max;
        if (maxed) {
            this.current = max;
        }
    }
}
