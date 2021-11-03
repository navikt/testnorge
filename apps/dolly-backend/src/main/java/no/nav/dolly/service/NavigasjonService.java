package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testident.RsWhereAmI;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.IdentRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NavigasjonService {

    private final IdentRepository identRepository;
    private final IdentService identService;
    private final TpsfService tpsfService;
    private final MapperFacade mapperFacade;
    private final PdlDataConsumer pdlDataConsumer;

    public RsWhereAmI navigerTilIdent(String ident) {

        var identer = Stream.of(List.of(ident),
                        tpsfService.hentTestpersoner(List.of(ident)).stream().findFirst().orElse(new Person())
                                .getRelasjoner().stream()
                                .map(Relasjon::getPersonRelasjonMed)
                                .map(Person::getIdent)
                                .toList(),
                        pdlDataConsumer.getPersoner(List.of(ident)).stream().findFirst().orElse(new FullPersonDTO())
                                .getRelasjoner().stream()
                                .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                .map(PersonDTO::getIdent)
                                .toList())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        var identerFound = identRepository.findByIdentIn(identer);
        if (!identerFound.isEmpty()) {

            Testident testident = identerFound.get(0);
            return RsWhereAmI.builder()
                    .gruppe(mapperFacade.map(testident.getTestgruppe(), RsTestgruppe.class))
                    .identHovedperson(testident.getIdent())
                    .identNavigerTil(ident)
                    .sidetall(Math.floorDiv(
                            identService.getPaginertIdentIndex(testident.getIdent(), testident.getTestgruppe().getId()).orElse(0), 10))
                    .build();
        } else {

            throw new NotFoundException(ident + " ble ikke funnet i database");
        }
    }
}
