package no.nav.registre.sdforvalter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.consumer.rs.OrganisasjonConsumer;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.sdforvalter.domain.status.ereg.Organisasjon;
import no.nav.registre.sdforvalter.domain.status.ereg.OrganisasjonStatus;
import no.nav.registre.sdforvalter.domain.status.ereg.OrganisasjonStatusMap;

@Service
@RequiredArgsConstructor
public class EregStatusService {
    private final EregAdapter adapter;
    private final OrganisasjonConsumer consumer;

    public OrganisasjonStatusMap getStatusByOrgnr(String miljo, String gruppe, Boolean equal) {
        return getStatus(miljo, adapter.fetchByIds(Set.of(gruppe)), equal);
    }


    public OrganisasjonStatusMap getStatusByGruppe(String miljo, String gruppe, Boolean equal) {
        return getStatus(miljo, adapter.fetchBy(gruppe), equal);
    }


    private OrganisasjonStatusMap getStatus(String miljo, EregListe liste, Boolean equal) {
        Map<String, Organisasjon> organisasjoner = consumer.getOrganisasjoner(
                liste.getListe().stream().map(Ereg::getOrgnr).toList(),
                miljo
        );
        return new OrganisasjonStatusMap(
                miljo,
                liste
                        .getListe()
                        .stream()
                        .map(item -> new OrganisasjonStatus(item.getOrgnr(), item, organisasjoner.get(item.getOrgnr())))
                        .filter(item -> equal == null || item.isEqual() == equal)
                        .toList()
        );
    }

}
