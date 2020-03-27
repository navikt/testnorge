package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.adapter.EregAdapter;
import no.nav.registre.sdForvalter.consumer.rs.ereg.EregConsumer;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.EregListe;
import no.nav.registre.sdForvalter.domain.status.ereg.OranisasjonStatusMap;
import no.nav.registre.sdForvalter.domain.status.ereg.Organisasjon;
import no.nav.registre.sdForvalter.domain.status.ereg.OrganisasjonStatus;

@Service
@RequiredArgsConstructor
public class EregStatusService {
    private final EregAdapter adapter;
    private final EregConsumer consumer;

    public OranisasjonStatusMap getStatus(String miljo, String gruppe) {
        EregListe eregListe = adapter.fetchBy(gruppe);
        Map<String, Organisasjon> organisasjoner = consumer.getOrganisasjoner(
                eregListe.getListe().stream().map(Ereg::getOrgnr).collect(Collectors.toList()),
                miljo
        );
        OranisasjonStatusMap liste = new OranisasjonStatusMap(miljo);
        eregListe
                .getListe()
                .forEach(ereg -> liste.put(
                        ereg.getOrgnr(),
                        new OrganisasjonStatus(ereg, organisasjoner.get(ereg.getOrgnr())))
                );
        return liste;
    }

}
