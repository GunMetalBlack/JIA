package org.gunmetalblack.rkis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CombatStateTest {
    @Test void onlyNeighboringLanesAreAdjacent() {
        assertTrue(Lane.LEFT.isAdjacentTo(Lane.CENTER));
        assertTrue(Lane.CENTER.isAdjacentTo(Lane.RIGHT));
        assertFalse(Lane.LEFT.isAdjacentTo(Lane.RIGHT));
    }

    @Test void sharedEnergyAndRepeatSurchargeAreApplied() {
        CombatState state = new CombatState();
        state.attack(Lane.LEFT);
        assertEquals(6, state.energy());
        state.attack(Lane.LEFT);
        assertEquals(3, state.energy());
        String rejected = state.attack(Lane.LEFT);
        assertTrue(rejected.contains("TWICE"));
        assertEquals(3, state.energy());
    }

    @Test void invalidMemoryTargetDoesNotMutateState() {
        CombatState state = new CombatState();
        String result = state.memory(Lane.RIGHT, Lane.LEFT);
        assertTrue(result.contains("ADJACENT"));
        assertEquals(8, state.energy());
        assertEquals(0, state.actions());
    }

    @Test void energyRegeneratesAndCapsAtNine() {
        CombatState state = new CombatState();
        state.attack(Lane.LEFT);
        state.endPhase();
        assertEquals(9, state.energy());
    }

    @Test void guardAndSteelCoverReduceIncomingDamage() {
        CombatState state = new CombatState();
        Card clerk = state.card(Lane.RIGHT);
        state.guard(Lane.RIGHT);
        state.endPhase();
        assertEquals(18, clerk.hp());
        assertEquals(0, clerk.shield());
    }

    @Test void legacyTraitsTriggerOnlyOncePerEncounter() {
        CombatState reckless = new CombatState();
        reckless.attack(Lane.LEFT);
        assertEquals(24, reckless.enemy().hp());
        reckless.attack(Lane.LEFT);
        assertEquals(17, reckless.enemy().hp());

        CombatState loyal = new CombatState();
        loyal.memory(Lane.CENTER, Lane.RIGHT);
        assertEquals(7, loyal.card(Lane.RIGHT).shield());

        CombatState frugal = new CombatState();
        frugal.memory(Lane.RIGHT, Lane.CENTER);
        assertEquals(7, frugal.energy());
        frugal.memory(Lane.RIGHT, Lane.CENTER);
        assertEquals(4, frugal.energy());
    }

    @Test void deathCreatesVacancyAndChosenReserveDeploys() {
        Roster roster = new Roster();
        CombatState state = new CombatState(Encounter.DRONE, roster);
        state.card(Lane.RIGHT).receiveDamage(20);
        state.endPhase();
        assertTrue(state.needsReplacement());
        assertEquals(Lane.RIGHT, state.replacementLane());
        assertEquals(2, state.reserves().size());
        state.deployReserve(1);
        assertEquals("INTERN", state.card(Lane.RIGHT).name());
        assertFalse(state.needsReplacement());
        assertFalse(roster.cards().get(2).isAlive());
    }

    @Test void forgeCanAtomicallyReplaceAPlaugeRosterSlot() {
        Roster roster = new Roster();
        Card original = roster.cards().get(0);
        original.receiveDamage(999);
        assertFalse(original.isAlive());
        Card rebuilt = new Card("RE-SPARK", CardType.STEEL, 32, 5, 5,
                "BRACE", "HOLD THE LINE", original.trait());
        roster.replace(0, rebuilt);
        assertSame(rebuilt, roster.cards().get(0));
        assertTrue(rebuilt.isAlive());
        assertEquals("RECKLESS", rebuilt.trait());
    }

    @Test void bossesUseTheirAuthoredSystemMechanics() {
        CombatState len = new CombatState(Encounter.LEN);
        len.endPhase();
        len.endPhase();
        assertEquals(Lane.RIGHT, len.distortedLane());

        CombatState paywall = new CombatState(Encounter.PAYWALL);
        paywall.endPhase();
        assertTrue(paywall.hiddenFees());
        paywall.attack(Lane.LEFT);
        assertEquals(6, paywall.energy());

        CombatState armstrong = new CombatState(Encounter.ARMSTRONG);
        armstrong.endPhase();
        assertEquals(1, armstrong.productivityTax());
        armstrong.attack(Lane.LEFT);
        assertEquals(6, armstrong.energy());
    }

    @Test void positionSwapMemoryExchangesAdjacentDeployedCards() {
        Roster roster = new Roster();
        CombatState state = new CombatState(Encounter.DRONE, roster);
        state.card(Lane.RIGHT).receiveDamage(20);
        state.endPhase();
        state.deployReserve(0);
        assertEquals("SHIFT", state.card(Lane.RIGHT).name());
        String result = state.memory(Lane.RIGHT, Lane.CENTER);
        assertTrue(result.contains("SWAPS"));
        assertEquals("SHIFT", state.card(Lane.CENTER).name());
        assertEquals("RIVET", state.card(Lane.RIGHT).name());
    }
}
