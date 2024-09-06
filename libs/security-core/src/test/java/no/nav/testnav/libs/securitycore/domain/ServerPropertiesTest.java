package no.nav.testnav.libs.securitycore.domain;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ServerPropertiesTest {

    @Test
    void testAllPropertiesMissing() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var props = new TestServerProperties();
            props.setCluster(null);
            props.setName(null);
            props.setNamespace(null);
            props.setUrl(null);
            var violations = factory.getValidator().validate(props);
            assertThat(violations)
                    .hasSize(4)
                    .allMatch(violation -> violation.getMessage().equals("must not be blank"));
        }
    }

    @Test
    void testMissingURL() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var props = new TestServerProperties();
            props.setCluster("test");
            props.setName("test");
            props.setNamespace("test");
            var violations = factory.getValidator().validate(props);
            assertThat(violations)
                    .hasSize(1)
                    .allMatch(violation -> violation.getMessage().equals("must not be blank"));
        }
    }

    @Test
    void testInvalidURL() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var props = new TestServerProperties();
            props.setCluster("test");
            props.setName("test");
            props.setNamespace("test");
            props.setUrl("invalid");
            var violations = factory.getValidator().validate(props);
            assertThat(violations)
                    .hasSize(1)
                    .allMatch(violation -> violation.getMessage().equals("must be a valid URL"));
        }
    }

    @Test
    void testAllPropertiesSet() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var props = new TestServerProperties();
            props.setCluster("test");
            props.setName("test");
            props.setNamespace("test");
            props.setUrl("http://this.is.valid/for/sure");
            var violations = factory.getValidator().validate(props);
            assertThat(violations).isEmpty();
        }
    }

    @Test
    void testExpectedScopes() {
        var props = new TestServerProperties();
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
    private static class TestServerProperties extends ServerProperties {
    }

}
