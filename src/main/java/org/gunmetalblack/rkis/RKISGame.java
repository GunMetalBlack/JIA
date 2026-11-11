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
    private String message = "Select a card with 1-3. Press I to inspect its Memories.";

    public RKISGame(AsciiPanel terminal) { this.terminal = terminal; }

    public void render() {
        terminal.clear(' ', AsciiPanel.white, AsciiPanel.black);
        if (screen != Screen.COMBAT) {
            renderRunScreen(); terminal.repaint(); return;
        }
        frame(); headerLayer(); battlefieldLayer(); informationLayer(); logAndControlsLayer();
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
        put("^^^^^^^^^^", 12, 9, Color.DARK_GRAY); put("^^^^^^", 49, 33, Color.DARK_GRAY);
        put("~~~~~~~", 73, 9, AsciiPanel.blue); put("~~~~~~~", 74, 10, AsciiPanel.blue);
        put("[F]", 53, 38, AsciiPanel.brightCyan); put("CARD FORGE", 48, 40, AsciiPanel.brightCyan);
        for (int i = 0; i < route.length; i++) {
            int x = encounterPositions[i][0], y = encounterPositions[i][1];
            Color color = i < encounterIndex ? AsciiPanel.brightGreen : i == encounterIndex ? AsciiPanel.brightRed : Color.DARK_GRAY;
            put(i < encounterIndex ? "x" : i == encounterIndex ? "!" : "?", x, y, color);
            put(i < encounterIndex ? "CLEARED" : route[i].displayName(), Math.max(6, x - 8), y + 2, color);
        }
        put("@", worldX, worldY, AsciiPanel.brightYellow);
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
        centered("WORK ORDER COMPLETE", 11, AsciiPanel.brightGreen);
        centered(won.displayName() + " DEFEATED", 16, AsciiPanel.brightWhite);
        centered("EXPERIENCE CONVERTED TO TRAINING DATA", 21, AsciiPanel.brightRed);
        centered("+" + won.fibers() + " FIBERS", 26, AsciiPanel.brightYellow);
        centered("The machine thanks you for improving it.", 31, Color.GRAY);
        centered("[ENTER] RETURN TO ROUTE", 39, AsciiPanel.brightGreen);
    }

    private void renderForge() {
        put("CARD FORGE / MEMORY WORKSHOP", 5, 4, AsciiPanel.brightCyan); put("FIBERS: " + fibers, 99, 4, AsciiPanel.brightYellow);
        long plauge = roster.cards().stream().filter(card -> !card.isAlive()).count();
        put(plauge == 0 ? "The Forge reconstructs PLAUGE husks. No roster slot is currently PLAUGE."
                : "PLAUGE DETECTED: " + plauge + " husk(s) await reconstruction.", 8, 9, plauge == 0 ? Color.LIGHT_GRAY : AsciiPanel.brightRed);
        put("FIVE-CARD RUN ROSTER", 8, 13, AsciiPanel.brightWhite);
        int y = 17;
        int slot = 1;
        for (Card c : roster.cards()) {
            put(String.format("%d. %-8s %-8s HP %2d/%2d  ATK %d  DEF %d  ACTIVE %-10s PASSIVE %-14s TRAIT %s",
                    slot++, c.isAlive() ? c.name() : "PLAUGE", c.type(), c.hp(), c.maxHp(), c.attack(), c.defense(), c.memory(), c.passive(), c.trait()),
                    8, y, c.isAlive() ? AsciiPanel.brightCyan : AsciiPanel.brightRed);
            y += 4;
        }
        put(plauge > 0 ? "Point-buy and guided reconstruction controls are the next Forge layer." : "A card becomes PLAUGE immediately when its HP reaches zero.", 8, 40, AsciiPanel.brightMagenta);
        put("[ESC] RETURN TO OVERWORLD", 8, 51, AsciiPanel.brightGreen);
    }

    private void renderRunReport() {
        centered("RUN REPORT / THE MACHINE REMEMBERS", 8, AsciiPanel.brightRed);
        centered("DESIGNATION: " + designation, 13, AsciiPanel.brightWhite);
        centered("WORK ORDERS COMPLETED: " + route.length, 17, Color.LIGHT_GRAY);
        centered("FIBERS GENERATED: " + fibers, 20, AsciiPanel.brightYellow);
        centered("ARMSTRONG: You're not playing. You're working.", 26, AsciiPanel.brightMagenta);
        centered("Every choice trained RKIS. Even reaching this screen was useful.", 30, Color.GRAY);
        centered("[ENTER] BEGIN ANOTHER WORK DAY", 39, AsciiPanel.brightGreen);
    }

    private void frame() {
        horizontal(1, 1, SCREEN_WIDTH - 2, '-'); horizontal(1, SCREEN_HEIGHT - 2, SCREEN_WIDTH - 2, '-');
        for (int y = 2; y < SCREEN_HEIGHT - 2; y++) { put("|", 1, y, Color.DARK_GRAY); put("|", SCREEN_WIDTH - 2, y, Color.DARK_GRAY); }
        put("+", 1, 1, Color.GRAY); put("+", SCREEN_WIDTH - 2, 1, Color.GRAY);
        put("+", 1, SCREEN_HEIGHT - 2, Color.GRAY); put("+", SCREEN_WIDTH - 2, SCREEN_HEIGHT - 2, Color.GRAY);
    }

    private void headerLayer() {
        put("R.K.I.S. / ORDER 002", 4, 2, AsciiPanel.brightWhite);
        put(enemyPhase ? "ENEMY RESPONSE - INPUT PAUSED" : "PLAYER PHASE", 42, 2,
                enemyPhase ? AsciiPanel.brightRed : AsciiPanel.brightGreen);
        put(String.format("ROUND %02d  ENERGY %d/9  ACTIONS %d/4", combat.round(), combat.energy(), combat.actions()), 84, 2, AsciiPanel.brightYellow);
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
        put(card.name(), x + 12, y + 2, AsciiPanel.brightWhite, bg); put(card.type().name(), x + 12, y + 3, AsciiPanel.brightCyan, bg);
        put("HP " + card.hp() + "/" + card.maxHp(), x + 12, y + 5, card.hp() * 3 < card.maxHp() ? AsciiPanel.brightRed : AsciiPanel.brightGreen, bg);
        put("SH " + card.shield(), x + 12, y + 6, AsciiPanel.brightGreen, bg);
        put(card.isPrimed() ? "PRIMED" : "READY", x + 12, y + 8, card.isPrimed() ? AsciiPanel.brightMagenta : Color.GRAY, bg);
    }

    private String[] portrait(CardType type) { return switch (type) {
        case MAGIE -> new String[]{"  .*.  ", " /o o\\ ", "|  ^  |", " \\___/ ", "  /|\\  ", " /_|_\\ "};
        case STEEL -> new String[]{" .---. ", "[|o o|]", " |___| ", " /###\\ ", "|#####|", " /_|_\\ "};
        case GENERIC -> new String[]{" .---. ", " |o o| ", " |_-_| ", "  /|\\  ", " / | \\ ", "  / \\  "}; }; }

    private void drawEnemySide(int x, int y) {
        terminal.clear(' ', x, y, 24, 11, Color.WHITE, HOSTILE); box(x, y, 24, 11, AsciiPanel.brightRed, HOSTILE);
        put(" ENEMY", x + 2, y, AsciiPanel.brightRed, HOSTILE);
        put("   .------.   ", x + 4, y + 2, Color.LIGHT_GRAY, HOSTILE); put("  / [==]  \\  ", x + 4, y + 3, Color.LIGHT_GRAY, HOSTILE);
        put(" |  DRONE  | ", x + 4, y + 4, AsciiPanel.brightRed, HOSTILE); put("  \\  /\\  /  ", x + 4, y + 5, Color.LIGHT_GRAY, HOSTILE);
        put("   \\/  \\/   ", x + 4, y + 6, Color.LIGHT_GRAY, HOSTILE);
        put("HP " + combat.enemy().hp() + "/" + combat.enemy().maxHp() + " SH " + combat.enemy().shield(), x + 3, y + 8, AsciiPanel.brightRed, HOSTILE);
        String lane = combat.enemy().roundIndex() % 3 == 2 ? "SELF" : combat.enemy().targetLane().name();
        put("TARGET: " + lane, x + 3, y + 9, AsciiPanel.brightYellow, HOSTILE);
        for (Lane candidate : Lane.values()) {
            int markerX = 7 + candidate.ordinal() * 28;
            boolean threatened = combat.enemy().roundIndex() % 3 != 2 && combat.enemy().targetLane() == candidate;
            put(threatened ? "[!! INCOMING ATTACK !!]" : "[ lane clear ]", markerX, 5, threatened ? AsciiPanel.brightRed : Color.DARK_GRAY);
        }
    }

    private void informationLayer() {
        Card card = combat.card(source); horizontal(3, 20, 115, '='); put("SELECTED CARD DETAILS", 5, 21, AsciiPanel.brightYellow);
        if (card == null) {
            put(source + " LANE IS EMPTY. SELECT AN OCCUPIED LANE.", 5, 24, AsciiPanel.brightRed);
            return;
        }
        put(card.name() + " / " + card.type() + " / " + source + " LANE", 5, 23, AsciiPanel.brightWhite);
        put("HP " + card.hp() + "/" + card.maxHp() + "   ATK " + card.attack() + "   DEF " + card.defense() + "   SHIELD " + card.shield() + "/8   ACTIONS " + card.actions() + "/2", 5, 24, AsciiPanel.brightGreen);
        put("ACTIVE MEMORY: " + card.memory() + " (" + CardRules.memoryCost(card) + "E)  Target: " + CardRules.memoryTarget(card), 5, 26, AsciiPanel.brightCyan);
        put(fit(CardRules.memoryDescription(card), 108), 5, 27, Color.LIGHT_GRAY);
        put("PASSIVE: " + card.passive() + " - " + fit(CardRules.passiveDescription(card), 86), 5, 29, AsciiPanel.brightMagenta);
        put("TRAIT: " + card.trait() + " [" + (card.traitUsed() ? "USED" : "READY") + "] - " + fit(CardRules.traitDescription(card), 78), 5, 31, AsciiPanel.brightYellow);
        put("Press [I] for the full card and Memory inspection layer.", 5, 32, Color.GRAY);
    }

    private void logAndControlsLayer() {
        horizontal(3, 34, 115, '='); put("COMBAT LOG", 5, 35, AsciiPanel.brightWhite); String[] logs = combat.logLines();
        for (int i = 0; i < 4; i++) put(i < logs.length ? fit(logs[i], 108) : "", 5, 36 + i, i == 0 ? Color.WHITE : Color.GRAY);
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
        if (enemyPhase) return;
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
            case KeyEvent.VK_A -> message = combat.attack(source); case KeyEvent.VK_M -> message = combat.memory(source, target); case KeyEvent.VK_G -> message = combat.guard(source);
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
                }
            }
            case FORGE -> { if (event.getKeyCode() == KeyEvent.VK_ESCAPE) screen = Screen.OVERWORLD; }
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
    private void selectFirstLiving() {
        for (Lane lane : Lane.values()) if (combat.card(lane) != null && combat.card(lane).isAlive()) { source = target = lane; return; }
        source = target = Lane.LEFT;
    }
    private void beginEnemyResponse() {
        if (combat.finished()) { message = "ORDER COMPLETE. PRESS R TO RESTART."; return; }
        enemyPhase = true; message = "Enemy is executing the displayed intent...";
        Timer timer = new Timer(1100, event -> { combat.endPhase(); enemyPhase = false; message = combat.finished() ? "Combat ended. Press R to restart." : "New player phase. Read the hostile lane marker before acting."; render(); });
        timer.setRepeats(false); timer.start();
    }

    private void box(int x, int y, int w, int h, Color color, Color bg) {
        put("+" + "-".repeat(w - 2) + "+", x, y, color, bg); put("+" + "-".repeat(w - 2) + "+", x, y + h - 1, color, bg);
        for (int row = y + 1; row < y + h - 1; row++) { put("|", x, row, color, bg); put("|", x + w - 1, row, color, bg); }
    }
    private void horizontal(int x, int y, int width, char character) { put(String.valueOf(character).repeat(width), x, y, Color.DARK_GRAY); }
    private void moveWorld(int dx, int dy) { worldX = Math.max(5, Math.min(109, worldX + dx)); worldY = Math.max(8, Math.min(47, worldY + dy)); }
    private boolean nearCurrentEncounter() { return encounterIndex < route.length && Math.abs(worldX - encounterPositions[encounterIndex][0]) + Math.abs(worldY - encounterPositions[encounterIndex][1]) <= 1; }
    private boolean nearForge() { return Math.abs(worldX - 53) + Math.abs(worldY - 38) <= 2; }
    private void centered(String text, int y, Color color) { put(text, Math.max(2, (SCREEN_WIDTH - text.length()) / 2), y, color); }
    private String fit(String text, int width) { return text.length() <= width ? text : text.substring(0, width - 3) + "..."; }
    private void put(String text, int x, int y, Color fg) { terminal.write(text, x, y, fg); }
    private void put(String text, int x, int y, Color fg, Color bg) { terminal.write(text, x, y, fg, bg); }
    @Override public void keyTyped(KeyEvent event) { }
    @Override public void keyReleased(KeyEvent event) { }
}
