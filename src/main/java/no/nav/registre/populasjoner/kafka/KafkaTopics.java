package no.nav.registre.populasjoner.kafka;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaTopics {

    private String pdlDokument;

    public static KafkaTopics create() {
        KafkaTopics kafkaTopics = new KafkaTopics();
        kafkaTopics.setPdlDokument("aapen-person-pdl-dokument-v1");
        return kafkaTopics;
    }

    public String[] getAllTopics() {
        return new String[] {
                this.getPdlDokument()
        };
    }
}
