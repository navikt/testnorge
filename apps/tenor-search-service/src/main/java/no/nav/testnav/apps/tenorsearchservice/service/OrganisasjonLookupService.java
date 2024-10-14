package no.nav.testnav.apps.tenorsearchservice.service;

import no.nav.testnav.apps.tenorsearchservice.domain.LabelEnum;
import no.nav.testnav.apps.tenorsearchservice.domain.OrganisasjonLookups;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrganisasjonLookupService {

    public Map<String, String> getLookup(OrganisasjonLookups lookup) {
        return Stream.of(lookup.getValue().getEnumConstants())
                .collect(Collectors.toMap(LabelEnum::getName, LabelEnum::getLabel));
    }
}
