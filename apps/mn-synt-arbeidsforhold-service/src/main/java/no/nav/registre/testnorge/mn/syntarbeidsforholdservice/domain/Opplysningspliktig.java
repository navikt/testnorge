package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OpplysningspliktigDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.VirksomhetDTO;

@Slf4j
@DependencyOn("arbeidsforhold-api")
@RequiredArgsConstructor
public class Opplysningspliktig {
    private final OpplysningspliktigDTO dto;
    private final Random random = new Random();

    public String getRandomVirksomhetsnummer() {
        return dto.getVirksomheter().get(random.nextInt(dto.getVirksomheter().size())).getOrganisajonsnummer();
    }


    public void addArbeidsforhold(String virksomhetsnummer, Arbeidsforhold arbeidsforhold) {
        VirksomhetDTO virksomhet = dto.getVirksomheter()
                .stream()
                .filter(value -> value.getOrganisajonsnummer().equals(virksomhetsnummer))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Finner ikke viksomhent " + virksomhetsnummer));


        Optional<PersonDTO> optional = virksomhet.getPersoner()
                .stream()
                .filter(value -> value.getIdent().equals(arbeidsforhold.getIdent()))
                .findFirst();

        if (optional.isEmpty()) {
            virksomhet.getPersoner().add(new PersonDTO(arbeidsforhold.getIdent(), Collections.singletonList(
                    arbeidsforhold.toDTO()
            )));
        } else {
            PersonDTO personDTO = optional.get();
            Optional<ArbeidsforholdDTO> tidligereArbeidsforhold = personDTO.getArbeidsforhold()
                    .stream()
                    .filter(arbeidsforholdDTO -> arbeidsforholdDTO.getArbeidsforholdId().equals(arbeidsforhold.getArbeidsforholdId()))
                    .findFirst();

            List<ArbeidsforholdDTO> list = new ArrayList<>(personDTO.getArbeidsforhold());
            if (tidligereArbeidsforhold.isPresent()) {
                ArbeidsforholdDTO dto = tidligereArbeidsforhold.get();
                log.info("Fjerner tidligere arbeidsforhold {}", dto.getArbeidsforholdId());
                list.remove(dto);
            }

            list.add(arbeidsforhold.toDTO());
            personDTO.setArbeidsforhold(list);
        }
    }

    public void setKalendermaaned(LocalDate kalendermaaned) {
        dto.setKalendermaaned(kalendermaaned);
    }

    public Set<String> getIdenter() {
        Set<String> identer = new HashSet<>();
        dto.getVirksomheter()
                .forEach(virksomhet -> virksomhet.getPersoner().forEach(person -> identer.add(person.getIdent())));
        return identer;
    }

    public OpplysningspliktigDTO toDTO() {
        return dto;
    }

}
