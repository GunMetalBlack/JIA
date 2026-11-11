package org.gunmetalblack.rkis;

public enum Encounter {
    DRONE("COMPLIANCE DRONE", 32, 1, 4, "Routine work order"),
    COLLECTOR("OVERTIME COLLECTOR", 38, 2, 5, "Extraction audit"),
    LEN("LEN BHAPIRO / THE LIE", 55, 2, 5, "Corporate narrative layer"),
    PAYWALL("PAYWALL / THE TRAP", 68, 3, 7, "Everything has a cost"),
    ARMSTRONG("ARMSTRONG / THE TRUTH", 80, 3, 10, "You are not playing. You are working.");

    private final String displayName, subtitle;
    private final int hp, defense, fibers;
    Encounter(String displayName, int hp, int defense, int fibers, String subtitle) {
        this.displayName = displayName; this.hp = hp; this.defense = defense; this.fibers = fibers; this.subtitle = subtitle;
    }
    public String displayName() { return displayName; }
    public int hp() { return hp; }
    public int defense() { return defense; }
    public int fibers() { return fibers; }
    public String subtitle() { return subtitle; }
}
