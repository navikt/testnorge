package no.nav.registre.skd.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.consumer.PersonApiConsumer;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PersonApiService {

    @Autowired
    private PersonApiConsumer personApiConsumer;

    public void leggTilIdenterIPdl(List<String> identer) {
        if(!identer.isEmpty()){
            log.info("Legger til {} ident(er) i PDL", identer.size());

            for(var ident : identer){
                var personApiResponse = personApiConsumer.leggTilIdentIPdl(PersonDTO.builder().ident(ident).build());
                if(!personApiResponse.getStatusCode().is2xxSuccessful()){
                    log.error("Kunne ikke legge følgende ident til i PDL: {}", ident);
                }
            }
        }
    }

}
