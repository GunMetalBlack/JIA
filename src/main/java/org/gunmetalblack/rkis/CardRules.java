package org.gunmetalblack.rkis;

public final class CardRules {
    private CardRules() { }
    public static int memoryCost(Card card) { return switch (card.memory()) { case "CONDUIT", "BRACE" -> 3; case "OVERCLOCK" -> 2; default -> 0; }; }
    public static String memoryTarget(Card card) { return switch (card.memory()) {
        case "CONDUIT" -> "Enemy; bonuses from adjacent Generic and PRIME";
        case "BRACE" -> "Self or one adjacent living ally";
        case "OVERCLOCK" -> "One adjacent living ally";
        default -> "None"; }; }
    public static String memoryDescription(Card card) { return switch (card.memory()) {
        case "CONDUIT" -> "Deal floor(ATK x 1.2) raw. +3 with adjacent Generic; consume PRIME +3.";
        case "BRACE" -> "Grant 6 shield, capped at 8. Shield expires after enemy response.";
        case "OVERCLOCK" -> "PRIME an adjacent ally. Its next damaging action gets +3 raw this phase.";
        default -> "No active Memory equipped."; }; }
    public static String passiveDescription(Card card) { return switch (card.passive()) {
        case "HOLD THE LINE" -> "Adjacent allies reduce their first post-armor hit each round by 2.";
        default -> "No passive Memory equipped."; }; }
    public static String traitDescription(Card card) { return switch (card.trait()) {
        case "RECKLESS" -> "First damaging action each encounter gains +1 raw.";
        case "LOYAL" -> "First shield given to an adjacent ally gains +1, still capped at 8.";
        case "FRUGAL" -> "First active Memory each encounter costs 1E less, minimum 1E.";
        case "RESOLUTE" -> "First incoming HP damage each encounter is reduced by 1 (lineage pending).";
        default -> "No identity trait."; }; }
}
