package no.nav.registre.orkestratoren.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import no.nav.registre.testnorge.avro.report.Entry;
import no.nav.registre.testnorge.avro.report.Report;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dependencyanalysis.DependencyType;
import no.nav.registre.testnorge.libs.reporting.ReportConsumer;

@Component
@DependencyOn(value = "rapportering-api", type = DependencyType.QUEUE)
public class RapporteringConsumer implements ReportConsumer {

    private final String topic;
    private final KafkaTemplate<String, Report> kafkaTemplate;
    public final String applicationName;

    public RapporteringConsumer(@Value("${kafka.topic}") String topic, KafkaTemplate<String, Report> kafkaTemplate, @Value("${application.name}") String applicationName) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
        this.applicationName = applicationName;
    }

    @Override
    public void send(no.nav.registre.testnorge.libs.reporting.domin.Report report) {
        kafkaTemplate.send(
                topic,
                Report.newBuilder()
                        .setApplicationName(applicationName)
                        .setStart(report.getStart().toString())
                        .setEnd(report.getEnd().toString())
                        .setName(report.getName())
                        .setEntries(report.getEntries().stream().map(value -> Entry
                                .newBuilder()
                                .setDescription(value.getDescription())
                                .setStatus(value.getStatus().name())
                                .setTime(value.getTime().toString())
                                .build()
                        ).collect(Collectors.toList()))
                        .build()
        );
    }
}
