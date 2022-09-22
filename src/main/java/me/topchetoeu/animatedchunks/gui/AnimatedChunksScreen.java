package me.topchetoeu.animatedchunks.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper.Argb;
import me.topchetoeu.animatedchunks.ConfigManager;
import me.topchetoeu.animatedchunks.Manager;
import me.topchetoeu.animatedchunks.animation.Animation;
import me.topchetoeu.animatedchunks.easing.Ease;
import me.topchetoeu.animatedchunks.gui.Section.OrderType;
import net.minecraft.client.MinecraftClient;

public class AnimatedChunksScreen extends Screen {
    public final Screen parent;
    private final HorizontalSection mainSection = new HorizontalSection();
    private final Manager<Animation> animation;
    private final Manager<Ease> ease;
    private final ConfigManager config;

    public static void playClick() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (parent == null) renderBackground(matrices);
        else parent.render(matrices, Integer.MIN_VALUE, Integer.MIN_VALUE, delta);

        fill(matrices, 0, 0, width, height, Argb.getArgb(191, 0, 0, 0));
        mainSection.render(matrices, mouseX, mouseY, delta);
        // super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        super.init();
        parent.init(client, width, height);

        addDrawableChild(mainSection);
        mainSection.children.clear();
        mainSection.setTargetWidth(width - 15);
        mainSection.order = OrderType.Justified;
        mainSection.children.addSelectableChild(selectionsSection());
        mainSection.children.addSelectableChild(previewSection());
        mainSection.children.addSelectableChild(new Input(0, 0, null));
        // setZOffset(parent.getZOffset() + 1); 
    }

    @Override
    public void close() {
        config.save();
        MinecraftClient.getInstance().setScreen(parent);
    }
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        mainSection.mouseMoved(mouseX, mouseY);
    }

    private Section previewSection() {
        var res = new HorizontalSection();
        res.x = res.y = 5;
        res.title = Text.of("Preview:");

        res.children.addSelectableChild(new ChunkPreview(0, 0, 150, 150));

        return res;
    }
    private <T> Section selectionSection(Manager<T> manager, String name) {
        var res = new HorizontalSection();
        res.x = 5;
        res.y = 10;
        res.title = Text.of(name + ":");
        res.setTargetWidth(width / 2);

        var selectScreen = new SelectionScreen<>(this, manager, val -> manager.set(val.getName()));
        var buttonSection = new HorizontalSection();
        buttonSection.setTargetWidth(width / 2);
        buttonSection.order = OrderType.Justified;
        buttonSection.children.addChild(new Label(
            5, 7,
            Text.of(manager.get().getDisplayName()).copy().formatted(Formatting.BOLD)
        ));
        buttonSection.children.addSelectableChild(new Button(5, 5, Text.of("Select ..."), () -> client.setScreen(selectScreen)));
        res.children.addSelectableChild(buttonSection);
        res.children.addChild(new Label(
            5, 3,
            Text.of("Author: " + manager.get().getAuthorOrDefault())
        ).setMaxWidth(width / 2));
        res.children.addChild(new Label(
            5, 3,
            Text.of(manager.get().getDescriptionOrDefault()).copy().formatted(Formatting.ITALIC)
        ).setMaxWidth(width / 2));

        return res;
    }
    private Section selectionsSection() {
        var res = new HorizontalSection();
        res.x = res.y = 5;
        res.title = Text.of("Animation config:");
        res.children.addSelectableChild(selectionSection(animation, "Animation"));
        res.children.addSelectableChild(selectionSection(ease, "Ease"));
        return res;
    }
    public AnimatedChunksScreen(Screen parent, Manager<Animation> animation, Manager<Ease> ease, ConfigManager config) {
        super(Text.of("Animated Chunks Config"));
        config.reload();
        this.animation = animation;
        this.ease = ease;
        this.config = config;
        mainSection.x = mainSection.y = 5;

        this.parent = parent;
    }
}
