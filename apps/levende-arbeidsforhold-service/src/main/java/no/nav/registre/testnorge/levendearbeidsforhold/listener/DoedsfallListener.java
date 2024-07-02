package no.nav.registre.testnorge.levendearbeidsforhold.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.person.pdl.leesah.Personhendelse;
import no.nav.registre.testnorge.levendearbeidsforhold.service.ArbeidsforholdService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class DoedsfallListener {
    private static final String doedsfallTopic = "pdl.leesah-v1";
    @Autowired
    private final ArbeidsforholdService arbeidsforholdsService;
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        log.info("Hello World");
        String id = "18426839454";
        arbeidsforholdsService.getArbeidsforhold(id);
    }

    @KafkaListener(topics = doedsfallTopic)
    public void getHendelser(List<ConsumerRecord<String, Personhendelse>> records) {
        for (ConsumerRecord<String, Personhendelse> record: records){

            String aktorID = record.key();

            //Validerer om vi skal fortsette eller ignorere hendelsen
            Boolean riktigHendelse = validerHendelse(record.value().get(4).toString());

            //Flyt for doedsfall-hendelser
            if (riktigHendelse){
                log.info("DØDSFALL. Aktør-ID: {} ", aktorID);
            }

        }

    }

    /**
     * Validerer om hendelsen er dødsfall
     * @param personhendelse - Hendelse/opplysningstype, f.eks: FOLKEREGISTERIDENTIFIKATOR_V1, NAVN_V1, SIVILSTAND_V1, etc.
     * @return true dersom det er dødsfall, false hvis ikke
     */
    private Boolean validerHendelse(String personhendelse) {
        return personhendelse.equals("DOEDSFALL_V1");
    }
}
