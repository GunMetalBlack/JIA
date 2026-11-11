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
        int bonus = switch (encounter) { case DRONE -> 0; case COLLECTOR, LEN -> 2; case PAYWALL -> 3; case ARMSTRONG -> 4; };
        return switch (roundIndex % 3) {
            case 0 -> "HIT RIGHT " + (10 + bonus) + " RAW | LOCKED";
            case 1 -> "HIT CENTER " + (8 + bonus) + " RAW | LOCKED";
            default -> "SHIELD SELF " + (4 + bonus / 2);
        };
    }

    public Lane targetLane() { return roundIndex % 3 == 0 ? Lane.RIGHT : Lane.CENTER; }
    public int intentDamage() {
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
