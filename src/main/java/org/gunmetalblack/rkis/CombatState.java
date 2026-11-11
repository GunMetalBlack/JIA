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
    private Lane distortedLane;
    private boolean hiddenFees;
    private boolean memoryLoss;
    private int productivityTax;
    private Lane auditLane;
    private Lane interceptLane;
    private Lane interceptorLane;

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
    public Lane distortedLane() { return distortedLane; }
    public boolean hiddenFees() { return hiddenFees; }
    public boolean memoryLoss() { return memoryLoss; }
    public int productivityTax() { return productivityTax; }
    public int attackCost(Lane lane) {
        Card card = card(lane);
        return card == null ? 0 : cost(card, 2);
    }
    public int memoryCost(Lane lane) {
        Card card = card(lane);
        if (card == null) return 0;
        int base = Math.max(1, CardRules.memoryCost(card) - ((!card.traitUsed() && card.trait().equals("FRUGAL")) ? 1 : 0));
        return cost(card, base);
    }
    public String bossRule() {
        return switch (enemy.encounter()) {
            case LEN -> "MEDIA BIAS distorts HUD data; Inspect still reveals the truth.";
            case PAYWALL -> "HIDDEN FEES increase every paid action by 1E for one phase.";
            case ARMSTRONG -> "RKIS attacks identity: rising tax, degradation, loss, automation.";
            default -> "Authored lane intents repeat deterministically.";
        };
    }
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
        int listedCost = CardRules.memoryCost(card);
        int baseCost = Math.max(1, listedCost - ((!card.traitUsed() && card.trait().equals("FRUGAL")) ? 1 : 0));
        String invalid = validate(card, baseCost);
        if (invalid != null) return invalid;
        String memory = card.memory();
        if (memory.equals("CONDUIT") || memory.equals("DESPERATE PUSH") || memory.equals("FURNACE MEMORY")) {
            int cost = cost(card, baseCost);
            spend(card, cost);
            int raw;
            if (memory.equals("CONDUIT")) {
                raw = (int) Math.floor(card.attack() * 1.2);
                if (isAdjacentType(source, CardType.GENERIC)) raw += 3;
            } else if (memory.equals("FURNACE MEMORY")) raw = (int) Math.floor(card.attack() * 2.1);
            else raw = card.attack() + 5;
            raw += card.consumePrime() + card.useRecklessBonus();
            int damage = enemy.receiveDamage(raw);
            if (memory.equals("DESPERATE PUSH")) card.receiveTrueDamage(2);
            String result = card.name() + ": " + memory + " " + raw + " RAW, " + damage + " DAMAGE (" + cost + "E).";
            addLog(result);
            checkVictory();
            return result;
        }
        Card recipient = card(target);
        if (recipient == null || !recipient.isAlive()) return reject("MEMORY TARGET MUST BE A LIVING CARD.");
        boolean requiresNeighbor = memory.equals("BRACE") || memory.equals("OVERCLOCK") || memory.equals("TAKE THE HIT");
        if (requiresNeighbor && target != source && !source.isAdjacentTo(target)) return reject("MEMORY TARGET MUST BE SELF OR ADJACENT.");
        if ((memory.equals("OVERCLOCK") || memory.equals("TAKE THE HIT")) && target == source) return reject(memory + " REQUIRES AN ADJACENT ALLY.");
        if (memory.equals("POSITION SWAP") && (target == source || !source.isAdjacentTo(target))) return reject("POSITION SWAP REQUIRES AN ADJACENT DEPLOYED ALLY.");
        int cost = cost(card, baseCost);
        spend(card, cost);
        card.useFrugalDiscount();
        if (memory.equals("POSITION SWAP")) {
            Card other = formation.get(target);
            formation.put(source, other);
            formation.put(target, card);
            card.endEnemyResponse();
            other.endEnemyResponse();
            return record(card.name() + " SWAPS " + source + " <-> " + target + " WITH " + other.name() + " (" + cost + "E).");
        }
        if (memory.equals("BRACE")) {
            recipient.addShield(6 + card.useLoyalBonus(target != source));
            return record(card.name() + " -> " + recipient.name() + ": BRACE, SHIELD " + recipient.shield() + " (" + cost + "E).");
        }
        if (memory.equals("OVERCLOCK")) {
            recipient.prime();
            return record(card.name() + " -> " + recipient.name() + ": OVERCLOCK, PRIMED +3 RAW (" + cost + "E).");
        }
        if (memory.equals("AUDIT")) {
            auditLane = target;
            return record(card.name() + ": AUDIT PROTECTS " + target + " FROM 3 RAW (" + cost + "E).");
        }
        interceptLane = target;
        interceptorLane = source;
        return record(card.name() + " WILL INTERCEPT FOR " + recipient.name() + " (" + cost + "E).");
    }

    public String endPhase() {
        if (finished) return "THE ORDER IS COMPLETE. PRESS R TO RESTART.";
        hiddenFees = false;
        distortedLane = null;
        memoryLoss = false;
        if (enemy.isShieldIntent()) {
            enemy.gainShield();
            addLog(enemy.name() + " RAISES ITS DEFENSIVE VEIL.");
        } else if (enemy.isAttackIntent()) {
            Lane lane = enemy.encounter() == Encounter.PAYWALL && enemy.roundIndex() % 4 == 2
                    ? lowestHpLane() : enemy.targetLane();
            if (lane == interceptLane && interceptorLane != null && card(interceptorLane) != null && card(interceptorLane).isAlive()) {
                addLog(card(interceptorLane).name() + " INTERCEPTS THE ATTACK ON " + lane + ".");
                lane = interceptorLane;
            }
            Card target = card(lane);
            if (target == null || !target.isAlive()) {
                addLog(enemy.name() + " ATTACKS " + lane + ": MISS (LANE EMPTY).");
            } else {
                int raw = Math.max(0, enemy.intentDamage() - (lane == auditLane ? 3 : 0));
                int damage = target.receiveDamage(raw);
                addLog(enemy.name() + " -> " + target.name() + ": " + damage + " HP DAMAGE.");
                if (!target.isAlive()) {
                    addLog(target.name() + " BECOMES PLAUGE IN " + lane + ".");
                    if (!reserves.isEmpty()) replacementLane = lane;
                }
            }
        } else {
            resolveBossSystemIntent();
        }
        for (Card card : formation.values()) card.endEnemyResponse();
        auditLane = null;
        interceptLane = null;
        interceptorLane = null;
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

    private void resolveBossSystemIntent() {
        if (enemy.encounter() == Encounter.LEN) {
            distortedLane = Lane.RIGHT;
            addLog("MEDIA BIAS DISTORTS RIGHT-LANE DATA. INSPECT REVEALS ACTUAL VALUES.");
        } else if (enemy.encounter() == Encounter.PAYWALL) {
            hiddenFees = true;
            addLog("HIDDEN FEES: ALL PAID ACTIONS COST +1E THIS PHASE.");
        } else if (enemy.encounter() == Encounter.ARMSTRONG) {
            switch (enemy.roundIndex() % 4) {
                case 0 -> { productivityTax = Math.min(3, productivityTax + 1); addLog("PRODUCTIVITY TAX RISES TO +" + productivityTax + "E PER ACTION."); }
                case 1 -> {
                    for (Lane lane : Lane.values()) {
                        Card card = card(lane);
                        if (card != null && card.isAlive()) {
                            card.receiveTrueDamage(2);
                            if (!card.isAlive() && replacementLane == null && !reserves.isEmpty()) replacementLane = lane;
                        }
                    }
                    addLog("ENSHITTIFICATION: ALL DEPLOYED CARDS LOSE 2 TRUE HP.");
                }
                case 2 -> { memoryLoss = true; addLog("MEMORY LOSS: CARD IDENTITIES ARE HIDDEN. INSPECT TO REMEMBER."); }
                default -> {
                    Lane lane = firstLivingLane();
                    if (lane != null) card(lane).useAction();
                    addLog("AUTOMATION: RKIS SPENDS ONE ACTION FROM " + (lane == null ? "NOTHING" : card(lane).name()) + ".");
                }
            }
        }
    }

    private Lane lowestHpLane() {
        Lane result = null;
        for (Lane lane : Lane.values()) if (card(lane) != null && card(lane).isAlive()
                && (result == null || card(lane).hp() < card(result).hp())) result = lane;
        return result == null ? Lane.LEFT : result;
    }
    private Lane firstLivingLane() {
        for (Lane lane : Lane.values()) if (card(lane) != null && card(lane).isAlive()) return lane;
        return null;
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

    private int cost(Card card, int base) { return base + (card.actions() == 1 ? 1 : 0) + (hiddenFees ? 1 : 0) + productivityTax; }
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
