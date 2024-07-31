package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.TenorConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonRequest;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonSelectOptions.OrganisasjonForm.BEDR;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenorService {
    private final TenorConsumer tenorConsumer;

    private TenorOrganisasjonRequest lagOrganisasjonRequest() {

        return TenorOrganisasjonRequest.builder()
                .organisasjonsform(TenorOrganisasjonRequest.Organisasjonsform.builder()
                        .kode(BEDR)
                        .build())
                .build();
    }

    public List<OrganisasjonDTO> hentOrganisasjoner(int antall) throws JsonProcessingException {
        return tenorConsumer.hentOrganisasjoner(lagOrganisasjonRequest(), String.valueOf(antall));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void test() throws JsonProcessingException {
        log.info("Henter organisasjoner {}", hentOrganisasjoner(5));
    }
}
