package no.nav.registre.testnorge.applikasjonsanalyseservice.domain;

import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.yml.topic.v1.KindTopic;
import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;
import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.DependencyDTO;

@RequiredArgsConstructor
public class TopicAnalyse {
    private final KindTopic topic;

    public Dependencies getDependencies() {
        var outbound = topic
                .getSpec()
                .getAcl()
                .stream()
                .filter(value -> value.getAccess().equals("readwrite") || value.getAccess().equals("write"))
                .collect(Collectors.toList());

        var inbound = topic
                .getSpec()
                .getAcl()
                .stream()
                .filter(value -> value.getAccess().equals("readwrite") || value.getAccess().equals("read"))
                .collect(Collectors.toList());


        var set = outbound
                .stream()
                .map(out -> ApplicationDependenciesDTO
                        .builder()
                        .applicationName(out.getApplication())
                        .dependencies(inbound.stream().map(inn -> DependencyDTO
                                .builder()
                                .external(!inn.getTeam().equals("dolly"))
                                .name(inn.getApplication())
                                .build()
                        ).collect(Collectors.toSet()))
                        .build()
                ).collect(Collectors.toSet());

        return new Dependencies(set);
    }
}
