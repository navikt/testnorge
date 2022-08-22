package no.nav.testnav.apps.apptilganganalyseservice.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import no.nav.testnav.apps.apptilganganalyseservice.consumer.GithubConsumer;
import no.nav.testnav.apps.apptilganganalyseservice.domain.DeployConfig;
import no.nav.testnav.apps.apptilganganalyseservice.domain.DocumentType;
import no.nav.testnav.apps.apptilganganalyseservice.domain.SearchArgs;
import no.nav.testnav.apps.apptilganganalyseservice.repository.DocumentRepository;

@Service
public class ReadDeployConfigService extends ReadConfigService {

    public ReadDeployConfigService(
            GithubConsumer githubConsumer,
            DocumentRepository documentRepository
    ) {
        super(githubConsumer, documentRepository);
    }

    public Flux<DeployConfig> getConfigBy(String owner, String repo) {
        var searchArgs = SearchArgs.Builder
                .builder()
                .addLanguage("yml")
                .addLanguage("yaml")
                .addSearchString("uses: navikt/testnorge/.github/workflows/common.deploy.yml")
                .repo(repo)
                .owner(owner)
                .build();
        return githubConsumer.search(searchArgs, owner, repo)
                .flatMapMany(this::resolve)
                .map(DeployConfig::new);
    }

    @Override
    DocumentType getType() {
        return DocumentType.DEPLOY_V2;
    }
}
