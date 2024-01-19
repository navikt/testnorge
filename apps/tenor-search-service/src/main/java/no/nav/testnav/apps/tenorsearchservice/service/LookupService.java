package no.nav.testnav.apps.tenorsearchservice.service;

import no.nav.testnav.apps.tenorsearchservice.domain.Lookups;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class LookupService {

    public List<String> getLookup(Lookups lookup) {

        return Arrays.stream(lookup.getValue().getEnumConstants())
                .map(Object::toString)
                .toList();
    }
}
