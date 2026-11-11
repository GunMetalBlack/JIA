package org.gunmetalblack.rkis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** A Java2D presentation layer for RKIS. The combat model remains completely UI-agnostic. */
public final class RKISGraphicalGame extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private enum View { TITLE, COMBAT, VICTORY, DEFEAT }
    private static final Color VOID = new Color(3, 6, 12), PANEL = new Color(7, 16, 27, 235);
    private static final Color CYAN = new Color(55, 232, 255), MAGENTA = new Color(255, 55, 184);
    private static final Color RED = new Color(255, 55, 78), LIME = new Color(135, 255, 126), AMBER = new Color(255, 198, 70);
    private final Roster roster = new Roster();
    private final Encounter[] route = Encounter.values();
    private final Random random = new Random(42);
    private final List<Particle> particles = new ArrayList<>();
    private final BufferedImage backdrop = createBackdropTexture();
    private CombatState combat = new CombatState(Encounter.DRONE, roster);
    private View view = View.TITLE;
    private Lane source = Lane.CENTER, target = Lane.CENTER;
    private int encounterIndex, hoverLane = -1, hoverButton = -1, pulse, shake;
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
        for (int i = particles.size() - 1; i >= 0; i--) if (!particles.get(i).tick()) particles.remove(i);
        if (shake > 0) shake--;
        repaint();
    }

    @Override protected void paintComponent(Graphics raw) {
        super.paintComponent(raw);
        Graphics2D g = (Graphics2D) raw.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        double scale = Math.min(getWidth() / 1440.0, getHeight() / 900.0);
        double ox = (getWidth() - 1440 * scale) / 2, oy = (getHeight() - 900 * scale) / 2;
        g.translate(ox + (shake > 0 ? random.nextInt(9) - 4 : 0), oy); g.scale(scale, scale);
        drawBackdrop(g);
        if (view == View.TITLE) drawTitle(g); else drawBattle(g);
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
        drawTopBar(g); drawBoard(g); drawEnemy(g); drawCards(g); drawCommandDeck(g); drawParticles(g);
        if (view == View.VICTORY || view == View.DEFEAT) drawEndOverlay(g);
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
        g.setStroke(new BasicStroke(3)); g.setColor(RED); g.draw(new Ellipse2D.Double(662, 128 + bob, 116, 116));
        g.setColor(new Color(25, 5, 12)); g.fill(new Ellipse2D.Double(670, 136 + bob, 100, 100));
        g.setColor(RED); g.fill(new Arc2D.Double(685, 151 + bob, 70, 70, 20 + pulse * 2, 130, Arc2D.PIE));
        g.setColor(Color.WHITE); g.fill(new Ellipse2D.Double(711, 176 + bob, 18, 18));
        text(g, e.name().toUpperCase(), 720, 112, 18, Color.WHITE, true);
        drawBar(g, 555, 257, 330, 13, e.hp(), e.maxHp(), RED);
        text(g, "HP " + e.hp() + "/" + e.maxHp() + "   DEF " + e.defense() + "   SH " + e.shield(), 720, 287, 11, new Color(235, 175, 185), true);
        g.setColor(new Color(25, 8, 15, 235)); g.fill(new RoundRectangle2D.Double(500, 303, 440, 45, 8, 8));
        text(g, "NEXT // " + e.intentText(), 720, 331, 12, AMBER, true);
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
        String[] labels = {"ATTACK", "MEMORY", "GUARD", "END PHASE"}; Color[] colors = {RED, MAGENTA, CYAN, AMBER};
        for (int i = 0; i < 4; i++) {
            double x = 470 + i * 205; buttons[i].setRect(x, 817, 180, 46);
            g.setColor(i == hoverButton ? new Color(colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), 70) : new Color(12, 25, 36)); g.fill(new RoundRectangle2D.Double(x, 817, 180, 46, 7, 7));
            g.setColor(colors[i]); g.draw(new RoundRectangle2D.Double(x, 817, 180, 46, 7, 7)); text(g, labels[i], x + 90, 846, 12, Color.WHITE, true);
        }
        Card c = combat.card(source); text(g, c == null ? "NO UNIT" : c.name(), 95, 826, 14, Color.WHITE, false);
        text(g, message, 95, 853, 10, new Color(145, 177, 193), false); text(g, "1-3 SELECT", 95, 873, 9, CYAN, false);
    }

    private void drawParticles(Graphics2D g) { for (Particle p : particles) { g.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), Math.max(0, p.life * 10))); g.fill(new Ellipse2D.Double(p.x, p.y, p.size, p.size)); } }

    private void drawEndOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 205)); g.fillRect(0, 0, 1440, 900); Color c = view == View.VICTORY ? LIME : RED;
        glowText(g, view == View.VICTORY ? "WORK ORDER COMPLETE" : "ALL CARDS LOST", 720, 390, 44, c, true);
        text(g, view == View.VICTORY && encounterIndex < route.length - 1 ? "PRESS ENTER // NEXT ENCOUNTER" : "PRESS ENTER // NEW RUN", 720, 475, 16, Color.WHITE, true);
    }

    private void command(int command) {
        if (view != View.COMBAT || combat.finished()) return;
        Card c = combat.card(source); int before = combat.actions();
        message = switch (command) { case 0 -> combat.attack(source); case 1 -> combat.memory(source, target); case 2 -> combat.guard(source); default -> combat.endPhase(); };
        if (command == 3) burst(720, 350, RED); else if (combat.actions() > before) burst(command == 1 ? 720 : 840, 400, command == 1 ? MAGENTA : command == 2 ? CYAN : AMBER);
        if (combat.finished()) view = combat.victorious() ? View.VICTORY : View.DEFEAT;
        shake = command == 0 || command == 3 ? 7 : 0;
    }

    private void burst(double x, double y, Color color) { for (int i = 0; i < 32; i++) particles.add(new Particle(x, y, random.nextDouble() * 12 - 6, random.nextDouble() * 10 - 5, color)); }
    private void newEncounter() { combat = new CombatState(route[encounterIndex], roster); view = View.COMBAT; source = target = Lane.CENTER; message = "READ HOSTILE INTENT // SELECT UNIT"; requestFocusInWindow(); }

    private void drawBar(Graphics2D g, double x, double y, double w, double h, int value, int max, Color color) { g.setColor(new Color(30, 43, 50)); g.fill(new RoundRectangle2D.Double(x, y, w, h, h, h)); g.setColor(color); g.fill(new RoundRectangle2D.Double(x, y, w * Math.max(0, value) / Math.max(1, max), h, h, h)); }
    private void text(Graphics2D g, String s, double x, double y, int size, Color color, boolean center) { g.setFont(new Font("Dialog", Font.BOLD, size)); g.setColor(color); if (center) x -= g.getFontMetrics().stringWidth(s) / 2.0; g.drawString(s, (float)x, (float)y); }
    private void textRightFitted(Graphics2D g, String value, double right, double y, int size, Color color, int maxWidth) {
        g.setFont(new Font("Dialog", Font.BOLD, size));
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
    private void glowText(Graphics2D g, String s, double x, double y, int size, Color color, boolean center) { for (int i = 12; i > 0; i -= 3) { g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 18)); g.setFont(new Font("Dialog", Font.BOLD, size)); double px = center ? x - g.getFontMetrics().stringWidth(s) / 2.0 : x; g.drawString(s, (float)(px + i / 4.0), (float)(y + i / 4.0)); } text(g, s, x, y, size, color, center); }

    @Override public void keyPressed(KeyEvent e) {
        if (view == View.TITLE && e.getKeyCode() == KeyEvent.VK_ENTER) { newEncounter(); return; }
        if ((view == View.VICTORY || view == View.DEFEAT) && e.getKeyCode() == KeyEvent.VK_ENTER) { if (view == View.VICTORY && encounterIndex < route.length - 1) encounterIndex++; else encounterIndex = 0; newEncounter(); return; }
        if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_3) source = target = Lane.values()[e.getKeyCode() - KeyEvent.VK_1];
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) target = Lane.values()[Math.max(0, target.ordinal() - 1)];
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) target = Lane.values()[Math.min(2, target.ordinal() + 1)];
        else if (e.getKeyCode() == KeyEvent.VK_A) command(0); else if (e.getKeyCode() == KeyEvent.VK_M) command(1); else if (e.getKeyCode() == KeyEvent.VK_G) command(2); else if (e.getKeyCode() == KeyEvent.VK_E) command(3);
    }
    @Override public void mouseMoved(MouseEvent e) { Point2D p = logical(e); hoverLane = hoverButton = -1; for (int i=0;i<3;i++) if(cardHits[i].contains(p)) hoverLane=i; for(int i=0;i<4;i++) if(buttons[i].contains(p)) hoverButton=i; }
    @Override public void mouseClicked(MouseEvent e) { requestFocusInWindow(); if(view==View.TITLE){newEncounter();return;} Point2D p=logical(e); for(int i=0;i<3;i++) if(cardHits[i].contains(p)){source=target=Lane.values()[i];return;} for(int i=0;i<4;i++) if(buttons[i].contains(p)){command(i);return;} }
    private Point2D logical(MouseEvent e) { double s=Math.min(getWidth()/1440.0,getHeight()/900.0); return new Point2D.Double((e.getX()-(getWidth()-1440*s)/2)/s,(e.getY()-(getHeight()-900*s)/2)/s); }
    @Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {} @Override public void mouseDragged(MouseEvent e) {} @Override public void mousePressed(MouseEvent e) {} @Override public void mouseReleased(MouseEvent e) {} @Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) { hoverLane=hoverButton=-1; }

    private static final class Particle { double x,y,dx,dy,size=3; int life=24; final Color color; Particle(double x,double y,double dx,double dy,Color c){this.x=x;this.y=y;this.dx=dx;this.dy=dy;color=c;} boolean tick(){x+=dx;y+=dy;dx*=.96;dy*=.96;size+=.15;return --life>0;} }
}
