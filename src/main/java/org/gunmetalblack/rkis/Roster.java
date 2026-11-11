package org.gunmetalblack.rkis;

import java.util.List;

public final class Roster {
    private final List<Card> cards;

    public Roster() {
        cards = List.of(
                new Card("SPARK", CardType.MAGIE, 16, 8, 0, "CONDUIT", "NONE", "RECKLESS"),
                new Card("RIVET", CardType.STEEL, 26, 5, 3, "BRACE", "HOLD THE LINE", "LOYAL"),
                new Card("CLERK", CardType.GENERIC, 20, 6, 1, "OVERCLOCK", "NONE", "FRUGAL"),
                new Card("SHIFT", CardType.GENERIC, 22, 6, 2, "OVERCLOCK", "NONE", "RESOLUTE"),
                new Card("INTERN", CardType.STEEL, 20, 4, 2, "BRACE", "HOLD THE LINE", "LOYAL")
        );
    }

    public List<Card> cards() { return cards; }
    public boolean hasLivingCard() { return cards.stream().anyMatch(Card::isAlive); }
}
