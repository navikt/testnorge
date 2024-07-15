package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.OrganisasjonFasteDataConsumer;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class HentOrganisasjonNummerService {
    private final OrganisasjonFasteDataConsumer organisasjonFasteDataConsumer;

    //@EventListener(ApplicationReadyEvent.class)
    public List<OrganisasjonDTO> hentAlleOrganisasjoner() {
        return organisasjonFasteDataConsumer.hentOrganisasjoner();
    }

    public List<OrganisasjonDTO> hentAntallOrganisasjoner(int antall) {
        List<OrganisasjonDTO> alleOrganisasjoner = hentAlleOrganisasjoner();
        List<OrganisasjonDTO> tilfeldigeOrganisasjoner = new ArrayList<>();

        int antallOrganisasjoner = alleOrganisasjoner.size();
        
        for (int i = 0; i < antall; i++) {
            int tilfeldigTall = hentTilfeldigTall(antallOrganisasjoner);
            tilfeldigeOrganisasjoner.add(alleOrganisasjoner.get(tilfeldigTall));
            alleOrganisasjoner.remove(tilfeldigTall);
            antallOrganisasjoner--;
        }
        return tilfeldigeOrganisasjoner;
    }

    private int hentTilfeldigTall(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }
}