package com.donniebib.ironman;

import com.donniebib.ironman.item.ModItems;
import com.donniebib.ironman.network.IronManNetworking;
import com.donniebib.ironman.network.ServerHelmetStateManager;
import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IronManMod implements ModInitializer {
    public static final String MOD_ID = "ironman";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        IronManNetworking.initialize();

        ServerTickEvents.END_SERVER_TICK.register(ServerHelmetStateManager::tick);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                ServerHelmetStateManager.syncAllTo(handler.player));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                ServerHelmetStateManager.removePlayer(handler.player.getUUID()));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> ServerHelmetStateManager.clear());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("ironman")
                .then(Commands.literal("mask")
                    .then(Commands.literal("open").executes(ctx -> {
                        ServerHelmetStateManager.handleAction(ctx.getSource().getPlayerOrException(),
                                com.donniebib.ironman.animation.MaskAction.OPEN);
                        return Command.SINGLE_SUCCESS;
                    }))
                    .then(Commands.literal("close").executes(ctx -> {
                        ServerHelmetStateManager.handleAction(ctx.getSource().getPlayerOrException(),
                                com.donniebib.ironman.animation.MaskAction.CLOSE);
                        return Command.SINGLE_SUCCESS;
                    }))))
        );

        LOGGER.info("Iron Man Voice Mask initialized");
    }
}
