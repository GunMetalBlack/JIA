package org.gunmetalblack.jiaframework.render;

import org.gunmetalblack.jiaframework.entity.Entity;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class Level {

    /**
     * Creates a level from a png using entity ids
     */

    // HashMap to store specific RGB to ASCII character mappings
    private static final HashMap<Color, String> colorToAsciiMap = new HashMap<>();
    public static Level testLevel = new Level("test.png");
    private ArrayList<Entity> level = new ArrayList<>();

    public Level(String imageName) {
        // Define specific RGB to ASCII character mappings
        defineColorToAsciiMappings();

        try {
            // Load the image from the resources folder
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/" + imageName));

            if (image == null) {
                throw new RuntimeException("Image not found: " + imageName);
            }

            // Resize or scale image if necessary
            int width = image.getWidth();
            int height = image.getHeight();

            // Create 2D array to hold the ASCII characters
            String[][] asciiArray = new String[height][width];

            // Loop through each pixel
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Get the pixel color
                    Color color = new Color(image.getRGB(x, y));

                    // Map the color to an ASCII character (if it exists in the map)
                    String asciiChar = mapColorToAscii(color);

                    // Store the corresponding ASCII character in the array
                    asciiArray[y][x] = asciiChar;
                }
            }

            //I know this is scuffed Please forgive me
            //Takes the map array of strings and converts them into entities
            int x = 0;
            int y = 0;
            for (String[] row : asciiArray) {
                x = 0;
                for (String entityID : row) {
                    Entity entityToMap = getEntity(entityID);
                    entityToMap.setxPos(x);
                    entityToMap.setyPos(y);
                    level.add(entityToMap);
                    x++;
                }
                y++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Entity getEntity(String entityID) {
        Entity entityToMap = new Entity('?',0,0,false);
        if(entityID.equals("id_wall"))
        {
            entityToMap = new Entity((char)219,Color.DARK_GRAY,Color.BLACK,0,0,true,false);
        }else if (entityID.equals("id_spawn"))
        {
            entityToMap = new Entity('*',0,0,false);
        }else if (entityID.equals("id_blue"))
        {
            entityToMap =  new Entity('$',0,0,false);
        }else if (entityID.equals("id_empty"))
        {
            entityToMap = new Entity(' ',0,0,false);
        }else if (entityID.equals("id_floor"))
        {
            entityToMap = new Entity((char)176,new Color(58,191,22),Color.black,0,0,false,false);
        }else if(entityID.equals("id_breakable"))
        {
            entityToMap = new Entity((char)219 ,new Color(51, 24, 0),Color.black,0,0,true,true);
        }
        return entityToMap;
    }

    // Define specific RGB values and their corresponding ASCII characters
    private static void defineColorToAsciiMappings() {
        // Example mappings (you can add more specific colors as needed)
        colorToAsciiMap.put(new Color(255, 0, 0), "id_wall");    // Red wall
        colorToAsciiMap.put(new Color(0, 255, 0),  "id_spawn");    // Green Player Spawn !Important Only One can exist
        colorToAsciiMap.put(new Color(0, 0, 255), "id_blue");    // Blue
        colorToAsciiMap.put(new Color(255, 255, 255), "id_empty"); // White empty
        colorToAsciiMap.put(new Color(0, 0, 0), "id_floor");      // Black floor
        colorToAsciiMap.put(new Color(0x5b, 0x72, 0x63), "id_breakable");      // Breakable entity
        // Add more specific RGB to ASCII mappings here...
    }

    // Map a color to an ASCII character based on the specific RGB mappings
    private static String mapColorToAscii(Color color) {
        // Check if the color exists in the map, if not, return a default character (e.g., '?')
        return colorToAsciiMap.getOrDefault(color, "id_default");
    }

    public ArrayList<Entity> getLevel() {
        return level;
    }
}
