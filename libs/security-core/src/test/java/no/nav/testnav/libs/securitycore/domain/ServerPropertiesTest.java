package no.nav.testnav.libs.securitycore.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class ServerPropertiesTest {

    @Test
    void testOKScopes() {
        var props = new ServerProperties("url", "cluster", "name", "namespace");
        assertThatNoException().isThrownBy(props::validate);
    }

    @Test
    void testMissingUrl() {
        var props = new ServerProperties(null, "cluster", "name", "namespace");
        assertThatThrownBy(props::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("URL is not set; check configuration for ServerProperties");
    }

    @Test
    void testMissingCluster() {
        var props = new ServerProperties("url", null, "name", "namespace");
        assertThatThrownBy(props::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cluster is not set; check configuration for ServerProperties");
    }

    @Test
    void testMissingName() {
        var props = new ServerProperties("url", "cluster", null, "namespace");
        assertThatThrownBy(props::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Name is not set; check configuration for ServerProperties");
    }

    @Test
    void testMissingNamespace() {
        var props = new ServerProperties("url", "cluster", "name", null);
        assertThatThrownBy(props::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Namespace is not set; check configuration for ServerProperties");
    }

    @Test
    void testMultipleMissingAndSubclass() {
        var props = new ExtendsServerProperties(null, null, null, null);
        assertThatThrownBy(props::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("URL,Cluster,Name,Namespace is not set; check configuration for " + ExtendsServerProperties.class.getSimpleName());
    }

    private static class ExtendsServerProperties extends ServerProperties {
        private ExtendsServerProperties(String url, String cluster, String name, String namespace) {
            super(url, cluster, name, namespace);
        }
    }

}
