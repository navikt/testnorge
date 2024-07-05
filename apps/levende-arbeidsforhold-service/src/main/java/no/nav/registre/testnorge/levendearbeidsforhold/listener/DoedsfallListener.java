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
    private final ArbeidsforholdService arbeidsforholdService;
    private final String oensketHendelsestype = "DOEDSFALL_V1";

    /**
     * Lytter til og konsumerer hendelser fra Kafka hendelsesstrømmen på et gitt topic.
     * Behandler også alle hendelser av en gitt hendelsestype.
     * @param hendelser - Alle konsumerte hendelser fra hendelsesstrømmen
     */
    @KafkaListener(topics = doedsfallTopic)
    public void getHendelser(List<ConsumerRecord<String, Personhendelse>> hendelser) {
        for (ConsumerRecord<String, Personhendelse> hendelse: hendelser){

            String aktoerId = hendelse.key().split("\u001A")[1];
            String hendelsestype = hendelse.value().get(4).toString();

            if (validerHendelse(hendelsestype)){
                arbeidsforholdService.arbeidsforholdService(aktoerId);
            }
        }
    }

    /**
     * Validerer om hendelsen er av ønsket hendelsestype
     * @param personhendelse - Hendelse/opplysningstype, f.eks: FOLKEREGISTERIDENTIFIKATOR_V1, NAVN_V1, SIVILSTAND_V1, etc.
     * @return true dersom det er av ønsket hendelsestype, false hvis ikke
     */
    private Boolean validerHendelse(String personhendelse) {
        return personhendelse.equals(oensketHendelsestype);
    }
}

