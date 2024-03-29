package no.nav.testnav.apps.apptilganganalyseservice.service;

import no.nav.testnav.apps.apptilganganalyseservice.consumer.GithubConsumer;
import no.nav.testnav.apps.apptilganganalyseservice.domain.ApplicationConfig;
import no.nav.testnav.apps.apptilganganalyseservice.domain.DocumentType;
import no.nav.testnav.apps.apptilganganalyseservice.domain.SearchArgs;
import no.nav.testnav.apps.apptilganganalyseservice.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ReadApplicationConfigService extends ReadConfigService {

    public ReadApplicationConfigService(
            GithubConsumer githubConsumer,
            DocumentRepository documentRepository
    ) {
        super(githubConsumer, documentRepository);
    }

    public Flux<ApplicationConfig> getConfigBy(String appName, String owner, String repo) {
        var searchArgs = SearchArgs.Builder
                .builder()
                .addLanguage("yml")
                .addLanguage("yaml")
                .addSearchString("application: " + appName)
                .repo(repo)
                .owner(owner)
                .build();
        return githubConsumer
                .search(searchArgs, owner, repo)
                .flatMapMany(this::resolve)
                .map(ApplicationConfig::new);
    }

    @Override
    DocumentType getType() {
        return DocumentType.APPLICATION_V1;
    }
}
