package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PdlProxyConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.Tags;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.personsearchservice.v1.SivilstandDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class TagsService {
    public static final List<Tags> SYNT_TAGS = List.of(Tags.ARENASYNT);
    public static final List<String> GYLDIG_SIVILSTAND = Arrays.asList(
            "GIFT",
            "ENKE_ELLER_ENKEMANN",
            "SKILT",
            "SEPARERT",
            "REGISTRERT_PARTNER",
            "SKILT_PARTNER",
            "SEPARERT_PARTNER",
            "GJENLEVENDE_PARTNER"
    );

    private final PdlProxyConsumer pdlProxyConsumer;

    public boolean opprettetTagsPaaIdenterOgPartner(List<PersonDTO> personer) {
        var identer = new ArrayList<>(personer.stream().map(PersonDTO::getIdent).toList());
        var partnere = personer.stream()
                .map(PersonDTO::getSivilstand)
                .filter(sivilstand -> GYLDIG_SIVILSTAND.contains(sivilstand.getType()))
                .map(SivilstandDTO::getRelatertVedSivilstand)
                .filter(Objects::nonNull)
                .toList();

        identer.addAll(partnere);
        return pdlProxyConsumer.createTags(identer, SYNT_TAGS);
    }

    public void removeTagsPaaIdentOgPartner(PersonDTO person) {
        if (nonNull(person.getSivilstand()) && GYLDIG_SIVILSTAND.contains(person.getSivilstand().getType())) {
            pdlProxyConsumer.deleteTags(Arrays.asList(person.getIdent(), person.getSivilstand().getRelatertVedSivilstand()), SYNT_TAGS);
        } else {
            pdlProxyConsumer.deleteTags(Collections.singletonList(person.getIdent()), SYNT_TAGS);
        }
    }
}
