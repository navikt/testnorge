package no.nav.registre.testnorge.originalpopulasjon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import no.nav.registre.testnorge.libs.core.util.IdentUtil;
import no.nav.testnav.libs.dto.syntperson.v1.SyntPersonDTO;
import no.nav.registre.testnorge.originalpopulasjon.consumer.SyntPersonConsumer;
import no.nav.registre.testnorge.originalpopulasjon.domain.Adresse;
import no.nav.registre.testnorge.originalpopulasjon.domain.Person;

@Service
@RequiredArgsConstructor
public class PopulasjonService {

    private static final Set<String> TAGS = Set.of("MININORGE");

    private final IdentService identService;
    private final SyntPersonConsumer syntPersonConsumer;

    public List<Person> createPopulasjon(Integer antall) {
        List<String> identliste = identService.getIdenter(antall);
        List<SyntPersonDTO> personinfoliste = getPersonInfo(antall);

        List<Person> populasjon = new ArrayList<>();
        for (int i = 0; i < identliste.size(); i++) {
            SyntPersonDTO syntPerson = personinfoliste.get(i);
            String ident = identliste.get(i);
            populasjon.add(
                    Person.builder()
                            .ident(ident)
                            .fornavn(syntPerson.getFornavn())
                            .etternavn(syntPerson.getSlektsnavn())
                            .foedselsdato(IdentUtil.getFoedselsdatoFraIdent(ident))
                            .adresse(Adresse.builder()
                                    .gatenavn(syntPerson.getAdressenavn())
                                    .postnummer(syntPerson.getPostnummer())
                                    .kommunenummer(syntPerson.getKommunenummer())
                                    .build()
                            )
                            .tags(TAGS)
                            .build()
            );
        }
        return populasjon;
    }

    private List<SyntPersonDTO> getPersonInfo(Integer antall) {
        return syntPersonConsumer.getPersonInfo(antall);
    }
}
