package me.topchetoeu.smoothchunks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.topchetoeu.smoothchunks.SmoothChunks;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;

// From flogic's mod
@Mixin(ChunkBuilder.BuiltChunk.class)
abstract class BuiltChunkMixin {
    @Inject(method = "clear", at = @At(value = "TAIL"), cancellable = true)
    public void clear(CallbackInfo ci) {
        // ci.cancel();
        // return;
        BlockPos origin = ((ChunkBuilder.BuiltChunk)(Object)this).getOrigin();
        SmoothChunks.getInstance().getProgressManager().unload(origin.getX(), 0, origin.getZ());
    }
}