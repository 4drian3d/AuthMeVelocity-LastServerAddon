package io.github._4drian3d.authmevelocity.lastserver;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import io.github._4drian3d.authmevelocity.lastserver.database.Database;
import io.github._4drian3d.authmevelocity.lastserver.listener.DisconnectListener;
import io.github._4drian3d.authmevelocity.lastserver.listener.LastLoginListener;
import io.github._4drian3d.authmevelocity.lastserver.listener.PreSendOnLoginListener;
import org.slf4j.Logger;

import java.util.stream.Stream;

@Plugin(
	id = "authmevelocity-lastlogin-addon",
	name = "AuthMeVelocity-LastLogin-Addon",
	description = "LastLogin Addon",
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
	
	@Subscribe
	void onProxyInitialization(final ProxyInitializeEvent event) {
		this.database.initDatabase();
		Stream.of(PreSendOnLoginListener.class,	DisconnectListener.class)
				.map(this.injector::getInstance)
				.forEach(LastLoginListener::register);
		this.logger.info("Started AuthMeVelocity LastServer Addon");
	}
}