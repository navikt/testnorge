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

    /**
     * Henter organisasjon- og postnummere fra Tenor, og bygger et OrganisasjonDTO-objekt
     * med orgnummer og postnummer per organisasjon som hentes
     *
     * @param antall Antall organisasjoner man vil hente
     * @return Returnerer en liste med OrganisasjonDTO-objekter
     */
    public List<OrganisasjonDTO> hentOrganisasjoner(int antall) {
        List<String> orgNummere = hentOrgNummere(antall);
        List<OrganisasjonDTO> organisasjoner = new ArrayList<>();
        orgNummere.forEach(
                orgNummer -> organisasjoner.add(OrganisasjonDTO.builder()
                        .organisasjonsnummer(orgNummer)
                        .organisasjonDetaljer(OrganisasjonDetaljerDTO.builder()
                                .forretningsadresser(List.of(AdresseDTO.builder()
                                        .postnummer(hentOrgPostnummer(orgNummer))
                                        .build()))
                                .build())
                        .build())
        );
        return organisasjoner;
    }

    /**
     *
     * @return TenorOrganisasjonRequest-objekt med organisasjonsform-koden "BEDR"
     * for å kun hente organisasjoner som kan ha ansettelser
     */
    private TenorOrganisasjonRequest lagOrganisasjonOversiktRequest() {
        return TenorOrganisasjonRequest.builder()
                .organisasjonsform(TenorOrganisasjonRequest.Organisasjonsform.builder()
                        .kode(BEDR)
                        .build())
                .build();
    }

    /**
     * Lager et TenorOrganisasjonRequest-objekt som skal sendes til Tenor for å hente brregKildedata
     *
     * @param organisasjonsnummer Organisasjonsnummeret til den organisasjonen det skal lages request for
     * @return TenorOrganisasjonRequest-objekt med det innsendte organisasjonsnummeret
     */
    private TenorOrganisasjonRequest lagOrganisasjonRequest(String organisasjonsnummer) {
        return TenorOrganisasjonRequest.builder()
                .organisasjonsnummer(organisasjonsnummer)
                .build();
    }

    /**
     * Henter tilfeldige organisasjoner fra Tenor
     *
     * @param antall Antall organisasjoner som skal hentes
     * @return TenorOversiktOrganisasjonResponse-objekt med tilfeldige organisasjoner fra Tenor
     */
    private TenorOversiktOrganisasjonResponse hentOrganisasjonerOversikt(int antall) {
        return tenorConsumer.hentOrganisasjonerOversikt(lagOrganisasjonOversiktRequest(), String.valueOf(antall));
    }

    /**
     * Omgjør listen med response-objekter fra Tenor til en liste med kun de tilsvarende organisasjonsnummerne
     *
     * @param antall Antall organisasjoner som skal hentes
     * @return Liste med organisasjonsnummere for det gitte antallet organisasjoner
     */
    private List<String> hentOrgNummere(int antall) {

        return hentOrganisasjonerOversikt(antall).getData().getOrganisasjoner().stream()
                .map(TenorOversiktOrganisasjonResponse.Organisasjon::getOrganisasjonsnummer)
                .toList();
    }

    /**
     * Henter all organisasjonsdata for det oppgitte organisasjonsnummeret fra Tenor,
     * slik at man får tak i postnummer fra brregKildedata
     *
     * @param organisasjonsnummer Organisasjonsnummeret til organisasjonen man vil hente data for
     * @return TenorOversiktOrganisasjonResponse-objekt med all informasjon
     */
    private TenorOversiktOrganisasjonResponse hentOrganisasjon(String organisasjonsnummer) {
        return tenorConsumer.hentOrganisasjon(lagOrganisasjonRequest(organisasjonsnummer));
    }

    /**
     * @param organisasjonsnummer Organisasjonsnummeret til organisasjonen man vil hente postnummer for
     * @return Postnummeret til organisasjonen i String-format eller null dersom brregKildedata er null
     */
    public String hentOrgPostnummer(String organisasjonsnummer) {
        TenorOversiktOrganisasjonResponse orgResponse = hentOrganisasjon(organisasjonsnummer);
        JsonNode brregKildedata = orgResponse.getData().getOrganisasjoner().getFirst().getBrregKildedata();
        if (!brregKildedata.isNull()) {
            return brregKildedata.get("forretningsadresse").get("postnummer").toString().replace("\"", "");
        }
        return null;
    }
}
