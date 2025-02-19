package de.maxhenkel.better_respawn.mixins;

import de.maxhenkel.better_respawn.Main;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin extends Screen {

    @Shadow
    protected ITextComponent field_243285_p;

    @Shadow
    private int enableButtonsTimer;

    protected Button respawnButton;

    protected int delay;

    protected DeathScreenMixin(ITextComponent titleIn) {
        super(titleIn);
    }

    @Inject(method = "init", at = @At("RETURN"), cancellable = true)
    public void init(CallbackInfo ci) {
        respawnButton = (Button) buttons.get(0);
        delay = Main.SERVER_CONFIG.respawnCooldown.get() <= 20 ? 0 : -1;
    }

    @Inject(method = "confirmCallback", at = @At("HEAD"), cancellable = true)
    public void confirmCallback(boolean disconnect, CallbackInfo ci) {
        if (disconnect) {
            return;
        }
        ci.cancel();
        minecraft.displayGuiScreen(this);
    }

    @Inject(method = "tick", at = @At("RETURN"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (delay > 0) {
            field_243285_p = new TranslationTextComponent("message.better_respawn.respawn_timer", (delay + 19) / 20);
        } else if (delay < 0) {
            field_243285_p = new StringTextComponent("");
        } else {
            field_243285_p = (new TranslationTextComponent("deathScreen.score")).appendString(": ").append((new StringTextComponent(Integer.toString(minecraft.player.getScore()))).mergeStyle(TextFormatting.YELLOW));
        }
        respawnButton.active = delay <= 0 && enableButtonsTimer >= 20;
        if (delay > 0) {
            delay--;
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

}
