package org.gunmetalblack.rkis;

public final class Card {
    private final String name;
    private final CardType type;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final String memory;
    private final String passive;
    private final String trait;
    private int hp;
    private int shield;
    private int actions;
    private boolean primed;
    private boolean coverAvailable;
    private boolean traitUsed;
    private boolean stubbornUsed;

    public Card(String name, CardType type, int maxHp, int attack, int defense,
                String memory, String passive, String trait) {
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.memory = memory;
        this.passive = passive;
        this.trait = trait;
    }

    public String name() { return name; }
    public CardType type() { return type; }
    public int hp() { return hp; }
    public int maxHp() { return maxHp; }
    public int attack() { return attack; }
    public int defense() { return defense; }
    public String memory() { return memory; }
    public String passive() { return passive; }
    public String trait() { return trait; }
    public int shield() { return shield; }
    public int actions() { return actions; }
    public boolean isAlive() { return hp > 0; }
    public boolean isPrimed() { return primed; }
    public void prime() { primed = true; }
    public int consumePrime() { if (!primed) return 0; primed = false; return 3; }
    public void addShield(int amount) { shield = Math.min(8, shield + amount); }
    public void useAction() { actions++; }
    public void beginRound(boolean hasSteelNeighbor) { actions = 0; coverAvailable = hasSteelNeighbor; }
    public void endEnemyResponse() { shield = 0; coverAvailable = false; primed = false; }
    public void startEncounter() { shield = 0; actions = 0; primed = false; coverAvailable = false; traitUsed = false; stubbornUsed = false; }
    public boolean traitUsed() { return traitUsed; }
    public int useRecklessBonus() {
        if (!traitUsed && trait.equals("RECKLESS")) { traitUsed = true; return 1; }
        return 0;
    }
    public int useLoyalBonus(boolean adjacentAlly) {
        if (!traitUsed && adjacentAlly && trait.equals("LOYAL")) { traitUsed = true; return 1; }
        return 0;
    }
    public int useFrugalDiscount() {
        if (!traitUsed && trait.equals("FRUGAL")) { traitUsed = true; return 1; }
        return 0;
    }

    public int receiveDamage(int raw) {
        int damage = raw == 0 ? 0 : Math.max(1, raw - defense);
        if (!stubbornUsed && passive.equals("STUBBORN") && damage > 0) {
            damage = Math.max(0, damage - 2);
            stubbornUsed = true;
        }
        if (coverAvailable) {
            damage = Math.max(0, damage - 2);
            coverAvailable = false;
        }
        int absorbed = Math.min(shield, damage);
        shield -= absorbed;
        int hpDamage = damage - absorbed;
        hp = Math.max(0, hp - hpDamage);
        return hpDamage;
    }
    public int receiveTrueDamage(int amount) {
        int damage = Math.min(hp, Math.max(0, amount));
        hp -= damage;
        return damage;
    }
}
