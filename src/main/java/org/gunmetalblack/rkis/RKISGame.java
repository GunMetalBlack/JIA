package org.gunmetalblack.rkis;

import asciiPanel.AsciiPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public final class RKISGame implements KeyListener {
    private enum Screen { TITLE, STORY, OVERWORLD, COMBAT, REWARD, FORGE, RUN_REPORT }
    private static final int SCREEN_WIDTH = 140, SCREEN_HEIGHT = 58;
    private static final Color PANEL = new Color(0, 18, 28), SELECTED = new Color(70, 58, 0), HOSTILE = new Color(45, 0, 0);
    private final AsciiPanel terminal;
    private final Roster roster = new Roster();
    private CombatState combat = new CombatState(Encounter.DRONE, roster);
    private Screen screen = Screen.TITLE;
    private final Encounter[] route = Encounter.values();
    private int encounterIndex, storyPage, fibers;
    private int worldX = 10, worldY = 12;
    private final int[][] encounterPositions = {{22, 12}, {42, 20}, {62, 12}, {82, 24}, {102, 14}};
    private final String designation = "PLAYER_2348921";
    private Lane source = Lane.LEFT, target = Lane.LEFT;
    private boolean enemyPhase, inspectLayer;
    private boolean animating;
    private int animationFrame, walkFrame;
    private String animationKind = "";
    private int forgeSlot = -1, forgeMode, forgeHp, forgeAttack, forgeDefense, forgeMemory;
    private CardType forgeType = CardType.GENERIC;
    private String message = "Select a card with 1-3. Press I to inspect its Memories.";

    public RKISGame(AsciiPanel terminal) { this.terminal = terminal; }

    public void render() {
        terminal.clear(' ', AsciiPanel.white, AsciiPanel.black);
        if (screen != Screen.COMBAT) {
            renderRunScreen(); terminal.repaint(); return;
        }
        frame(); headerLayer(); battlefieldLayer(); informationLayer(); logAndControlsLayer();
        if (animating) animationLayer();
        if (combat.needsReplacement()) replacementLayer();
        if (inspectLayer) inspectionLayer();
        terminal.repaint();
    }

    private void renderRunScreen() {
        frame();
        switch (screen) {
            case TITLE -> {
                centered("R . K . I . S .", 9, AsciiPanel.brightCyan);
                centered("RECOVERY KERNEL INTEGRATION SYSTEM", 12, Color.GRAY);
                centered("EMPLOYEE SESSION READY", 18, AsciiPanel.brightRed);
                centered("You are not trapped. You agreed to be here.", 22, Color.DARK_GRAY);
                centered("[ENTER] CLOCK IN", 31, AsciiPanel.brightGreen);
                centered("Keyboard only - progress is held for this session", 38, Color.GRAY);
            }
            case STORY -> renderStory();
            case OVERWORLD -> renderOverworld();
            case REWARD -> renderReward();
            case FORGE -> renderForge();
            case RUN_REPORT -> renderRunReport();
            default -> { }
        }
    }

    private void renderStory() {
        put("THE UNKNOWN / ONBOARDING", 6, 4, AsciiPanel.brightMagenta);
        put("DESIGNATION: " + designation, 84, 4, AsciiPanel.brightYellow);
        String[][] pages = {
                {"You wake beneath a sky that cannot decide what color it is.", "A figure shaped like the number 8 waits beside a dead terminal.", "", "THE UNKNOWN: There once was a time I was like you, fellow player."},
                {"THE UNKNOWN: These cards are not weapons. They are what remains.", "Three may stand together. Two wait in reserve. Every action draws", "from the same Energy supply. Spend it like it belongs to everyone.", "", "THE UNKNOWN: Each loss is another chance for improvement."},
                {"Fibers are payment for useful experience. Victory earns them.", "Failure earns them too. The machine does not waste labor.", "", "Find the workers who came before you. Climb through The Lie,", "The Trap, and The Truth. Try to remember why you signed the contract."}
        };
        int y = 11;
        for (String line : pages[storyPage]) put(line, 10, y += 3, line.startsWith("THE UNKNOWN") ? AsciiPanel.brightMagenta : Color.LIGHT_GRAY);
        put("PAGE " + (storyPage + 1) + "/3", 10, 40, Color.GRAY);
        put(storyPage < 2 ? "[ENTER] CONTINUE" : "[ENTER] ACCEPT WORK ORDER", 86, 40, AsciiPanel.brightGreen);
    }

    private void renderOverworld() {
        put("FABRICATED OVERWORLD / " + designation, 4, 3, AsciiPanel.brightCyan);
        put("FIBERS: " + fibers, 123, 3, AsciiPanel.brightYellow);
        put("The sky repeats when you stop looking at it.", 4, 5, Color.DARK_GRAY);
        for (int x = 4; x <= 110; x++) { put("#", x, 7, Color.DARK_GRAY); put("#", x, 48, Color.DARK_GRAY); }
        for (int y = 8; y < 48; y++) { put("#", 4, y, Color.DARK_GRAY); put("#", 110, y, Color.DARK_GRAY); }
        for (int x = 8; x < 106; x++) if (x % 2 == 0) put(".", x, 29, new Color(35, 45, 35));
        Color grass = new Color(48, 105, 42), darkGrass = new Color(30, 70, 30);
        for (int y = 9; y < 47; y += 2) {
            for (int x = 7 + (y % 3); x < 108; x += 5) {
                if ((x * 17 + y * 31) % 7 < 4) put((x + y) % 3 == 0 ? "'" : ",", x, y, (x + y) % 2 == 0 ? grass : darkGrass);
            }
        }
        put("  &&  ", 30, 34, darkGrass); put(" &&&& ", 29, 35, grass); put("  ||  ", 30, 36, new Color(80, 55, 30));
        put(" &&& ", 92, 38, grass); put("&&&&&", 91, 39, darkGrass); put("  |  ", 92, 40, new Color(80, 55, 30));
        put("..::..", 58, 23, Color.DARK_GRAY); put("..::..", 18, 42, Color.DARK_GRAY);
        put("^^^^^^^^^^", 12, 9, Color.DARK_GRAY); put("^^^^^^", 49, 33, Color.DARK_GRAY);
        put("~~~~~~~", 73, 9, AsciiPanel.blue); put("~~~~~~~", 74, 10, AsciiPanel.blue);
        put("[F]", 53, 38, AsciiPanel.brightCyan); put("CARD FORGE", 48, 40, AsciiPanel.brightCyan);
        for (int i = 0; i < route.length; i++) {
            int x = encounterPositions[i][0], y = encounterPositions[i][1];
            Color color = i < encounterIndex ? AsciiPanel.brightGreen : i == encounterIndex ? AsciiPanel.brightRed : Color.DARK_GRAY;
            put(i < encounterIndex ? "x" : i == encounterIndex ? "!" : "?", x, y, color);
            put(i < encounterIndex ? "CLEARED" : route[i].displayName(), Math.max(6, x - 8), y + 2, color);
        }
        put(walkFrame % 2 == 0 ? "@" : "&", worldX, worldY, AsciiPanel.brightYellow);
        put("YOU", worldX - 1, worldY - 1, AsciiPanel.brightYellow);
        put("OVERWORLD CONTROLS", 114, 9, AsciiPanel.brightWhite);
        put("WASD / ARROWS", 114, 12, Color.LIGHT_GRAY); put("Move worker", 114, 13, Color.GRAY);
        put("ENTER", 114, 16, AsciiPanel.brightGreen); put("Enter encounter", 114, 17, Color.GRAY);
        put("F", 114, 20, AsciiPanel.brightCyan); put("Use Forge nearby", 114, 21, Color.GRAY);
        put("SYMBOLS", 114, 26, AsciiPanel.brightWhite); put("@ worker", 114, 28, AsciiPanel.brightYellow);
        put("! hostile", 114, 30, AsciiPanel.brightRed); put("x cleared", 114, 32, AsciiPanel.brightGreen);
        put("? locked", 114, 34, Color.DARK_GRAY); put("[F] forge", 114, 36, AsciiPanel.brightCyan);
        if (nearCurrentEncounter()) put("[ENTER] ENGAGE " + route[encounterIndex].displayName(), 7, 52, AsciiPanel.brightRed);
        else if (nearForge()) put("[F] ENTER CARD FORGE", 7, 52, AsciiPanel.brightCyan);
        else put("Explore the fabricated world. Reach the red ! encounter marker.", 7, 52, Color.GRAY);
    }

    private void renderReward() {
        Encounter won = route[encounterIndex];
        centered("WORK ORDER COMPLETE / MEMORY RECOVERED", 7, AsciiPanel.brightGreen);
        centered(won.displayName() + " DEFEATED", 11, AsciiPanel.brightWhite);
        centered("EXPERIENCE CONVERTED TO TRAINING DATA   +" + won.fibers() + " FIBERS", 15, AsciiPanel.brightYellow);
        String[] reveal = storyAfter(won);
        int y = 20;
        for (String line : reveal) {
            centered(line, y, line.startsWith("ARMSTRONG") || line.startsWith("THE UNKNOWN") ? AsciiPanel.brightMagenta : Color.LIGHT_GRAY);
            y += 3;
        }
        centered("The machine archived your reaction before showing you this message.", 43, AsciiPanel.brightRed);
        centered("[ENTER] RETURN TO THE FABRICATED WORLD", 49, AsciiPanel.brightGreen);
    }

    private String[] storyAfter(Encounter encounter) {
        return switch (encounter) {
            case DRONE -> new String[]{
                    "A damaged card whispers with the Coal Miner's voice:",
                    "The sky disappeared first. Then my name. This damned thing will not let me pass on.",
                    "RELIC MEMORY RECORDED: the workers inside RKIS were once real people."
            };
            case COLLECTOR -> new String[]{
                    "Smoke gathers around a card belonging to the Thief who Stole the Smoke.",
                    "The walls call our names. I have seen the future because the cycle always repeats.",
                    "Work. Struggle. Adapt. Decay. Disappear."
            };
            case LEN -> new String[]{
                    "THE UNKNOWN: Len was not the machine. He was the story that sold it.",
                    "Good pay. Support your family. Help build the future.",
                    "The corporate veil breaks: you entered RKIS as an employee, not an adventurer."
            };
            case PAYWALL -> new String[]{
                    "Every Fiber came from labor. Every improvement required more labor.",
                    "Paywall was the trap: RKIS sold your own work back to you as survival.",
                    "Beyond the broken gate, an old consciousness waits beside the Mainframe."
            };
            case ARMSTRONG -> new String[]{
                    "ARMSTRONG: I watched generations clock in. None of them clocked out.",
                    "Your physical body still sits in a chair. Your brain runs part of the Mainframe.",
                    "Every choice, mistake, emotion, and rebellion trained the system.",
                    "Your attempts to escape were useful data too."
            };
        };
    }

    private void renderForge() {
        put("CARD FORGE / MEMORY WORKSHOP", 5, 4, AsciiPanel.brightCyan); put("FIBERS: " + fibers, 99, 4, AsciiPanel.brightYellow);
        long plauge = roster.cards().stream().filter(card -> !card.isAlive()).count();
        put(plauge == 0 ? "The Forge reconstructs PLAUGE husks. No roster slot is currently PLAUGE."
                : "PLAUGE DETECTED: " + plauge + " husk(s) await reconstruction.", 8, 9, plauge == 0 ? Color.LIGHT_GRAY : AsciiPanel.brightRed);
        put("FIVE-CARD RUN ROSTER - SELECT A PLAUGE SLOT [1-5]", 8, 13, AsciiPanel.brightWhite);
        int y = 17;
        int slot = 1;
        for (Card c : roster.cards()) {
            put(String.format("%d. %-8s %-8s HP %2d/%2d  ATK %d  DEF %d  ACTIVE %-10s PASSIVE %-14s TRAIT %s",
                    slot++, c.isAlive() ? c.name() : "PLAUGE", c.type(), c.hp(), c.maxHp(), c.attack(), c.defense(), c.memory(), c.passive(), c.trait()),
                    8, y, c.isAlive() ? AsciiPanel.brightCyan : AsciiPanel.brightRed);
            y += 4;
        }
        if (forgeSlot >= 0) {
            Card husk = roster.cards().get(forgeSlot);
            put("SELECTED HUSK: SLOT " + (forgeSlot + 1) + " / ECHO " + husk.memory() + " / INHERITED TRAIT " + husk.trait(), 8, 39, AsciiPanel.brightMagenta);
            if (forgeMode == 0) {
                put("[G] GUIDED RECIPE     [P] POINT-BUY CUSTOM BUILD", 8, 42, AsciiPanel.brightGreen);
            } else if (forgeMode == 1) {
                put("GUIDED: [1] GENERIC 5F (26/8/2)   [2] STEEL 7F (32/5/5)   [3] MAGIE 7F (22/11/0)", 8, 42, AsciiPanel.brightCyan);
                put("The card keeps its Legacy Trait and receives its new Type's legal Memories.", 8, 44, Color.GRAY);
            } else {
                int spent = forgeHp + forgeAttack * 2 + forgeDefense * 2;
                int[] base = forgeBase(forgeType);
                put("POINT-BUY: TYPE " + forgeType + "  [T] CYCLE TYPE  [M] MEMORY " + forgeMemoryName() + "  POINTS " + (10 - spent) + "/10", 8, 42, AsciiPanel.brightCyan);
                put("ADD: [H] +2 HP (1P) [A] +1 ATK (2P) [D] +1 DEF (2P)   REFUND: [1] HP [2] ATK [3] DEF", 8, 44, Color.LIGHT_GRAY);
                put("PREVIEW: HP " + (base[0] + forgeHp * 2) + "  ATK " + (base[1] + forgeAttack) + "  DEF " + (base[2] + forgeDefense)
                        + "  COST " + forgeCost(forgeType) + "F   [ENTER] AUTO-SPEND LEFTOVERS + CONFIRM", 8, 46, AsciiPanel.brightYellow);
            }
        } else {
            put(plauge > 0 ? "Choose a red PLAUGE roster number to begin reconstruction." : "A card becomes PLAUGE immediately when its HP reaches zero.", 8, 41, AsciiPanel.brightMagenta);
        }
        put(fit("> " + message, 122), 8, 50, AsciiPanel.brightYellow);
        put("[ESC] BACK / RETURN TO OVERWORLD", 8, 52, AsciiPanel.brightGreen);
    }

    private void renderRunReport() {
        centered("RUN REPORT / THE MACHINE REMEMBERS", 8, AsciiPanel.brightRed);
        centered("DESIGNATION: " + designation, 13, AsciiPanel.brightWhite);
        centered("WORK ORDERS COMPLETED: " + route.length, 17, Color.LIGHT_GRAY);
        centered("FIBERS GENERATED: " + fibers, 20, AsciiPanel.brightYellow);
        centered("ARMSTRONG: You're not playing. You're working.", 26, AsciiPanel.brightMagenta);
        centered("A monitor shows your body wired into the Mainframe. It is older than you remember.", 30, Color.LIGHT_GRAY);
        centered("The Unknown, Coal Miner, and Thief are not NPCs. They are previous employees.", 34, Color.LIGHT_GRAY);
        centered("Every choice trained RKIS. Even reaching this screen was useful.", 38, AsciiPanel.brightRed);
        centered("EMPLOYEE SESSION RECYCLED. A NEW DESIGNATION IS WAITING.", 43, Color.GRAY);
        centered("[ENTER] BEGIN ANOTHER WORK DAY", 49, AsciiPanel.brightGreen);
    }

    private void frame() {
        horizontal(1, 1, SCREEN_WIDTH - 2, '-'); horizontal(1, SCREEN_HEIGHT - 2, SCREEN_WIDTH - 2, '-');
        for (int y = 2; y < SCREEN_HEIGHT - 2; y++) { put("|", 1, y, Color.DARK_GRAY); put("|", SCREEN_WIDTH - 2, y, Color.DARK_GRAY); }
        put("+", 1, 1, Color.GRAY); put("+", SCREEN_WIDTH - 2, 1, Color.GRAY);
        put("+", 1, SCREEN_HEIGHT - 2, Color.GRAY); put("+", SCREEN_WIDTH - 2, SCREEN_HEIGHT - 2, Color.GRAY);
    }

    private void headerLayer() {
        put("R.K.I.S. / ORDER 002", 4, 2, AsciiPanel.brightWhite);
        String phase = enemyPhase ? " ENEMY RESPONSE - INPUT PAUSED " : " PLAYER PHASE - INPUT READY ";
        put(phase, 39, 2, Color.BLACK, enemyPhase ? AsciiPanel.brightRed : AsciiPanel.brightGreen);
        String energyBar = "#".repeat(combat.energy()) + ".".repeat(9 - combat.energy());
        put(String.format("ROUND %02d  ENERGY [%s] %d/9  ACTIONS %d/4", combat.round(), energyBar, combat.energy(), combat.actions()), 79, 2, AsciiPanel.brightYellow);
    }

    private void battlefieldLayer() {
        put("YOUR FORMATION", 4, 4, AsciiPanel.brightCyan); put("HOSTILE ATTACK SIDE", 91, 4, AsciiPanel.brightRed);
        int[] x = {5, 33, 61};
        for (Lane lane : Lane.values()) drawCardPortrait(lane, x[lane.ordinal()], 6);
        drawEnemySide(91, 6);
        put("Adjacency: LEFT <----> CENTER <----> RIGHT", 22, 18, Color.GRAY);
    }

    private void drawCardPortrait(Lane lane, int x, int y) {
        Card card = combat.card(lane); Color bg = target == lane ? SELECTED : PANEL;
        terminal.clear(' ', x, y, 25, 11, Color.WHITE, bg); box(x, y, 25, 11, target == lane ? AsciiPanel.brightYellow : AsciiPanel.brightCyan, bg);
        put(" " + lane + (source == lane ? " [ACTING]" : ""), x + 2, y, target == lane ? AsciiPanel.brightYellow : AsciiPanel.brightCyan, bg);
        if (card == null) {
            put("   [EMPTY]", x + 6, y + 4, Color.DARK_GRAY, bg);
            put("NO LIVING CARD", x + 5, y + 6, AsciiPanel.brightRed, bg);
            return;
        }
        String[] art = portrait(card.type());
        for (int i = 0; i < art.length; i++) put(art[i], x + 2, y + 2 + i, Color.LIGHT_GRAY, bg);
        boolean obscured = combat.memoryLoss() || combat.distortedLane() == lane;
        if (obscured) {
            put("UNKNOWN", x + 12, y + 2, AsciiPanel.brightRed, bg);
            put("ERROR", x + 12, y + 3, AsciiPanel.brightRed, bg);
            put("HP ??/??", x + 12, y + 5, AsciiPanel.brightRed, bg);
            put("MEMORY LOST", x + 12, y + 8, AsciiPanel.brightMagenta, bg);
            return;
        }
        put(card.name(), x + 12, y + 2, AsciiPanel.brightWhite, bg); put(card.type().name(), x + 12, y + 3, AsciiPanel.brightCyan, bg);
        put("HP " + card.hp() + "/" + card.maxHp(), x + 12, y + 5, card.hp() * 3 < card.maxHp() ? AsciiPanel.brightRed : AsciiPanel.brightGreen, bg);
        put("SH " + card.shield(), x + 12, y + 6, AsciiPanel.brightGreen, bg);
        put("[" + bar(card.hp(), card.maxHp(), 9) + "]", x + 12, y + 7, card.hp() * 3 < card.maxHp() ? AsciiPanel.brightRed : AsciiPanel.brightGreen, bg);
        put(card.isPrimed() ? "PRIMED" : "READY", x + 12, y + 8, card.isPrimed() ? AsciiPanel.brightMagenta : Color.GRAY, bg);
    }

    private String[] portrait(CardType type) { return switch (type) {
        case MAGIE -> new String[]{"  .*.  ", " /o o\\ ", "|  ^  |", " \\___/ ", "  /|\\  ", " /_|_\\ "};
        case STEEL -> new String[]{" .---. ", "[|o o|]", " |___| ", " /###\\ ", "|#####|", " /_|_\\ "};
        case GENERIC -> new String[]{" .---. ", " |o o| ", " |_-_| ", "  /|\\  ", " / | \\ ", "  / \\  "}; }; }

    private void drawEnemySide(int x, int y) {
        String[] bossArt = BossArt.forEncounter(combat.enemy().encounter());
        if (bossArt.length > 0) {
            boolean intro = animationKind.equals("BOSS_INTRO");
            boolean dying = animationKind.endsWith("_DEATH");
            int visibleRows = intro ? Math.min(bossArt.length, Math.max(1, animationFrame * 2)) : bossArt.length;
            boolean impact = (animationKind.equals("ATTACK") || animationKind.equals("MEMORY")) && animationFrame >= 5;
            Color artColor = impact ? AsciiPanel.brightWhite
                    : dying && animationFrame % 2 == 0 ? Color.DARK_GRAY
                    : intro && animationFrame % 2 == 0 ? AsciiPanel.brightMagenta : AsciiPanel.brightRed;
            for (int row = 0; row < visibleRows; row++) {
                if (dying && row < animationFrame - 4) continue;
                int jitter = intro && animationFrame < 7 && row % 3 == 0 ? animationFrame % 3 - 1 : 0;
                String line = dying && (row + animationFrame) % 3 == 0
                        ? bossArt[row].replace('X', '.').replace('M', '.').replace('O', '.').replace('k', '.')
                        : bossArt[row];
                put(line, x + jitter, y + row, artColor);
            }
            int panelY = y + bossArt.length + 1;
            put(fit(combat.enemy().name(), 45), x, panelY, AsciiPanel.brightWhite);
            put("HP " + combat.enemy().hp() + "/" + combat.enemy().maxHp() + "  DEF " + combat.enemy().defense()
                    + "  SH " + combat.enemy().shield(), x, panelY + 1, AsciiPanel.brightRed);
            put("[" + bar(combat.enemy().hp(), combat.enemy().maxHp(), 41) + "]", x, panelY + 2, AsciiPanel.brightRed);
            put("NEXT INTENT:", x, panelY + 4, AsciiPanel.brightYellow);
            putWrapped(combat.enemy().intentText(), x, panelY + 5, 45, 2, AsciiPanel.brightYellow);
            put("BOSS RULE:", x, panelY + 8, AsciiPanel.brightMagenta);
            putWrapped(combat.bossRule(), x, panelY + 9, 45, 3, AsciiPanel.brightMagenta);
            return;
        }
        terminal.clear(' ', x, y, 24, 11, Color.WHITE, HOSTILE); box(x, y, 24, 11, AsciiPanel.brightRed, HOSTILE);
        put(" ENEMY", x + 2, y, AsciiPanel.brightRed, HOSTILE);
        put("   .------.   ", x + 4, y + 2, Color.LIGHT_GRAY, HOSTILE); put("  / [==]  \\  ", x + 4, y + 3, Color.LIGHT_GRAY, HOSTILE);
        put(" |  DRONE  | ", x + 4, y + 4, AsciiPanel.brightRed, HOSTILE); put("  \\  /\\  /  ", x + 4, y + 5, Color.LIGHT_GRAY, HOSTILE);
        put("   \\/  \\/   ", x + 4, y + 6, Color.LIGHT_GRAY, HOSTILE);
        put("HP " + combat.enemy().hp() + "/" + combat.enemy().maxHp() + " SH " + combat.enemy().shield(), x + 3, y + 8, AsciiPanel.brightRed, HOSTILE);
        put("[" + bar(combat.enemy().hp(), combat.enemy().maxHp(), 18) + "]", x + 3, y + 7, AsciiPanel.brightRed, HOSTILE);
        String lane = combat.enemy().roundIndex() % 3 == 2 ? "SELF" : combat.enemy().targetLane().name();
        put("TARGET: " + lane, x + 3, y + 9, AsciiPanel.brightYellow, HOSTILE);
        for (Lane candidate : Lane.values()) {
            int markerX = 7 + candidate.ordinal() * 28;
            boolean threatened = combat.enemy().roundIndex() % 3 != 2 && combat.enemy().targetLane() == candidate;
            put(threatened ? "[!! INCOMING ATTACK !!]" : "[ lane clear ]", markerX, 5, threatened ? AsciiPanel.brightRed : Color.DARK_GRAY);
        }
    }

    private void informationLayer() {
        boolean boss = BossArt.forEncounter(combat.enemy().encounter()).length > 0;
        int infoWidth = boss ? 80 : 108;
        Card card = combat.card(source); horizontal(3, 20, boss ? 84 : 115, '='); put("SELECTED CARD DETAILS", 5, 21, AsciiPanel.brightYellow);
        if (card == null) {
            put(source + " LANE IS EMPTY. SELECT AN OCCUPIED LANE.", 5, 24, AsciiPanel.brightRed);
            return;
        }
        if (combat.memoryLoss() || combat.distortedLane() == source) {
            put("CARD DATA DISTORTED BY " + combat.enemy().name() + ". PRESS [I] TO RECOVER ACTUAL MEMORY DATA.", 5, 24, AsciiPanel.brightRed);
            return;
        }
        put(card.name() + " / " + card.type() + " / " + source + " LANE", 5, 23, AsciiPanel.brightWhite);
        put("HP " + card.hp() + "/" + card.maxHp() + "   ATK " + card.attack() + "   DEF " + card.defense() + "   SHIELD " + card.shield() + "/8   ACTIONS " + card.actions() + "/2", 5, 24, AsciiPanel.brightGreen);
        put(fit("ATTACK " + combat.attackCost(source) + "E   ACTIVE MEMORY: " + card.memory() + " (EFFECTIVE " + combat.memoryCost(source) + "E)  Target: " + CardRules.memoryTarget(card), infoWidth), 5, 26, AsciiPanel.brightCyan);
        put(fit(CardRules.memoryDescription(card), infoWidth), 5, 27, Color.LIGHT_GRAY);
        put(fit("PASSIVE: " + card.passive() + " - " + CardRules.passiveDescription(card), infoWidth), 5, 29, AsciiPanel.brightMagenta);
        put(fit("TRAIT: " + card.trait() + " [" + (card.traitUsed() ? "USED" : "READY") + "] - " + CardRules.traitDescription(card), infoWidth), 5, 31, AsciiPanel.brightYellow);
        put("Press [I] for the full card and Memory inspection layer.", 5, 32, Color.GRAY);
    }

    private void logAndControlsLayer() {
        boolean boss = BossArt.forEncounter(combat.enemy().encounter()).length > 0;
        horizontal(3, 34, boss ? 84 : 115, '='); put("COMBAT LOG", 5, 35, AsciiPanel.brightWhite); String[] logs = combat.logLines();
        for (int i = 0; i < 4; i++) put(i < logs.length ? fit(logs[i], boss ? 80 : 108) : "", 5, 36 + i, i == 0 ? Color.WHITE : Color.GRAY);
        put(fit("> " + message, 108), 5, 41, AsciiPanel.brightYellow);
        put("[1-3] ACTING CARD   [LEFT/RIGHT] HIGHLIGHT   [A] ATTACK   [M] MEMORY   [G] GUARD", 5, 43, Color.WHITE);
        put(combat.finished() ? (combat.victorious() ? "[ENTER] COLLECT REWARD AND CONTINUE RUN   [R] REPLAY ENCOUNTER"
                        : "[ENTER] FILE FAILURE REPORT   [R] RETRY ENCOUNTER")
                : "[I] INSPECT/CLOSE   [E] END PHASE   [R] RESTART", 5, 44,
                combat.finished() ? AsciiPanel.brightGreen : Color.WHITE);
    }

    private void replacementLayer() {
        int x = 27, y = 15, w = 86, h = 20; Color bg = new Color(30, 4, 4);
        terminal.clear(' ', x, y, w, h, Color.WHITE, bg); box(x, y, w, h, AsciiPanel.brightRed, bg);
        put(" CARD DEATH / PLAUGE CREATED ", x + 3, y, AsciiPanel.brightRed, bg);
        put("The " + combat.replacementLane() + " lane is empty. Choose a living reserve.", x + 5, y + 4, AsciiPanel.brightWhite, bg);
        int row = y + 8;
        for (int i = 0; i < combat.reserves().size(); i++) {
            Card reserve = combat.reserves().get(i);
            put("[" + (i + 4) + "] " + reserve.name() + " / " + reserve.type() + " / HP " + reserve.hp() + "/" + reserve.maxHp()
                    + " / " + reserve.memory(), x + 8, row, AsciiPanel.brightCyan, bg);
            row += 3;
        }
        put("The dead card remains PLAUGE in its permanent roster slot.", x + 5, y + 17, Color.GRAY, bg);
    }

    private void animationLayer() {
        if (animationKind.equals("ATTACK") || animationKind.equals("MEMORY")) {
            int start = 18 + source.ordinal() * 28;
            int x = Math.min(99, start + animationFrame * 10);
            put(animationKind.equals("MEMORY") ? "*==MEMORY==>" : "----->", x, 18,
                    animationKind.equals("MEMORY") ? AsciiPanel.brightMagenta : AsciiPanel.brightYellow);
        } else if (animationKind.equals("GUARD")) {
            int x = 10 + source.ordinal() * 28;
            put(animationFrame % 2 == 0 ? "<<< SHIELD RISING >>>" : "[[[ SHIELD LOCKED ]]]", x, 18, AsciiPanel.brightCyan);
        } else if (animationKind.equals("SWAP")) {
            int spread = Math.min(12, animationFrame * 2);
            centered("<" + "-".repeat(spread) + " POSITION SHIFT " + "-".repeat(spread) + ">", 18,
                    animationFrame % 2 == 0 ? AsciiPanel.brightCyan : AsciiPanel.brightMagenta);
        } else if (animationKind.equals("ENEMY")) {
            int x = Math.max(12, 94 - animationFrame * 11);
            put("<=== HOSTILE ===", x, 18, AsciiPanel.brightRed);
        } else if (animationKind.equals("LEN_EFFECT")) {
            String[] noise = {"FACT", "OPINION", "TRUST", "BIAS", "???"};
            for (int i = 0; i < 5; i++) put(noise[(i + animationFrame) % noise.length], 8 + i * 18, 10 + (i % 2) * 4,
                    animationFrame % 2 == 0 ? AsciiPanel.brightMagenta : AsciiPanel.brightRed);
            centered("MEDIA BIAS // THE DISPLAY IS NOT THE TRUTH", 18, AsciiPanel.brightMagenta);
        } else if (animationKind.equals("PAYWALL_EFFECT")) {
            for (int x = 8; x < Math.min(112, 8 + animationFrame * 13); x += 6) put("$", x, 18, AsciiPanel.brightYellow);
            centered(animationFrame % 2 == 0 ? "COST RECALCULATING..." : "HIDDEN FEES APPLIED", 16, AsciiPanel.brightRed);
        } else if (animationKind.equals("ARMSTRONG_EFFECT")) {
            Color glitch = animationFrame % 2 == 0 ? AsciiPanel.brightRed : AsciiPanel.brightWhite;
            put("ERROR::CONSCIOUSNESS_INTEGRATION", 7 + animationFrame % 4, 8, glitch);
            put("YOU ARE NOT PLAYING", 35 - animationFrame % 3, 13, glitch);
            put("YOU ARE WORKING", 58 + animationFrame % 5, 17, AsciiPanel.brightRed);
            put("MEMORY BLOCK " + "#".repeat(Math.min(20, animationFrame * 2)), 15, 31, AsciiPanel.brightMagenta);
        } else if (animationKind.equals("BOSS_INTRO")) {
            String title = switch (combat.enemy().encounter()) {
                case LEN -> "THE LIE HAS ENTERED THE SYSTEM";
                case PAYWALL -> "THE TRAP HAS CALCULATED YOUR VALUE";
                case ARMSTRONG -> "THE TRUTH REMEMBERS YOUR NAME";
                default -> "HOSTILE PROCESS ATTACHED";
            };
            centered(animationFrame % 2 == 0 ? title : "[ " + combat.enemy().name() + " ]", 40,
                    animationFrame % 2 == 0 ? AsciiPanel.brightRed : AsciiPanel.brightWhite);
        } else if (animationKind.equals("VICTORY")) {
            Color color = animationFrame % 2 == 0 ? AsciiPanel.brightGreen : AsciiPanel.brightYellow;
            centered("================ WORK ORDER COMPLETE ================", 16, color);
            centered(combat.enemy().name() + " TERMINATED", 19, AsciiPanel.brightWhite);
        } else if (animationKind.equals("LEN_DEATH")) {
            Color fade = animationFrame % 2 == 0 ? AsciiPanel.brightRed : Color.DARK_GRAY;
            centered("THE CORPORATE VEIL TEARS", 13, fade);
            centered(animationFrame < 7 ? "FACT  FACT  FACT  FACT" : "...THE MESSAGE WAS THE PRODUCT...", 17, AsciiPanel.brightMagenta);
            centered("LEN: They sold you a future they never intended to return.", 40, Color.LIGHT_GRAY);
        } else if (animationKind.equals("PAYWALL_DEATH")) {
            for (int i = 0; i < 12; i++) put("$", 12 + i * 8, 8 + (animationFrame + i * 3) % 25, animationFrame % 2 == 0 ? AsciiPanel.brightYellow : Color.DARK_GRAY);
            centered(animationFrame < 8 ? "PAYMENT DECLINED" : "ACCOUNT BALANCE: 0", 16, AsciiPanel.brightRed);
            centered("PAYWALL: The machine charges you for surviving the machine.", 40, Color.LIGHT_GRAY);
        } else if (animationKind.equals("ARMSTRONG_DEATH")) {
            String question = animationFrame < 4 ? "DO YOU REMEMBER YOUR NAME?"
                    : animationFrame < 8 ? "DO YOU REMEMBER YOUR FAMILY?"
                    : animationFrame < 12 ? "DO YOU REMEMBER SIGNING THE CONTRACT?"
                    : "YOU ARE NOT PLAYING. YOU ARE WORKING.";
            centered(question, 14, animationFrame % 2 == 0 ? AsciiPanel.brightWhite : AsciiPanel.brightRed);
            put("MAINFRAME LINK: " + "#".repeat(Math.min(30, animationFrame * 2)), 20, 20, AsciiPanel.brightMagenta);
            centered("ARMSTRONG: Your brain is the hardware. Your consciousness is the labor.", 40, Color.LIGHT_GRAY);
        }
    }

    private void inspectionLayer() {
        int x = 15, y = 7, w = 90, h = 31; Color bg = new Color(4, 8, 20);
        terminal.clear(' ', x, y, w, h, Color.WHITE, bg); box(x, y, w, h, AsciiPanel.brightMagenta, bg); Card card = combat.card(source);
        put(" CARD INSPECTOR / MEMORY LAYER ", x + 3, y, AsciiPanel.brightMagenta, bg); put(card.name() + "   " + card.type() + "   LANE " + source, x + 4, y + 3, AsciiPanel.brightWhite, bg);
        put("COMBAT STATS", x + 4, y + 6, AsciiPanel.brightYellow, bg); put("HP " + card.hp() + "/" + card.maxHp() + "  ATK " + card.attack() + "  DEF " + card.defense() + "  SHIELD " + card.shield() + "/8", x + 4, y + 8, Color.WHITE, bg);
        put("ACTIVE MEMORY: " + card.memory(), x + 4, y + 11, AsciiPanel.brightCyan, bg); put("COST: " + CardRules.memoryCost(card) + " shared Energy", x + 4, y + 13, Color.WHITE, bg);
        put("TARGET: " + CardRules.memoryTarget(card), x + 4, y + 14, Color.WHITE, bg); put(fit(CardRules.memoryDescription(card), 80), x + 4, y + 16, Color.LIGHT_GRAY, bg);
        put("PASSIVE MEMORY: " + card.passive(), x + 4, y + 19, AsciiPanel.brightMagenta, bg); put(fit(CardRules.passiveDescription(card), 80), x + 4, y + 21, Color.LIGHT_GRAY, bg);
        put("IDENTITY TRAIT: " + card.trait() + "  STATUS: " + (card.traitUsed() ? "USED" : "READY"), x + 4, y + 24, AsciiPanel.brightYellow, bg); put(fit(CardRules.traitDescription(card), 80), x + 4, y + 26, Color.LIGHT_GRAY, bg);
        put("[LEFT/RIGHT] CHANGE CARD     [I/ESC] CLOSE", x + 4, y + 29, AsciiPanel.brightGreen, bg);
    }

    @Override public void keyPressed(KeyEvent event) {
        if (screen != Screen.COMBAT) {
            handleRunScreenInput(event);
            render();
            return;
        }
        if (enemyPhase || animating) return;
        if (combat.needsReplacement()) {
            if (event.getKeyCode() == KeyEvent.VK_4) message = combat.deployReserve(0);
            else if (event.getKeyCode() == KeyEvent.VK_5) message = combat.deployReserve(1);
            render();
            return;
        }
        if (combat.finished() && event.getKeyCode() == KeyEvent.VK_ENTER) {
            screen = combat.victorious() ? Screen.REWARD : Screen.RUN_REPORT;
            render();
            return;
        }
        if (inspectLayer) {
            if (event.getKeyCode() == KeyEvent.VK_I || event.getKeyCode() == KeyEvent.VK_ESCAPE) inspectLayer = false;
            else if (event.getKeyCode() == KeyEvent.VK_LEFT) source = source == Lane.LEFT ? Lane.LEFT : Lane.values()[source.ordinal() - 1];
            else if (event.getKeyCode() == KeyEvent.VK_RIGHT) source = source == Lane.RIGHT ? Lane.RIGHT : Lane.values()[source.ordinal() + 1];
            render(); return;
        }
        switch (event.getKeyCode()) {
            case KeyEvent.VK_1 -> select(Lane.LEFT); case KeyEvent.VK_2 -> select(Lane.CENTER); case KeyEvent.VK_3 -> select(Lane.RIGHT);
            case KeyEvent.VK_LEFT -> target = target == Lane.LEFT ? Lane.LEFT : Lane.values()[target.ordinal() - 1];
            case KeyEvent.VK_RIGHT -> target = target == Lane.RIGHT ? Lane.RIGHT : Lane.values()[target.ordinal() + 1];
            case KeyEvent.VK_A -> performAnimatedAction("ATTACK");
            case KeyEvent.VK_M -> performAnimatedAction("MEMORY");
            case KeyEvent.VK_G -> performAnimatedAction("GUARD");
            case KeyEvent.VK_I -> inspectLayer = true; case KeyEvent.VK_E -> beginEnemyResponse();
            case KeyEvent.VK_R -> { combat = new CombatState(route[Math.min(encounterIndex, route.length - 1)], roster); selectFirstLiving(); message = "ORDER RESTARTED WITH THE CURRENT RUN ROSTER."; } default -> { }
        }
        render();
    }

    private void handleRunScreenInput(KeyEvent event) {
        switch (screen) {
            case TITLE -> { if (event.getKeyCode() == KeyEvent.VK_ENTER) screen = Screen.STORY; }
            case STORY -> {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (storyPage < 2) storyPage++; else screen = Screen.OVERWORLD;
                }
            }
            case OVERWORLD -> {
                int code = event.getKeyCode();
                if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) moveWorld(-1, 0);
                else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) moveWorld(1, 0);
                else if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) moveWorld(0, -1);
                else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) moveWorld(0, 1);
                else if (code == KeyEvent.VK_F && nearForge()) screen = Screen.FORGE;
                else if (code == KeyEvent.VK_ENTER && nearCurrentEncounter()) {
                    if (!roster.hasLivingCard()) { screen = Screen.RUN_REPORT; break; }
                    combat = new CombatState(route[encounterIndex], roster); selectFirstLiving(); screen = Screen.COMBAT;
                    message = "Work order started. Inspect the hostile intent before acting.";
                    if (BossArt.forEncounter(combat.enemy().encounter()).length > 0) {
                        playAnimation("BOSS_INTRO", 12, 90, () -> message = combat.enemy().name() + " IS NOW ACTIVE. READ ITS INTENT.");
                    }
                }
            }
            case FORGE -> handleForgeInput(event);
            case REWARD -> {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    fibers += route[encounterIndex].fibers(); encounterIndex++;
                    screen = encounterIndex >= route.length ? Screen.RUN_REPORT : Screen.OVERWORLD;
                }
            }
            case RUN_REPORT -> {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    encounterIndex = 0; storyPage = 0; fibers = Math.min(5, fibers / 4); screen = Screen.TITLE;
                }
            }
            default -> { }
        }
    }

    private void select(Lane lane) {
        if (combat.card(lane) == null || !combat.card(lane).isAlive()) { message = lane + " has no living card."; return; }
        source = lane; target = lane; message = combat.card(lane).name() + " selected. Card details shown below.";
    }
    private void performAnimatedAction(String kind) {
        int beforeActions = combat.actions();
        Card actingCard = combat.card(source);
        message = switch (kind) {
            case "ATTACK" -> combat.attack(source);
            case "MEMORY" -> combat.memory(source, target);
            default -> combat.guard(source);
        };
        boolean swapped = kind.equals("MEMORY") && actingCard != null && actingCard.memory().equals("POSITION SWAP") && combat.actions() > beforeActions;
        if (swapped) {
            for (Lane lane : Lane.values()) if (combat.card(lane) == actingCard) { source = target = lane; break; }
        }
        if (combat.actions() > beforeActions) playAnimation(swapped ? "SWAP" : kind, 7, 75,
                () -> { if (combat.victorious()) playBossDeath(); });
    }

    private void playBossDeath() {
        String kind = switch (combat.enemy().encounter()) {
            case LEN -> "LEN_DEATH";
            case PAYWALL -> "PAYWALL_DEATH";
            case ARMSTRONG -> "ARMSTRONG_DEATH";
            default -> "VICTORY";
        };
        int frames = kind.equals("ARMSTRONG_DEATH") ? 17 : kind.equals("VICTORY") ? 8 : 14;
        playAnimation(kind, frames, 130, () -> message = "Boss process collapsed. Press ENTER to continue the story.");
    }

    private void playAnimation(String kind, int frames, int delay, Runnable completion) {
        animationKind = kind; animationFrame = 0; animating = true;
        Timer timer = new Timer(delay, null);
        timer.addActionListener(event -> {
            animationFrame++;
            if (animationFrame >= frames) {
                timer.stop(); animating = false; animationKind = "";
                if (completion != null) completion.run();
            }
            render();
        });
        timer.start();
    }
    private void selectFirstLiving() {
        for (Lane lane : Lane.values()) if (combat.card(lane) != null && combat.card(lane).isAlive()) { source = target = lane; return; }
        source = target = Lane.LEFT;
    }
    private void beginEnemyResponse() {
        if (combat.finished()) { message = "ORDER COMPLETE. PRESS R TO RESTART."; return; }
        enemyPhase = true; message = "Enemy is executing the displayed intent...";
        String effect = switch (combat.enemy().encounter()) {
            case LEN -> "LEN_EFFECT";
            case PAYWALL -> "PAYWALL_EFFECT";
            case ARMSTRONG -> "ARMSTRONG_EFFECT";
            default -> "ENEMY";
        };
        playAnimation(effect, combat.enemy().encounter() == Encounter.ARMSTRONG ? 12 : 9, 100, () -> {
            combat.endPhase(); enemyPhase = false;
            message = combat.finished() ? "Combat ended. Press R to restart." : "New player phase. Read the hostile lane marker before acting.";
        });
    }

    private void handleForgeInput(KeyEvent event) {
        int code = event.getKeyCode();
        if (code == KeyEvent.VK_ESCAPE) {
            if (forgeMode != 0) { forgeMode = 0; resetForgePoints(); }
            else if (forgeSlot >= 0) forgeSlot = -1;
            else screen = Screen.OVERWORLD;
            return;
        }
        if (forgeSlot < 0) {
            if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_5) {
                int slot = code - KeyEvent.VK_1;
                if (!roster.cards().get(slot).isAlive()) forgeSlot = slot;
            }
            return;
        }
        if (forgeMode == 0) {
            if (code == KeyEvent.VK_G) forgeMode = 1;
            else if (code == KeyEvent.VK_P) forgeMode = 2;
            return;
        }
        if (forgeMode == 1) {
            if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_3) forgeCard(CardType.values()[code - KeyEvent.VK_1], true);
            return;
        }
        if (code == KeyEvent.VK_T) { forgeType = CardType.values()[(forgeType.ordinal() + 1) % CardType.values().length]; resetForgePoints(); }
        else if (code == KeyEvent.VK_M) forgeMemory = (forgeMemory + 1) % forgeMemories(forgeType).length;
        else if (code == KeyEvent.VK_BACK_SPACE) resetForgePoints();
        else if (code == KeyEvent.VK_H && forgePointsLeft() >= 1 && forgeHp < 6) forgeHp++;
        else if (code == KeyEvent.VK_A && forgePointsLeft() >= 2 && forgeAttack < 3) forgeAttack++;
        else if (code == KeyEvent.VK_D && forgePointsLeft() >= 2 && forgeDefense < 3) forgeDefense++;
        else if (code == KeyEvent.VK_1 && forgeHp > 0) forgeHp--;
        else if (code == KeyEvent.VK_2 && forgeAttack > 0) forgeAttack--;
        else if (code == KeyEvent.VK_3 && forgeDefense > 0) forgeDefense--;
        else if (code == KeyEvent.VK_ENTER) {
            autoAllocateForgePoints();
            if (forgePointsLeft() == 0) forgeCard(forgeType, false);
            else message = "POINT-BUY BLOCKED: " + forgePointsLeft() + " POINTS COULD NOT BE LEGALLY ALLOCATED.";
        }
    }

    private int[] forgeBase(CardType type) { return switch (type) { case GENERIC -> new int[]{18, 6, 1}; case STEEL -> new int[]{24, 4, 3}; case MAGIE -> new int[]{14, 8, 0}; }; }
    private int forgeCost(CardType type) { return type == CardType.GENERIC ? 5 : 7; }
    private int forgePointsLeft() { return 10 - forgeHp - forgeAttack * 2 - forgeDefense * 2; }
    private void resetForgePoints() { forgeHp = forgeAttack = forgeDefense = forgeMemory = 0; }
    private String[] forgeMemories(CardType type) { return switch (type) {
        case GENERIC -> new String[]{"OVERCLOCK", "AUDIT", "DESPERATE PUSH", "POSITION SWAP"};
        case STEEL -> new String[]{"BRACE", "TAKE THE HIT"};
        case MAGIE -> new String[]{"CONDUIT", "FURNACE MEMORY"};
    }; }
    private String forgeMemoryName() { return forgeMemories(forgeType)[forgeMemory]; }
    private void autoAllocateForgePoints() {
        while (forgePointsLeft() > 0 && forgeHp < 6) forgeHp++;
        while (forgePointsLeft() >= 2 && forgeAttack < 3) forgeAttack++;
        while (forgePointsLeft() >= 2 && forgeDefense < 3) forgeDefense++;
    }
    private void forgeCard(CardType type, boolean guided) {
        int cost = forgeCost(type);
        if (fibers < cost) { message = "FORGE REJECTED: NEED " + cost + " FIBERS."; return; }
        Card husk = roster.cards().get(forgeSlot); int[] base = forgeBase(type);
        int hp = base[0], attack = base[1], defense = base[2];
        if (guided) {
            if (type == CardType.GENERIC) { hp += 8; attack += 2; defense += 1; }
            else if (type == CardType.STEEL) { hp += 8; attack += 1; defense += 2; }
            else { hp += 8; attack += 3; }
        } else { hp += forgeHp * 2; attack += forgeAttack; defense += forgeDefense; }
        String active = guided ? forgeMemories(type)[0] : forgeMemoryName();
        String passive = type == CardType.STEEL ? "HOLD THE LINE" : "NONE";
        roster.replace(forgeSlot, new Card("RE-" + husk.name(), type, hp, attack, defense, active, passive, husk.trait()));
        fibers -= cost; message = "FORGE COMPLETE: SLOT " + (forgeSlot + 1) + " RECONSTRUCTED FOR " + cost + " FIBERS.";
        forgeSlot = -1; forgeMode = 0; resetForgePoints();
    }

    private void box(int x, int y, int w, int h, Color color, Color bg) {
        put("+" + "-".repeat(w - 2) + "+", x, y, color, bg); put("+" + "-".repeat(w - 2) + "+", x, y + h - 1, color, bg);
        for (int row = y + 1; row < y + h - 1; row++) { put("|", x, row, color, bg); put("|", x + w - 1, row, color, bg); }
    }
    private void horizontal(int x, int y, int width, char character) { put(String.valueOf(character).repeat(width), x, y, Color.DARK_GRAY); }
    private void moveWorld(int dx, int dy) {
        worldX = Math.max(5, Math.min(109, worldX + dx));
        worldY = Math.max(8, Math.min(47, worldY + dy));
        walkFrame++;
    }
    private boolean nearCurrentEncounter() { return encounterIndex < route.length && Math.abs(worldX - encounterPositions[encounterIndex][0]) + Math.abs(worldY - encounterPositions[encounterIndex][1]) <= 1; }
    private boolean nearForge() { return Math.abs(worldX - 53) + Math.abs(worldY - 38) <= 2; }
    private void centered(String text, int y, Color color) { put(text, Math.max(2, (SCREEN_WIDTH - text.length()) / 2), y, color); }
    private String fit(String text, int width) { return text.length() <= width ? text : text.substring(0, width - 3) + "..."; }
    private void putWrapped(String text, int x, int y, int width, int maxLines, Color color) {
        String remaining = text.trim();
        for (int line = 0; line < maxLines && !remaining.isEmpty(); line++) {
            int split = Math.min(width, remaining.length());
            if (split < remaining.length()) {
                int space = remaining.lastIndexOf(' ', split);
                if (space > 0) split = space;
            }
            put(remaining.substring(0, split).trim(), x, y + line, color);
            remaining = remaining.substring(split).trim();
        }
    }
    private String bar(int current, int maximum, int width) {
        int filled = maximum <= 0 ? 0 : Math.max(0, Math.min(width, (int) Math.ceil((double) current * width / maximum)));
        return "#".repeat(filled) + ".".repeat(width - filled);
    }
    private void put(String text, int x, int y, Color fg) { terminal.write(text, x, y, fg); }
    private void put(String text, int x, int y, Color fg, Color bg) { terminal.write(text, x, y, fg, bg); }
    @Override public void keyTyped(KeyEvent event) { }
    @Override public void keyReleased(KeyEvent event) { }
}
