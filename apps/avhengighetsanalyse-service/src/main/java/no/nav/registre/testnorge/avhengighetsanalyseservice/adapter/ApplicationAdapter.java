package no.nav.registre.testnorge.avhengighetsanalyseservice.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.testnorge.avhengighetsanalyseservice.domain.Application;
import no.nav.registre.testnorge.avhengighetsanalyseservice.repository.ApplicationRepository;

@Component
@RequiredArgsConstructor
public class ApplicationAdapter {
    private final ApplicationRepository repository;

    public List<Application> getAllApplications() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(Application::new)
                .collect(Collectors.toList());
    }

    public void save(Application application) {
        repository.save(application.toModel());
    }

    public void deleteApplication(String name) {
        repository.deleteById(name);
    }

}
