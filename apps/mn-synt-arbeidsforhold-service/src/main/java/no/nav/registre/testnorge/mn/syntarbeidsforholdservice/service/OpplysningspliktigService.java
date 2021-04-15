package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.MNOrganisasjonConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.exception.MNOrganisasjonException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpplysningspliktigService {
    private final OppsummeringsdokumentConsumer oppsummeringsdokumentConsumer;
    private final MNOrganisasjonConsumer organisasjonConsumer;


    private List<Organisajon> getOpplysningspliktigeOrganisasjoner(String miljo) {
        log.info("Henter opplysningspliktige organisasjoner...");
        List<Organisajon> organisajoner = organisasjonConsumer
                .getOrganisajoner(miljo)
                .stream()
                .filter(Organisajon::isOpplysningspliktig)
                .filter(Organisajon::isDriverVirksomheter)
                .collect(Collectors.toList());

        if (organisajoner.isEmpty()) {
            throw new MNOrganisasjonException("Fant ingen opplysningspliktige i Mini-Norge som driver virksomheter.");
        }

        log.info("Fant {} opplysningspliktige organisasjoner.", organisajoner.size());
        return organisajoner;
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

    public List<Opplysningspliktig> getAllOpplysningspliktig(LocalDate kalendermaaned, String miljo) {
        var list = getOpplysningspliktigeOrganisasjoner(miljo);
        log.info("Fant {} opplysningspliktig i Mini-Norge Ereg.", list.stream().map(Organisajon::getOrgnummer).collect(Collectors.joining(", ")));
        return list.stream().map(organisajon -> getOpplysningspliktig(organisajon, kalendermaaned, miljo)).collect(Collectors.toList());
    }

    public void send(List<Opplysningspliktig> list, String miljo) {
        var opplysningspliktige = list.stream().filter(value -> {
            if (!value.isChanged()) {
                log.info(
                        "Raporter ikke for opplysningspliktig {} siden det ikke er en endring den {}.",
                        value.getOrgnummer(),
                        value.getKalendermaaned()
                );
            }
            return value.isChanged();
        }).collect(Collectors.toList());
        oppsummeringsdokumentConsumer.sendOpplysningspliktig(opplysningspliktige, miljo);
    }
}
