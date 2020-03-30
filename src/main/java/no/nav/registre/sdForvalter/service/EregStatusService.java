package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.adapter.EregAdapter;
import no.nav.registre.sdForvalter.consumer.rs.ereg.EregConsumer;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.EregListe;
import no.nav.registre.sdForvalter.domain.status.ereg.OrganisasjonStatusMap;
import no.nav.registre.sdForvalter.domain.status.ereg.Organisasjon;
import no.nav.registre.sdForvalter.domain.status.ereg.OrganisasjonStatus;

@Service
@RequiredArgsConstructor
public class EregStatusService {
    private final EregAdapter adapter;
    private final EregConsumer consumer;

    public OrganisasjonStatusMap getStatus(String miljo, String gruppe, Boolean equal) {
        EregListe eregListe = adapter.fetchBy(gruppe);
        Map<String, Organisasjon> organisasjoner = consumer.getOrganisasjoner(
                eregListe.getListe().stream().map(Ereg::getOrgnr).collect(Collectors.toList()),
                miljo
        );
        return new OrganisasjonStatusMap(
                miljo,
                eregListe
                        .getListe()
                        .stream()
                        .map(item -> new OrganisasjonStatus(item.getOrgnr(), item, organisasjoner.get(item.getOrgnr())))
                        .filter(item -> equal == null || item.isEqual() == equal)
                        .collect(Collectors.toList())
                );
    }
}
