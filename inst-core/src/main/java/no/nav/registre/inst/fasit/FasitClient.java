package no.nav.registre.inst.fasit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.freg.fasit.utils.FasitService;

@Component
@RequiredArgsConstructor
public class FasitClient {

    private final FasitService fasitService;

    public List<String> getAllEnvironments(String... environments) {
        return Arrays.stream(environments)
                .map(fasitService::findEnvironmentNames)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
