package no.nav.skattekortservice.service;

import no.nav.testnav.libs.dto.skattekortservice.v1.Oppslagstyper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OppslagService {
    public Mono<Map<String, String>> getTyper(Oppslagstyper oppslagstype) {

        var enums = Arrays.stream(oppslagstype.getValue().getEnumConstants());
//                .collect(Collectors.toMap(value -> value, value -> ((oppslagstype.getValue()) value).getName))
        return Mono.empty();
    }
}
