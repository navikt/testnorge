package no.nav.registre.testnorge.applikasjonsanalyseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.applikasjonsanalyseservice.adapter.ApplikasjonsanalyseAdapter;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.ApplicationAnalyseList;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.Dependencies;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.Properties;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.TopicAnalyseList;

@Service
@RequiredArgsConstructor
public class ApplikasjonsanalyseService {
    private final ApplikasjonsanalyseAdapter adapter;

    public Dependencies getApplikasjonsanalyseList(Properties properties) {
        var dependencies = new Dependencies();
        var applicationAnalyseList = adapter.search(properties);
        var topicsAnalyseList = adapter.findTopics(".nais/topics.yml");

        dependencies.addAll(applicationAnalyseList.getDependencies());
        dependencies.addAll(topicsAnalyseList.getDependencies());
        return dependencies;
    }
}
