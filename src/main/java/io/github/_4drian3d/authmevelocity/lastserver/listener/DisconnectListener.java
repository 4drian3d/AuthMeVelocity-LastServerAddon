package io.github._4drian3d.authmevelocity.lastserver.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.github._4drian3d.authmevelocity.lastserver.LastServerAddon;
import io.github._4drian3d.authmevelocity.lastserver.database.Database;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class DisconnectListener implements LastLoginListener<DisconnectEvent> {
    @Inject
    private EventManager eventManager;
    @Inject
    private LastServerAddon plugin;
    @Inject
    private Database database;

    @Override
    public void register() {
        this.eventManager.register(plugin, DisconnectEvent.class, this);
    }

    @Override
    public @Nullable EventTask executeAsync(final DisconnectEvent event) {
        if (event.getLoginStatus() == DisconnectEvent.LoginStatus.CONFLICTING_LOGIN) {
            return null;
        }
        return EventTask.async(() -> {
            final Player player = event.getPlayer();
            final String server = player.getCurrentServer()
                    .map(ServerConnection::getServerInfo)
                    .map(ServerInfo::getName)
                    .orElse("");

            List<String> excludedServers = plugin.config().get().getExcludedServers();
            if (!excludedServers.contains(server)) {
                database.setLastServer(player.getUsername(), server);
            }
        });
    }
}
