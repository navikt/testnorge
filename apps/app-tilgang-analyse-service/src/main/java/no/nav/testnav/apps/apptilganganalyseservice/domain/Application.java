package no.nav.testnav.apps.apptilganganalyseservice.domain;

import lombok.SneakyThrows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.apps.apptilganganalyseservice.domain.yml.application.v1.AccessPolicy;
import no.nav.testnav.apps.apptilganganalyseservice.domain.yml.application.v1.KindApplikasjon;
import no.nav.testnav.apps.apptilganganalyseservice.domain.yml.application.v1.Rule;
import no.nav.testnav.apps.apptilganganalyseservice.dto.ApplicationDTO;
import no.nav.testnav.apps.apptilganganalyseservice.util.YAMLUtil;

public class Application {
    private final KindApplikasjon kindApplikasjon;

    @SneakyThrows
    public Application(String content) {
        try {
            String value = content
                    .replace("{{ image }}", "unknown")
                    .replace("{{image}}", "unknown");
            this.kindApplikasjon = YAMLUtil.Instance().read(
                    value,
                    KindApplikasjon.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Klarer ikke å convertere til yaml.", e);
        }
    }

    public List<String> getInbound() {
        if (getAccessPolicy() == null || getAccessPolicy().getInbound() == null) {
            return Collections.emptyList();
        }
        return getAccessPolicy()
                .getInbound()
                .getRules()
                .stream()
                .map(Rule::getApplication)
                .collect(Collectors.toList());
    }


    public List<String> getOutbound() {
        if (getAccessPolicy() == null || getAccessPolicy().getOutbound() == null) {
            return Collections.emptyList();
        }
        return getAccessPolicy()
                .getOutbound()
                .getRules()
                .stream()
                .map(Rule::getApplication)
                .collect(Collectors.toList());
    }

    private AccessPolicy getAccessPolicy() {
        return kindApplikasjon.getSpec().getAccessPolicy();
    }

    public String getName() {
        return kindApplikasjon.getMetadata().getName();
    }

    public String getNamespace() {
        return kindApplikasjon.getMetadata().getNamespace();
    }

    public ApplicationDTO toDTO() {
        return new ApplicationDTO(getName(), getNamespace());
    }

}