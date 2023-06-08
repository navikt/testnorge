package no.nav.testnav.libs.securitycore.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ValidatingServerPropertiesTest {

    @Test
    void testAllPropertiesMissing() {
        var props = new TestServerProperties();
        var violations = props.validation();
        assertThat(violations)
                .hasSize(4)
                .allMatch(violation -> violation.getMessage().equals("must not be blank"));
    }

    @Test
    void testMissingURL() {
        var props = new TestServerProperties();
        props.setCluster("test");
        props.setName("test");
        props.setNamespace("test");
        var violations = props.validation();
        assertThat(violations)
                .hasSize(1)
                .allMatch(violation -> violation.getMessage().equals("must not be blank"));
    }

    @Test
    void testIllegalUseOfSetters() {
        var props = new TestServerProperties();
        props.setCluster("test");
        props.setNamespace("test");
        props.setName("test");
        props.setUrl("http://this.is.valid/for/sure");
        assertAll(
                () -> assertThatThrownBy(() -> props.setCluster(null))
                        .isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(() -> props.setCluster(""))
                        .isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(() -> props.setNamespace(null))
                        .isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(() -> props.setNamespace(""))
                        .isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(() -> props.setName(null))
                        .isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(() -> props.setName(""))
                        .isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(() -> props.setUrl(null))
                        .isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(() -> props.setUrl(""))
                        .isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(() -> props.setUrl("invalid url"))
                        .isInstanceOf(IllegalStateException.class)
        );
    }

    @Test
    void testAllPropertiesSet() {
        var props = new TestServerProperties();
        props.setCluster("test");
        props.setName("test");
        props.setNamespace("test");
        props.setUrl("http://this.is.valid/for/sure");
        var violations = props.validation();
        assertThat(violations).isEmpty();
    }

    @Test
    void testFailureThroughScopes() {
        var props = new TestServerProperties();
        assertThatThrownBy(() -> props.getScope(ResourceServerType.AZURE_AD))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("""
                        TestServerProperties.cluster must not be blank
                        TestServerProperties.name must not be blank
                        TestServerProperties.namespace must not be blank
                        TestServerProperties.url must not be blank""");
        assertThatThrownBy(() -> props.getScope(ResourceServerType.TOKEN_X))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("""
                        TestServerProperties.cluster must not be blank
                        TestServerProperties.name must not be blank
                        TestServerProperties.namespace must not be blank
                        TestServerProperties.url must not be blank""");
    }

    @Test
    void testExpectedScopes() {
        var props = new TestServerProperties();
        props.setUrl("http://needed.for.validation");
        props.setCluster("cluster");
        props.setName("name");
        props.setNamespace("namespace");
        assertThat(props.getScope(ResourceServerType.AZURE_AD))
                .isEqualTo("api://cluster.namespace.name/.default");
        assertThat(props.getScope(ResourceServerType.TOKEN_X))
                .isEqualTo("cluster:namespace:name");
    }

    @Configuration
    @ConfigurationProperties(prefix = "test")
    private static class TestServerProperties extends ValidatingServerProperties {
    }

}
