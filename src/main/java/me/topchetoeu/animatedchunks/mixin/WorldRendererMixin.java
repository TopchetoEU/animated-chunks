package me.topchetoeu.animatedchunks.mixin;

import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.WorldRenderer.ChunkInfo;
import net.minecraft.client.render.chunk.ChunkBuilder.BuiltChunk;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.util.math.Matrix4f;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import me.topchetoeu.animatedchunks.AnimatedChunks;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    private long lastTime = System.nanoTime();

    @Accessor abstract BuiltChunkStorage getChunks();

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void renderStart(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        long currTime = System.nanoTime();
        AnimatedChunks.getInstance().animator.tick((currTime - lastTime) / 1000000000f);
        lastTime = currTime;
    }

    @Inject(method = "renderLayer", at = @At(value = "HEAD"))
    private void renderLayerAfter(RenderLayer renderLayer, MatrixStack matrices, double playerX, double playerY, double playerZ, Matrix4f positionMatrix, CallbackInfo ci) {
        int chunkX = (int)(playerX / 16);
        int chunkY = (int)(playerY / 16);
        int chunkZ = (int)(playerZ / 16);

        if (playerX < 0) chunkX--;
        if (playerY < 0) chunkY--;
        if (playerZ < 0) chunkZ--;

        AnimatedChunks.getInstance().animator.unloadAllFar((int)((WorldRenderer)(Object)this).getViewDistance(), chunkX, chunkY, chunkZ);
    }
    @Inject(method = "renderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/VertexBuffer;drawElements()V"), locals=LocalCapture.CAPTURE_FAILHARD)
    private void renderChunkBefore(RenderLayer renderLayer, MatrixStack matrices, double playerX, double playerY, double playerZ, Matrix4f positionMatrix, CallbackInfo ci,
            boolean _1, ObjectListIterator<?> _2, Shader shader, GlUniform _4, ChunkInfo _6, BuiltChunk chunk) {

        matrices.push();

        AnimatedChunks.getInstance().animator.animate(matrices, chunk.getOrigin(), new Vector3d(playerX, playerY, playerZ));

        // if (getProgressManager().isChunkLoaded(x, 0, z)) {
        //     float progress = getProgressManager().getChunkProgress(x, 0, z);

        //     if (progress < 0.999) {
        //         progress = AnimatedChunks.getInstance().getEaseManager().getValue().ease(progress);

        //         float centerX = (float)playerX - x;
        //         float centerY = (float)playerY - y;
        //         float centerZ = (float)playerZ - z;

        //         matrices.translate(-centerX, -centerY, -centerZ);
        //         AnimatedChunks.getInstance().getAnimationManager().getValue().animate(progress, matrices, x, y, z, (float)playerX, (float)playerY, (float)playerZ);
        //         matrices.translate(centerX, centerY, centerZ);
        //     }
        // }
        // else {
        //     matrices.scale(0, 0, 0);
        // }
        
        shader.modelViewMat.set(matrices.peek().getPositionMatrix());
        matrices.pop();
        shader.bind();
    }
    @Inject(method = "renderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/VertexBuffer;drawElements()V"), locals=LocalCapture.CAPTURE_FAILHARD)
    private void renderChunkAfter(RenderLayer renderLayer, MatrixStack matrices, double playerX, double playerY, double playerZ, Matrix4f positionMatrix, CallbackInfo ci,
            boolean _1, ObjectListIterator<?> _2, Shader shader, GlUniform _4, ChunkInfo _6, BuiltChunk chunk) {
        int x = chunk.getOrigin().getX();
        int z = chunk.getOrigin().getZ();
        AnimatedChunks.getInstance().animator.load(x, 0, z);
    }
}
