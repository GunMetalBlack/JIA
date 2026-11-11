package org.gunmetalblack.rkis;

public final class Enemy {
    private final Encounter encounter;
    private int hp;
    private int shield;
    private int roundIndex;

    public Enemy(Encounter encounter) { this.encounter = encounter; this.hp = encounter.hp(); }
    public Encounter encounter() { return encounter; }
    public String name() { return encounter.displayName(); }
    public int hp() { return hp; }
    public int maxHp() { return encounter.hp(); }
    public int defense() { return encounter.defense(); }
    public int shield() { return shield; }
    public boolean isAlive() { return hp > 0; }
    public int roundIndex() { return roundIndex; }

    public String intentText() {
        if (encounter == Encounter.LEN) return switch (roundIndex % 4) {
            case 0 -> "SOUND BITE: HIT CENTER 10 RAW";
            case 1 -> "MEDIA BIAS: DISTORT RIGHT CARD DATA";
            case 2 -> "SPIN: HIT RIGHT 12 RAW";
            default -> "CORPORATE VEIL: SHIELD SELF 7";
        };
        if (encounter == Encounter.PAYWALL) return switch (roundIndex % 4) {
            case 0 -> "HIDDEN FEES: ACTIONS COST +1E NEXT PHASE";
            case 1 -> "EXTRACTION: HIT LEFT 14 RAW";
            case 2 -> "PAY A CARD: HIT LOWEST HP 12 RAW";
            default -> "FORECAST ECONOMY: SHIELD SELF 8";
        };
        if (encounter == Encounter.ARMSTRONG) return switch (roundIndex % 4) {
            case 0 -> "PRODUCTIVITY TAX: PERMANENT COSTS RISE";
            case 1 -> "ENSHITTIFICATION: ALL CARDS LOSE 2 HP";
            case 2 -> "MEMORY LOSS: HIDE CARD INFORMATION";
            default -> "AUTOMATION: RKIS CONTROLS A CARD";
        };
        int bonus = switch (encounter) { case DRONE -> 0; case COLLECTOR, LEN -> 2; case PAYWALL -> 3; case ARMSTRONG -> 4; };
        return switch (roundIndex % 3) {
            case 0 -> "HIT RIGHT " + (10 + bonus) + " RAW | LOCKED";
            case 1 -> "HIT CENTER " + (8 + bonus) + " RAW | LOCKED";
            default -> "SHIELD SELF " + (4 + bonus / 2);
        };
    }

    public boolean isShieldIntent() {
        return (encounter == Encounter.LEN || encounter == Encounter.PAYWALL) ? roundIndex % 4 == 3
                : encounter != Encounter.ARMSTRONG && roundIndex % 3 == 2;
    }
    public boolean isAttackIntent() {
        if (encounter == Encounter.LEN) return roundIndex % 4 == 0 || roundIndex % 4 == 2;
        if (encounter == Encounter.PAYWALL) return roundIndex % 4 == 1 || roundIndex % 4 == 2;
        if (encounter == Encounter.ARMSTRONG) return false;
        return roundIndex % 3 != 2;
    }
    public Lane targetLane() {
        if (encounter == Encounter.LEN) return roundIndex % 4 == 0 ? Lane.CENTER : Lane.RIGHT;
        if (encounter == Encounter.PAYWALL) return roundIndex % 4 == 1 ? Lane.LEFT : Lane.CENTER;
        return roundIndex % 3 == 0 ? Lane.RIGHT : Lane.CENTER;
    }
    public int intentDamage() {
        if (encounter == Encounter.LEN) return roundIndex % 4 == 0 ? 10 : 12;
        if (encounter == Encounter.PAYWALL) return roundIndex % 4 == 1 ? 14 : 12;
        int bonus = switch (encounter) { case DRONE -> 0; case COLLECTOR, LEN -> 2; case PAYWALL -> 3; case ARMSTRONG -> 4; };
        return (roundIndex % 3 == 0 ? 10 : 8) + bonus;
    }

    public int receiveDamage(int raw) {
        int damage = raw == 0 ? 0 : Math.max(1, raw - defense());
        int absorbed = Math.min(shield, damage);
        shield -= absorbed;
        damage -= absorbed;
        hp = Math.max(0, hp - damage);
        return damage;
    }

    public void gainShield() { shield += 4 + (encounter.ordinal() / 2); }
    public void advance() { roundIndex++; }
}
