package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.GenererOrganisasjonPopulasjonConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.OrganisasjonConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Organisajon;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonService {
    private final GenererOrganisasjonPopulasjonConsumer genererOrganisasjonPopulasjonConsumer;
    private final OrganisasjonConsumer organisasjonConsumer;

    public List<Organisajon> getOpplysningspliktigeOrganisasjoner(String miljo) {
        var opplysningspliktigOrgnummer = genererOrganisasjonPopulasjonConsumer.getOpplysningspliktig(miljo);
        var list = organisasjonConsumer
                .getOrganisasjoner(opplysningspliktigOrgnummer, miljo)
                .collectList()
                .block();
        log.info("Funnet {} opplysningspliktige organiasjoner.", list.size());
        return list
                .stream()
                .filter(value -> !value.getDriverVirksomheter().isEmpty())
                .map(Organisajon::new)
                .collect(Collectors.toList());
    }
}
