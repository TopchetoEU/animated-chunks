package me.topchetoeu.smoothchunks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import me.topchetoeu.smoothchunks.animation.Animation;
import me.topchetoeu.smoothchunks.easing.Ease;

public class ConfigManager {
    public final File configFile;
    private final Manager<Animation> animation;
    private final Manager<Ease> ease;

    private String readString(InputStreamReader reader) throws IOException {
        String res = "";
        int i;

        while ((i = reader.read()) != 0) {
            if (i == -1) break;
            res += (char)i;
        }

        return res;
    }
    private void writeString(OutputStreamWriter writer, String str) throws IOException {
        for (int i = 0; i < str.length(); i++) {
            writer.write((char)str.charAt(i));
        }
        writer.write(0);
    }

    public void reload() {
        try {
            var reader = new FileReader(configFile);
            String animation = readString(reader);
            String ease = readString(reader);

            reader.close();

            this.animation.set(animation);
            this.ease.set(ease);
        }
        catch (FileNotFoundException e) {
            save();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void save() {
        try {
            var writer = new FileWriter(configFile);
            writeString(writer, animation.get().getName());
            writeString(writer, ease.get().getName());
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ConfigManager(File configFile, Manager<Animation> animation, Manager<Ease> ease) {
        this.configFile = configFile;
        this.animation = animation;
        this.ease = ease;

        reload();
    }
}
