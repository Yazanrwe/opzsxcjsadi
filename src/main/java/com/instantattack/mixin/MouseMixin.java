package com.instantattack.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At("TAIL"))
    private void instantattack$onClick(long window, int button, int action, int mods, CallbackInfo ci) {
        // action 1 = press
        if (action != 1) return;
        MinecraftClient mc = this.client;
        if (mc.player == null || mc.world == null || mc.currentScreen != null) return;
        if (!mc.mouse.isCursorLocked()) return;
        if (!mc.options.attackKey.matchesMouse(button)) return;

        // Attack right now (same thing vanilla does, just without waiting for the tick)
        ((MinecraftClientInvoker) mc).instantattack$doAttack();

        // Drain the queued press so the tick doesn't attack a second time
        while (mc.options.attackKey.wasPressed()) { }
    }
}
