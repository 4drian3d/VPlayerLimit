package io.github._4drian3d.vplayerlimit;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "vplayerlimit",
        name = "VPlayerLimit",
        description = "Limit how many players can join your server",
        version = Constants.VERSION,
        authors = {"4drian3d"}
)
public final class VPlayerLimit {
    @Inject
    private ProxyServer proxyServer;
    @Inject
    private Logger logger;
    @Inject
    @DataDirectory
    private Path path;
    @Inject
    private EventManager eventManager;

    @Subscribe
    void onProxyInitialization(final ProxyInitializeEvent event) throws IOException {
        logger.info("Starting VPlayerLimit");
        final Configuration configuration = Configuration.loadConfig(path, proxyServer.getConfiguration());

        this.eventManager.register(this, PreLoginEvent.class, PostOrder.NORMAL, preLoginEvent -> {
            if (proxyServer.getPlayerCount() > configuration.playerLimit()) {
                preLoginEvent.setResult(PreLoginEvent.PreLoginComponentResult.denied(configuration.message()));
            }
        });
    }
}