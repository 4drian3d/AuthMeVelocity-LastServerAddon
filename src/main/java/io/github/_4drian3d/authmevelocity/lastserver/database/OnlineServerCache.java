package io.github._4drian3d.authmevelocity.lastserver.database;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.concurrent.TimeUnit;

@Singleton
public class OnlineServerCache {
    private final LoadingCache<String, Boolean> cache;

    @Inject
    public OnlineServerCache(final ProxyServer proxyServer) {
        this.cache =  Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .build(server -> proxyServer.getServer(server)
                        .filter(registeredServer -> registeredServer.ping()
                                .handle((ping, throwable) -> ping != null)
                                .join())
                        .isPresent());
    }

    public boolean isOnline(final String server) {
        return this.cache.get(server);
    }

    public boolean isOnline(final RegisteredServer server) {
        return this.isOnline(server.getServerInfo().getName());
    }
}