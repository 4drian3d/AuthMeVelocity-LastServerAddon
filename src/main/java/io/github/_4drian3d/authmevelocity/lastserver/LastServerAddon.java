package io.github._4drian3d.authmevelocity.lastserver;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import io.github._4drian3d.authmevelocity.lastserver.configuration.Configuration;
import io.github._4drian3d.authmevelocity.lastserver.configuration.ConfigurationContainer;
import io.github._4drian3d.authmevelocity.lastserver.database.Database;
import io.github._4drian3d.authmevelocity.lastserver.listener.DisconnectListener;
import io.github._4drian3d.authmevelocity.lastserver.listener.LastLoginListener;
import io.github._4drian3d.authmevelocity.lastserver.listener.PreSendOnLoginListener;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.stream.Stream;

@Plugin(
	id = "authmevelocity-lastserver-addon",
	name = "AuthMeVelocity-LastServer-Addon",
	description = "LastServer Addon",
	version = Constants.VERSION,
	authors = { "4drian3d" },
	dependencies = {@Dependency(id = "authmevelocity")}
)
public final class LastServerAddon {
	@Inject
	private Injector injector;
	@Inject
	private Logger logger;
	@Inject
	private Database database;
	@Inject
	@DataDirectory
	private Path pluginDirectory;
	private ConfigurationContainer<Configuration> config;

	@Subscribe
	void onProxyInitialization(final ProxyInitializeEvent event) {
		try {
			this.config = ConfigurationContainer.load(pluginDirectory, Configuration.class);
		} catch (Exception e) {
			logger.error("Could not load config.conf file", e);
			return;
		}

		this.database.initDatabase();

		Stream.of(PreSendOnLoginListener.class,	DisconnectListener.class)
				.map(this.injector::getInstance)
				.forEach(LastLoginListener::register);
		this.logger.info("Started AuthMeVelocity LastServer Addon");
	}

	public ConfigurationContainer<Configuration> config() {
		return this.config;
	}
}