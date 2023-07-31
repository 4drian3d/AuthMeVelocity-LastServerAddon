package io.github._4drian3d.authmevelocity.lastserver.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public final class Database {
    private static final String CREATE_SENTENCE = """
                        CREATE TABLE IF NOT EXISTS last_server\
                        (\
                        `player` VARCHAR(20) NOT NULL PRIMARY KEY, \
                        `server` VARCHAR(20) NOT NULL\
                        );
                        """;
    private static final String SELECT_BY_PLAYER = "SELECT `server` FROM last_server WHERE `player` = ?;";
    private static final String INSERT_DATA = "INSERT INTO last_server(`player`, `server`) VALUES (?, ?);";

    private final HikariDataSource source;
    private final Logger logger;
    @Inject
    public Database(
            final @DataDirectory Path dataDirectory,
            final Logger logger
    ) throws IOException {
        if (Files.notExists(dataDirectory)) {
            Files.createDirectory(dataDirectory);
        }
        final Path databasePath = dataDirectory.resolve("database.db").toAbsolutePath();
        if (Files.notExists(databasePath)) {
            Files.createFile(databasePath);
        }
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.h2.Driver");
        hikariConfig.setJdbcUrl("jdbc:h2:"+databasePath);

        this.source = new HikariDataSource(hikariConfig);
        this.logger = logger;
    }

    public String lastServerOf(final String playerName) {
        try (final Connection connection = this.source.getConnection();
             final PreparedStatement statement = fromPlayer(playerName, connection);
             final ResultSet rs = statement.executeQuery()
        ) {
            return rs.getString("server");
        } catch (final SQLException e) {
            return null;
        }
    }

    public void setLastServer(final String playerName, final String server) {
        try (final Connection connection = this.source.getConnection();
             final PreparedStatement statement = connection.prepareStatement(INSERT_DATA);
        ) {
            statement.setString(1, playerName);
            statement.setString(2, server);
            statement.executeUpdate();
        } catch (final SQLException e) {
            this.logger.warn("An error occurred updating last server information of player {}", playerName);
        }
    }

    private PreparedStatement fromPlayer(final String playerName, final Connection connection) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(SELECT_BY_PLAYER);
        statement.setString(1, playerName);
        return statement;
    }

    public void initDatabase() {
        try (final Connection connection = this.source.getConnection();
             final PreparedStatement statement = connection.prepareStatement(CREATE_SENTENCE)
        ) {
            statement.executeUpdate();
        } catch (final Exception e) {
            this.logger.warn("An error occurred loading database", e);
        }
    }
}
