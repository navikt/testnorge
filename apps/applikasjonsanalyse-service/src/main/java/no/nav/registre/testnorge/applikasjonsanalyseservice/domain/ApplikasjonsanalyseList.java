package no.nav.registre.testnorge.applikasjonsanalyseservice.domain;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;
import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.DependencyDTO;

@RequiredArgsConstructor
public class ApplikasjonsanalyseList {
    private final List<Applikasjonsanalyse> applikasjonsanalyse;

    public Set<ApplicationDependenciesDTO> toDTO() {

        var outboundMap = applikasjonsanalyse.stream().filter(Applikasjonsanalyse::hasOutbound)
                .collect(Collectors.toMap(Applikasjonsanalyse::getName, Applikasjonsanalyse::getOutboundDependencies));


        var inboundMap = applikasjonsanalyse.stream().filter(Applikasjonsanalyse::hasInbound)
                .collect(Collectors.toMap(Applikasjonsanalyse::getName, Applikasjonsanalyse::getInboundDependencies));


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

        return outboundMap.entrySet().stream().map(entry -> ApplicationDependenciesDTO
                .builder()
                .applicationName(entry.getKey())
                .dependencies(entry.getValue())
                .build()
        ).collect(Collectors.toSet());
    }
}
