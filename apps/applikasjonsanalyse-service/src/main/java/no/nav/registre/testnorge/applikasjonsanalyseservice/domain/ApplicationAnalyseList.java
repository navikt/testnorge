package no.nav.registre.testnorge.applikasjonsanalyseservice.domain;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;
import no.nav.testnav.libs.dto.dependencyanalysis.v1.DependencyDTO;

@RequiredArgsConstructor
public class ApplicationAnalyseList {
    private final List<ApplicationAnalyse> applikasjonsanalyse;

    public Dependencies getDependencies() {

        var outboundMap = applikasjonsanalyse.stream().filter(ApplicationAnalyse::hasOutbound)
                .collect(Collectors.toMap(ApplicationAnalyse::getName, ApplicationAnalyse::getOutboundDependencies));


        var inboundMap = applikasjonsanalyse.stream().filter(ApplicationAnalyse::hasInbound)
                .collect(Collectors.toMap(ApplicationAnalyse::getName, ApplicationAnalyse::getInboundDependencies));


        inboundMap.forEach((key, value) -> value.forEach(dto -> {
            var set = outboundMap.get(dto.getName());
            var dependency = DependencyDTO.builder().name(key).external(false).build();
            if (set != null) {
                set.add(dependency);
                outboundMap.put(dto.getName(), set);
            } else {
                outboundMap.put(dto.getName(), new HashSet<>(Collections.singletonList(dependency)));
            }
        }));

        var set = outboundMap.entrySet().stream().map(entry -> ApplicationDependenciesDTO
                .builder()
                .applicationName(entry.getKey())
                .dependencies(entry.getValue())
                .build()
        ).collect(Collectors.toSet());
        return new Dependencies(set);
    }
}
