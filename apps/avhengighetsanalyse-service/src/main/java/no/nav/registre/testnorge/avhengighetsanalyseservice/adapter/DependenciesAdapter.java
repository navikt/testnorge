package no.nav.registre.testnorge.avhengighetsanalyseservice.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

import no.nav.registre.testnorge.avhengighetsanalyseservice.consumer.DependencyConsumer;
import no.nav.registre.testnorge.avhengighetsanalyseservice.repository.DependenciesHistoryRepository;
import no.nav.registre.testnorge.avhengighetsanalyseservice.repository.DependenciesVersionRepository;
import no.nav.registre.testnorge.avhengighetsanalyseservice.repository.model.DependenciesHistoryModel;
import no.nav.registre.testnorge.avhengighetsanalyseservice.repository.model.DependenciesVersionModel;
import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

@Slf4j
@Component
@RequiredArgsConstructor
public class DependenciesAdapter {
    private final DependencyConsumer consumer;
    private final DependenciesHistoryRepository dependenciesHistoryRepository;
    private final DependenciesVersionRepository dependenciesVersionRepository;
    private final ObjectMapper mapper;

    private DependenciesVersionModel getDependencyVersion(Set<ApplicationDependenciesDTO> dependencies) throws JsonProcessingException {
        long hash = dependencies.hashCode();
        String json = mapper.writeValueAsString(dependencies);
        return dependenciesVersionRepository
                .findById(hash)
                .orElseGet(() -> dependenciesVersionRepository.save(
                        DependenciesVersionModel
                                .builder()
                                .hash(hash)
                                .json(json)
                                .build()
                ));
    }

    private void save(Set<ApplicationDependenciesDTO> dependencies) throws JsonProcessingException {
        DependenciesHistoryModel model = DependenciesHistoryModel
                .builder()
                .dependencyVersion(getDependencyVersion(dependencies))
                .build();
        dependenciesHistoryRepository.save(model);
    }

    public Set<ApplicationDependenciesDTO> getDependencies() {
        return consumer.getDependencies();
    }

    public void registerDependenciesHistory() {
        try {
            Set<ApplicationDependenciesDTO> dependencies = consumer.getDependencies();
            log.info("Registrerer nye avhengigheter");
            save(dependencies);
        } catch (Exception e) {
            log.error("Klarte ikke å registrere historikk for avhengigheter.", e);
        }
    }

}
