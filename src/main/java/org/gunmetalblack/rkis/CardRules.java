package org.gunmetalblack.rkis;

public final class CardRules {
    private CardRules() { }
    public static int memoryCost(Card card) { return switch (card.memory()) {
        case "CONDUIT", "BRACE", "TAKE THE HIT", "DESPERATE PUSH" -> 3;
        case "OVERCLOCK", "AUDIT", "POSITION SWAP" -> 2;
        case "FURNACE MEMORY" -> 4;
        default -> 0; }; }
    public static String memoryTarget(Card card) { return switch (card.memory()) {
        case "CONDUIT" -> "Enemy; bonuses from adjacent Generic and PRIME";
        case "BRACE" -> "Self or one adjacent living ally";
        case "OVERCLOCK" -> "One adjacent living ally";
        case "AUDIT" -> "Any occupied lane";
        case "TAKE THE HIT" -> "One adjacent living ally";
        case "POSITION SWAP" -> "One adjacent deployed living ally";
        case "DESPERATE PUSH", "FURNACE MEMORY" -> "Enemy";
        default -> "None"; }; }
    public static String memoryDescription(Card card) { return switch (card.memory()) {
        case "CONDUIT" -> "Deal floor(ATK x 1.2) raw. +3 with adjacent Generic; consume PRIME +3.";
        case "BRACE" -> "Grant 6 shield, capped at 8. Shield expires after enemy response.";
        case "OVERCLOCK" -> "PRIME an adjacent ally. Its next damaging action gets +3 raw this phase.";
        case "AUDIT" -> "Chosen lane takes -3 raw from the next announced attack this round.";
        case "TAKE THE HIT" -> "Intercept the next single-target attack against an adjacent ally.";
        case "POSITION SWAP" -> "Exchange this card's lane with an adjacent deployed ally immediately.";
        case "DESPERATE PUSH" -> "Attack for ATK +5 raw, then suffer 2 true recoil HP.";
        case "FURNACE MEMORY" -> "A 2.1x ATK burst trained from a smelter's memory.";
        default -> "No active Memory equipped."; }; }
    public static String passiveDescription(Card card) { return switch (card.passive()) {
        case "HOLD THE LINE" -> "Adjacent allies reduce their first post-armor hit each round by 2.";
        case "STUBBORN" -> "First hit taken by this card each encounter is reduced by 2.";
        default -> "No passive Memory equipped."; }; }
    public static String traitDescription(Card card) { return switch (card.trait()) {
        case "RECKLESS" -> "First damaging action each encounter gains +1 raw.";
        case "LOYAL" -> "First shield given to an adjacent ally gains +1, still capped at 8.";
        case "FRUGAL" -> "First active Memory each encounter costs 1E less, minimum 1E.";
        case "RESOLUTE" -> "First incoming HP damage each encounter is reduced by 1 (lineage pending).";
        default -> "No identity trait."; }; }
}
