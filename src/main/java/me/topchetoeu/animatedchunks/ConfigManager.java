package me.topchetoeu.animatedchunks;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import me.topchetoeu.animatedchunks.animation.Animator;

public class ConfigManager {
    public final File configFile;
    private final Animator animator;

    private String readString(InputStream reader) throws IOException {
        String res = "";
        int i;

        while ((i = reader.read()) != 0) {
            if (i == -1) break;
            res += (char)i;
        }

        return res;
    }
    private void writeString(OutputStream writer, String str) throws IOException {
        for (int i = 0; i < str.length(); i++) {
            writer.write((byte)str.charAt(i));
        }
        writer.write((byte)0);
    }

    private float readFloat(InputStream reader) throws IOException {
        try {
            var bytes = reader.readNBytes(4);
            return ByteBuffer.wrap(bytes).getFloat();
        }
        catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    public void reload() {
        try {
            var reader = new FileInputStream(configFile);
            String animation = readString(reader);
            String ease = readString(reader);
            float duration = readFloat(reader);

            reader.close();

            this.animator.ANIMATIONS.set(animation);
            this.animator.EASES.set(ease);
            this.animator.setDuration(duration);
        }
        catch (IOException e) {
            save();
        }
    }
    public void save() {
        try {
            var writer = new FileOutputStream(configFile);
            writeString(writer, this.animator.ANIMATIONS.get().getName());
            writeString(writer, this.animator.EASES.get().getName());
            writer.write(ByteBuffer.allocate(4).putFloat(animator.getDuration()).array());
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ConfigManager(File configFile, Animator animator) {
        this.configFile = configFile;
        this.animator = animator;

        reload();
    }
}
