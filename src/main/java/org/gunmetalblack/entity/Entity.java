package org.gunmetalblack.entity;

import asciiPanel.AsciiCharacterData;

import java.awt.*;

public class Entity {
    private int xPos;
    private int yPos;
    private AsciiCharacterData graphic;

    public Entity(char character, Color foregroundColor, Color backgroundColor, int xPos, int yPos)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.graphic = new AsciiCharacterData(character,foregroundColor,backgroundColor);
    }

    public Entity(char character, int xPos, int yPos)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.graphic = new AsciiCharacterData(character,Color.WHITE, Color.black);
    }

    public Entity(char character, Color foregroundColor, Color backgroundColor)
    {
        this.graphic = new AsciiCharacterData(character,foregroundColor,backgroundColor);
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getyPos() {
        return yPos;
    }

    public AsciiCharacterData getGraphic() {
        return graphic;
    }
}
