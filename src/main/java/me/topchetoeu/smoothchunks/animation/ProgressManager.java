package me.topchetoeu.smoothchunks.animation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public final class ProgressManager {
    public static class ChunkLoc {
        public final int x;
        public final int y;
        public final int z;

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ChunkLoc && ((ChunkLoc)obj).x == x && ((ChunkLoc)obj).y == y && ((ChunkLoc)obj).z == z;
        }

        @Override
        public int hashCode() {
            return 137 * x + 149 * y + 163 * z;
        }

        public ChunkLoc(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private Hashtable<ChunkLoc, Float> chunkToStage = new Hashtable<>();
    private HashSet<ChunkLoc> chunks = new HashSet<>();
    private float duration = 1;

    public ProgressManager() {

    }

    public float getDuration() {
        return duration;
    }
    public void setDuration(float duration) {
        this.duration = duration;
    }

    /**
     * Removes all loaded chunks (called when a world is unloaded and loaded)
     */
    public void reset() {
        chunkToStage.clear();
        chunks.clear();
    }
    /**
     * Advances the animation for all tracked chunks, according to the duration and the specified delta
     * @param delta The second delta to advance the animation by
     */
    public void tick(float delta) {
        for (ChunkLoc loc : new ArrayList<>(chunkToStage.keySet())) {
            float val = chunkToStage.get(loc);
            val += delta / duration;
            if (val > 1f) chunkToStage.remove(loc);
            chunkToStage.put(loc, val);
        }
    }

    /**
     * Loads a specified chunk (starts tracking its animation)
     * @param x The x of the chunk
     * @param y The y of the chunk
     * @param y The z of the chunk
     */
    public void load(int x, int y, int z) {
        if (isChunkLoaded(x, y, z)) return;
        ChunkLoc loc = new ChunkLoc(x, y, z);
        chunks.add(loc);
        chunkToStage.put(loc, 0f);
    }
    /**
     * Unloads a specified chunk (stops tracking its animation)
     * @param x The x of the chunk
     * @param y The y of the chunk
     * @param y The z of the chunk
     */
    public void unload(int x, int y, int z) {
        ChunkLoc loc = new ChunkLoc(x, y, z);
        chunkToStage.remove(loc);
        chunks.remove(loc);
    }
    /**
     * Unloads all loaded chunks
     */
    public void unloadAll() {
        chunkToStage.clear();
        chunks.clear();
    }
    /**
     * Unloads all the chunks that are outside the render distance specified
     * @param viewDistance The view distance (in chunks, radius)
     * @param playerChunkX The x coordinate of the chunk in which the player stands
     * @param playerChunkY The y coordinate of the chunk in which the player stands
     * @param playerChunkZ The z coordinate of the chunk in which the player stands
     */
    public void unloadAllFar(int viewDistance, int playerChunkX, int playerChunkY, int playerChunkZ) {
        float circleVD = viewDistance + 1.38f;
        int squareVD = viewDistance;

        for (ChunkLoc loc : new ArrayList<>(chunks)) {
            int chunkX = loc.x / 16;
            int chunkZ = loc.z / 16;

            int diffSquareX = playerChunkX - chunkX ;
            int diffSquareZ = playerChunkZ - chunkZ;
            
            int diffCircleX = playerChunkX - chunkX;
            int diffCircleZ = playerChunkZ - chunkZ;

            int dist = diffCircleX * diffCircleX + diffCircleZ * diffCircleZ;

            if (dist > circleVD * circleVD) unload(loc.x, loc.y, loc.z);
            if (Math.abs(diffSquareX) > squareVD || Math.abs(diffSquareZ) > squareVD)  unload(loc.x, loc.y, loc.z);
        }
    }

    /**
     * Checks whether or not a chunk is loaded
     * @param x The x of the chunk
     * @param y The y of the chunk
     * @param y The z of the chunk
     * @return A value indicating whether or not the specified chunk is tracked
     */
    public boolean isChunkLoaded(int x, int y, int z) {
        return chunks.contains(new ChunkLoc(x, y, z));
    }

    /**
     * Gets the animation progress of the specified chunk
     * @param x The x of the chunk
     * @param y The y of the chunk
     * @param y The z of the chunk
     * @return A float value from 0 to 1 (0 if the chunk is not tracked), indicating the animation progress of the specified chunk
     */
    public float getChunkProgress(int x, int y, int z) {
        if (!isChunkLoaded(x, y, z)) return 0f;
        if (!chunkToStage.containsKey(new ChunkLoc(x, y, z))) return 1f;
        return chunkToStage.get(new ChunkLoc(x, y, z));
    }
}
