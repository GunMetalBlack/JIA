package org.gunmetalblack.rkis;

public enum Lane {
    LEFT, CENTER, RIGHT;

    public boolean isAdjacentTo(Lane other) {
        return Math.abs(ordinal() - other.ordinal()) == 1;
    }
}
