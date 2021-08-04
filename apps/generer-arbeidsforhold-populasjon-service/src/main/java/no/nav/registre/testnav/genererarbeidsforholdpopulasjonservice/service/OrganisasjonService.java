package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.GenererOrganisasjonPopulasjonConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.OrganisasjonConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Organisajon;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonService {
    private final GenererOrganisasjonPopulasjonConsumer genererOrganisasjonPopulasjonConsumer;
    private final OppsummeringsdokumentConsumer oppsummeringsdokumentConsumer;
    private final OrganisasjonConsumer organisasjonConsumer;

    public List<Organisajon> getOpplysningspliktigeOrganisasjoner(String miljo, boolean unused) {
       var oppsummeringsdokuments = unused ? oppsummeringsdokumentConsumer.getAll(miljo) : new ArrayList<OppsummeringsdokumentDTO>();

        var opplysningspliktigOrgnummer = genererOrganisasjonPopulasjonConsumer.getOpplysningspliktig(miljo);
        var list = organisasjonConsumer
                .getOrganisasjoner(opplysningspliktigOrgnummer, miljo)
                .collectList()
                .block();
        log.info("Funnet {} opplysningspliktige organiasjon(er).", list.size());
        return list
                .stream()
                .filter(value -> !oppsummeringsdokuments
                        .stream()
                        .map(OppsummeringsdokumentDTO::getOpplysningspliktigOrganisajonsnummer)
                        .collect(Collectors.toSet())
                        .contains(value)
                )
                .filter(value -> !value.getDriverVirksomheter().isEmpty())
                .map(Organisajon::new)
                .collect(Collectors.toList());
    }

}
