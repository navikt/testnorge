package no.nav.registre.testnorge.levendearbeidsforhold.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.person.pdl.leesah.Personhendelse;
import no.nav.registre.testnorge.levendearbeidsforhold.service.ArbeidsforholdService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void getArbeidsforhold(String aktorID) {
        arbeidsforholdsService.getArbeidsforhold(aktorID);
    }

    @KafkaListener(topics = doedsfallTopic)
    public void getHendelser(List<ConsumerRecord<String, Personhendelse>> records) {
        for (ConsumerRecord<String, Personhendelse> record: records){

            String aktorID = record.key().split("\u001A")[1];

            //Validerer om vi skal fortsette eller ignorere hendelsen
            Boolean riktigHendelse = validerHendelse(record.value().get(4).toString());

            //Flyt for doedsfall-hendelser
            if (riktigHendelse){
                log.info("DØDSFALL. Hendelse: {} {}", record.key(), record.value().toString());
                getArbeidsforhold(aktorID); //List<Arbeidsforhold> arbeidsforhold = arbeidsforholdsService.getArbeidsforhold(aktorID);
                //siOppArbeidsforhold(arbeidsforhold);
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

