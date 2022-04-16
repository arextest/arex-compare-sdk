package io.arex.diff.model.key;

public class RatioEntity {

    private int firstIndex;

    private int secondIndex;

    private float ratio;

    public RatioEntity() {
    }

    public RatioEntity(int firstIndex, int secondInedx, float ratio) {
        this.firstIndex = firstIndex;
        this.secondIndex = secondInedx;
        this.ratio = ratio;
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public int getSecondIndex() {
        return secondIndex;
    }

    public float getRatio() {
        return ratio;
    }
}