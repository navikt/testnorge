package no.nav.registre.testnorge.applikasjonsanalyseservice.domain;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.yml.application.v1.KindApplikasjon;
import no.nav.registre.testnorge.applikasjonsanalyseservice.util.YAMLUtil;
import no.nav.testnav.libs.dto.dependencyanalysis.v1.DependencyDTO;

@Slf4j
public class ApplicationAnalyse {
    private final KindApplikasjon kindApplikasjon;



    public String getName() {
        return kindApplikasjon.getMetadata().getName();
    }

    public boolean hasAccessPolicy() {
        return kindApplikasjon.getSpec().getAccessPolicy() != null;
    }

    public boolean hasOutbound() {
        return hasAccessPolicy() && kindApplikasjon.getSpec().getAccessPolicy().getOutbound() != null;
    }

    public boolean hasInbound() {
        return hasAccessPolicy() && kindApplikasjon.getSpec().getAccessPolicy().getInbound() != null;
    }

    public Set<DependencyDTO> getInboundDependencies() {
        var accessPolicy = kindApplikasjon.getSpec().getAccessPolicy();
        if (!hasInbound() || accessPolicy.getInbound().getRules().isEmpty()) {
            return Collections.emptySet();
        }

        return accessPolicy
                .getInbound()
                .getRules()
                .stream()
                .map(rule -> DependencyDTO
                        .builder()
                        .name(rule.getApplication())
                        .external(rule.getNamespace() != null && !rule.getNamespace().equals("dolly"))
                        .build()
                )
                .collect(Collectors.toSet());
    }

    public Set<DependencyDTO> getOutboundDependencies() {
        var accessPolicy = kindApplikasjon.getSpec().getAccessPolicy();
        if (!hasOutbound()) {
            return Collections.emptySet();
        }

        var outbound = Optional.ofNullable(accessPolicy.getOutbound().getRules())
                .map(value -> value.stream()
                        .map(rule -> DependencyDTO
                                .builder()
                                .name(rule.getApplication())
                                .external(rule.getNamespace() != null && !rule.getNamespace().equals("dolly"))
                                .build()
                        ))
                .orElse(Stream.empty());

        var externals = Optional.ofNullable(accessPolicy
                .getOutbound()
                .getExternal()).map(value -> value.stream()
                .map(external -> DependencyDTO
                        .builder()
                        .name(external.getHost())
                        .external(true)
                        .build()
                )).orElse(Stream.empty());

        return Stream
                .concat(externals, outbound)
                .collect(Collectors.toSet());
    }

}
