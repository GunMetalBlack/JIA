package org.gunmetalblack.render;

import org.gunmetalblack.entity.Entity;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class Level {

    // HashMap to store specific RGB to ASCII character mappings
    private static final HashMap<Color, Entity> colorToAsciiMap = new HashMap<>();
    public static Level testLevel = new Level("test.png");
    private Entity[][] level;

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
            Entity[][] asciiArray = new Entity[height][width];

            // Loop through each pixel
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Get the pixel color
                    Color color = new Color(image.getRGB(x, y));

                    // Map the color to an ASCII character (if it exists in the map)
                    Entity asciiChar = mapColorToAscii(color);

                    // Store the corresponding ASCII character in the array
                    asciiArray[y][x] = asciiChar;
                }
            }

            // Print the ASCII 2D array (optional)
            level = new Entity[asciiArray.length][asciiArray[0].length];
            int x = 0;
            int y = 0;
            for (Entity[] row : asciiArray) {
                x = 0;
                for (Entity entity : row) {
                    entity.setxPos(x);
                    entity.setyPos(y);
                    level[y][x] = entity;
                    x++;
                }
                y++;
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Define specific RGB values and their corresponding ASCII characters
    private static void defineColorToAsciiMappings() {
        // Example mappings (you can add more specific colors as needed)
        colorToAsciiMap.put(new Color(255, 0, 0), new Entity((char)219,0,0));    // Red
        colorToAsciiMap.put(new Color(0, 255, 0),  new Entity('#',0,0));    // Green
        colorToAsciiMap.put(new Color(0, 0, 255), new Entity('$',0,0));    // Blue
        colorToAsciiMap.put(new Color(255, 255, 255), new Entity(' ',0,0)); // White
        colorToAsciiMap.put(new Color(0, 0, 0), new Entity((char)176,Color.DARK_GRAY,Color.black,0,0));      // Black
        // Add more specific RGB to ASCII mappings here...
    }

    // Map a color to an ASCII character based on the specific RGB mappings
    private static Entity mapColorToAscii(Color color) {
        // Check if the color exists in the map, if not, return a default character (e.g., '?')
        return colorToAsciiMap.getOrDefault(color, new Entity('?',0,0));
    }

    public Entity[][] getLevel() {
        return level;
    }
}
