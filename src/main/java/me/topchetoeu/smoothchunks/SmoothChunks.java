package me.topchetoeu.smoothchunks;

import java.io.File;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.topchetoeu.smoothchunks.Manager.RegisterEvent;
import me.topchetoeu.smoothchunks.animation.Animation;
import me.topchetoeu.smoothchunks.animation.FallAnimation;
import me.topchetoeu.smoothchunks.animation.FlyInAnimation;
import me.topchetoeu.smoothchunks.animation.ProgressManager;
import me.topchetoeu.smoothchunks.animation.RiseAnimation;
import me.topchetoeu.smoothchunks.animation.ScaleAnimation;
import me.topchetoeu.smoothchunks.easing.Ease;
import me.topchetoeu.smoothchunks.easing.ElasticEase;
import me.topchetoeu.smoothchunks.easing.LinearEase;
import me.topchetoeu.smoothchunks.easing.SineEase;
import me.topchetoeu.smoothchunks.gui.SmoothChunksScreen;
import me.topchetoeu.smoothchunks.easing.QuadraticEase;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;

public final class SmoothChunks implements ClientModInitializer, ModMenuApi {
    private static SmoothChunks instance;
    public static SmoothChunks getInstance() {
        return instance;
    }

    /**
     * An event, fired once, when eases are being registered
     */
    public final Event<RegisterEvent<Ease>> EASES_REGISTERING = Manager.createEvent();
    /**
     * An event, fired once, when animations are being registered
     */
    public final Event<RegisterEvent<Animation>> ANIMATIONS_REGISTERING = Manager.createEvent();

    private ProgressManager progress;
    private ConfigManager config;
    private Manager<Ease> ease;
    private Manager<Animation> animation;

    /**
     * Gets the config manager
     */
    public ConfigManager getConfigManager() {
        return config;
    }
    /**
     * Gets the chunk progress manager
     */
    public ProgressManager getProgressManager() {
        return progress;
    }
    /**
     * Gets the animation manager
     */
    public Manager<Animation> getAnimationManager() {
        return animation;
    }
    /**
     * Gets the ease manager
     */
    public Manager<Ease> getEaseManager() {
        return ease;
    }

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

        manager.set("elastic");
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

        progress = new ProgressManager();
        ease = new Manager<>(x -> 1);
        ease.get()
            .description("Ends the animation as soon as it has started.")
            .displayName("No animation");
        animation = new Manager<>((a, b, c, d, e, f, g, h) -> {});
        animation.get()
            .description("Does nothing.")
            .displayName("No animation");

        registerEases(ease);
        registerAnimations(animation);

        config = new ConfigManager(new File("config/smooth-chunks.dat"), animation, ease);

        EASES_REGISTERING.invoker().register(ease);
        ANIMATIONS_REGISTERING.invoker().register(animation);

        ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            BlockPos pos = chunk.getPos().getStartPos();
            progress.unload(pos.getX(), pos.getY(), pos.getZ());
        });
    }
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return new ConfigScreenFactory<Screen>() {
            @Override
            public Screen create(Screen parent) {
                return new SmoothChunksScreen(parent);
            }
        };
    }
}
