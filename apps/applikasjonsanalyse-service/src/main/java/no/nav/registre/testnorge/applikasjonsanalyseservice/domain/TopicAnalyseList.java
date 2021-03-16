package no.nav.registre.testnorge.applikasjonsanalyseservice.domain;

import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.List;

import no.nav.registre.testnorge.applikasjonsanalyseservice.util.YAMLUtil;

public class TopicAnalyseList {
    private final List<TopicAnalyse> topics;

    @SneakyThrows
    public TopicAnalyseList(String content) {
        var array = YAMLUtil.Instance().read(
                content,
                TopicAnalyse[].class
        );
        topics = Arrays.asList(array.clone());
    }

    public Dependencies getDependencies() {
        var dependencies = new Dependencies();
        topics.forEach(value -> dependencies.addAll(value.getDependencies()));
        return dependencies;
    }
}
