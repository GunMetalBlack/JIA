package org.gunmetalblack.jiaframework.entity;

import asciiPanel.AsciiCharacterData;
import org.gunmetalblack.jiaframework.events.CollisionEvent;
import org.gunmetalblack.jiaframework.events.GlobalEventManager;
import org.gunmetalblack.jiaframework.events.JIAEventListener;
import org.gunmetalblack.jiaframework.events.JIAGenericEvent;

import java.awt.*;

/**
 * Represents an entity in a 2D world, defined by its position and visual representation using ASCII characters.
 */
public class Entity {
    private int xPos;
    private int yPos;
    private boolean isBreakable;
    private boolean canCollide;
    private AsciiCharacterData graphic;
    private JIAEventListener<CollisionEvent> collisionJIAEventListener;

    /**
     * Constructs an Entity with specified ASCII character, foreground color, background color, and position.
     *
     * @param character       the ASCII character that represents the entity.
     * @param foregroundColor the color of the character.
     * @param backgroundColor the background color behind the character.
     * @param xPos            the x-coordinate of the entity in the world.
     * @param yPos            the y-coordinate of the entity in the world.
     * @param isBreakable     the flag the determines if the entity can be broken.
     */
    public Entity(char character, Color foregroundColor, Color backgroundColor, int xPos, int yPos, boolean canCollide, boolean isBreakable) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.graphic = new AsciiCharacterData(character, foregroundColor, backgroundColor);
        this.canCollide = canCollide;
        this.isBreakable = isBreakable;
        this.collisionJIAEventListener = this::onCollisionEvent;
        if(this.canCollide)
        {
            GlobalEventManager.collisionEventManager.addEventListener(collisionJIAEventListener);
        }
    }
    /*
    * Removes the entity from any listeners or other functions
    * */
    public void killEntity()
    {
        GlobalEventManager.collisionEventManager.removeEventListener(collisionJIAEventListener);
    }

    /**
     * Constructs an Entity with specified ASCII character and position, using default colors.
     * The default colors are white for the foreground and black for the background.
     *
     * @param character the ASCII character that represents the entity.
     * @param xPos      the x-coordinate of the entity in the world.
     * @param yPos      the y-coordinate of the entity in the world.
     * @param canCollide the value on instantiation that determines how it interacts with other entities when colliding
     */
    public Entity(char character, int xPos, int yPos, boolean canCollide) {
        this(character,Color.white, Color.black, xPos, yPos, canCollide,false);
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

    public void onCollisionEvent(JIAGenericEvent<CollisionEvent> wrapper)
    {
        CollisionEvent event = wrapper.<CollisionEvent>getEvent();
        if(event.getxPos() == getxPos() && event.getyPos() == getyPos())
        {
            event.getCollisions().add(this);
        }
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

    /*
    * Is the entity breakable
    * */
    public boolean isBreakable() {
        return isBreakable;
    }

    public void setBreakable(boolean breakable) {
        isBreakable = breakable;
    }

    public boolean canCollide() {
        return canCollide;
    }
    public void setCanCollide(boolean canCollide) {
        this.canCollide = canCollide;
    }

    @Override
    public String toString() {
        return "CustomEntity{name=" + getGraphic() + "}";
    }
}
