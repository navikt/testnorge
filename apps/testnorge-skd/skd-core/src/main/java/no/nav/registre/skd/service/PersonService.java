package no.nav.registre.skd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.consumer.PersonConsumer;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonConsumer personConsumer;

    public void leggTilIdenterIPdl(List<Long> identer) {
        if(!identer.isEmpty()){
            log.info("Legger til {} ident(er) i PDL", identer.size());

            for(var ident : identer){
                var personApiResponse = personConsumer.leggTilIdentIPdl(PersonDTO.builder().ident(ident+"").build());
                if(!personApiResponse.getStatusCode().is2xxSuccessful()){
                    log.error("Kunne ikke legge følgende ident til i PDL: {}", ident);
                }
            }
        }
    }

}
