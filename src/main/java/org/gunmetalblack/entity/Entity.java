package org.gunmetalblack.entity;

import asciiPanel.AsciiCharacterData;

import java.awt.*;

/**
 * Represents an entity in a 2D world, defined by its position and visual representation using ASCII characters.
 */
public class Entity {
    private int xPos;
    private int yPos;
    private AsciiCharacterData graphic;

    /**
     * Constructs an Entity with specified ASCII character, foreground color, background color, and position.
     *
     * @param character       the ASCII character that represents the entity.
     * @param foregroundColor the color of the character.
     * @param backgroundColor the background color behind the character.
     * @param xPos            the x-coordinate of the entity in the world.
     * @param yPos            the y-coordinate of the entity in the world.
     */
    public Entity(char character, Color foregroundColor, Color backgroundColor, int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.graphic = new AsciiCharacterData(character, foregroundColor, backgroundColor);
    }

    /**
     * Constructs an Entity with specified ASCII character and position, using default colors.
     * The default colors are white for the foreground and black for the background.
     *
     * @param character the ASCII character that represents the entity.
     * @param xPos      the x-coordinate of the entity in the world.
     * @param yPos      the y-coordinate of the entity in the world.
     */
    public Entity(char character, int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.graphic = new AsciiCharacterData(character, Color.WHITE, Color.black);
    }

    /**
     * Constructs an Entity with specified ASCII character and colors, without specifying a position.
     *
     * @param character       the ASCII character that represents the entity.
     * @param foregroundColor the color of the character.
     * @param backgroundColor the background color behind the character.
     */
    public Entity(char character, Color foregroundColor, Color backgroundColor) {
        this.graphic = new AsciiCharacterData(character, foregroundColor, backgroundColor);
    }

    /**
     * Gets the x-coordinate of the entity.
     *
     * @return the current x-coordinate of the entity.
     */
    public int getxPos() {
        return xPos;
    }

    /**
     * Sets the x-coordinate of the entity.
     *
     * @param xPos the new x-coordinate of the entity.
     */
    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    /**
     * Gets the y-coordinate of the entity.
     *
     * @return the current y-coordinate of the entity.
     */
    public int getyPos() {
        return yPos;
    }

    /**
     * Sets the y-coordinate of the entity.
     *
     * @param yPos the new y-coordinate of the entity.
     */
    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    /**
     * Gets the ASCII graphic data of the entity, which includes the character and its colors.
     *
     * @return the ASCII character data for the entity's visual representation.
     */
    public AsciiCharacterData getGraphic() {
        return graphic;
    }
}
