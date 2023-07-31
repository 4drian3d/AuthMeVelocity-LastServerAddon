package io.github._4drian3d.authmevelocity.lastserver.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github._4drian3d.authmevelocity.api.velocity.event.PreSendOnLoginEvent;
import io.github._4drian3d.authmevelocity.api.velocity.event.ServerResult;
import io.github._4drian3d.authmevelocity.lastserver.LastServerAddon;
import io.github._4drian3d.authmevelocity.lastserver.database.Database;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PreSendOnLoginListener implements LastLoginListener<PreSendOnLoginEvent> {
    @Inject
    private EventManager eventManager;
    @Inject
    private ProxyServer proxyServer;
    @Inject
    private LastServerAddon plugin;
    @Inject
    private Database database;

    @Override
    public EventTask executeAsync(final PreSendOnLoginEvent event) {
        return EventTask.withContinuation(continuation -> {
            final String player = event.player().getUsername();
            final String newServer = this.database.lastServerOf(player);
            if (newServer != null) {
                this.proxyServer.getServer(newServer)
                        .ifPresent(server -> event.setResult(ServerResult.allowed(server)));
            }
            continuation.resume();
        });
    }

    @Override
    public void register() {
        this.eventManager.register(plugin, PreSendOnLoginEvent.class, this);
    }
}
