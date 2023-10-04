package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.GenererOrganisasjonPopulasjonConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.OrganisasjonConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpplysningspliktigService {
    private final OppsummeringsdokumentConsumer oppsummeringsdokumentConsumer;
    private final OrganisasjonConsumer organisasjonConssumer;
    private final GenererOrganisasjonPopulasjonConsumer genererOrganisasjonPopulasjonConsumer;

    public List<Organisajon> getOpplysningspliktigeOrganisasjoner(String miljo) {
        var opplysningspliktigOrgnummer = genererOrganisasjonPopulasjonConsumer.getOpplysningspliktig(miljo);
        return organisasjonConssumer
                .getOrganisasjoner(opplysningspliktigOrgnummer, miljo)
                .stream()
                .filter(value -> !value.getDriverVirksomheter().isEmpty())
                .map(Organisajon::new)
                .toList();
    }

    public List<Opplysningspliktig> getAllOpplysningspliktig(LocalDate kalendermaaned, String miljo) {
        var list = getOpplysningspliktigeOrganisasjoner(miljo);
        log.info("Fant {} opplysningspliktig i Mini-Norge Ereg.", list.stream().map(Organisajon::getOrgnummer).collect(Collectors.joining(", ")));
        return list.stream().map(organisajon -> getOpplysningspliktig(organisajon, kalendermaaned, miljo)).toList();
    }

    public void send(List<Opplysningspliktig> list, String miljo) {
        var opplysningspliktige = list.stream().filter(value -> {
            if (!value.isChanged()) {
                log.trace(
                        "Raporter ikke for opplysningspliktig {} siden det ikke er en endring den {}.",
                        value.getOrgnummer(),
                        value.getKalendermaaned()
                );
            }
            return value.isChanged();
        }).toList();
        oppsummeringsdokumentConsumer.sendOpplysningspliktig(opplysningspliktige, miljo);
    }

    private Opplysningspliktig getOpplysningspliktig(Organisajon organisajon, LocalDate kalendermaaned, String miljo) {
        Optional<Opplysningspliktig> opplysningspliktig = oppsummeringsdokumentConsumer.getOpplysningspliktig(organisajon, kalendermaaned, miljo);
        if (opplysningspliktig.isPresent()) {
            Opplysningspliktig temp = opplysningspliktig.get();
            temp.setVersion(temp.getVersion() + 1);
            temp.setKalendermaaned(kalendermaaned);
            return temp;
        }
        return new Opplysningspliktig(organisajon, kalendermaaned);
    }
}
