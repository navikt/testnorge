package no.nav.registre.testnorge.originalpopulasjon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.core.util.IdentUtil;
import no.nav.registre.testnorge.libs.dto.syntperson.v1.SyntPersonDTO;
import no.nav.registre.testnorge.originalpopulasjon.consumer.IdentPoolConsumer;
import no.nav.registre.testnorge.originalpopulasjon.consumer.SyntPersonConsumer;
import no.nav.registre.testnorge.originalpopulasjon.domain.Adresse;
import no.nav.registre.testnorge.originalpopulasjon.domain.Alderskategori;
import no.nav.registre.testnorge.originalpopulasjon.domain.Person;

@Service
@RequiredArgsConstructor
public class PopulasjonService {

    private final StatistikkService statistikkService;
    private final SyntPersonConsumer syntPersonConsumer;
    private final IdentPoolConsumer identPoolConsumer;

    public List<Person> createPopulasjon(Integer antall) {
        List<String> identliste = getIdenter(antall);
        List<SyntPersonDTO> personinfoliste = getPersonInfo(antall.toString());

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
                            ).build()
            );
        }
        return populasjon;
    }

    private List<String> getIdenter(Integer antall) {
        List<Alderskategori> liste = statistikkService.getAlderskategorier(antall);

        return liste.stream()
                .map(alderskategori -> {
                    if (alderskategori.getAntall() > 0) {
                        return identPoolConsumer.getIdenter(alderskategori);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<SyntPersonDTO> getPersonInfo(String antall) {
        return syntPersonConsumer.getPersonInfo(antall);
    }
}
