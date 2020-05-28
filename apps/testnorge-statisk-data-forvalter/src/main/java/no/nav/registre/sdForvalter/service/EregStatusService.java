package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
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

    public OrganisasjonStatusMap getStatusByOrgnr(String miljo, String gruppe, Boolean equal) {
        return getStatus(miljo, adapter.fetchByIds(Set.of(gruppe)), equal);
    }


    public OrganisasjonStatusMap getStatusByGruppe(String miljo, String gruppe, Boolean equal) {
        return getStatus(miljo, adapter.fetchBy(gruppe), equal);
    }


    private OrganisasjonStatusMap getStatus(String miljo, EregListe liste, Boolean equal) {
        Map<String, Organisasjon> organisasjoner = consumer.getOrganisasjoner(
                liste.getListe().stream().map(Ereg::getOrgnr).collect(Collectors.toList()),
                miljo
        );
        return new OrganisasjonStatusMap(
                miljo,
                liste
                        .getListe()
                        .stream()
                        .map(item -> new OrganisasjonStatus(item.getOrgnr(), item, organisasjoner.get(item.getOrgnr())))
                        .filter(item -> equal == null || item.isEqual() == equal)
                        .collect(Collectors.toList())
        );
    }

}
