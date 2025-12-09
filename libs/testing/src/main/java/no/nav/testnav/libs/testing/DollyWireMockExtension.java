package no.nav.testnav.libs.testing;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DollyWireMockExtension implements BeforeAllCallback, AfterAllCallback, AfterEachCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DollyWireMockExtension.class);
    private static final String SERVER_KEY = "wireMockServer";

    @Override
    public void beforeAll(ExtensionContext context) {
        WireMockServer server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        server.start();
        WireMock.configureFor("localhost", server.port());
        context.getStore(NAMESPACE).put(SERVER_KEY, server);
        System.setProperty("wiremock.server.port", String.valueOf(server.port()));
    }

    @Override
    public void afterAll(ExtensionContext context) {
        WireMockServer server = context.getStore(NAMESPACE).get(SERVER_KEY, WireMockServer.class);
        if (server != null) {
            server.stop();
        }
        System.clearProperty("wiremock.server.port");
    }

    @Override
    public void afterEach(ExtensionContext context) {
        WireMockServer server = context.getStore(NAMESPACE).get(SERVER_KEY, WireMockServer.class);
        if (server != null) {
            server.resetAll();
        }
    }

    public static int getPort(ExtensionContext context) {
        WireMockServer server = context.getStore(NAMESPACE).get(SERVER_KEY, WireMockServer.class);
        return server != null ? server.port() : 0;
    }
}

