package com.serverlistzoom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.option.SimpleOption;

/**
 * Forces GUI Scale to a fixed value ONLY while the multiplayer server list
 * screen ("Play Multiplayer" / saved servers list) is open, then restores
 * whatever GUI Scale the player normally uses the moment that screen closes.
 *
 * Nothing else - inventory, chat, options, Direct Connect / Add Server,
 * every other screen - is touched at all.
 */
public class ServerListZoomMod implements ClientModInitializer {

    // Change this if you want a different scale for the server list.
    // Matches vanilla's GUI Scale option: 0 = Auto, 1, 2, 3, 4...
    private static final int SERVER_LIST_SCALE = 2;

    // Remembers the player's normal scale so we can put it back exactly.
    private int savedScale = -1;
    private boolean active = false;

    @Override
    public void onInitializeClient() {
        // AFTER_INIT (not BEFORE_INIT) so we don't fight with the screen's
        // own initial init() call - see README for why this matters.
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof MultiplayerScreen) || active) {
                return;
            }

            SimpleOption<Integer> guiScale = client.options.getGuiScale();
            savedScale = guiScale.getValue();
            active = true;

            if (savedScale != SERVER_LIST_SCALE) {
                guiScale.setValue(SERVER_LIST_SCALE);
                // Re-applies the new scale and re-inits the current screen at it.
                client.onResolutionChanged();
            }

            // Fires the moment THIS screen instance closes - going back to the
            // title screen, opening Direct Connect / Add Server, or joining a
            // world all count as "closing" this screen.
            ScreenEvents.remove(screen).register(closedScreen -> {
                if (active) {
                    SimpleOption<Integer> gs = client.options.getGuiScale();
                    if (gs.getValue() != savedScale) {
                        gs.setValue(savedScale);
                        client.onResolutionChanged();
                    }
                }
                active = false;
                savedScale = -1;
            });
        });
    }
}
