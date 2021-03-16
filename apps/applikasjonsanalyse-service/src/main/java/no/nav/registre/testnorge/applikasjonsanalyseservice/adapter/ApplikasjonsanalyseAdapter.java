package no.nav.registre.testnorge.applikasjonsanalyseservice.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.GithubConsumer;
import no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.dto.SearchDTO;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.ApplicationAnalyse;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.ApplicationAnalyseList;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.Properties;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.TopicAnalyseList;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.ApplicationRepository;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationModel;

@Component
@RequiredArgsConstructor
public class ApplikasjonsanalyseAdapter {
    private final GithubConsumer githubConsumer;
    private final ApplicationRepository repository;

    public ApplicationAnalyseList search(Properties properties) {
        return new ApplicationAnalyseList(fetch(githubConsumer.search(properties), properties));
    }

    public TopicAnalyseList findTopics(String path) {
        var blob = githubConsumer.getBlobFromPathAndRef(path, "avien-kafka");
        return new TopicAnalyseList(new String(blob));
    }

    private List<ApplicationAnalyse> fetch(SearchDTO searchDTO, Properties properties) {
        return searchDTO
                .getItems()
                .stream()
                .map(item -> fetch(item.getSha(), properties))
                .collect(Collectors.toList());
    }

    private ApplicationAnalyse fetch(String sha, Properties properties) {
        var model = repository.findByShaAndRepositoryAndOrganisation(
                sha,
                properties.getRepository(),
                properties.getOrganisation()
        );
        if (model.isPresent()) {
            return new ApplicationAnalyse(model.get().getContent());
        }

        var blob = githubConsumer.getBlobFromSha(sha);
        var content = new String(blob);
        var applikasjonsanalyse = new ApplicationAnalyse(content);

        repository.save(ApplicationModel
                .builder()
                .sha(sha)
                .content(content)
                .name(applikasjonsanalyse.getName())
                .organisation(properties.getOrganisation())
                .repository(properties.getRepository())
                .build()
        );

        return applikasjonsanalyse;
    }

}
