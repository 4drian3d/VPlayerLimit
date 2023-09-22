package io.github._4drian3d.vplayerlimit;

import com.velocitypowered.api.proxy.config.ProxyConfig;
import net.kyori.adventure.text.Component;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public interface Configuration {
    int playerLimit();

    Component message();

    static Configuration loadConfig(final Path path, ProxyConfig proxyConfig) throws IOException {
        final Path configPath = loadFiles(path);
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setPath(configPath)
                .build();

        final CommentedConfigurationNode loaded = loader.load();

        final int playerLimit = loaded.getNode("player-limit")
                .getInt(proxyConfig.getShowMaxPlayers());
        final Component kickMessage =  miniMessage().deserialize(loaded.getNode("kick-message")
                .getString(""));

        return new Configuration() {
            @Override
            public int playerLimit() {
                return playerLimit;
            }

            @Override
            public Component message() {
                return kickMessage;
            }
        };
    }

    private static Path loadFiles(Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }
        final Path configPath = path.resolve("config.conf");
        if (Files.notExists(configPath)) {
            try (var stream = Configuration.class.getClassLoader().getResourceAsStream("config.conf")) {
                Files.copy(Objects.requireNonNull(stream), configPath);
            }
        }
        return configPath;
    }
}