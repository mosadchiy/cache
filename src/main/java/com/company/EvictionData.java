package com.company;

/**
 * MFU implementation of {@link IEvictionData}
 */
public class EvictionData implements IEvictionData<EvictionData> {
    private long timeUsed;

    public EvictionData() {
        update();
    }

    @Override
    public int compareTo(EvictionData o) {
        if (timeUsed < o.timeUsed) {
            return 1;
        } else if (timeUsed > o.timeUsed) {
            return -1;
        }
        return 0;
    }

    @Override
    public void update() {
        timeUsed = System.nanoTime();
    }
}
