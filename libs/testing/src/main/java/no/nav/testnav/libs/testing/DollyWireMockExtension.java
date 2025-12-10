package no.nav.testnav.libs.testing;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DollyWireMockExtension implements BeforeAllCallback, AfterEachCallback {

    private static final Object LOCK = new Object();
    private static WireMockServer server;

    @Override
    public void beforeAll(ExtensionContext context) {
        synchronized (LOCK) {
            if (server == null) {
                server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
                server.start();
                System.setProperty("wiremock.server.port", String.valueOf(server.port()));
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (server != null && server.isRunning()) {
                        server.stop();
                    }
                }));
            }
            WireMock.configureFor("localhost", server.port());
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (server != null) {
            server.resetAll();
        }
    }

    public static int getPort() {
        return server != null ? server.port() : 0;
    }
}

