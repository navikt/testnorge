package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.TenorConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.AdresseDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDetaljerDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonRequest;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.tenor.TenorOversiktOrganisasjonResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonSelectOptions.OrganisasjonForm.BEDR;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenorService {
    private final TenorConsumer tenorConsumer;

    public List<OrganisasjonDTO> hentOrganisasjoner(int antall) {
        List<String> orgNummere = hentOrgNummere(antall);
        List<OrganisasjonDTO> organisasjoner = new ArrayList<>();
        orgNummere.forEach(
                orgNummer -> {
                    organisasjoner.add(OrganisasjonDTO.builder()
                            .organisasjonsnummer(orgNummer)
                            .organisasjonDetaljer(OrganisasjonDetaljerDTO.builder()
                                    .forretningsadresser(List.of(AdresseDTO.builder()
                                            .postnummer(hentOrgPostnummer(orgNummer))
                                            .build()))
                                    .build())
                            .build());
                }
        );
        return organisasjoner;
    }

    private TenorOrganisasjonRequest lagOrganisasjonOversiktRequest() {
        return TenorOrganisasjonRequest.builder()
                .organisasjonsform(TenorOrganisasjonRequest.Organisasjonsform.builder()
                        .kode(BEDR)
                        .build())
                .build();
    }
    private TenorOrganisasjonRequest lagOrganisasjonRequest(String organisasjonsnummer) {
        return TenorOrganisasjonRequest.builder()
                .organisasjonsnummer(organisasjonsnummer)
                .build();
    }

    private TenorOversiktOrganisasjonResponse hentOrganisasjonerOversikt(int antall) {
        return tenorConsumer.hentOrganisasjonerOversikt(lagOrganisasjonOversiktRequest(), String.valueOf(antall));
    }

    private List<String> hentOrgNummere(int antall) {
        List<TenorOversiktOrganisasjonResponse.Organisasjon> organisasjoner = hentOrganisasjonerOversikt(antall).getData().getOrganisasjoner();
        List<String> orgNummere = new ArrayList<>();
        organisasjoner.forEach(
                org -> {
                    orgNummere.add(org.getOrganisasjonsnummer());
                }
        );
        return orgNummere;
    }

    private TenorOversiktOrganisasjonResponse hentOrganisasjon(String organisasjonsnummer) {
        return tenorConsumer.hentOrganisasjon(lagOrganisasjonRequest(organisasjonsnummer));
    }

    public String hentOrgPostnummer(String organisasjonsnummer) {
        TenorOversiktOrganisasjonResponse orgResponse = hentOrganisasjon(organisasjonsnummer);
        JsonNode brregKildedata = orgResponse.getData().getOrganisasjoner().getFirst().getBrregKildedata();
        return brregKildedata.get("forretningsadresse").get("postnummer").toString();
    }
}
