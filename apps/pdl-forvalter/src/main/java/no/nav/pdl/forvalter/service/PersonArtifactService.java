package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AddressServiceConsumer;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.dto.PersonUpdateRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonArtifactService {

    private final KontaktAdresseService kontaktAdresseService;
    private final MapperFacade mapperFacade;
    private final AddressServiceConsumer adresseServiceConsumer;

    public PdlPerson buildPerson(PersonUpdateRequest request) {

        return PdlPerson.builder()
                .kontaktadresse(kontaktAdresseService.resolve(request.getPerson().getKontaktadresse()))
                .build();
    }
}
