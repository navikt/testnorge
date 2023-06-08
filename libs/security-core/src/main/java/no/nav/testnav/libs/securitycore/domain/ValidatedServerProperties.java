package no.nav.testnav.libs.securitycore.domain;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ConfigurationPropertiesScan("no.nav.testnav")
@NoArgsConstructor
@Getter
public abstract class ValidatedServerProperties implements ServerProperties {

    /**
     * NAIS ingress URL for target service.
     */
    @NotBlank
    @URL
    private String url;

    /**
     * NAIS cluster for target service, e.g. <pre>dev-gcp</pre.
     */
    @NotBlank
    private String cluster;

    /**
     * NAIS defined name for target service.
     */
    @NotBlank
    private String name;

    /**
     * NAIS namespace for target service, e.g. <pre>dolly</pre>.
     */
    @NotBlank
    private String namespace;

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();


    @PostConstruct
    public void postConstruct() {
        handle(validation());
    }

    Set<ConstraintViolation<ValidatedServerProperties>> validation() {
        return Stream
                .of(validation("url"), validation("cluster"), validation("name"), validation("namespace"))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Set<ConstraintViolation<ValidatedServerProperties>> validation(String property) {
        var validator = factory.getValidator();
        return validator.validateProperty(this, property);
    }

    private static void handle(Set<ConstraintViolation<ValidatedServerProperties>> violations)
            throws IllegalStateException {
        if (!violations.isEmpty()) {
            var messages = new ArrayList<String>(violations.size());
            violations.forEach(violation -> messages.add("%s.%s %s".formatted(
                    violation.getLeafBean().getClass().getSimpleName(),
                    violation.getPropertyPath(),
                    violation.getMessage()
            )));
            Collections.sort(messages);
            throw new IllegalStateException(String.join("\n", messages));
        }
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
        handle(validation("cluster"));
    }

    public void setName(String name) {
        this.name = name;
        handle(validation("name"));
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
        handle(validation("namespace"));
    }

    public void setUrl(@NotBlank String url) {
        this.url = url;
        handle(validation("url"));
    }

    @Override
    public String toTokenXScope() {
        handle(validation());
        return ServerProperties.super.toTokenXScope();
    }

    @Override
    public String toAzureAdScope() {
        handle(validation());
        return ServerProperties.super.toAzureAdScope();
    }

}