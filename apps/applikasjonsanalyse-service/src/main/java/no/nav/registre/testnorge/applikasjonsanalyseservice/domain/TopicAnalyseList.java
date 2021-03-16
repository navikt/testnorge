package no.nav.registre.testnorge.applikasjonsanalyseservice.domain;

import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.yml.topic.v1.KindTopic;
import no.nav.registre.testnorge.applikasjonsanalyseservice.util.YAMLUtil;

public class TopicAnalyseList {
    private final List<TopicAnalyse> topics;

    @SneakyThrows
    public TopicAnalyseList(String content) {
        topics = Arrays.stream(content.split("---"))
                .filter(value -> !value.equals(""))
                .map(TopicAnalyse::new)
                .collect(Collectors.toList());
    }

    public Dependencies getDependencies() {
        var dependencies = new Dependencies();
        topics.forEach(value -> dependencies.addAll(value.getDependencies()));
        return dependencies;
    }
}
