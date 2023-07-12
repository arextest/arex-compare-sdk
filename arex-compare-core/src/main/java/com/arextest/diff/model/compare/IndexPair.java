package com.arextest.diff.model.compare;

public class IndexPair {
    int leftIndex;
    int rightIndex;

    public IndexPair(int leftIndex, int rightIndex) {
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
    }

    @Override
    public int hashCode() {
        int result = leftIndex;
        return result + 31 * rightIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IndexPair) {
            IndexPair that = (IndexPair) obj;
            return (leftIndex == that.leftIndex) && (rightIndex == that.rightIndex);
        }
        return false;
    }
}