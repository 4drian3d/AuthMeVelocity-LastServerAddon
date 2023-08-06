package io.github._4drian3d.authmevelocity.lastserver.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

@ConfigSerializable
@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal"})
public final class Configuration {
    @Comment("Require players to have the lastserver.use permission?")
    private boolean requirePermission = false;

    @Comment("A list of servers to not save as a player's last server")
    private List<String> excludedServers = List.of("login", "spawn");

    public boolean getRequirePermission() {
        return this.requirePermission;
    }

    public List<String> getExcludedServers() {
        return this.excludedServers;
    }
}