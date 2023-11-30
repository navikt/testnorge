package no.nav.testnav.apps.apptilganganalyseservice.domain;

import no.nav.testnav.apps.apptilganganalyseservice.domain.yml.application.v1.AccessPolicy;
import no.nav.testnav.apps.apptilganganalyseservice.domain.yml.application.v1.KindApplikasjon;
import no.nav.testnav.apps.apptilganganalyseservice.domain.yml.application.v1.Rule;
import no.nav.testnav.apps.apptilganganalyseservice.repository.entity.DocumentEntity;
import no.nav.testnav.apps.apptilganganalyseservice.util.YAMLUtil;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.Collections;
import java.util.List;

public class ApplicationConfig {
    private final KindApplikasjon kindApplikasjon;
    private final String path;

    public ApplicationConfig(DocumentEntity entity) {
        this.path = entity.getPath();
        try {
            String value = entity.getContent()
                    .replace("{{ image }}", "unknown")
                    .replace("{ { image } }", "unknown")
                    .replace("{{image}}", "unknown");
            this.kindApplikasjon = YAMLUtil.Instance().read(
                    value,
                    KindApplikasjon.class
            );
        } catch (Exception e) {
            throw new YAMLException("Klarer ikke å convertere til yaml. ( " + entity.getPath() + " )", e);
        }
    }

    public List<String> getInbound() {
        if (getAccessPolicy() == null
                || getAccessPolicy().getInbound() == null
                || getAccessPolicy().getInbound().getRules() == null
        ) {
            return Collections.emptyList();
        }
        return getAccessPolicy()
                .getInbound()
                .getRules()
                .stream()
                .map(Rule::getApplication)
                .toList();
    }

    public List<String> getOutbound() {
        if (getAccessPolicy() == null
                || getAccessPolicy().getOutbound() == null
                || getAccessPolicy().getOutbound().getRules() == null
        ) {
            return Collections.emptyList();
        }
        return getAccessPolicy()
                .getOutbound()
                .getRules()
                .stream()
                .map(Rule::getApplication)
                .toList();
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return kindApplikasjon.getMetadata().getName();
    }

    public String getNamespace() {
        return kindApplikasjon.getMetadata().getNamespace();
    }

    private AccessPolicy getAccessPolicy() {
        return kindApplikasjon.getSpec().getAccessPolicy();
    }
}