package org.gunmetalblack.rkis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** A Java2D presentation layer for RKIS. The combat model remains completely UI-agnostic. */
public final class RKISGraphicalGame extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private enum View { TITLE, STORY, OVERWORLD, COMBAT, REWARD, FORGE, REPORT }
    private static final Color VOID = new Color(3, 6, 12), PANEL = new Color(7, 16, 27, 235);
    private static final Color CYAN = new Color(55, 232, 255), MAGENTA = new Color(255, 55, 184);
    private static final Color RED = new Color(255, 55, 78), LIME = new Color(135, 255, 126), AMBER = new Color(255, 198, 70);
    private static final Font PIXEL_FONT = loadPixelFont();
    private final Roster roster = new Roster();
    private final Encounter[] route = Encounter.values();
    private final Random random = new Random(42);
    private final List<Particle> particles = new ArrayList<>();
    private final BufferedImage backdrop = createBackdropTexture();
    private CombatState combat = new CombatState(Encounter.DRONE, roster);
    private View view = View.TITLE;
    private Lane source = Lane.CENTER, target = Lane.CENTER;
    private int encounterIndex, hoverLane = -1, hoverButton = -1, pulse, shake, storyPage, fibers, forgeSlot = -1;
    private int effectFrames, effectTotal;
    private String effectKind = "";
    private View pendingView;
    private Lane enemyBeamTarget = Lane.CENTER;
    private double worldX = 145, worldY = 590;
    private boolean inspect;
    private static final double[][] WORLD_NODES = {
            {280, 565},  // Compliance Drone
            {490, 385},  // Overtime Collector
            {720, 535},  // Len / The Lie
            {950, 355},  // Paywall / The Trap
            {1180,520}   // Armstrong / The Truth
    };
    private static final double FORGE_X = 720, FORGE_Y = 690;
    private String message = "SELECT A UNIT // ISSUE A COMMAND";
    private final Rectangle2D[] cardHits = {new Rectangle2D.Double(), new Rectangle2D.Double(), new Rectangle2D.Double()};
    private final Rectangle2D[] buttons = {new Rectangle2D.Double(), new Rectangle2D.Double(), new Rectangle2D.Double(), new Rectangle2D.Double()};

    public RKISGraphicalGame() {
        setPreferredSize(new Dimension(1440, 900));
        setMinimumSize(new Dimension(960, 600));
        setBackground(VOID); setOpaque(true); setDoubleBuffered(true);
        setFocusable(true); addKeyListener(this); addMouseListener(this); addMouseMotionListener(this);
        new Timer(33, e -> tick()).start();
    }

    private void tick() {
        pulse++;
        if (effectFrames > 0 && --effectFrames == 0) {
            effectKind = "";
            if (pendingView != null) { view = pendingView; pendingView = null; }
        }
        for (int i = particles.size() - 1; i >= 0; i--) if (!particles.get(i).tick()) particles.remove(i);
        if (shake > 0) shake--;
        repaint();
    }

    @Override protected void paintComponent(Graphics raw) {
        super.paintComponent(raw);
        Graphics2D g = (Graphics2D) raw.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        double scale = Math.min(getWidth() / 1440.0, getHeight() / 900.0);
        double ox = (getWidth() - 1440 * scale) / 2, oy = (getHeight() - 900 * scale) / 2;
        g.translate(ox + (shake > 0 ? random.nextInt(9) - 4 : 0), oy); g.scale(scale, scale);
        drawBackdrop(g);
        switch (view) {
            case TITLE -> drawTitle(g);
            case STORY -> drawStory(g);
            case OVERWORLD -> drawOverworld(g);
            case COMBAT -> drawBattle(g);
            case REWARD -> drawReward(g);
            case FORGE -> drawForge(g);
            case REPORT -> drawReport(g);
        }
        g.dispose();
    }

    private void drawBackdrop(Graphics2D g) {
        // This BufferedImage becomes an accelerated texture after its first draw.
        // The expensive perspective grid and thousands of scanline pixels are not
        // reconstructed every animation frame.
        g.drawImage(backdrop, 0, 0, null);
        for (int i = 0; i < 18; i++) {
            int x = (i * 193) % 1440, y = (i * 97 + pulse / 3) % 900;
            g.setColor(i % 3 == 0 ? new Color(255, 40, 170, 65) : new Color(40, 220, 255, 55)); g.fillRect(x, y, 2, 2);
        }
    }

    private static BufferedImage createBackdropTexture() {
        BufferedImage image = new BufferedImage(1440, 900, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setPaint(new GradientPaint(0, 0, new Color(2, 4, 10), 0, 900, new Color(8, 18, 28))); g.fillRect(0, 0, 1440, 900);
        g.setColor(new Color(31, 126, 153, 24));
        for (int x = -900; x < 2300; x += 55) g.drawLine(720, 310, x, 900);
        for (int y = 370; y < 900; y += 45) g.drawLine(0, y, 1440, y);
        g.setColor(new Color(65, 220, 255, 17));
        for (int y = 0; y < 900; y += 4) g.drawLine(0, y, 1440, y);
        g.dispose();
        return image;
    }

    private void drawTitle(Graphics2D g) {
        glowText(g, "R  K  I  S", 720, 265, 88, CYAN, true);
        text(g, "RECOVERY KERNEL INTEGRATION SYSTEM", 720, 318, 18, Color.WHITE, true);
        text(g, "TACTICAL CONSCIOUSNESS INTERFACE", 720, 352, 13, new Color(145, 173, 190), true);
        g.setStroke(new BasicStroke(2)); g.setColor(new Color(55, 232, 255, 100)); g.draw(new RoundRectangle2D.Double(480, 430, 480, 100, 12, 12));
        text(g, "EMPLOYEE SESSION READY", 720, 466, 15, RED, true);
        text(g, "ENTER SYSTEM", 720, 507, 24, Color.WHITE, true);
        text(g, "[ ENTER ]", 720, 620, 18, (pulse / 18) % 2 == 0 ? LIME : new Color(70, 110, 90), true);
        text(g, "MOUSE + KEYBOARD  //  2.5D BOARD PROTOCOL", 720, 790, 12, new Color(95, 125, 143), true);
    }

    private void drawBattle(Graphics2D g) {
        drawTopBar(g); drawBoard(g); drawEnemy(g); drawCards(g); drawCommandDeck(g); drawParticles(g); drawCombatEffect(g);
        drawCardInformation(g);
        if (inspect) drawInspector(g);
        if (combat.needsReplacement()) drawReplacement(g);
    }

    private void drawTopBar(Graphics2D g) {
        g.setColor(PANEL); g.fillRect(0, 0, 1440, 82); g.setColor(new Color(55, 232, 255, 90)); g.drawLine(0, 81, 1440, 81);
        text(g, "RKIS // COMBAT NODE", 34, 34, 17, CYAN, false);
        text(g, String.format("WORK ORDER %02d", encounterIndex + 1), 34, 59, 11, new Color(130, 155, 171), false);
        text(g, "ROUND " + String.format("%02d", combat.round()), 718, 34, 13, Color.WHITE, true);
        text(g, "PLAYER PHASE", 718, 58, 11, LIME, true);
        text(g, "ENERGY", 1110, 31, 11, new Color(145, 170, 185), false);
        for (int i = 0; i < 9; i++) {
            g.setColor(i < combat.energy() ? CYAN : new Color(28, 53, 64));
            g.fill(new RoundRectangle2D.Double(1180 + i * 24, 20, 16, 32, 3, 3));
        }
        text(g, combat.energy() + "/9", 1405, 67, 11, Color.WHITE, false);
    }

    private void drawBoard(Graphics2D g) {
        Path2D table = new Path2D.Double(); table.moveTo(355, 250); table.lineTo(1085, 250); table.lineTo(1325, 760); table.lineTo(115, 760); table.closePath();
        g.setPaint(new GradientPaint(0, 250, new Color(14, 31, 42), 0, 760, new Color(5, 12, 20))); g.fill(table);
        g.setStroke(new BasicStroke(3)); g.setColor(new Color(41, 205, 231, 90)); g.draw(table);
        for (int lane = 0; lane < 3; lane++) {
            double topL = 355 + lane * 243, topR = topL + 243, bottomL = 115 + lane * 403, bottomR = bottomL + 403;
            Path2D p = new Path2D.Double(); p.moveTo(topL, 250); p.lineTo(topR, 250); p.lineTo(bottomR, 760); p.lineTo(bottomL, 760); p.closePath();
            g.setColor(lane == target.ordinal() ? new Color(44, 190, 220, 20) : new Color(0, 0, 0, 0)); g.fill(p);
            g.setColor(new Color(55, 232, 255, lane == target.ordinal() ? 95 : 28)); g.draw(p);
        }
        text(g, "HOSTILE ZONE", 720, 234, 10, RED, true); text(g, "OPERATOR ZONE", 720, 783, 10, CYAN, true);
    }

    private void drawEnemy(Graphics2D g) {
        Enemy e = combat.enemy();
        double bob = Math.sin(pulse * .08) * 5;
        g.setColor(new Color(255, 35, 75, 22)); g.fill(new Ellipse2D.Double(560, 125 + bob, 320, 210));
        drawEnemyAvatar(g, e.encounter(), 720, 185 + bob);
        textCenteredFitted(g, e.name().toUpperCase(), 720, 101, 18, Color.WHITE, 470);
        drawBar(g, 555, 257, 330, 13, e.hp(), e.maxHp(), RED);
        text(g, "HP " + e.hp() + "/" + e.maxHp() + "   DEF " + e.defense() + "   SH " + e.shield(), 720, 287, 11, new Color(235, 175, 185), true);
        g.setColor(new Color(25, 8, 15, 235)); g.fill(new RoundRectangle2D.Double(500, 303, 440, 45, 8, 8));
        text(g, "NEXT // " + e.intentText(), 720, 331, 12, AMBER, true);
    }

    private void drawEnemyAvatar(Graphics2D g, Encounter encounter, double x, double y) {
        g.setStroke(new BasicStroke(3));
        String[] originalArt = BossArt.forEncounter(encounter);
        if (originalArt.length > 0) {
            drawAsciiBoss(g, originalArt, encounter, x, y);
            return;
        }
        switch (encounter) {
            case DRONE -> {
                g.setColor(RED); g.draw(new Ellipse2D.Double(x-58,y-58,116,116)); g.setColor(new Color(25,5,12));g.fill(new Ellipse2D.Double(x-50,y-50,100,100));
                g.setColor(RED);g.fill(new Arc2D.Double(x-35,y-35,70,70,20+pulse*2,130,Arc2D.PIE));g.setColor(Color.WHITE);g.fill(new Ellipse2D.Double(x-9,y-9,18,18));
            }
            case COLLECTOR -> {
                g.setColor(new Color(255,105,50));g.draw(new Ellipse2D.Double(x-62,y-62,124,124));
                for(int i=0;i<8;i++){double a=Math.toRadians(i*45+pulse);double ex=x+Math.cos(a)*50,ey=y+Math.sin(a)*50;g.draw(new Line2D.Double(x,y,ex,ey));}
                g.setColor(new Color(35,12,6));g.fill(new Ellipse2D.Double(x-39,y-39,78,78));g.setColor(AMBER);text(g,"$",x,y+17,38,AMBER,true);
            }
            case LEN -> {
                g.setColor(MAGENTA);g.draw(new RoundRectangle2D.Double(x-62,y-60,124,120,20,20));g.draw(new Line2D.Double(x-35,y-15,x-12,y-5));g.draw(new Line2D.Double(x+12,y-5,x+35,y-15));
                g.setColor(Color.WHITE);g.fill(new Rectangle2D.Double(x-32,y-15,18,7));g.fill(new Rectangle2D.Double(x+14,y-15,18,7));g.setColor(MAGENTA);g.drawArc((int)x-28,(int)y+8,56,28,200,140);
                for(int i=0;i<3;i++)g.draw(new Arc2D.Double(x-82-i*13,y-25-i*13,164+i*26,50+i*26,195,150,Arc2D.OPEN));
            }
            case PAYWALL -> {
                g.setColor(AMBER);g.draw(new RoundRectangle2D.Double(x-58,y-35,116,94,10,10));g.draw(new Arc2D.Double(x-38,y-72,76,78,0,180,Arc2D.OPEN));
                g.setColor(new Color(35,23,4));g.fill(new RoundRectangle2D.Double(x-48,y-25,96,74,7,7));g.setColor(AMBER);g.fill(new Ellipse2D.Double(x-9,y-5,18,18));g.fill(new Rectangle2D.Double(x-4,y+8,8,19));
            }
            case ARMSTRONG -> {
                double r=58+Math.sin(pulse*.15)*5;g.setColor(Color.WHITE);g.draw(new Ellipse2D.Double(x-r,y-r,r*2,r*2));g.setColor(RED);g.draw(new Ellipse2D.Double(x-r+10,y-r+10,(r-10)*2,(r-10)*2));
                g.setColor(new Color(18,3,8));g.fill(new Ellipse2D.Double(x-42,y-42,84,84));g.setColor(Color.WHITE);g.fill(new Rectangle2D.Double(x-28,y-13,20,8));g.fill(new Rectangle2D.Double(x+8,y-13,20,8));
                g.setColor(RED);g.draw(new Line2D.Double(x-25,y+24,x+25,y+24));g.draw(new Line2D.Double(x,y-42,x,y+42));
            }
        }
    }

    private void drawAsciiBoss(Graphics2D g, String[] art, Encounter encounter, double centerX, double centerY) {
        int fontSize = 6, lineHeight = 7;
        Font font = pixelFont(fontSize);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        int maxWidth = 0;
        for (String row : art) maxWidth = Math.max(maxWidth, metrics.stringWidth(row));
        double left = centerX - maxWidth / 2.0;
        double top = centerY - art.length * lineHeight / 2.0;
        Color base = switch (encounter) { case LEN -> MAGENTA; case PAYWALL -> AMBER; case ARMSTRONG -> RED; default -> RED; };
        int visibleRows = effectKind.startsWith("BOSS_INTRO")
                ? Math.max(1, (int) Math.ceil(art.length * (1.0 - (double) effectFrames / Math.max(1, effectTotal)))) : art.length;
        boolean dying = effectKind.equals("BOSS_DEATH");
        for (int row = 0; row < visibleRows; row++) {
            String line = art[row];
            if (dying && (row + effectFrames / 3) % 4 == 0)
                line = line.replace('M', '.').replace('X', '.').replace('N', '.').replace('K', '.').replace('O', '.').replace('k', '.');
            double jitter = dying ? ((row * 17 + effectFrames) % 5 - 2) * 2.0 : 0;
            float px = (float) (left + jitter), py = (float) (top + row * lineHeight + fontSize);
            g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 42));
            g.drawString(line, px - 2, py); g.drawString(line, px + 2, py); g.drawString(line, px, py - 2); g.drawString(line, px, py + 2);
            g.setColor(dying && effectFrames % 8 < 4 ? Color.WHITE : base);
            g.drawString(line, px, py);
        }
    }

    private void drawCards(Graphics2D g) {
        for (Lane lane : Lane.values()) {
            int i = lane.ordinal(); Card c = combat.card(lane);
            double x = 225 + i * 365, y = 505 - (i == 1 ? 8 : 0), w = 260, h = 210;
            cardHits[i].setRect(x, y, w, h);
            boolean selected = source == lane, hovered = hoverLane == i;
            if (selected || hovered) y -= selected ? 20 : 8;
            Color type = c == null ? new Color(70, 75, 80) : switch (c.type()) { case STEEL -> new Color(115, 200, 255); case MAGIE -> MAGENTA; case GENERIC -> CYAN; };
            g.setColor(new Color(0, 0, 0, 150)); g.fill(new RoundRectangle2D.Double(x + 12, y + 18, w, h, 14, 14));
            g.setPaint(new GradientPaint((float)x, (float)y, new Color(18, 34, 48), (float)x, (float)(y+h), new Color(4, 9, 16))); g.fill(new RoundRectangle2D.Double(x, y, w, h, 14, 14));
            g.setStroke(new BasicStroke(selected ? 4 : 2)); g.setColor(selected ? AMBER : type); g.draw(new RoundRectangle2D.Double(x, y, w, h, 14, 14));
            text(g, lane.name(), x + 18, y + 27, 10, type, false);
            if (c == null) { text(g, "EMPTY", x + w / 2, y + 115, 19, new Color(90, 100, 105), true); continue; }
            boolean obscured = combat.memoryLoss() || combat.distortedLane() == lane;
            if (obscured) {
                g.setColor(new Color(255, 55, 184, 28));
                for (int glitch = 0; glitch < 8; glitch++)
                    g.fillRect((int) x + 12 + (glitch * 31 + pulse * 3) % 220, (int) y + 38 + glitch * 19, 42, 5);
                text(g, "UNKNOWN", x + w / 2, y + 78, 18, RED, true);
                text(g, "DATA // DISTORTED", x + w / 2, y + 112, 11, MAGENTA, true);
                drawBar(g, x + 18, y + 145, w - 36, 10, 0, 1, RED);
                text(g, "HP ??/??", x + 18, y + 178, 11, RED, false);
                textRightFitted(g, "MEMORY LOST", x + w - 18, y + 178, 10, MAGENTA, 112);
                text(g, "[ I ] RECOVER DATA", x + 18, y + 198, 9, AMBER, false);
                continue;
            }
            drawUnitIcon(g, x + 50, y + 79, c.type(), type);
            text(g, c.name(), x + 92, y + 65, 17, Color.WHITE, false); text(g, c.type().name(), x + 92, y + 87, 10, type, false);
            text(g, "ATK " + c.attack() + "   DEF " + c.defense(), x + 92, y + 116, 12, new Color(190, 210, 220), false);
            drawBar(g, x + 18, y + 145, w - 36, 10, c.hp(), c.maxHp(), c.hp() * 3 < c.maxHp() ? RED : LIME);
            text(g, "HP " + c.hp() + "/" + c.maxHp(), x + 18, y + 178, 11, Color.WHITE, false);
            textRightFitted(g, c.memory(), x + w - 18, y + 178, 10, MAGENTA, 112);
            text(g, "SHIELD " + c.shield(), x + 18, y + 198, 9, new Color(120, 205, 255), false);
        }
    }

    private void drawUnitIcon(Graphics2D g, double x, double y, CardType type, Color color) {
        g.setStroke(new BasicStroke(3)); g.setColor(color); g.draw(new Ellipse2D.Double(x - 25, y - 25, 50, 50));
        if (type == CardType.STEEL) g.draw(new Rectangle2D.Double(x - 13, y - 13, 26, 26));
        else if (type == CardType.MAGIE) { Path2D p = new Path2D.Double(); p.moveTo(x, y - 18); p.lineTo(x + 17, y + 15); p.lineTo(x - 17, y + 15); p.closePath(); g.draw(p); }
        else { g.drawLine((int)x - 15, (int)y, (int)x + 15, (int)y); g.drawLine((int)x, (int)y - 15, (int)x, (int)y + 15); }
    }

    private void drawCommandDeck(Graphics2D g) {
        g.setColor(PANEL); g.fill(new RoundRectangle2D.Double(70, 795, 1300, 90, 12, 12)); g.setColor(new Color(55, 232, 255, 65)); g.draw(new RoundRectangle2D.Double(70, 795, 1300, 90, 12, 12));
        Card selected = combat.card(source);
        String[] labels = {"ATTACK  " + (selected == null ? "-" : combat.attackCost(source) + "E"),
                "MEMORY  " + (selected == null ? "-" : combat.memoryCost(source) + "E"), "GUARD  " + (selected == null ? "-" : combat.attackCost(source) + "E"), "END PHASE"}; Color[] colors = {RED, MAGENTA, CYAN, AMBER};
        for (int i = 0; i < 4; i++) {
            double x = 470 + i * 205; buttons[i].setRect(x, 817, 180, 46);
            g.setColor(i == hoverButton ? new Color(colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), 70) : new Color(12, 25, 36)); g.fill(new RoundRectangle2D.Double(x, 817, 180, 46, 7, 7));
            g.setColor(colors[i]); g.draw(new RoundRectangle2D.Double(x, 817, 180, 46, 7, 7)); text(g, labels[i], x + 90, 846, 12, Color.WHITE, true);
        }
        Card c = combat.card(source); text(g, c == null ? "NO UNIT" : c.name(), 95, 826, 14, Color.WHITE, false);
        textFitted(g, message, 95, 853, 10, new Color(145, 177, 193), 345); text(g, "1-3 SELECT  •  I INSPECT", 95, 873, 9, CYAN, false);
    }

    private void drawParticles(Graphics2D g) { for (Particle p : particles) { g.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), Math.max(0, p.life * 10))); g.fill(new Ellipse2D.Double(p.x, p.y, p.size, p.size)); } }

    private void drawCombatEffect(Graphics2D g) {
        if (effectFrames <= 0) return;
        double progress = 1.0 - (double) effectFrames / Math.max(1, effectTotal);
        if (effectKind.equals("PLAYER_ATTACK") || effectKind.equals("PLAYER_MEMORY")) {
            double sx=355+source.ordinal()*365, sy=540, ex=720, ey=205, t=Math.min(1,progress*2.4);
            double x=sx+(ex-sx)*t,y=sy+(ey-sy)*t;Color c=effectKind.equals("PLAYER_MEMORY")?MAGENTA:AMBER;
            g.setStroke(new BasicStroke(7));g.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),90));g.draw(new Line2D.Double(sx,sy,x,y));g.setColor(Color.WHITE);g.fill(new Ellipse2D.Double(x-8,y-8,16,16));
        } else if (effectKind.equals("GUARD")) {
            double x=355+source.ordinal()*365,y=585,r=75+Math.sin(pulse*.4)*8;g.setColor(new Color(55,232,255,35));g.fill(new Ellipse2D.Double(x-r,y-r,r*2,r*2));g.setColor(CYAN);g.draw(new Ellipse2D.Double(x-r,y-r,r*2,r*2));
        } else if (effectKind.startsWith("BOSS_INTRO")) {
            g.setColor(new Color(0,0,0,155));g.fillRect(0,0,1440,900);String title=switch(combat.enemy().encounter()){case DRONE->"COMPLIANCE PROCESS ATTACHED";case COLLECTOR->"OVERTIME HAS COME TO COLLECT";case LEN->"THE LIE CONTROLS THE MESSAGE";case PAYWALL->"THE TRAP HAS PRICED YOUR LIFE";case ARMSTRONG->"THE TRUTH REMEMBERS YOU";};
            glowText(g,title,720,420,30,effectFrames%10<5?RED:Color.WHITE,true);text(g,combat.enemy().name(),720,475,18,MAGENTA,true);
        } else if (effectKind.equals("LEN_EFFECT")) {
            String[] words={"FACT","BIAS","TRUST","SPIN","???"};for(int i=0;i<14;i++)text(g,words[(i+pulse/3)%words.length],90+(i%7)*205,170+(i/7)*260,18,i%2==0?MAGENTA:RED,true);
            text(g,"MEDIA BIAS // DISPLAY CORRUPTED",720,430,25,Color.WHITE,true);
        } else if (effectKind.equals("PAYWALL_EFFECT")) {
            for(int i=0;i<22;i++)text(g,"$",80+(i%11)*130,170+(i/11)*420+(pulse+i*17)%80,24,AMBER,true);text(g,"HIDDEN FEES APPLIED",720,430,28,RED,true);
        } else if (effectKind.equals("ARMSTRONG_EFFECT")) {
            g.setColor(new Color(255,255,255,effectFrames%6<3?65:12));g.fillRect(0,0,1440,900);text(g,"YOU ARE NOT PLAYING",720+(pulse%7-3)*4,390,32,Color.WHITE,true);text(g,"YOU ARE WORKING",720-(pulse%5-2)*5,450,32,RED,true);
        } else if (effectKind.equals("ENEMY_ATTACK")) {
            double ex=720,ey=220,tx=355+enemyBeamTarget.ordinal()*365,ty=555,t=Math.min(1,progress*2.4);g.setStroke(new BasicStroke(9));g.setColor(RED);g.draw(new Line2D.Double(ex,ey,ex+(tx-ex)*t,ey+(ty-ey)*t));
        } else if (effectKind.equals("BOSS_DEATH")) {
            g.setColor(new Color(255,255,255,effectFrames%8<4?80:15));g.fillRect(0,0,1440,900);glowText(g,"HOSTILE PROCESS COLLAPSED",720,420,34,LIME,true);text(g,combat.enemy().name()+" // TERMINATED",720,475,17,Color.WHITE,true);
        }
    }

    private void command(int command) {
        if (view != View.COMBAT || combat.finished() || effectFrames > 0) return;
        int before = combat.actions(), energyBefore = combat.energy();
        boolean enemyWasAttacking = command == 3 && combat.enemy().isAttackIntent();
        if (enemyWasAttacking) enemyBeamTarget = announcedEnemyTarget();
        message = switch (command) { case 0 -> combat.attack(source); case 1 -> combat.memory(source, target); case 2 -> combat.guard(source); default -> combat.endPhase(); };
        boolean actionSucceeded = combat.actions() > before || combat.energy() < energyBefore;
        if (command == 3) { burst(720, 350, RED); shake = 5; startEffect(enemyWasAttacking ? "ENEMY_ATTACK" : switch(combat.enemy().encounter()){case LEN->"LEN_EFFECT";case PAYWALL->"PAYWALL_EFFECT";case ARMSTRONG->"ARMSTRONG_EFFECT";default->"ENEMY_ATTACK";},42); }
        else if (actionSucceeded) { burst(command == 1 ? 720 : 840, 400, command == 1 ? MAGENTA : command == 2 ? CYAN : AMBER); shake = command == 0 ? 7 : 0; startEffect(command==0?"PLAYER_ATTACK":command==1?"PLAYER_MEMORY":"GUARD",28); }
        if (combat.finished()) { pendingView=combat.victorious()?View.REWARD:View.REPORT;startEffect("BOSS_DEATH",60); }
    }

    private void burst(double x, double y, Color color) { for (int i = 0; i < 32; i++) particles.add(new Particle(x, y, random.nextDouble() * 12 - 6, random.nextDouble() * 10 - 5, color)); }
    private void newEncounter() { combat = new CombatState(route[encounterIndex], roster); view = View.COMBAT; source = target = Lane.CENTER; message = "READ HOSTILE INTENT // SELECT UNIT"; startEffect("BOSS_INTRO",60); requestFocusInWindow(); }
    private void startEffect(String kind,int frames){effectKind=kind;effectFrames=effectTotal=frames;}
    private Lane announcedEnemyTarget() {
        Enemy enemy = combat.enemy();
        if (enemy.encounter() == Encounter.PAYWALL && enemy.roundIndex() % 4 == 2) {
            Lane result = Lane.LEFT;
            int lowest = Integer.MAX_VALUE;
            for (Lane lane : Lane.values()) {
                Card card = combat.card(lane);
                if (card != null && card.isAlive() && card.hp() < lowest) { lowest = card.hp(); result = lane; }
            }
            return result;
        }
        return enemy.targetLane();
    }

    private void drawStory(Graphics2D g) {
        String[][] pages = {
                {"You wake beneath a sky that cannot decide what color it is.", "A figure shaped like the number 8 waits beside a dead terminal.", "THE UNKNOWN: There once was a time I was like you, fellow player."},
                {"These cards are not weapons. They are what remains.", "Three stand together. Two wait in reserve. Every action draws", "from the same Energy supply. Spend it like it belongs to everyone."},
                {"Victory earns Fibers. Failure does too. The machine wastes no labor.", "Climb through The Lie, The Trap, and The Truth.", "Try to remember why you signed the contract."}
        };
        panel(g, 210, 130, 1020, 620, CYAN);
        text(g, "THE UNKNOWN // ONBOARDING", 260, 190, 17, MAGENTA, false);
        textRightFitted(g, "DESIGNATION: PLAYER_2348921", 1170, 190, 12, AMBER, 330);
        int y=295; for(String line:pages[storyPage]) { textCenteredFitted(g,line,720,y,18,line.startsWith("THE UNKNOWN")?MAGENTA:Color.WHITE,880); y+=70; }
        text(g,"PAGE " + (storyPage+1) + "/3",270,700,11,new Color(130,155,170),false);
        text(g,storyPage<2?"[ ENTER ] CONTINUE":"[ ENTER ] ACCEPT WORK ORDER",1160,700,13,LIME,true);
    }

    private void drawOverworld(Graphics2D g) {
        text(g,"FABRICATED OVERWORLD",55,62,24,CYAN,false); text(g,"FIBERS  " + fibers,1360,62,16,AMBER,true);
        panel(g,70,105,1300,680,new Color(45,140,165));
        g.setColor(new Color(13,35,38)); g.fill(new RoundRectangle2D.Double(90,125,1260,640,10,10));
        g.setStroke(new BasicStroke(5));
        for(int i=0;i<WORLD_NODES.length-1;i++){g.setColor(i<encounterIndex?LIME:new Color(45,76,82));g.draw(new Line2D.Double(WORLD_NODES[i][0],WORLD_NODES[i][1],WORLD_NODES[i+1][0],WORLD_NODES[i+1][1]));}
        for(int i=0;i<WORLD_NODES.length;i++){
            Color c=i<encounterIndex?LIME:i==encounterIndex?RED:new Color(67,82,88);
            g.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),35));g.fill(new Ellipse2D.Double(WORLD_NODES[i][0]-42,WORLD_NODES[i][1]-42,84,84));
            g.setColor(c);g.fill(new Ellipse2D.Double(WORLD_NODES[i][0]-13,WORLD_NODES[i][1]-13,26,26));
            text(g,i<encounterIndex?"CLEARED":route[i].displayName(),WORLD_NODES[i][0],WORLD_NODES[i][1]-30,11,c,true);
        }
        double fx=FORGE_X,fy=FORGE_Y;
        g.setColor(new Color(55,232,255,35)); g.fill(new Ellipse2D.Double(fx-38,fy-38,76,76));
        g.setStroke(new BasicStroke(4)); g.setColor(Color.WHITE); g.draw(new Ellipse2D.Double(fx-30,fy-30,60,60));
        g.setStroke(new BasicStroke(2)); g.setColor(CYAN); g.draw(new Ellipse2D.Double(fx-22,fy-22,44,44));
        text(g,"F",fx,fy+7,16,Color.WHITE,true); text(g,"FORGE",fx,fy+58,11,CYAN,true);
        g.setColor(AMBER);g.fill(new Ellipse2D.Double(worldX-12,worldY-12,24,24));g.setColor(Color.WHITE);g.draw(new Ellipse2D.Double(worldX-19,worldY-19,38,38));
        text(g,"YOU",worldX,worldY-27,10,AMBER,true);
        String prompt=nearWorldNode()?"[ ENTER ] ENGAGE " + route[encounterIndex].displayName():nearForge()?"[ F ] OPEN CARD FORGE":"WASD / ARROWS TO MOVE";
        text(g,prompt,720,835,15,nearWorldNode()?RED:CYAN,true);
    }

    private void drawReward(Graphics2D g) {
        Encounter won=route[encounterIndex]; panel(g,245,145,950,610,LIME);
        glowText(g,"WORK ORDER COMPLETE",720,235,36,LIME,true); text(g,won.displayName()+" TERMINATED",720,285,17,Color.WHITE,true);
        text(g,"TRAINING DATA CONVERTED  //  +"+won.fibers()+" FIBERS",720,345,15,AMBER,true);
        String[] reveal=switch(won){
            case DRONE -> new String[]{"A damaged card whispers with the Coal Miner's voice.","The workers inside RKIS were once real people."};
            case COLLECTOR -> new String[]{"The walls call our names. The cycle always repeats.","Work. Struggle. Adapt. Decay. Disappear."};
            case LEN -> new String[]{"THE UNKNOWN: Len was the story that sold the machine.","You entered RKIS as an employee, not an adventurer."};
            case PAYWALL -> new String[]{"RKIS sold your own work back to you as survival.","Beyond the gate, an old consciousness waits."};
            case ARMSTRONG -> new String[]{"Your physical body still sits wired to the Mainframe.","Every escape attempt trained the system too."};};
        int y=445;for(String line:reveal){text(g,line,720,y,16,Color.WHITE,true);y+=55;}
        text(g,"[ ENTER ] RETURN TO OVERWORLD",720,700,14,LIME,true);
    }

    private void drawForge(Graphics2D g) {
        text(g,"CARD FORGE // MEMORY WORKSHOP",60,65,23,CYAN,false);text(g,"FIBERS  "+fibers,1350,65,16,AMBER,true);
        panel(g,100,120,1240,665,CYAN); text(g,"RECONSTRUCT A PLAUGE CARD",720,175,16,Color.WHITE,true);
        int y=230;
        for(int i=0;i<roster.cards().size();i++){
            Card c=roster.cards().get(i); Color color=c.isAlive()?new Color(95,120,130):RED;
            g.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),20));g.fill(new RoundRectangle2D.Double(170,y-32,1100,75,8,8));
            text(g,"["+(i+1)+"]  "+(c.isAlive()?c.name():"PLAUGE"),205,y,15,color,false);
            text(g,c.type()+"   HP "+c.hp()+"/"+c.maxHp()+"   ATK "+c.attack()+"   DEF "+c.defense()+"   MEMORY "+c.memory(),480,y,12,color,false);y+=95;
        }
        if(forgeSlot>=0) text(g,"SELECT RECIPE: [1] GENERIC 5F   [2] STEEL 7F   [3] MAGIE 7F   // ESC CANCEL",720,750,13,LIME,true);
        else text(g,"SELECT A RED PLAUGE SLOT [1-5]   //   ESC RETURN",720,750,13,CYAN,true);
    }

    private void drawReport(Graphics2D g) {
        panel(g,250,150,940,600,RED);glowText(g,combat.finished()&&combat.victorious()?"RUN COMPLETE":"RECOVERY FAILED",720,250,38,RED,true);
        text(g,"WORK ORDERS COMPLETED  "+encounterIndex,720,330,16,Color.WHITE,true);text(g,"FIBERS GENERATED  "+fibers,720,380,16,AMBER,true);
        text(g,"ARMSTRONG: You're not playing. You're working.",720,480,18,MAGENTA,true);
        text(g,"Every choice trained RKIS. Even reaching this screen was useful.",720,535,15,Color.WHITE,true);
        text(g,"[ ENTER ] BEGIN ANOTHER WORK DAY",720,680,14,LIME,true);
    }

    private void drawCardInformation(Graphics2D g) {
        Card c=combat.card(source); if(c==null)return;
        panel(g,18,105,345,238,source==target?AMBER:CYAN);
        if (combat.memoryLoss() || combat.distortedLane() == source) {
            text(g,"CARD DATA // CORRUPTED",38,145,14,RED,false);
            text(g,"MEDIA BIAS HAS REPLACED",38,190,11,MAGENTA,false);
            text(g,"THE DISPLAYED RECORD.",38,215,11,MAGENTA,false);
            text(g,"[ I ] RECOVER TRUE MEMORY",38,290,11,AMBER,false);
            return;
        }
        text(g,"SELECTED // "+c.name()+" // "+source,38,137,12,Color.WHITE,false);
        text(g,"HP "+c.hp()+"/"+c.maxHp()+"   ATK "+c.attack()+"   DEF "+c.defense()+"   SH "+c.shield(),38,165,11,LIME,false);
        text(g,"ACTIVE MEMORY",38,199,10,MAGENTA,false);textFitted(g,c.memory()+"  ["+combat.memoryCost(source)+"E]",38,220,12,Color.WHITE,305);
        drawWrapped(g,CardRules.memoryDescription(c),38,245,300,11,new Color(170,190,200),2);
        text(g,"PASSIVE  "+c.passive(),38,293,10,CYAN,false);text(g,"TRAIT  "+c.trait()+"  ["+(c.traitUsed()?"USED":"READY")+"]",38,318,10,AMBER,false);
    }

    private void drawInspector(Graphics2D g) {
        Card c=combat.card(source);if(c==null)return;g.setColor(new Color(0,0,0,215));g.fillRect(0,0,1440,900);panel(g,230,120,980,650,MAGENTA);
        glowText(g,c.name()+" // MEMORY INSPECTOR",720,190,27,MAGENTA,true);
        text(g,c.type()+"   LANE "+source+"   HP "+c.hp()+"/"+c.maxHp()+"   ATK "+c.attack()+"   DEF "+c.defense()+"   SHIELD "+c.shield()+"/8",720,245,14,Color.WHITE,true);
        inspectorSection(g,"ACTIVE MEMORY // "+c.memory()+" // "+combat.memoryCost(source)+"E",CardRules.memoryDescription(c)+"  TARGET: "+CardRules.memoryTarget(c),300,CYAN);
        inspectorSection(g,"PASSIVE MEMORY // "+c.passive(),CardRules.passiveDescription(c),420,MAGENTA);
        inspectorSection(g,"IDENTITY TRAIT // "+c.trait()+" // "+(c.traitUsed()?"USED":"READY"),CardRules.traitDescription(c),540,AMBER);
        text(g,"LEFT / RIGHT CHANGE CARD    •    I / ESC CLOSE",720,720,13,LIME,true);
    }

    private void inspectorSection(Graphics2D g,String title,String body,int y,Color c){text(g,title,290,y,14,c,false);drawWrapped(g,body,290,y+36,860,14,Color.WHITE,3);}
    private void drawReplacement(Graphics2D g){g.setColor(new Color(0,0,0,205));g.fillRect(0,0,1440,900);panel(g,330,260,780,340,RED);text(g,"CARD BECAME PLAUGE // REPLACE "+combat.replacementLane(),720,325,21,RED,true);int y=390;for(int i=0;i<combat.reserves().size();i++){Card c=combat.reserves().get(i);text(g,"["+(i+4)+"]  "+c.name()+"  "+c.type()+"  HP "+c.hp()+"/"+c.maxHp()+"  "+c.memory(),720,y,15,CYAN,true);y+=55;}}
    private void panel(Graphics2D g,double x,double y,double w,double h,Color edge){g.setColor(PANEL);g.fill(new RoundRectangle2D.Double(x,y,w,h,12,12));g.setStroke(new BasicStroke(2));g.setColor(new Color(edge.getRed(),edge.getGreen(),edge.getBlue(),150));g.draw(new RoundRectangle2D.Double(x,y,w,h,12,12));}
    private void drawWrapped(Graphics2D g,String value,double x,double y,int width,int size,Color color,int maxLines){g.setFont(pixelFont(size));String left=value;for(int line=0;line<maxLines&&!left.isBlank();line++){int cut=left.length();while(cut>1&&g.getFontMetrics().stringWidth(left.substring(0,cut))>width)cut--;if(cut<left.length()){int space=left.lastIndexOf(' ',cut);if(space>0)cut=space;}text(g,left.substring(0,cut).trim(),x,y+line*(size+5),size,color,false);left=left.substring(cut).trim();}}
    private boolean nearWorldNode(){return encounterIndex<route.length&&Point2D.distance(worldX,worldY,WORLD_NODES[encounterIndex][0],WORLD_NODES[encounterIndex][1])<48;}
    private boolean nearForge(){return Point2D.distance(worldX,worldY,FORGE_X,FORGE_Y)<58;}

    private void drawBar(Graphics2D g, double x, double y, double w, double h, int value, int max, Color color) { g.setColor(new Color(30, 43, 50)); g.fill(new RoundRectangle2D.Double(x, y, w, h, h, h)); g.setColor(color); g.fill(new RoundRectangle2D.Double(x, y, w * Math.max(0, value) / Math.max(1, max), h, h, h)); }
    private void text(Graphics2D g, String s, double x, double y, int size, Color color, boolean center) { g.setFont(pixelFont(size)); g.setColor(color); if (center) x -= g.getFontMetrics().stringWidth(s) / 2.0; g.drawString(s, (float)x, (float)y); }
    private void textFitted(Graphics2D g,String value,double x,double y,int size,Color color,int width){g.setFont(pixelFont(size));String shown=value;while(shown.length()>1&&g.getFontMetrics().stringWidth(shown)>width)shown=shown.substring(0,shown.length()-1);if(!shown.equals(value))shown=shown.substring(0,Math.max(1,shown.length()-1))+"…";text(g,shown,x,y,size,color,false);}
    private void textCenteredFitted(Graphics2D g,String value,double center,double y,int size,Color color,int width){int fitted=size;while(fitted>8&&pixelFont(fitted).getStringBounds(value,g.getFontRenderContext()).getWidth()>width)fitted--;text(g,value,center,y,fitted,color,true);}
    private void textRightFitted(Graphics2D g, String value, double right, double y, int size, Color color, int maxWidth) {
        g.setFont(pixelFont(size));
        FontMetrics metrics = g.getFontMetrics();
        String shown = value;
        while (shown.length() > 1 && metrics.stringWidth(shown) > maxWidth) shown = shown.substring(0, shown.length() - 1);
        if (!shown.equals(value)) {
            while (shown.length() > 1 && metrics.stringWidth(shown + "…") > maxWidth) shown = shown.substring(0, shown.length() - 1);
            shown += "…";
        }
        g.setColor(color);
        g.drawString(shown, (float) (right - metrics.stringWidth(shown)), (float) y);
    }
    private void glowText(Graphics2D g, String s, double x, double y, int size, Color color, boolean center) { for (int i = 12; i > 0; i -= 3) { g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 18)); g.setFont(pixelFont(size)); double px = center ? x - g.getFontMetrics().stringWidth(s) / 2.0 : x; g.drawString(s, (float)(px + i / 4.0), (float)(y + i / 4.0)); } text(g, s, x, y, size, color, center); }

    private static Font pixelFont(float size) { return PIXEL_FONT.deriveFont(Font.PLAIN, size); }
    private static Font loadPixelFont() {
        try (InputStream stream = RKISGraphicalGame.class.getResourceAsStream("/BigBlueTerm437.ttf")) {
            if (stream == null) throw new IllegalStateException("Pixel font resource missing");
            Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        } catch (Exception ignored) {
            return new Font(Font.MONOSPACED, Font.PLAIN, 12);
        }
    }

    @Override public void keyPressed(KeyEvent e) {
        int code=e.getKeyCode();
        if (view == View.TITLE && code == KeyEvent.VK_ENTER) { view=View.STORY; return; }
        if(view==View.STORY&&code==KeyEvent.VK_ENTER){if(storyPage<2)storyPage++;else view=View.OVERWORLD;return;}
        if(view==View.OVERWORLD){
            if(code==KeyEvent.VK_LEFT||code==KeyEvent.VK_A)worldX-=18;if(code==KeyEvent.VK_RIGHT||code==KeyEvent.VK_D)worldX+=18;
            if(code==KeyEvent.VK_UP||code==KeyEvent.VK_W)worldY-=18;if(code==KeyEvent.VK_DOWN||code==KeyEvent.VK_S)worldY+=18;
            worldX=Math.max(105,Math.min(1330,worldX));worldY=Math.max(145,Math.min(735,worldY));
            if(code==KeyEvent.VK_ENTER&&nearWorldNode())newEncounter();else if(code==KeyEvent.VK_F&&nearForge())view=View.FORGE;return;
        }
        if(view==View.REWARD&&code==KeyEvent.VK_ENTER){fibers+=route[encounterIndex].fibers();encounterIndex++;if(encounterIndex>=route.length)view=View.REPORT;else view=View.OVERWORLD;return;}
        if(view==View.REPORT&&code==KeyEvent.VK_ENTER){encounterIndex=storyPage=0;fibers=Math.min(5,fibers/4);worldX=145;worldY=590;view=View.TITLE;return;}
        if(view==View.FORGE){handleForgeKey(code);return;}
        if(view!=View.COMBAT)return;
        if(combat.needsReplacement()){if(code==KeyEvent.VK_4)message=combat.deployReserve(0);else if(code==KeyEvent.VK_5)message=combat.deployReserve(1);return;}
        if(inspect){if(code==KeyEvent.VK_I||code==KeyEvent.VK_ESCAPE)inspect=false;else if(code==KeyEvent.VK_LEFT){source=Lane.values()[Math.max(0,source.ordinal()-1)];target=source;}else if(code==KeyEvent.VK_RIGHT){source=Lane.values()[Math.min(2,source.ordinal()+1)];target=source;}return;}
        if(code==KeyEvent.VK_I){inspect=true;return;}
        if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_3) source = target = Lane.values()[e.getKeyCode() - KeyEvent.VK_1];
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) target = Lane.values()[Math.max(0, target.ordinal() - 1)];
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) target = Lane.values()[Math.min(2, target.ordinal() + 1)];
        else if (e.getKeyCode() == KeyEvent.VK_A) command(0); else if (e.getKeyCode() == KeyEvent.VK_M) command(1); else if (e.getKeyCode() == KeyEvent.VK_G) command(2); else if (e.getKeyCode() == KeyEvent.VK_E) command(3);
    }
    private void handleForgeKey(int code){
        if(code==KeyEvent.VK_ESCAPE){if(forgeSlot>=0)forgeSlot=-1;else view=View.OVERWORLD;return;}
        if(forgeSlot<0&&code>=KeyEvent.VK_1&&code<=KeyEvent.VK_5){int slot=code-KeyEvent.VK_1;if(!roster.cards().get(slot).isAlive())forgeSlot=slot;return;}
        if(forgeSlot>=0&&code>=KeyEvent.VK_1&&code<=KeyEvent.VK_3){CardType type=CardType.values()[code-KeyEvent.VK_1];int cost=type==CardType.GENERIC?5:7;if(fibers<cost){message="FORGE REJECTED // NEED "+cost+" FIBERS";return;}Card old=roster.cards().get(forgeSlot);int hp=type==CardType.GENERIC?26:type==CardType.STEEL?32:22;int atk=type==CardType.GENERIC?8:type==CardType.STEEL?5:11;int def=type==CardType.GENERIC?2:type==CardType.STEEL?5:0;String mem=type==CardType.GENERIC?"OVERCLOCK":type==CardType.STEEL?"BRACE":"CONDUIT";roster.replace(forgeSlot,new Card("RE-"+old.name(),type,hp,atk,def,mem,type==CardType.STEEL?"HOLD THE LINE":"NONE",old.trait()));fibers-=cost;forgeSlot=-1;}
    }
    @Override public void mouseMoved(MouseEvent e) { Point2D p = logical(e); hoverLane = hoverButton = -1; for (int i=0;i<3;i++) if(cardHits[i].contains(p)) hoverLane=i; for(int i=0;i<4;i++) if(buttons[i].contains(p)) hoverButton=i; }
    @Override public void mouseClicked(MouseEvent e) { requestFocusInWindow(); if(view==View.TITLE){view=View.STORY;return;} if(view!=View.COMBAT||inspect||combat.needsReplacement())return; Point2D p=logical(e); for(int i=0;i<3;i++) if(cardHits[i].contains(p)){source=target=Lane.values()[i];return;} for(int i=0;i<4;i++) if(buttons[i].contains(p)){command(i);return;} }
    private Point2D logical(MouseEvent e) { double s=Math.min(getWidth()/1440.0,getHeight()/900.0); return new Point2D.Double((e.getX()-(getWidth()-1440*s)/2)/s,(e.getY()-(getHeight()-900*s)/2)/s); }
    @Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {} @Override public void mouseDragged(MouseEvent e) {} @Override public void mousePressed(MouseEvent e) {} @Override public void mouseReleased(MouseEvent e) {} @Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) { hoverLane=hoverButton=-1; }

    private static final class Particle { double x,y,dx,dy,size=3; int life=24; final Color color; Particle(double x,double y,double dx,double dy,Color c){this.x=x;this.y=y;this.dx=dx;this.dy=dy;color=c;} boolean tick(){x+=dx;y+=dy;dx*=.96;dy*=.96;size+=.15;return --life>0;} }
}
