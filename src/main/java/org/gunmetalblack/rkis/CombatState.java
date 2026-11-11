package org.gunmetalblack.rkis;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public final class CombatState {
    private final Map<Lane, Card> formation = new EnumMap<>(Lane.class);
    private final Enemy enemy;
    private final List<Card> reserves = new ArrayList<>();
    private final Deque<String> log = new ArrayDeque<>();
    private int energy = 8;
    private int actions;
    private int round = 1;
    private boolean finished;
    private boolean victorious;
    private Lane replacementLane;

    public CombatState() { this(Encounter.DRONE, new Roster()); }
    public CombatState(Encounter encounter) { this(encounter, new Roster()); }
    public CombatState(Encounter encounter, Roster roster) {
        enemy = new Enemy(encounter);
        List<Card> living = roster.cards().stream().filter(Card::isAlive).toList();
        for (Card card : living) card.startEncounter();
        for (int i = 0; i < Math.min(3, living.size()); i++) formation.put(Lane.values()[i], living.get(i));
        for (int i = 3; i < living.size(); i++) reserves.add(living.get(i));
        beginRound();
        addLog("ORDER RECEIVED: " + encounter.displayName() + ". READ INTENT.");
    }

    public Card card(Lane lane) { return formation.get(lane); }
    public Enemy enemy() { return enemy; }
    public int energy() { return energy; }
    public int actions() { return actions; }
    public int round() { return round; }
    public boolean finished() { return finished; }
    public boolean victorious() { return victorious; }
    public boolean needsReplacement() { return replacementLane != null; }
    public Lane replacementLane() { return replacementLane; }
    public List<Card> reserves() { return List.copyOf(reserves); }
    public String[] logLines() { return log.toArray(String[]::new); }

    public String attack(Lane source) {
        Card card = card(source);
        String invalid = validate(card, 2);
        if (invalid != null) return invalid;
        int cost = cost(card, 2);
        spend(card, cost);
        int raw = card.attack() + card.consumePrime() + card.useRecklessBonus();
        int damage = enemy.receiveDamage(raw);
        String result = card.name() + " ATTACKS: " + raw + " RAW, " + damage + " DAMAGE (" + cost + "E).";
        addLog(result);
        checkVictory();
        return result;
    }

    public String guard(Lane source) {
        Card card = card(source);
        String invalid = validate(card, 2);
        if (invalid != null) return invalid;
        int cost = cost(card, 2);
        spend(card, cost);
        card.addShield(5);
        String result = card.name() + " GUARDS: SHIELD " + card.shield() + " (" + cost + "E).";
        addLog(result);
        return result;
    }

    public String memory(Lane source, Lane target) {
        Card card = card(source);
        int listedCost = source == Lane.CENTER ? 3 : source == Lane.LEFT ? 3 : 2;
        int baseCost = Math.max(1, listedCost - ((!card.traitUsed() && card.trait().equals("FRUGAL")) ? 1 : 0));
        String invalid = validate(card, baseCost);
        if (invalid != null) return invalid;
        if (source == Lane.LEFT) {
            int cost = cost(card, baseCost);
            spend(card, cost);
            int raw = (int) Math.floor(card.attack() * 1.2);
            if (isAdjacentType(source, CardType.GENERIC)) raw += 3;
            raw += card.consumePrime() + card.useRecklessBonus();
            int damage = enemy.receiveDamage(raw);
            String result = "SPARK: CONDUIT " + raw + " RAW, " + damage + " DAMAGE (" + cost + "E).";
            addLog(result);
            checkVictory();
            return result;
        }
        Card recipient = card(target);
        if (recipient == null || !recipient.isAlive() || (target != source && !source.isAdjacentTo(target))) {
            return reject("MEMORY TARGET MUST BE SELF OR ADJACENT LIVING CARD.");
        }
        if (source == Lane.RIGHT && target == source) {
            return reject("OVERCLOCK REQUIRES AN ADJACENT ALLY.");
        }
        int cost = cost(card, baseCost);
        spend(card, cost);
        card.useFrugalDiscount();
        if (source == Lane.CENTER) {
            recipient.addShield(6 + card.useLoyalBonus(target != source));
            return record("RIVET -> " + recipient.name() + ": BRACE, SHIELD " + recipient.shield() + " (" + cost + "E).");
        }
        recipient.prime();
        return record("CLERK -> " + recipient.name() + ": OVERCLOCK, PRIMED +3 RAW (" + cost + "E).");
    }

    public String endPhase() {
        if (finished) return "THE ORDER IS COMPLETE. PRESS R TO RESTART.";
        if (enemy.roundIndex() % 3 == 2) {
            enemy.gainShield();
            addLog("DRONE GAINS 4 SHIELD.");
        } else {
            Lane lane = enemy.targetLane();
            Card target = card(lane);
            if (target == null || !target.isAlive()) {
                addLog("DRONE ATTACKS " + lane + ": MISS (LANE EMPTY).");
            } else {
                int damage = target.receiveDamage(enemy.intentDamage());
                addLog("DRONE -> " + target.name() + ": " + damage + " HP DAMAGE.");
                if (!target.isAlive()) {
                    addLog(target.name() + " BECOMES PLAUGE IN " + lane + ".");
                    if (!reserves.isEmpty()) replacementLane = lane;
                }
            }
        }
        for (Card card : formation.values()) card.endEnemyResponse();
        if (formation.values().stream().noneMatch(Card::isAlive) && reserves.stream().noneMatch(Card::isAlive)) {
            finished = true;
            return record("ALL CARDS PLAUGE. RECOVERY ATTEMPT FAILED.");
        }
        enemy.advance();
        round++;
        energy = Math.min(9, energy + 6);
        beginRound();
        return "ENEMY RESPONSE RESOLVED. ROUND " + round + ".";
    }

    public String deployReserve(int index) {
        if (replacementLane == null) return "NO LANE NEEDS A RESERVE.";
        if (index < 0 || index >= reserves.size()) return "THAT RESERVE SLOT IS EMPTY.";
        Card incoming = reserves.remove(index);
        formation.put(replacementLane, incoming);
        String result = incoming.name() + " DEPLOYS TO " + replacementLane + ". THE PLAUGE HUSK IS RETAINED.";
        replacementLane = null;
        beginRound();
        return record(result);
    }

    private void beginRound() {
        actions = 0;
        for (Lane lane : Lane.values()) {
            Card card = card(lane);
            boolean steelNeighbor = false;
            for (Lane other : Lane.values()) {
                Card neighbor = card(other);
                if (lane.isAdjacentTo(other) && neighbor != null && neighbor.isAlive() && neighbor.type() == CardType.STEEL) {
                    steelNeighbor = true;
                }
            }
            card.beginRound(steelNeighbor);
        }
    }

    private boolean isAdjacentType(Lane source, CardType type) {
        for (Lane lane : Lane.values()) {
            Card candidate = card(lane);
            if (source.isAdjacentTo(lane) && candidate != null && candidate.isAlive() && candidate.type() == type) return true;
        }
        return false;
    }

    private int cost(Card card, int base) { return base + (card.actions() == 1 ? 1 : 0); }
    private String validate(Card card, int base) {
        if (finished) return "ORDER COMPLETE. PRESS R TO RESTART.";
        if (replacementLane != null) return "CHOOSE A RESERVE FOR THE EMPTY " + replacementLane + " LANE.";
        if (card == null || !card.isAlive()) return "THAT CARD IS PLAUGE.";
        if (actions >= 4) return "TEAM ACTION LIMIT REACHED. END PHASE.";
        if (card.actions() >= 2) return card.name() + " HAS ALREADY ACTED TWICE.";
        int cost = cost(card, base);
        return energy < cost ? "NOT ENOUGH ENERGY: NEED " + cost + "E." : null;
    }
    private void spend(Card card, int cost) { energy -= cost; actions++; card.useAction(); }
    private String reject(String text) { addLog("INVALID: " + text); return text; }
    private String record(String text) { addLog(text); return text; }
    private void checkVictory() { if (!enemy.isAlive()) { finished = true; victorious = true; addLog(enemy.name() + " DEFEATED. +" + enemy.encounter().fibers() + " FIBERS."); } }
    private void addLog(String text) { log.addFirst(text); while (log.size() > 4) log.removeLast(); }
}
