package io.github._4drian3d.authmevelocity.lastserver.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public final class ConfigurationContainer<C> {
    private final AtomicReference<C> config;

    private ConfigurationContainer(
            final C config
    ) {
        this.config = new AtomicReference<>(config);
    }

    public C get() {
        return this.config.get();
    }

    public static <C> ConfigurationContainer<C> load(Path path, Class<C> clazz) throws IOException {
        path = path.resolve("config.conf");
        final boolean firstCreation = Files.notExists(path);
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts
                        .shouldCopyDefaults(true)
                        .header("""
                    AuthMeVelocity-LastServer-Addon | by 4drian3d
                    """)
                )
                .path(path)
                .build();


        final CommentedConfigurationNode node = loader.load();
        final C config = node.get(clazz);
        if (firstCreation) {
            node.set(clazz, config);
            loader.save(node);
        }

        return new ConfigurationContainer<>(config);
    }
}
