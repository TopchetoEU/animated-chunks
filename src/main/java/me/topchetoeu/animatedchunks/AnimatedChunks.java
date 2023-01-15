package me.topchetoeu.animatedchunks;

import java.io.File;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.topchetoeu.animatedchunks.animation.Animation;
import me.topchetoeu.animatedchunks.animation.FallAnimation;
import me.topchetoeu.animatedchunks.animation.FlyInAnimation;
import me.topchetoeu.animatedchunks.animation.Animator;
import me.topchetoeu.animatedchunks.animation.RiseAnimation;
import me.topchetoeu.animatedchunks.animation.ScaleAnimation;
import me.topchetoeu.animatedchunks.easing.Ease;
import me.topchetoeu.animatedchunks.easing.ElasticEase;
import me.topchetoeu.animatedchunks.easing.LinearEase;
import me.topchetoeu.animatedchunks.easing.QuadraticEase;
import me.topchetoeu.animatedchunks.easing.SineEase;
import me.topchetoeu.animatedchunks.gui.AnimatedChunksScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;

public final class AnimatedChunks implements ClientModInitializer, ModMenuApi {
    private static AnimatedChunks instance;
    /**
     * Gets the currently running instance of this mod
     */
    public static AnimatedChunks getInstance() {
        return instance;
    }

    /**
     * The animator used by the mod. Used to manage animation progress and animate chunks
     */
    public final Animator animator;
    /**
     * The config manager used by the mod. Used to save/load config from disk
     */
    public final ConfigManager config;


    private static void registerEases(Manager<Ease> manager) {
        manager.register(new Descriptor<>(new LinearEase(), "linear")
            .displayName("Linear")
            .author("TopchetoEU")
            .description("Animates with an even velocity")
        );
        manager.register(new Descriptor<>(new SineEase(), "sine")
            .displayName("Sine")
            .author("TopchetoEU")
            .description("Animation takes off relatively quickly, and ends smoothly. No abrupt ending is noticeable.")
        );
        manager.register(new Descriptor<>(new QuadraticEase(), "quad")
            .displayName("Quadratic")
            .author("TopchetoEU")
            .description("Animation takes off quickly, and then slows down. There is a noticeable stop in the animation.")
        );
        manager.register(new Descriptor<>(new ElasticEase(), "elastic")
            .displayName("Elastic")
            .author("TopchetoEU")
            .description("Animation takes off very quickly, overshoots, then undershoots, until it reaches the end.")
        );

        manager.set("sine");
    }
    private static void registerAnimations(Manager<Animation> manager) {
        manager.register(new Descriptor<>(new RiseAnimation(), "rise")
            .displayName("Rise")
            .author("TopchetoEU")
            .description("Chunks will rise out of the ground (go from below upwards).")
        );
        manager.register(new Descriptor<>(new FallAnimation(), "fall")
            .displayName("Fall")
            .author("TopchetoEU")
            .description("Chunks will fall from the sky (go from above downwards).")
        );
        manager.register(new Descriptor<>(new ScaleAnimation(), "scale")
            .displayName("Scale")
            .author("TopchetoEU")
            .description("Chunks will scale up from 0 to normal size.")
        );
        manager.register(new Descriptor<>(new FlyInAnimation(), "fly-in")
            .displayName("Fly In")
            .author("TopchetoEU")
            .description("Chunks will slide torwards you until they get to their locations.")
        );

        manager.set("rise");
    }

    @Override public void onInitializeClient() {
        instance = this;

        ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            BlockPos pos = chunk.getPos().getStartPos();
            animator.unload(pos.getX(), pos.getY(), pos.getZ());
        });
    }
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (Screen parent) -> {
            var _this = getInstance();
            return new AnimatedChunksScreen(parent, _this.config, _this.animator);
        };
    }

    public AnimatedChunks() {
        var eases = new Manager<>(new Descriptor<Ease>(x -> 1, "default")
            .author("TopchetoEU")
            .description("Ends the animation as soon as it has started.")
            .displayName("No animation")
        );
        var animations = new Manager<>(new Descriptor<Animation>((a, b, c, d, e, f, g, h) -> {}, "default")
            .author("TopchetoEU")
            .description("Does nothing.")
            .displayName("No animation")
        );

        registerEases(eases);
        registerAnimations(animations);

        animator = new Animator(animations, eases);
        config = new ConfigManager(new File("config/animated-chunks.dat"), animator);
    }
}
