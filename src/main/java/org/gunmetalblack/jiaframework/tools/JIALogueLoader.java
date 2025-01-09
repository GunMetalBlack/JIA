package org.gunmetalblack.jiaframework.tools;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.FileReader;
import java.io.IOException;

public class JIALogueLoader {// Path to your JSON file
    public JIALogueLoader(String filePath) {
        try (
                FileReader reader = new FileReader(filePath)) {
            // Create a Gson instance
            Gson gson = new Gson();

            // Deserialize JSON to JIALogueFile object
            JIALogueFile jiaLogueFile = gson.fromJson(reader, JIALogueFile.class);

            // Access jiaLogue
            for (JIALogue jiaLogue : jiaLogueFile.getJIALogues()) {
                System.out.println(jiaLogue.getSpeaker() + ": " + jiaLogue.getText());
            }
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
           JIALogger.error(e.getMessage() + e.getStackTrace());
        }
    }
}
